package com.labdent.servlet;

import com.labdent.dao.PedidoDAO;
import com.labdent.model.Pedido;
import com.labdent.model.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/misPedidos")
public class MisPedidosServlet extends HttpServlet {

    private PedidoDAO pedidoDAO;

    @Override
    public void init() {
        pedidoDAO = new PedidoDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuario") == null) {
            response.sendRedirect("login");
            return;
        }

        Usuario usuario = (Usuario) session.getAttribute("usuario");

        // Obtener pedidos del odontólogo
        List<Pedido> pedidos = pedidoDAO.listarPorOdontologo(usuario.getId());
        request.setAttribute("pedidos", pedidos);

        request.getRequestDispatcher("/misPedidos.jsp").forward(request, response);
    }
}