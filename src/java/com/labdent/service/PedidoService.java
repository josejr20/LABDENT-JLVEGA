package com.labdent.service;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.labdent.dao.PedidoDAO;
import com.labdent.dao.TransicionEstadoDAO;
import com.labdent.exception.InvalidStateTransitionException;
import com.labdent.exception.PedidoNotFoundException;
import com.labdent.model.Pedido;
import com.labdent.model.TransicionEstado;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Servicio para gestionar la lógica de negocio de pedidos
 * Implementa Single Responsibility Principle (SOLID)
 */
public class PedidoService {
    
    private static final Logger logger = LoggerFactory.getLogger(PedidoService.class);
    
    private final PedidoDAO pedidoDAO;
    private final TransicionEstadoDAO transicionDAO;
    private final NotificacionService notificacionService;
    
    // Mapa de transiciones válidas usando Guava
    private static final ImmutableMap<String, List<String>> TRANSICIONES_VALIDAS = 
        ImmutableMap.<String, List<String>>builder()
            .put("RECEPCION", List.of("PARALELIZADO"))
            .put("PARALELIZADO", List.of("DISENO_CAD"))
            .put("DISENO_CAD", List.of("PRODUCCION_CAM"))
            .put("PRODUCCION_CAM", List.of("CERAMICA", "CONTROL_CALIDAD")) // Puede saltar cerámica
            .put("CERAMICA", List.of("CONTROL_CALIDAD"))
            .put("CONTROL_CALIDAD", List.of("LISTO_ENTREGA"))
            .put("LISTO_ENTREGA", List.of("ENTREGADO"))
            .build();
    
    public PedidoService(PedidoDAO pedidoDAO, TransicionEstadoDAO transicionDAO, 
                         NotificacionService notificacionService) {
        this.pedidoDAO = Preconditions.checkNotNull(pedidoDAO, "PedidoDAO no puede ser null");
        this.transicionDAO = Preconditions.checkNotNull(transicionDAO, "TransicionEstadoDAO no puede ser null");
        this.notificacionService = Preconditions.checkNotNull(notificacionService, 
                                                              "NotificacionService no puede ser null");
    }
    
    /**
     * Obtiene un pedido por ID
     * @param pedidoId ID del pedido
     * @return Optional con el pedido si existe
     */
    public Optional<Pedido> obtenerPedidoPorId(int pedidoId) {
        Preconditions.checkArgument(pedidoId > 0, "El ID del pedido debe ser mayor a 0");
        
        logger.debug("Obteniendo pedido con ID: {}", pedidoId);
        Pedido pedido = pedidoDAO.obtenerPorId(pedidoId);
        return Optional.ofNullable(pedido);
    }
    
    /**
     * Obtiene pedidos por estado
     * @param estado Estado del pedido
     * @return Lista de pedidos
     */
    public List<Pedido> obtenerPedidosPorEstado(String estado) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(estado), "El estado no puede ser vacío");
        
        logger.debug("Obteniendo pedidos con estado: {}", estado);
        return pedidoDAO.listarPorEstado(estado);
    }
    
    /**
     * Actualiza el estado de un pedido con validaciones
     * @param pedidoId ID del pedido
     * @param nuevoEstado Nuevo estado
     * @param usuarioId ID del usuario que realiza el cambio
     * @param observaciones Observaciones del cambio
     * @return true si se actualizó correctamente
     * @throws PedidoNotFoundException si el pedido no existe
     * @throws InvalidStateTransitionException si la transición no es válida
     */
    public boolean actualizarEstadoPedido(int pedidoId, String nuevoEstado, 
                                          int usuarioId, String observaciones) {
        Preconditions.checkArgument(pedidoId > 0, "El ID del pedido debe ser mayor a 0");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(nuevoEstado), "El nuevo estado no puede ser vacío");
        Preconditions.checkArgument(usuarioId > 0, "El ID del usuario debe ser mayor a 0");
        
        logger.info("Actualizando estado del pedido {} a {}", pedidoId, nuevoEstado);
        
        // Obtener pedido actual
        Pedido pedido = pedidoDAO.obtenerPorId(pedidoId);
        if (pedido == null) {
            logger.error("Pedido no encontrado: {}", pedidoId);
            throw new PedidoNotFoundException("Pedido con ID " + pedidoId + " no encontrado");
        }
        
        String estadoActual = pedido.getEstadoActual();
        
        // Validar transición
        if (!esTransicionValida(estadoActual, nuevoEstado)) {
            logger.warn("Transición inválida: {} -> {}", estadoActual, nuevoEstado);
            throw new InvalidStateTransitionException(estadoActual, nuevoEstado);
        }
        
        // Calcular tiempo en estado anterior
        Integer tiempoEnEstado = calcularTiempoEnEstado(pedidoId);
        
        // Registrar transición
        TransicionEstado transicion = new TransicionEstado();
        transicion.setPedidoId(pedidoId);
        transicion.setEstadoAnterior(estadoActual);
        transicion.setEstadoNuevo(nuevoEstado);
        transicion.setUsuarioId(usuarioId);
        transicion.setObservaciones(StringUtils.defaultString(observaciones));
        transicion.setChecklistCompletado(true);
        transicion.setTiempoEnEstado(tiempoEnEstado);
        
        boolean transicionRegistrada = transicionDAO.registrarTransicion(transicion);
        
        if (!transicionRegistrada) {
            logger.error("Error al registrar transición para pedido {}", pedidoId);
            return false;
        }
        
        // Actualizar estado del pedido
        boolean actualizado = pedidoDAO.actualizarEstado(pedidoId, nuevoEstado, usuarioId);
        
        if (actualizado) {
            logger.info("Estado actualizado correctamente para pedido {}", pedidoId);
            
            // Enviar notificación al cliente
            notificacionService.notificarCambioEstado(pedido, nuevoEstado);
        }
        
        return actualizado;
    }
    
    /**
     * Valida si una transición de estado es válida
     */
    private boolean esTransicionValida(String estadoActual, String nuevoEstado) {
        if (Strings.isNullOrEmpty(estadoActual) || Strings.isNullOrEmpty(nuevoEstado)) {
            return false;
        }
        
        List<String> estadosPermitidos = TRANSICIONES_VALIDAS.get(estadoActual);
        return estadosPermitidos != null && estadosPermitidos.contains(nuevoEstado);
    }
    
    /**
     * Calcula el tiempo que el pedido estuvo en el estado anterior (en minutos)
     */
    private Integer calcularTiempoEnEstado(int pedidoId) {
        try {
            TransicionEstado ultimaTransicion = transicionDAO.obtenerUltimaTransicion(pedidoId);
            if (ultimaTransicion == null) {
                return null;
            }
            
            Timestamp ahora = new Timestamp(System.currentTimeMillis());
            long diferenciaMs = ahora.getTime() - ultimaTransicion.getFechaTransicion().getTime();
            return (int) (diferenciaMs / (1000 * 60)); // Convertir a minutos
            
        } catch (Exception e) {
            logger.error("Error al calcular tiempo en estado", e);
            return null;
        }
    }
    
    /**
     * Obtiene los siguientes estados posibles para un pedido
     */
    public List<String> obtenerSiguientesEstadosDisponibles(String estadoActual) {
        return TRANSICIONES_VALIDAS.getOrDefault(estadoActual, List.of());
    }
}