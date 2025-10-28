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
import java.util.List;

@WebServlet("/PanelDeliverista")
public class PanelDeliveristaServlet extends HttpServlet {

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
        if (usuario == null || !"DELIVERISTA".equals(usuario.getRol())) {
            response.sendRedirect("login.jsp");
            return;
        }

        System.out.println("🚚 [PanelDeliverista] Usuario: " + usuario.getNombreCompleto());

        try {
            // Obtener pedidos listos para entrega
            List<Pedido> pedidosListos = pedidoDAO.listarPorEstado("LISTO_ENTREGA");

            // Obtener pedidos en delivery (mis pedidos asignados)
            List<PedidoDelivery> misDeliveries = deliveryDAO.listarPorDeliverista(usuario.getId());

            System.out.println("📦 [PanelDeliverista] Pedidos Listos: " + 
                             (pedidosListos != null ? pedidosListos.size() : 0));
            System.out.println("🚚 [PanelDeliverista] Mis Deliveries: " + 
                             (misDeliveries != null ? misDeliveries.size() : 0));

            request.setAttribute("pedidosListos", pedidosListos);
            request.setAttribute("misDeliveries", misDeliveries);

            request.getRequestDispatcher("panelDeliverista.jsp").forward(request, response);

        } catch (Exception e) {
            System.err.println("❌ Error en PanelDeliverista: " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect("login.jsp");
        }
    }
}