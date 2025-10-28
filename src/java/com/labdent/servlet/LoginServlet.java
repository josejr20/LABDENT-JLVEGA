package com.labdent.servlet;

import com.labdent.dao.UsuarioDAO;
import com.labdent.model.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private UsuarioDAO usuarioDAO;

    @Override
    public void init() {
        usuarioDAO = new UsuarioDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Si ya hay sesión activa, redirigir al dashboard correspondiente
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("usuario") != null) {
            Usuario usuario = (Usuario) session.getAttribute("usuario");
            System.out.println("ID del usuario autenticado: " + usuario.getId());
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

        // Validar campos vacíos
        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            request.setAttribute("error", "Por favor complete todos los campos");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }

        // Autenticar usuario
        Usuario usuario = usuarioDAO.autenticar(email, password);

        if (usuario != null) {
            // Crear sesión
            HttpSession session = request.getSession(true);
            session.setAttribute("usuario", usuario);
            session.setAttribute("usuarioId", usuario.getId());
            session.setAttribute("usuarioNombre", usuario.getNombreCompleto());
            session.setAttribute("usuarioRol", usuario.getRol());
            session.setMaxInactiveInterval(1800); // 30 minutos

            // Cookie para recordar (opcional)
            if (recordar) {
                Cookie cookieEmail = new Cookie("labdent_email", email);
                cookieEmail.setMaxAge(7 * 24 * 60 * 60); // 7 días
                response.addCookie(cookieEmail);
            }

            // Registrar login en log (opcional)
            System.out.println("Usuario autenticado: " + usuario.getEmail() + " - Rol: " + usuario.getRol());
            System.out.println("ID del usuario autenticado: " + usuario.getId());
            // Redirigir según el rol
            redirigirPorRol(response, usuario.getRol());
            
        } else {
            request.setAttribute("error", "Credenciales inválidas. Verifique su email y contraseña.");
            request.setAttribute("email", email);
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }

    private void redirigirPorRol(HttpServletResponse response, String rol) throws IOException {
        switch (rol) {
            case "ADMIN":
                response.sendRedirect("dashboard");
                break;
            case "ODONTOLOGO":
                response.sendRedirect("misPedidos");
                break;
            case "DELIVERISTA":
                response.sendRedirect("panelDeliverista.jsp");
                break;
            case "TECNICO":
            case "CERAMISTA":
                response.sendRedirect("kanban");
                break;
            case "CLIENTE":
                response.sendRedirect("PanelUsuario.jsp");
                break;
            default:
                response.sendRedirect("index.jsp");
        }
    }
}