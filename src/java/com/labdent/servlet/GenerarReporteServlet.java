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

//@WebServlet({"/GenerarReporte", "/reportes"})
public class GenerarReporteServlet extends HttpServlet {
    
    private ReporteDAO reporteDAO;
    
    @Override
    public void init() throws ServletException {
        super.init();
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
        
        // Solo administradores pueden ver reportes
        if (!"ADMIN".equals(usuario.getRol())) {
            session.setAttribute("error", "No tienes permisos para ver reportes");
            response.sendRedirect("dashboard");
            return;
        }
        
        try {
            // Generar el reporte general
            Reporte reporte = reporteDAO.generarReporteGeneral();
            
            // Log para debugging
            System.out.println("📊 Reporte generado:");
            System.out.println("  Total: " + reporte.getTotalPedidos());
            System.out.println("  En Proceso: " + reporte.getPedidosEnProceso());
            System.out.println("  Entregados: " + reporte.getPedidosEntregados());
            System.out.println("  Atrasados: " + reporte.getPedidosAtrasados());
            
            // Pasar el reporte al JSP
            request.setAttribute("reporte", reporte);
            
            // Forward al JSP
            request.getRequestDispatcher("/WEB-INF/views/reportes.jsp")
                   .forward(request, response);
            
        } catch (Exception e) {
            System.err.println("❌ Error al generar reporte: " + e.getMessage());
            e.printStackTrace();
            session.setAttribute("error", "Error al generar reporte: " + e.getMessage());
            response.sendRedirect("dashboard");
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doGet(request, response);
    }
}