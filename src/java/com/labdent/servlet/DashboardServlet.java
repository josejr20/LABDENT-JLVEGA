package com.labdent.servlet;

import com.labdent.dao.PedidoDAO;
import com.labdent.dao.ReporteDAO;
import com.labdent.model.Pedido;
import com.labdent.model.Reporte;
import com.labdent.model.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {

    private PedidoDAO pedidoDAO;
    private ReporteDAO reporteDAO;

    @Override
    public void init() {
        pedidoDAO = new PedidoDAO();
        reporteDAO = new ReporteDAO();
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

        // Solo admin puede acceder al dashboard completo
        if (!"ADMIN".equals(usuario.getRol())) {
            response.sendRedirect("index.jsp");
            return;
        }

        // Generar reporte general
        Reporte reporte = reporteDAO.generarReporteGeneral();
        request.setAttribute("reporte", reporte);

        // Listar pedidos atrasados
        List<Pedido> pedidosAtrasados = pedidoDAO.listarAtrasados();
        request.setAttribute("pedidosAtrasados", pedidosAtrasados);

        // Listar últimos pedidos
        List<Pedido> ultimosPedidos = pedidoDAO.listarTodos();
        if (ultimosPedidos.size() > 10) {
            ultimosPedidos = ultimosPedidos.subList(0, 10);
        }
        request.setAttribute("ultimosPedidos", ultimosPedidos);

        request.getRequestDispatcher("/dashboard.jsp").forward(request, response);
    }
}