package com.labdent.servlet;

import com.labdent.dao.PedidoDAO;
import com.labdent.dao.TransicionDAO;
import com.labdent.dao.UsuarioDAO;
import com.labdent.model.Pedido;
import com.labdent.model.TransicionEstado;
import com.labdent.model.Usuario;
import com.labdent.util.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.logging.Logger;

@WebServlet("/registro-pedido")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 2,  // 2MB
    maxFileSize = 1024 * 1024 * 10,       // 10MB
    maxRequestSize = 1024 * 1024 * 50     // 50MB
)
public class RegistroPedidoServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(RegistroPedidoServlet.class.getName());
    private PedidoDAO pedidoDAO;
    private TransicionDAO transicionDAO;
    private UsuarioDAO usuarioDAO;

    @Override
    public void init() {
        pedidoDAO = new PedidoDAO();
        transicionDAO = new TransicionDAO();
        usuarioDAO = new UsuarioDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuario") == null) {
            response.sendRedirect("login?error=session_expired");
            return;
        }

        // ✅ Validar token JWT
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        String token = (String) session.getAttribute("token");
        
        if (token == null || !JWTUtil.validarToken(token) || JWTUtil.isTokenExpirado(token)) {
            session.invalidate();
            response.sendRedirect("login?error=token_expired");
            return;
        }

        // ✅ Generar CSRF token para el formulario
        String csrfToken = SessionDataUtil.generarCSRFToken(session);
        request.setAttribute("csrfToken", csrfToken);

        request.getRequestDispatcher("/registro-pedido.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // ✅ 1. Validar sesión
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuario") == null) {
            response.sendRedirect("login?error=session_expired");
            return;
        }

        Usuario usuario = (Usuario) session.getAttribute("usuario");

        // ✅ 2. Validar token JWT
        String token = (String) session.getAttribute("token");
        if (token == null || !JWTUtil.validarToken(token) || JWTUtil.isTokenExpirado(token)) {
            session.invalidate();
            response.sendRedirect("login?error=token_expired");
            return;
        }

        // ✅ 3. Validar token en BD
        if (!usuarioDAO.validarTokenEnBD(usuario.getId(), token)) {
            session.invalidate();
            response.sendRedirect("login?error=invalid_token");
            return;
        }

        // ✅ 4. Validar CSRF token
        String csrfToken = request.getParameter("csrf_token");
        if (!SessionDataUtil.validarCSRFToken(session, csrfToken)) {
            LOGGER.warning("⚠️ CSRF token inválido - Usuario: " + usuario.getId());
            request.setAttribute("error", "Token de seguridad inválido. Recargue la página.");
            request.getRequestDispatcher("/registro-pedido.jsp").forward(request, response);
            return;
        }

        try {
            // ✅ 5. Validar y sanitizar parámetros
            String nombrePaciente = InputValidationUtil.limpiarTexto(
                request.getParameter("nombrePaciente"), 100
            );
            String piezasDentales = InputValidationUtil.limpiarTexto(
                request.getParameter("piezasDentales"), 50
            );
            String tipoProtesis = InputValidationUtil.limpiarTexto(
                request.getParameter("tipoProtesis"), 50
            );
            String material = InputValidationUtil.limpiarTexto(
                request.getParameter("material"), 50
            );
            String colorShade = InputValidationUtil.limpiarTexto(
                request.getParameter("colorShade"), 20
            );
            String fechaCompromisoStr = request.getParameter("fechaCompromiso");
            String prioridad = request.getParameter("prioridad");
            String observaciones = InputValidationUtil.limpiarTexto(
                request.getParameter("observaciones"), 500
            );

            // ✅ Validar campos obligatorios
            if (!InputValidationUtil.noEstaVacio(nombrePaciente) ||
                !InputValidationUtil.noEstaVacio(tipoProtesis) ||
                !InputValidationUtil.esFechaValida(fechaCompromisoStr)) {
                
                request.setAttribute("error", "Todos los campos obligatorios deben estar completos");
                request.getRequestDispatcher("/registro-pedido.jsp").forward(request, response);
                return;
            }

            // ✅ Validar prioridad
            if (!InputValidationUtil.esPrioridadValida(prioridad)) {
                prioridad = "NORMAL"; // Default seguro
            }

            // ✅ Crear objeto Pedido
            Pedido pedido = new Pedido();
            pedido.setUsuarioId(usuario.getId());

            // ✅ Asignar odontólogo aleatorio
            int odontologoId = obtenerOdontologoAleatorio();
            if (odontologoId == -1) {
                request.setAttribute("error", "No hay odontólogos disponibles. Contacte al administrador.");
                request.getRequestDispatcher("/registro-pedido.jsp").forward(request, response);
                return;
            }
            
            pedido.setOdontologoId(odontologoId);
            pedido.setNombrePaciente(nombrePaciente);
            pedido.setPiezasDentales(piezasDentales);
            pedido.setTipoProtesis(tipoProtesis);
            pedido.setMaterial(material);
            pedido.setColorShade(colorShade);
            pedido.setFechaCompromiso(Date.valueOf(fechaCompromisoStr));
            pedido.setPrioridad(prioridad);
            pedido.setObservaciones(observaciones);

            // ✅ 6. Manejo seguro de archivo adjunto
            Part filePart = request.getPart("archivoAdjunto");
            if (filePart != null && filePart.getSize() > 0) {
                File tempFile = File.createTempFile("upload_", "_temp");
                filePart.write(tempFile.getAbsolutePath());

                // Validar archivo
                String originalFilename = extractFileName(filePart);
                FileUploadSecurityUtil.ValidationResult validacion = 
                    FileUploadSecurityUtil.validarArchivo(tempFile, originalFilename);

                if (!validacion.isValido()) {
                    tempFile.delete();
                    LOGGER.warning("⚠️ Archivo rechazado: " + validacion.getMensaje());
                    request.setAttribute("error", validacion.getMensaje());
                    request.getRequestDispatcher("/registro-pedido.jsp").forward(request, response);
                    return;
                }

                // Guardar archivo con nombre seguro
                String safeFileName = FileUploadSecurityUtil.generarNombreUnico(originalFilename);
                String uploadPath = getServletContext().getRealPath("") + File.separator + "uploads";
                File uploadDir = new File(uploadPath);
                if (!uploadDir.exists()) uploadDir.mkdirs();

                String filePath = uploadPath + File.separator + safeFileName;
                tempFile.renameTo(new File(filePath));
                
                pedido.setArchivoAdjunto(safeFileName);
                LOGGER.info("✅ Archivo subido: " + safeFileName);
            }

            // ✅ 7. Registrar pedido
            if (pedidoDAO.registrar(pedido)) {
                // Registrar transición inicial
                TransicionEstado transicion = new TransicionEstado();
                transicion.setPedidoId(pedido.getId());
                transicion.setEstadoAnterior(null);
                transicion.setEstadoNuevo("RECEPCION");
                transicion.setUsuarioId(usuario.getId());
                transicion.setObservaciones("Pedido registrado por " + usuario.getNombreCompleto());
                transicion.setChecklistCompletado(true);
                transicionDAO.registrar(transicion);

                LOGGER.info("✅ Pedido registrado - ID: " + pedido.getId() + 
                           " | Código: " + pedido.getCodigoUnico());

                request.setAttribute("success", "Pedido registrado exitosamente");
                request.setAttribute("codigoPedido", pedido.getCodigoUnico());
                request.setAttribute("mensaje", "Su código de seguimiento es: " + 
                                               pedido.getCodigoUnico() + 
                                               ". Guárdelo para consultar el estado de su pedido.");
            } else {
                request.setAttribute("error", "Error al registrar el pedido. Intente nuevamente.");
            }

            request.getRequestDispatcher("/registro-pedido.jsp").forward(request, response);

        } catch (Exception e) {
            LOGGER.severe("❌ Error: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Error al procesar el pedido: " + e.getMessage());
            request.getRequestDispatcher("/registro-pedido.jsp").forward(request, response);
        }
    }

    private int obtenerOdontologoAleatorio() {
        String sql = "SELECT id FROM usuarios WHERE rol = 'ODONTOLOGO' AND activo = 1 ORDER BY RAND() LIMIT 1";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("id");
            }
            return -1;

        } catch (SQLException e) {
            LOGGER.severe("❌ Error obteniendo odontólogo: " + e.getMessage());
            return -1;
        }
    }

    private String extractFileName(Part part) {
        String contentDisp = part.getHeader("content-disposition");
        if (contentDisp == null) return "archivo_" + System.currentTimeMillis();
        
        for (String token : contentDisp.split(";")) {
            if (token.trim().startsWith("filename")) {
                String filename = token.substring(token.indexOf("=") + 2, token.length() - 1);
                return FileUploadSecurityUtil.sanitizarNombreArchivo(filename);
            }
        }
        return "archivo_" + System.currentTimeMillis();
    }
}