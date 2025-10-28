package com.labdent.servlet;

import com.labdent.dao.PedidoDeliveryDAO;
import com.labdent.model.PedidoDelivery;
import com.labdent.model.Usuario;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/TomarPedido")
public class TomarPedidoServlet extends HttpServlet {

    private final PedidoDeliveryDAO deliveryDAO = new PedidoDeliveryDAO();

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

        String pedidoIdParam = request.getParameter("pedidoId");

        System.out.println("📦 [TomarPedido] Pedido ID: " + pedidoIdParam);

        try {
            int pedidoId = Integer.parseInt(pedidoIdParam);

            // Verificar si ya existe un delivery para este pedido
            PedidoDelivery existente = deliveryDAO.obtenerPorPedidoId(pedidoId);

            if (existente != null) {
                request.setAttribute("error", "Este pedido ya ha sido asignado");
                response.sendRedirect("PanelDeliverista");
                return;
            }

            // Crear nuevo registro de delivery
            PedidoDelivery delivery = new PedidoDelivery();
            delivery.setPedidoId(pedidoId);
            delivery.setDeliveristaId(usuario.getId());
            delivery.setEstadoDelivery("SALIO_EMPRESA");
            delivery.setObservaciones("Pedido tomado por el deliverista");

            boolean guardado = deliveryDAO.registrar(delivery);

            if (guardado) {
                System.out.println("✅ [TomarPedido] Pedido asignado correctamente");
                request.setAttribute("success", "Pedido asignado correctamente");
            } else {
                request.setAttribute("error", "Error al asignar el pedido");
            }

        } catch (Exception e) {
            System.err.println("❌ Error en TomarPedido: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Error al procesar la solicitud");
        }

        response.sendRedirect("PanelDeliverista");
    }
}