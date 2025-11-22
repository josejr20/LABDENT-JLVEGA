package com.labdent.servlet;

import com.labdent.dao.UsuarioDAO;
import com.labdent.model.Usuario;
import com.labdent.util.DatabaseConnection;
import com.labdent.util.JWTUtil;
import com.labdent.util.SessionDataUtil;
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
import java.util.logging.Logger;

@WebServlet("/RegistrarPedidoServlet")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 2,  // 2MB
    maxFileSize = 1024 * 1024 * 10,       // 10MB
    maxRequestSize = 1024 * 1024 * 50     // 50MB
)
public class RegistrarPedidoServlet extends HttpServlet {
    
    private static final Logger LOGGER = Logger.getLogger(RegistrarPedidoServlet.class.getName());
    private UsuarioDAO usuarioDAO;
    
    @Override
    public void init() {
        usuarioDAO = new UsuarioDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        // ✅ Validar sesión
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuario") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.print("{\"success\":false,\"message\":\"Sesión expirada. Por favor, inicie sesión nuevamente.\"}");
            return;
        }

        // ✅ Validar token JWT
        String token = (String) session.getAttribute("token");
        if (token == null || !JWTUtil.validarToken(token) || JWTUtil.isTokenExpirado(token)) {
            session.invalidate();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.print("{\"success\":false,\"message\":\"Token inválido o expirado.\"}");
            return;
        }

        // ✅ Obtener usuario de sesión
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        int usuarioId = usuario.getId();
        
        // ✅ Validar token en base de datos
        if (!usuarioDAO.validarTokenEnBD(usuarioId, token)) {
            session.invalidate();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.print("{\"success\":false,\"message\":\"Token no válido en el servidor.\"}");
            return;
        }
        
        LOGGER.info("Registro de pedido - Usuario ID: " + usuarioId + " | Email: " + usuario.getEmail());

        try {
            // ✅ Validar CSRF Token (opcional pero recomendado)
            String csrfToken = request.getParameter("csrf_token");
            if (csrfToken != null && !SessionDataUtil.validarCSRFToken(session, csrfToken)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                out.print("{\"success\":false,\"message\":\"Token CSRF inválido.\"}");
                return;
            }
            
            // ✅ Sanitizar y validar parámetros
            String codigoUnico = sanitizar(request.getParameter("codigo_unico"));
            String odontologoIdStr = request.getParameter("odontologo_id");
            String nombrePaciente = sanitizar(request.getParameter("nombre_paciente"));
            String piezasDentales = sanitizar(request.getParameter("piezas_dentales"));
            String tipoProtesis = sanitizar(request.getParameter("tipo_protesis"));
            String material = sanitizar(request.getParameter("material"));
            String colorShade = sanitizar(request.getParameter("color_shade"));
            String fechaCompromiso = request.getParameter("fecha_compromiso");
            String prioridad = sanitizar(request.getParameter("prioridad"));
            String observaciones = sanitizar(request.getParameter("observaciones"));
            String responsableActualStr = request.getParameter("responsable_actual");
            
            // ✅ Validar campos obligatorios
            if (codigoUnico == null || codigoUnico.isEmpty() ||
                odontologoIdStr == null || nombrePaciente == null || nombrePaciente.isEmpty() ||
                tipoProtesis == null || fechaCompromiso == null) {
                
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"success\":false,\"message\":\"Faltan campos obligatorios.\"}");
                return;
            }
            
            // ✅ Validar formato de números
            int odontologoId;
            int responsableActual;
            
