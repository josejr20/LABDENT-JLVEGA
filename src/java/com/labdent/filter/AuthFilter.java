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

import org.apache.commons.lang3.StringUtils;          // ✅ Para manejo seguro de Strings
import org.apache.commons.collections4.CollectionUtils; // ✅ Si usas listas de rutas públicas

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

//@WebFilter("/cambiar el*") // ✅ Aplica el filtro a todas las rutas
public class AuthFilter implements Filter {

    // ✅ Lista de rutas públicas, más ordenado y fácil de mantener
    private static final List<String> RUTAS_PUBLICAS = Arrays.asList(
        "login", "login.jsp", "index.jsp",
        "registroCliente.jsp", "registrarUsuario.jsp",
        "consulta-publica", "/css/", "/js/", "/images/"
    );

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Inicialización opcional
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);

        // ✅ Evita null con Apache Commons
        String uri = StringUtils.defaultString(httpRequest.getRequestURI());

        // ✅ Usa CollectionUtils para comprobar coincidencias de forma limpia
        boolean isPublic = CollectionUtils.exists(RUTAS_PUBLICAS, ruta -> uri.contains(ruta))
                || uri.equals(httpRequest.getContextPath() + "/");

        // ✅ Verificación más legible con Apache Commons
        boolean sesionActiva = (session != null && session.getAttribute("usuario") != null);

        if (isPublic || sesionActiva) {
            chain.doFilter(request, response);
        } else {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login");
        }
    }

    @Override
    public void destroy() {
        // Limpieza si es necesaria
    }
}
