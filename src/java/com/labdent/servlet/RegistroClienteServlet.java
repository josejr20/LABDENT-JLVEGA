package com.labdent.servlet;

import com.labdent.model.Usuario;
import com.labdent.dao.UsuarioDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/registro-cliente")
public class RegistroClienteServlet extends HttpServlet {

    private UsuarioDAO usuarioDAO = new UsuarioDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String nombre = request.getParameter("nombreCompleto");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        if (nombre == null || email == null || password == null
                || nombre.isEmpty() || email.isEmpty() || password.isEmpty()) {

            request.setAttribute("error", "Todos los campos con * son obligatorios.");
            request.getRequestDispatcher("registroCliente.jsp").forward(request, response);
            return;
        }

        Usuario cliente = new Usuario();
        cliente.setNombreCompleto(nombre);
        cliente.setEmail(email);
        cliente.setPassword(password);
        cliente.setRol("CLIENTE"); // Rol predeterminado
        cliente.setActivo(true);

        boolean registrado = usuarioDAO.registrar(cliente);

        if (registrado) {
            request.setAttribute("success", "Registro exitoso. Ya puedes iniciar sesión.");
        } else {
            request.setAttribute("error", "El correo ya está registrado o ocurrió un error.");
        }

        request.getRequestDispatcher("registroCliente.jsp").forward(request, response);
    }
}