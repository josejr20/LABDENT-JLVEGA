package com.labdent.servlet;

import com.labdent.dao.PedidoDAO;
import com.labdent.dao.TransicionDAO;
import com.labdent.model.Pedido;
import com.labdent.model.TransicionEstado;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/detalle-pedido")
public class GestionPedidoServlet extends HttpServlet {

    private PedidoDAO pedidoDAO;
    private TransicionDAO transicionDAO;

    @Override
    public void init() {
        pedidoDAO = new PedidoDAO();
        transicionDAO = new TransicionDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuario") == null) {
            response.sendRedirect("login");
            return;
        }

        String idParam = request.getParameter("id");
        if (idParam != null) {
            try {
                int pedidoId = Integer.parseInt(idParam);
                Pedido pedido = pedidoDAO.obtenerPorId(pedidoId);
                
                if (pedido != null) {
                    request.setAttribute("pedido", pedido);
                    
                    // Obtener historial de transiciones
                    List<TransicionEstado> transiciones = transicionDAO.listarPorPedido(pedidoId);
                    request.setAttribute("transiciones", transiciones);
                    
                    request.getRequestDispatcher("/detalle-pedido.jsp").forward(request, response);
                    return;
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        response.sendRedirect("kanban");
    }
}