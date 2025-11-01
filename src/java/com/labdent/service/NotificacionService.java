package com.labdent.service;

import com.labdent.dao.NotificacionDAO;
import com.labdent.model.Notificacion;
import com.labdent.model.Pedido;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servicio para gestionar notificaciones
 * Implementa Single Responsibility Principle
 */
public class NotificacionService {
    
    private static final Logger logger = LoggerFactory.getLogger(NotificacionService.class);
    
    private final NotificacionDAO notificacionDAO;
    
    public NotificacionService(NotificacionDAO notificacionDAO) {
        this.notificacionDAO = notificacionDAO;
    }
    
    /**
     * Notifica al cliente sobre el cambio de estado de su pedido
     */
    public void notificarCambioEstado(Pedido pedido, String nuevoEstado) {
        try {
            if (pedido.getUsuarioId() > 0) {
                Notificacion notificacion = new Notificacion();
                notificacion.setUsuarioId(pedido.getUsuarioId());
                notificacion.setPedidoId(pedido.getId());
                notificacion.setTipo("INFO");
                notificacion.setTitulo("Actualización de Pedido");
                notificacion.setMensaje(String.format(
                    "Tu pedido %s ha avanzado a la etapa: %s",
                    pedido.getCodigoUnico(),
                    obtenerDescripcionEstado(nuevoEstado)
                ));
                notificacion.setLeida(false);
                
                notificacionDAO.registrar(notificacion);
                logger.info("Notificación enviada al usuario {} para pedido {}", 
                           pedido.getUsuarioId(), pedido.getId());
            }
        } catch (Exception e) {
            logger.error("Error al enviar notificación", e);
            // No lanzamos la excepción para no afectar el flujo principal
        }
    }
    
    private String obtenerDescripcionEstado(String estado) {
        switch (estado) {
            case "RECEPCION": return "Recepción";
            case "PARALELIZADO": return "Paralelizado";
            case "DISENO_CAD": return "Diseño CAD";
            case "PRODUCCION_CAM": return "Producción CAM";
            case "CERAMICA": return "Cerámica";
            case "CONTROL_CALIDAD": return "Control de Calidad";
            case "LISTO_ENTREGA": return "Listo para Entrega";
            case "ENTREGADO": return "Entregado";
            default: return estado;
        }
    }
}