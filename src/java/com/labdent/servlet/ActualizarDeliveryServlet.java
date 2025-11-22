package com.labdent.servlet;

import com.labdent.dao.PedidoDeliveryDAO;
import com.labdent.dao.PedidoDAO;
import com.labdent.dao.UsuarioDAO;
import com.labdent.model.PedidoDelivery;
import com.labdent.model.Usuario;
import com.labdent.util.*;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.logging.Logger;

@WebServlet("/ActualizarDelivery")
public class ActualizarDeliveryServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(ActualizarDeliveryServlet.class.getName());
    private final PedidoDeliveryDAO deliveryDAO = new PedidoDeliveryDAO();
    private final PedidoDAO pedidoDAO = new PedidoDAO();
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // ✅ 1. Validar sesión
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect("login?error=session_expired");
            return;
        }

        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null || !"DELIVERISTA".equals(usuario.getRol())) {
            response.sendRedirect("login?error=unauthorized");
            return;
        }

        // ✅ 2. Validar token JWT
        String token = (String) session.getAttribute("token");
        if (token == null || !JWTUtil.validarToken(token) || JWTUtil.isTokenExpirado(token)) {
            session.invalidate();
            response.sendRedirect("login?error=token_expired");
            return;
        }

        // ✅ 3. Validar token en BD
        if (!usuarioDAO.validarTokenEnBD(usuario.getId(), token)) {
            session.invalidate();
            response.sendRedirect("login?error=invalid_token");
            return;
        }

        // ✅ 4. Validar CSRF token
        String csrfToken = request.getParameter("csrf_token");
        if (!SessionDataUtil.validarCSRFToken(session, csrfToken)) {
            LOGGER.warning("⚠️ CSRF token inválido - Usuario: " + usuario.getId());
            session.setAttribute("error", "Token de seguridad inválido");
            response.sendRedirect("PanelDeliverista");
            return;
        }

        // ✅ 5. Validar parámetros
        String deliveryIdParam = request.getParameter("deliveryId");
        String nuevoEstado = request.getParameter("estado");
        String observaciones = request.getParameter("observaciones");

        if (!InputValidationUtil.esIdValido(deliveryIdParam)) {
            session.setAttribute("error", "ID de delivery inválido");
            response.sendRedirect("PanelDeliverista");
            return;
        }

        if (!InputValidationUtil.esEstadoDeliveryValido(nuevoEstado)) {
            session.setAttribute("error", "Estado de delivery inválido");
            response.sendRedirect("PanelDeliverista");
            return;
        }

        // ✅ Sanitizar observaciones
        observaciones = InputValidationUtil.limpiarTexto(observaciones, 500);

        int deliveryId = Integer.parseInt(deliveryIdParam);

        LOGGER.info("🔄 Actualizando delivery " + deliveryId + " a estado: " + nuevoEstado);

        try {
            PedidoDelivery delivery = deliveryDAO.obtenerPorId(deliveryId);
            
            if (delivery == null) {
                session.setAttribute("error", "Delivery no encontrado");
                response.sendRedirect("PanelDeliverista");
                return;
            }

            // ✅ 6. Verificar que el deliverista es el dueño
            if (delivery.getDeliveristaId() != usuario.getId()) {
                LOGGER.warning("⚠️ Intento de actualizar delivery ajeno - Usuario: " + 
                             usuario.getId() + " | Delivery: " + deliveryId);
                session.setAttribute("error", "No tiene permisos para actualizar este delivery");
                response.sendRedirect("PanelDeliverista");
                return;
            }

            // ✅ 7. Actualizar estado del delivery
            boolean actualizado = deliveryDAO.actualizarEstado(deliveryId, nuevoEstado, observaciones);

            if (actualizado) {
                LOGGER.info("✅ Delivery " + deliveryId + " actualizado a: " + nuevoEstado);

                // Si el estado es "PEDIDO_ENTREGADO", actualizar el pedido principal
                if ("PEDIDO_ENTREGADO".equals(nuevoEstado)) {
                    boolean pedidoEntregado = pedidoDAO.marcarComoEntregado(delivery.getPedidoId());
                    if (pedidoEntregado) {
                        LOGGER.info("📦 Pedido " + delivery.getPedidoId() + " marcado como entregado");
                    }
                }

                session.setAttribute("success", "Estado actualizado correctamente");
            } else {
                session.setAttribute("error", "Error al actualizar el estado");
            }

        } catch (Exception e) {
            LOGGER.severe("❌ Error: " + e.getMessage());
            e.printStackTrace();
            session.setAttribute("error", "Error al procesar la solicitud");
        }

        response.sendRedirect("PanelDeliverista");
    }
}