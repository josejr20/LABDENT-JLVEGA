package com.labdent.servlet;

import com.labdent.model.Usuario;
import com.labdent.util.DatabaseConnection;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import java.io.*;
import java.sql.*;

@WebServlet("/RegistrarPedidoServlet")
@MultipartConfig(fileSizeThreshold=1024*1024*2, // 2MB
        maxFileSize=1024*1024*10,      // 10MB
        maxRequestSize=1024*1024*50)   // 50MB
public class RegistrarPedidoServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // --- Validar sesión ---
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuario") == null) {
            response.sendRedirect("login");
            return;
        }

        // Obtener el usuario de sesión
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        System.out.println("DEBUG Usuario ID en sesión: " + usuario.getId());
        int usuarioId = usuario.getId(); // ✅ Aquí capturamos el ID del usuario logueado

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        // Obtener parámetros del formulario
        String codigoUnico = request.getParameter("codigo_unico");
        int odontologoId = Integer.parseInt(request.getParameter("odontologo_id"));
        String nombrePaciente = request.getParameter("nombre_paciente");
        String piezasDentales = request.getParameter("piezas_dentales");
        String tipoProtesis = request.getParameter("tipo_protesis");
        String material = request.getParameter("material");
        String colorShade = request.getParameter("color_shade");
        String fechaCompromiso = request.getParameter("fecha_compromiso");
        String prioridad = request.getParameter("prioridad");
        String observaciones = request.getParameter("observaciones");
        int responsableActual = Integer.parseInt(request.getParameter("responsable_actual"));

        // Manejo del archivo adjunto
        Part filePart = request.getPart("archivo_adjunto");
        String fileName = null;
        if (filePart != null && filePart.getSize() > 0) {
            fileName = extractFileName(filePart);
            String uploadPath = getServletContext().getRealPath("") + File.separator + "uploads";
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) uploadDir.mkdir();
            filePart.write(uploadPath + File.separator + fileName);
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO pedidos " +
                         "(codigo_unico, odontologo_id, usuario_id, nombre_paciente, piezas_dentales, tipo_protesis, " +
                         "material, color_shade, fecha_compromiso, prioridad, observaciones, archivo_adjunto, responsable_actual) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, codigoUnico);
            stmt.setInt(2, odontologoId);
            stmt.setInt(3, usuarioId);
            stmt.setString(4, nombrePaciente);
            stmt.setString(5, piezasDentales);
            stmt.setString(6, tipoProtesis);
            stmt.setString(7, material);
            stmt.setString(8, colorShade);
            stmt.setDate(9, Date.valueOf(fechaCompromiso));
            stmt.setString(10, prioridad);
            stmt.setString(11, observaciones);
            stmt.setString(12, fileName);
            stmt.setInt(13, responsableActual);

            int filas = stmt.executeUpdate();
            System.out.println("Pedido registrado por usuario ID: " + usuarioId);

            PrintWriter out = response.getWriter();
            if (filas > 0) {
                out.println("<h3>¡Pedido registrado exitosamente!</h3>");
            } else {
                out.println("<h3>Error al registrar el pedido.</h3>");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException("Error al registrar pedido: " + e.getMessage());
        }
    }

    private String extractFileName(Part part) {
        String contentDisp = part.getHeader("content-disposition");
        for (String token : contentDisp.split(";")) {
            if (token.trim().startsWith("filename")) {
                return token.substring(token.indexOf("=") + 2, token.length() - 1);
            }
        }
        return null;
    }
}
