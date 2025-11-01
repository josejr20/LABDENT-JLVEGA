package com.labdent.controller;

import com.labdent.dao.NotificacionDAO;
import com.labdent.dao.PedidoDAO;
import com.labdent.dao.TransicionEstadoDAO;
import com.labdent.exception.InvalidStateTransitionException;
import com.labdent.exception.PedidoNotFoundException;
import com.labdent.model.Usuario;
import com.labdent.service.NotificacionService;
import com.labdent.service.PedidoService;
import com.labdent.util.ValidationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Controller para actualizar el estado de un pedido
 */
@WebServlet("/ActualizarEstadoPedido")
public class ActualizarEstadoPedidoController extends HttpServlet {
    
    private static final Logger logger = LoggerFactory.getLogger(ActualizarEstadoPedidoController.class);
    
    private PedidoService pedidoService;
    
    @Override
    public void init() throws ServletException {
        super.init();
        
        PedidoDAO pedidoDAO = new PedidoDAO();
        TransicionEstadoDAO transicionDAO = new TransicionEstadoDAO();
        NotificacionDAO notificacionDAO = new NotificacionDAO();
        NotificacionService notificacionService = new NotificacionService(notificacionDAO);
        
        this.pedidoService = new PedidoService(pedidoDAO, transicionDAO, notificacionService);
        
        logger.info("ActualizarEstadoPedidoController inicializado");
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        
        // Obtener parámetros
        String pedidoIdStr = request.getParameter("pedidoId");
        String nuevoEstado = request.getParameter("nuevoEstado");
        String observaciones = request.getParameter("observaciones");
        
        logger.info("Actualizando pedido {} a estado {} por usuario {}", 
                   pedidoIdStr, nuevoEstado, usuario.getId());
        
        try {
            // Validar parámetros
            if (!ValidationUtil.isNotEmpty(pedidoIdStr) || !ValidationUtil.isNotEmpty(nuevoEstado)) {
                throw new IllegalArgumentException("Parámetros requeridos faltantes");
            }
            
            int pedidoId = Integer.parseInt(pedidoIdStr);
            
            // Sanitizar observaciones
            String observacionesSanitizadas = ValidationUtil.sanitizeHtml(
                StringUtils.defaultString(observaciones)
            );
            
            // Actualizar estado usando el servicio
            boolean actualizado = pedidoService.actualizarEstadoPedido(
                pedidoId, 
                nuevoEstado, 
                usuario.getId(), 
                observacionesSanitizadas
            );
            
            if (actualizado) {
                logger.info("Estado actualizado correctamente para pedido {}", pedidoId);
                session.setAttribute("success", "Estado actualizado correctamente");
            } else {
                logger.warn("No se pudo actualizar el estado del pedido {}", pedidoId);
                session.setAttribute("error", "No se pudo actualizar el estado");
            }
            
        } catch (PedidoNotFoundException e) {
            logger.error("Pedido no encontrado", e);
            session.setAttribute("error", "Pedido no encontrado");
            
        } catch (InvalidStateTransitionException e) {
            logger.warn("Transición de estado inválida", e);
            session.setAttribute("error", String.format(
                "No se puede cambiar de %s a %s", 
                e.getCurrentState(), 
                e.getTargetState()
            ));
            
        } catch (NumberFormatException e) {
            logger.error("ID de pedido inválido", e);
            session.setAttribute("error", "ID de pedido inválido");
            
        } catch (Exception e) {
            logger.error("Error inesperado al actualizar estado", e);
            session.setAttribute("error", "Error al actualizar el estado: " + e.getMessage());
        }
        
        response.sendRedirect("PanelTecnico");
    }
}