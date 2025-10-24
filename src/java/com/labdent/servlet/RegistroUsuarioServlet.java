package com.labdent.servlet;

import com.labdent.model.Usuario;
import com.labdent.dao.UsuarioDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "RegistroUsuarioServlet", urlPatterns = {"/RegistroUsuarioServlet"})
public class RegistroUsuarioServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String nombre = request.getParameter("nombreCompleto");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String rol = request.getParameter("rol");
        String telefono = request.getParameter("telefono");
        String direccion = request.getParameter("direccion");

        Usuario usuario = new Usuario();
        usuario.setNombreCompleto(nombre);
        usuario.setEmail(email);
        usuario.setPassword(password);
        usuario.setRol(rol);
        usuario.setTelefono(telefono);
        usuario.setDireccion(direccion);

        UsuarioDAO usuarioDAO = new UsuarioDAO();
        boolean registrado = usuarioDAO.registrar(usuario);

        if (registrado) {
            request.setAttribute("success", "Usuario registrado exitosamente");
        } else {
            request.setAttribute("error", "No se pudo registrar el usuario. Puede que el email ya esté registrado");
        }

        request.getRequestDispatcher("admin-usuarios.jsp").forward(request, response);
    }
}
