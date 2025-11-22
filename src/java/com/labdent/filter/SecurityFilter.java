package com.labdent.filter;

import com.labdent.dao.UsuarioDAO;
import com.labdent.model.Usuario;
import com.labdent.util.JWTUtil;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Filtro de seguridad que valida tokens JWT en cada petición
 */
@WebFilter(filterName = "SecurityFilter", urlPatterns = {
    "/dashboard", "/kanban", "/reportes", "/misPedidos", 
    "/admin-usuarios.jsp", "/panel*", "/pedidos/*"
})
public class SecurityFilter implements Filter {
    
    private UsuarioDAO usuarioDAO;
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        usuarioDAO = new UsuarioDAO();
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);
        
        // Verificar si hay sesión activa
        if (session == null || session.getAttribute("usuario") == null) {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login?error=session_expired");
            return;
        }
        
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        String tokenSesion = (String) session.getAttribute("token");
        
        // Verificar que exista el token en la sesión
        if (tokenSesion == null || tokenSesion.isEmpty()) {
            invalidarSesion(httpRequest, httpResponse);
            return;
        }
        
        // Validar el token JWT
        if (!JWTUtil.validarToken(tokenSesion)) {
            invalidarSesion(httpRequest, httpResponse);
            return;
        }
        
        // Verificar si el token expiró
        if (JWTUtil.isTokenExpirado(tokenSesion)) {
            invalidarSesion(httpRequest, httpResponse);
            return;
        }
        
        // Verificar que el token en sesión coincida con el de la BD
        Usuario usuarioDB = usuarioDAO.obtenerPorId(usuario.getId());
        if (usuarioDB == null || !tokenSesion.equals(usuarioDB.getTokenJwt())) {
            invalidarSesion(httpRequest, httpResponse);
            return;
        }
        
        // Advertir si el token está por expirar (menos de 30 minutos)
        long minutosRestantes = JWTUtil.obtenerTiempoRestante(tokenSesion);
        if (minutosRestantes > 0 && minutosRestantes < 30) {
            httpRequest.setAttribute("tokenWarning", 
                "Su sesión expirará en " + minutosRestantes + " minutos");
        }
        
        // Todo OK, continuar con la petición
        chain.doFilter(request, response);
    }
    
    private void invalidarSesion(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            Usuario usuario = (Usuario) session.getAttribute("usuario");
            if (usuario != null) {
                // Eliminar token de la BD
                usuarioDAO.eliminarToken(usuario.getId());
            }
            session.invalidate();
        }
        response.sendRedirect(request.getContextPath() + "/login?error=session_expired");
    }
    
    @Override
    public void destroy() {
        usuarioDAO = null;
    }
}