            try {
                odontologoId = Integer.parseInt(odontologoIdStr);
                responsableActual = Integer.parseInt(responsableActualStr);
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"success\":false,\"message\":\"Formato de números inválido.\"}");
                return;
            }
            
            // ✅ Validar formato de fecha
            Date fechaCompromisoDate;
            try {
                fechaCompromisoDate = Date.valueOf(fechaCompromiso);
            } catch (IllegalArgumentException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"success\":false,\"message\":\"Formato de fecha inválido.\"}");
                return;
            }

            // ✅ Manejo seguro del archivo adjunto
            String fileName = null;
            Part filePart = request.getPart("archivo_adjunto");
            
            if (filePart != null && filePart.getSize() > 0) {
                // Validar tipo de archivo
                String contentType = filePart.getContentType();
                if (!esArchivoPermitido(contentType)) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print("{\"success\":false,\"message\":\"Tipo de archivo no permitido.\"}");
                    return;
                }
                
                // Validar tamaño máximo (10MB)
                if (filePart.getSize() > 10 * 1024 * 1024) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print("{\"success\":false,\"message\":\"El archivo excede el tamaño máximo permitido (10MB).\"}");
                    return;
                }
                
                fileName = sanitizarNombreArchivo(extractFileName(filePart));
                
                // Guardar archivo de forma segura
                String uploadPath = getServletContext().getRealPath("") + File.separator + "uploads";
                File uploadDir = new File(uploadPath);
                if (!uploadDir.exists()) {
                    uploadDir.mkdirs();
                }
                
                // Agregar timestamp al nombre para evitar colisiones
                String uniqueFileName = System.currentTimeMillis() + "_" + fileName;
                String filePath = uploadPath + File.separator + uniqueFileName;
                
                filePart.write(filePath);
                fileName = uniqueFileName;
                
                LOGGER.info("Archivo subido: " + uniqueFileName + " | Usuario: " + usuario.getEmail());
            }

            // ✅ Insertar en base de datos con PreparedStatement
            try (Connection conn = DatabaseConnection.getConnection()) {
                String sql = "INSERT INTO pedidos " +
                             "(codigo_unico, odontologo_id, usuario_id, nombre_paciente, piezas_dentales, tipo_protesis, " +
                             "material, color_shade, fecha_compromiso, prioridad, observaciones, archivo_adjunto, responsable_actual) " +
                             "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                
                try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    stmt.setString(1, codigoUnico);
                    stmt.setInt(2, odontologoId);
                    stmt.setInt(3, usuarioId);
                    stmt.setString(4, nombrePaciente);
                    stmt.setString(5, piezasDentales);
                    stmt.setString(6, tipoProtesis);
                    stmt.setString(7, material);
                    stmt.setString(8, colorShade);
                    stmt.setDate(9, fechaCompromisoDate);
                    stmt.setString(10, prioridad);
                    stmt.setString(11, observaciones);
                    stmt.setString(12, fileName);
                    stmt.setInt(13, responsableActual);

                    int filas = stmt.executeUpdate();
                    
                    if (filas > 0) {
                        ResultSet generatedKeys = stmt.getGeneratedKeys();
                        int pedidoId = 0;
                        if (generatedKeys.next()) {
                            pedidoId = generatedKeys.getInt(1);
                        }
                        
                        LOGGER.info("✅ Pedido registrado - ID: " + pedidoId + " | Usuario: " + usuario.getEmail());
                        
                        response.setStatus(HttpServletResponse.SC_OK);
                        out.print("{\"success\":true,\"message\":\"Pedido registrado exitosamente.\",\"pedidoId\":" + pedidoId + "}");
                    } else {
                        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        out.print("{\"success\":false,\"message\":\"Error al registrar el pedido.\"}");
                    }
                }
            }
            
        } catch (SQLException e) {
            LOGGER.severe("Error SQL: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"success\":false,\"message\":\"Error en la base de datos.\"}");
            
        } catch (Exception e) {
            LOGGER.severe("Error inesperado: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"success\":false,\"message\":\"Error inesperado en el servidor.\"}");
        }
    }
    
    /**
     * ✅ Sanitiza entradas para prevenir XSS y SQL Injection
     */
    private String sanitizar(String input) {
        if (input == null) return null;
        return input.trim()
                   .replaceAll("<", "&lt;")
                   .replaceAll(">", "&gt;")
                   .replaceAll("\"", "&quot;")
                   .replaceAll("'", "&#x27;")
                   .replaceAll("/", "&#x2F;");
    }
    
    /**
     * ✅ Sanitiza nombre de archivo
     */
    private String sanitizarNombreArchivo(String filename) {
        if (filename == null) return null;
        // Remover caracteres peligrosos
        return filename.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
    
    /**
     * ✅ Valida que el tipo de archivo sea permitido
     */
    private boolean esArchivoPermitido(String contentType) {
        if (contentType == null) return false;
        
        String[] tiposPermitidos = {
            "image/jpeg", "image/jpg", "image/png", "image/gif",
            "application/pdf",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
        };
        
        for (String tipo : tiposPermitidos) {
            if (contentType.equals(tipo)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Extrae el nombre del archivo
     */
    private String extractFileName(Part part) {
        String contentDisp = part.getHeader("content-disposition");
        for (String token : contentDisp.split(";")) {
            if (token.trim().startsWith("filename")) {
                return token.substring(token.indexOf("=") + 2, token.length() - 1);
            }
        }
        return "archivo_" + System.currentTimeMillis();
    }
}