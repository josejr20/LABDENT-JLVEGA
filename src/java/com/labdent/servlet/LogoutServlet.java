package com.labdent.servlet;

import com.labdent.dao.UsuarioDAO;
import com.labdent.model.Usuario;
import com.labdent.util.SessionDataUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.logging.Logger;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {
    
    private static final Logger LOGGER = Logger.getLogger(LogoutServlet.class.getName());
    private UsuarioDAO usuarioDAO;

    @Override
    public void init() {
        usuarioDAO = new UsuarioDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        procesarLogout(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        procesarLogout(request, response);
    }

    private void procesarLogout(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        
        HttpSession session = request.getSession(false);
        
        if (session != null) {
            Usuario usuario = (Usuario) session.getAttribute("usuario");
            
            if (usuario != null) {
                // ✅ Eliminar token JWT de la base de datos
                usuarioDAO.eliminarToken(usuario.getId());
                
                LOGGER.info("Logout - Usuario: " + usuario.getEmail() + " | ID: " + usuario.getId());
            }
            
            // ✅ Invalidar sesión
            session.invalidate();
        }
        
        // ✅ Eliminar todas las cookies de usuario
        SessionDataUtil.eliminarCookiesUsuario(request, response);
        
        // Redirigir al login con mensaje
        response.sendRedirect("login?msg=logout_success");
    }
}