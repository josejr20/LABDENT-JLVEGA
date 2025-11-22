package com.labdent.servlet;

import com.labdent.util.SessionDataUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Servlet para verificar si la sesión sigue válida (AJAX)
 */
@WebServlet("/api/verificar-sesion")
public class VerificarSesionServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        boolean sesionValida = SessionDataUtil.tieneSesionValida(request);
        
        if (sesionValida) {
            response.setStatus(HttpServletResponse.SC_OK);
            out.print("{\"valid\":true}");
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.print("{\"valid\":false,\"message\":\"Sesión expirada\"}");
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }
}