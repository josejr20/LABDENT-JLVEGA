package com.labdent.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

//@WebFilter("/*")
public class AuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        String uri = httpRequest.getRequestURI();
        HttpSession session = httpRequest.getSession(false);
        
        // Rutas públicas que no requieren autenticación
        boolean isPublic = uri.endsWith("login") || uri.endsWith("login.jsp") || 
                          uri.endsWith("index.jsp") || uri.contains("/css/") || 
                          uri.contains("/js/") || uri.contains("/images/") ||
                          uri.equals(httpRequest.getContextPath() + "/");

        // Si es una ruta pública o hay sesión activa, continuar
        if (isPublic || (session != null && session.getAttribute("usuario") != null)) {
            chain.doFilter(request, response);
        } else {
            // Redirigir al login
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login");
        }
    }

    @Override
    public void destroy() {
        // Limpieza si es necesaria
    }
}