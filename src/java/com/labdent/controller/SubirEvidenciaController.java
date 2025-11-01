package com.labdent.controller;

import com.labdent.dao.EvidenciaDAO;
import com.labdent.exception.FileUploadException;
import com.labdent.model.Usuario;
import com.labdent.service.EvidenciaService;
import com.labdent.util.ValidationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Controller para subir evidencias y archivos
 */
@WebServlet("/SubirEvidencia")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 2,  // 2MB
    maxFileSize = 1024 * 1024 * 10,       // 10MB
    maxRequestSize = 1024 * 1024 * 50     // 50MB
)
public class SubirEvidenciaController extends HttpServlet {
    
    private static final Logger logger = LoggerFactory.getLogger(SubirEvidenciaController.class);
    
    private EvidenciaService evidenciaService;
    private String uploadPath;
    
    @Override
    public void init() throws ServletException {
        super.init();
        
        // Obtener ruta de carga desde el contexto
        uploadPath = getServletContext().getRealPath("") + File.separator + "uploads";
        
        EvidenciaDAO evidenciaDAO = new EvidenciaDAO();
        this.evidenciaService = new EvidenciaService(evidenciaDAO, uploadPath);
        
        logger.info("SubirEvidenciaController inicializado. Upload path: {}", uploadPath);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        
        try {
            // Verificar si es multipart
            if (!ServletFileUpload.isMultipartContent(request)) {
                throw new IllegalArgumentException("El formulario debe ser multipart/form-data");
            }
            
            // Configurar FileUpload
            DiskFileItemFactory factory = new DiskFileItemFactory();
            ServletFileUpload upload = new ServletFileUpload(factory);
            
            // Parsear request
            List<FileItem> formItems = upload.parseRequest(request);
            
            int pedidoId = 0;
            String tipoEvidencia = null;
            String descripcion = null;
            File archivoSubido = null;
            
            // Procesar items del formulario
            for (FileItem item : formItems) {
                if (item.isFormField()) {
                    String fieldName = item.getFieldName();
                    String fieldValue = item.getString("UTF-8");
                    
                    switch (fieldName) {
                        case "pedidoId":
                            pedidoId = Integer.parseInt(fieldValue);
                            break;
                        case "tipoEvidencia":
                            tipoEvidencia = fieldValue;
                            break;
                        case "descripcion":
                            descripcion = fieldValue;
                            break;
                    }
                } else {
                    // Es un archivo
                    String fileName = FilenameUtils.getName(item.getName());
                    
                    if (fileName != null && !fileName.isEmpty()) {
                        // Crear archivo temporal
                        File tempFile = File.createTempFile("upload_", "_" + fileName);
                        item.write(tempFile);
                        archivoSubido = tempFile;
                        
                        logger.debug("Archivo temporal creado: {}", tempFile.getAbsolutePath());
                    }
                }
            }
            
            // Validar datos
            if (!ValidationUtil.isValidId(pedidoId) || 
                !ValidationUtil.isNotEmpty(tipoEvidencia) || 
                archivoSubido == null) {
                throw new IllegalArgumentException("Datos del formulario incompletos");
            }
            
            logger.info("Subiendo evidencia para pedido {}, tipo: {}", pedidoId, tipoEvidencia);
            
            // Guardar evidencia usando el servicio
            boolean guardado = evidenciaService.guardarEvidencia(
                pedidoId, 
                tipoEvidencia, 
                archivoSubido, 
                descripcion, 
                usuario.getId()
            );
            
            // Limpiar archivo temporal
            if (archivoSubido != null && archivoSubido.exists()) {
                archivoSubido.delete();
            }
            
            if (guardado) {
                logger.info("Evidencia guardada correctamente para pedido {}", pedidoId);
                session.setAttribute("success", "Evidencia subida correctamente");
            } else {
                session.setAttribute("error", "Error al guardar la evidencia");
            }
            
        } catch (FileUploadException e) {
            logger.error("Error al subir archivo", e);
            session.setAttribute("error", e.getMessage());
            
        } catch (IllegalArgumentException e) {
            logger.error("Datos inválidos", e);
            session.setAttribute("error", e.getMessage());
            
        } catch (Exception e) {
            logger.error("Error inesperado al subir evidencia", e);
            session.setAttribute("error", "Error al subir la evidencia: " + e.getMessage());
        }
        
        // Redirigir de vuelta
        String referer = request.getHeader("Referer");
        response.sendRedirect(referer != null ? referer : "PanelTecnico");
    }
}