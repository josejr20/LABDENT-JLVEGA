package com.labdent.servlet;

import com.labdent.dao.PedidoDeliveryDAO;
import com.labdent.dao.UsuarioDAO;
import com.labdent.model.PedidoDelivery;
import com.labdent.model.Usuario;
import com.labdent.util.JWTUtil;
import com.labdent.util.SessionDataUtil;
import com.labdent.util.XSSProtectionUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.logging.Logger;

@WebServlet("/TomarPedido")
public class TomarPedidoServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(TomarPedidoServlet.class.getName());
    private final PedidoDeliveryDAO deliveryDAO = new PedidoDeliveryDAO();
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    
    // ✅ Lock para prevenir race condition
    private final Object pedidoLock = new Object();

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
            LOGGER.warning("⚠️ Token CSRF inválido - Usuario: " + usuario.getId());
            session.setAttribute("error", "Token CSRF inválido");
            response.sendRedirect("PanelDeliverista");
            return;
        }

        // ✅ 5. Validar y sanitizar pedidoId
        String pedidoIdParam = request.getParameter("pedidoId");
        if (!XSSProtectionUtil.isPositiveInteger(pedidoIdParam)) {
            session.setAttribute("error", "ID de pedido inválido");
            response.sendRedirect("PanelDeliverista");
            return;
        }

        int pedidoId = Integer.parseInt(pedidoIdParam);
        
        LOGGER.info("📦 Deliverista " + usuario.getId() + " intenta tomar pedido " + pedidoId);

        try {
            // ✅ 6. CRITICAL: Lock para prevenir race condition
            synchronized (pedidoLock) {
                // Verificar si ya existe un delivery para este pedido
                PedidoDelivery existente = deliveryDAO.obtenerPorPedidoId(pedidoId);

                if (existente != null) {
                    LOGGER.warning("⚠️ Pedido " + pedidoId + " ya asignado a deliverista " + 
                                 existente.getDeliveristaId());
                    session.setAttribute("error", "Este pedido ya ha sido asignado a otro deliverista");
                    response.sendRedirect("PanelDeliverista");
                    return;
                }

                // Crear nuevo registro de delivery
                PedidoDelivery delivery = new PedidoDelivery();
                delivery.setPedidoId(pedidoId);
                delivery.setDeliveristaId(usuario.getId());
                delivery.setEstadoDelivery("SALIO_EMPRESA");
                delivery.setObservaciones("Pedido tomado por " + usuario.getNombreCompleto());

                boolean guardado = deliveryDAO.registrar(delivery);

                if (guardado) {
                    LOGGER.info("✅ Pedido " + pedidoId + " asignado a deliverista " + usuario.getId());
                    session.setAttribute("success", "Pedido asignado correctamente a tu ruta");
                } else {
                    LOGGER.severe("❌ Error al asignar pedido " + pedidoId);
                    session.setAttribute("error", "Error al asignar el pedido. Intente nuevamente.");
                }
            }

        } catch (Exception e) {
            LOGGER.severe("❌ Error inesperado: " + e.getMessage());
            e.printStackTrace();
            session.setAttribute("error", "Error al procesar la solicitud");
        }

        response.sendRedirect("PanelDeliverista");
    }
}