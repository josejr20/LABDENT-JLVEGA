package com.labdent.servlet;

import com.labdent.dao.UsuarioDAO;
import com.labdent.model.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "RegistroClienteServlet", urlPatterns = {"/registroCliente"})
public class RegistroUsuarioServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String nombre = request.getParameter("nombreCompleto");
        String telefono = request.getParameter("telefono");
        String direccion = request.getParameter("direccion");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        if (nombre == null || telefono == null || email == null || password == null
                || nombre.isBlank() || telefono.isBlank() || email.isBlank() || password.isBlank()) {

            request.setAttribute("error", "Todos los campos son obligatorios.");
            request.getRequestDispatcher("registroCliente.jsp").forward(request, response);
            return;
        }

        if (!telefono.matches("\\d{9}")) {
            request.setAttribute("error", "El teléfono debe tener 9 dígitos.");
            request.getRequestDispatcher("registroCliente.jsp").forward(request, response);
            return;
        }

        UsuarioDAO usuarioDAO = new UsuarioDAO();

        // ✅ Verificar existencia de correo
        if (usuarioDAO.autenticar(email, password) != null) {
            request.setAttribute("error", "El correo ya se encuentra registrado.");
            request.getRequestDispatcher("registroCliente.jsp").forward(request, response);
            return;
        }

        Usuario usuario = new Usuario();
        usuario.setNombreCompleto(nombre.trim());
        usuario.setTelefono(telefono.trim());
        usuario.setDireccion(direccion.trim());
        usuario.setEmail(email.trim());
        usuario.setPassword(password.trim());
        usuario.setRol("CLIENTE");
        usuario.setActivo(true);

        boolean registrado = usuarioDAO.registrar(usuario);

        if (registrado) {
            request.setAttribute("success", "Cuenta creada exitosamente");
        } else {
            request.setAttribute("error", "No se pudo completar el registro. Intente nuevamente.");
        }

        request.getRequestDispatcher("registroCliente.jsp").forward(request, response);
    }
}
