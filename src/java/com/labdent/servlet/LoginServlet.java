package com.labdent.servlet;

import com.labdent.dao.UsuarioDAO;
import com.labdent.model.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(LoginServlet.class);
    private UsuarioDAO usuarioDAO;

    @Override
    public void init() {
        usuarioDAO = new UsuarioDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("usuario") != null) {
            Usuario usuario = (Usuario) session.getAttribute("usuario");
            redirigirPorRol(response, usuario.getRol());
            return;
        }

        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");
        boolean recordar = "on".equals(request.getParameter("recordar"));

        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            request.setAttribute("error", "Por favor complete todos los campos");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }

        Usuario usuario = usuarioDAO.autenticar(email, password);

        if (usuario != null) {
            HttpSession session = request.getSession(true);
            session.setAttribute("usuario", usuario);
            session.setAttribute("usuarioId", usuario.getId());
            session.setAttribute("usuarioNombre", usuario.getNombreCompleto());
            session.setAttribute("usuarioRol", usuario.getRol());
            session.setMaxInactiveInterval(1800); // 30 minutos

            if (recordar) {
                Cookie cookieEmail = new Cookie("labdent_email", email);
                cookieEmail.setMaxAge(7 * 24 * 60 * 60); // 7 días
                response.addCookie(cookieEmail);
            }

            // ✅ Logging profesional
            logger.info("Usuario autenticado: {} - Rol: {}", usuario.getEmail(), usuario.getRol());

            redirigirPorRol(response, usuario.getRol());

        } else {
            logger.warn("Intento fallido de login con email: {}", email);
            request.setAttribute("error", "Credenciales inválidas. Verifique su email y contraseña.");
            request.setAttribute("email", email);
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }

    private void redirigirPorRol(HttpServletResponse response, String rol) throws IOException {
        switch (rol) {
            case "ADMIN" -> response.sendRedirect("dashboard");
            case "ODONTOLOGO" -> response.sendRedirect("mis-pedidos");
            case "TECNICO", "CERAMISTA", "DELIVERISTA" -> response.sendRedirect("kanban");
            case "CLIENTE" -> response.sendRedirect("panel-usuario.jsp");
            default -> response.sendRedirect("index.jsp");
        }
    }
}