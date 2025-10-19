package com.labdent.servlet;

import com.labdent.dao.ReporteDAO;
import com.labdent.model.Reporte;
import com.labdent.model.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/reportes")
public class ReporteServlet extends HttpServlet {

    private ReporteDAO reporteDAO;

    @Override
    public void init() {
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

        // Solo admin puede ver reportes
        if (!"ADMIN".equals(usuario.getRol())) {
            response.sendRedirect("index.jsp");
            return;
        }

        Reporte reporte = reporteDAO.generarReporteGeneral();
        request.setAttribute("reporte", reporte);

        request.getRequestDispatcher("/reportes.jsp").forward(request, response);
    }
}