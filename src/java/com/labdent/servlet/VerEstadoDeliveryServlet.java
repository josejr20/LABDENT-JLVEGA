package com.labdent.servlet;

import com.labdent.dao.PedidoDAO;
import com.labdent.dao.PedidoDeliveryDAO;
import com.labdent.model.Pedido;
import com.labdent.model.PedidoDelivery;
import com.labdent.model.Usuario;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/VerEstadoDelivery")
public class VerEstadoDeliveryServlet extends HttpServlet {

    private final PedidoDAO pedidoDAO = new PedidoDAO();
    private final PedidoDeliveryDAO deliveryDAO = new PedidoDeliveryDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        Usuario usuario = (Usuario) session.getAttribute("usuario");
        Integer usuarioId = (Integer) session.getAttribute("usuarioId");

        if (usuario == null || usuarioId == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String pedidoIdParam = request.getParameter("id");
        if (pedidoIdParam == null || pedidoIdParam.trim().isEmpty()) {
            System.err.println("❌ [VerEstadoDelivery] ID del pedido no proporcionado");
            response.sendRedirect("KanbanUsuarioServlet");
            return;
        }

        try {
            int pedidoId = Integer.parseInt(pedidoIdParam);
            System.out.println("🔍 [VerEstadoDelivery] Buscando pedido ID: " + pedidoId);

            // Obtener el pedido
            Pedido pedido = pedidoDAO.obtenerPorId(pedidoId);

            if (pedido == null) {
                System.err.println("❌ [VerEstadoDelivery] Pedido no encontrado");
                response.sendRedirect("KanbanUsuarioServlet");
                return;
            }

            // Validar que el usuario sea el dueño del pedido
            if (usuario.isCliente() && pedido.getUsuarioId() != usuarioId) {
                System.err.println("⚠️ [VerEstadoDelivery] Usuario sin acceso al pedido");
                response.sendRedirect("KanbanUsuarioServlet");
                return;
            }

            // Obtener información de delivery si existe
            PedidoDelivery delivery = deliveryDAO.obtenerPorPedidoId(pedidoId);

            System.out.println("✅ [VerEstadoDelivery] Pedido: " + pedido.getCodigoUnico());
            System.out.println("📦 [VerEstadoDelivery] Delivery: " + (delivery != null ? "Sí" : "No"));

            // Enviar datos al JSP
            request.setAttribute("pedido", pedido);
            request.setAttribute("delivery", delivery);

            request.getRequestDispatcher("estadoDeliveryCliente.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            System.err.println("❌ [VerEstadoDelivery] ID inválido: " + pedidoIdParam);
            response.sendRedirect("KanbanUsuarioServlet");
        } catch (Exception e) {
            System.err.println("❌ [VerEstadoDelivery] Error: " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect("KanbanUsuarioServlet");
        }
    }
}