package com.labdent.servlet;

import com.labdent.dao.PedidoDeliveryDAO;
import com.labdent.dao.PedidoDAO;
import com.labdent.model.PedidoDelivery;
import com.labdent.model.Usuario;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.Timestamp;

@WebServlet("/ActualizarDelivery")
public class ActualizarDeliveryServlet extends HttpServlet {

    private final PedidoDeliveryDAO deliveryDAO = new PedidoDeliveryDAO();
    private final PedidoDAO pedidoDAO = new PedidoDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null || !"DELIVERISTA".equals(usuario.getRol())) {
            response.sendRedirect("login.jsp");
            return;
        }

        String deliveryIdParam = request.getParameter("deliveryId");
        String nuevoEstado = request.getParameter("estado");
        String observaciones = request.getParameter("observaciones");

        System.out.println("🔄 [ActualizarDelivery] ID: " + deliveryIdParam + " | Estado: " + nuevoEstado);

        try {
            int deliveryId = Integer.parseInt(deliveryIdParam);

            PedidoDelivery delivery = deliveryDAO.obtenerPorId(deliveryId);
            
            if (delivery == null) {
                request.setAttribute("error", "Delivery no encontrado");
                response.sendRedirect("PanelDeliverista");
                return;
            }

            // Actualizar estado del delivery
            boolean actualizado = deliveryDAO.actualizarEstado(deliveryId, nuevoEstado, observaciones);

            if (actualizado) {
                System.out.println("✅ [ActualizarDelivery] Estado actualizado correctamente");

                // Si el estado es "PEDIDO_ENTREGADO", actualizar el pedido principal
                if ("PEDIDO_ENTREGADO".equals(nuevoEstado)) {
                    boolean pedidoEntregado = pedidoDAO.marcarComoEntregado(delivery.getPedidoId());
                    System.out.println("📦 [ActualizarDelivery] Pedido marcado como entregado: " + pedidoEntregado);
                }

                request.setAttribute("success", "Estado actualizado correctamente");
            } else {
                request.setAttribute("error", "Error al actualizar el estado");
            }

        } catch (Exception e) {
            System.err.println("❌ Error en ActualizarDelivery: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Error al procesar la solicitud");
        }

        response.sendRedirect("PanelDeliverista");
    }
}