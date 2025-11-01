package com.labdent.service;

import com.google.common.base.Preconditions;
import com.google.common.io.Files;
import com.labdent.dao.EvidenciaDAO;
import com.labdent.exception.FileUploadException;
import com.labdent.model.Evidencia;
import com.labdent.util.FileUploadUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * Servicio para gestionar evidencias y archivos
 */
public class EvidenciaService {
    
    private static final Logger logger = LoggerFactory.getLogger(EvidenciaService.class);
    
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10 MB
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
        "jpg", "jpeg", "png", "gif", "pdf", "doc", "docx", "stl"
    );
    
    private final EvidenciaDAO evidenciaDAO;
    private final String uploadDirectory;
    
    public EvidenciaService(EvidenciaDAO evidenciaDAO, String uploadDirectory) {
        this.evidenciaDAO = Preconditions.checkNotNull(evidenciaDAO);
        this.uploadDirectory = Preconditions.checkNotNull(uploadDirectory);
    }
    
    /**
     * Guarda una evidencia con validaciones
     */
    public boolean guardarEvidencia(int pedidoId, String tipoEvidencia, 
                                    File archivo, String descripcion, 
                                    int usuarioId) throws FileUploadException {
        
        Preconditions.checkArgument(pedidoId > 0, "ID de pedido inválido");
        Preconditions.checkArgument(!StringUtils.isEmpty(tipoEvidencia), "Tipo de evidencia requerido");
        Preconditions.checkNotNull(archivo, "Archivo requerido");
        Preconditions.checkArgument(usuarioId > 0, "ID de usuario inválido");
        
        logger.info("Guardando evidencia para pedido {}", pedidoId);
        
        // Validar archivo
        validarArchivo(archivo);
        
        try {
            // Generar nombre único
            String extension = FilenameUtils.getExtension(archivo.getName());
            String nombreUnico = FileUploadUtil.generateUniqueFileName(
                "PED" + pedidoId, extension
            );
            
            // Crear directorio si no existe
            File uploadDir = new File(uploadDirectory);
            FileUtils.forceMkdir(uploadDir);
            
            // Copiar archivo
            File destino = new File(uploadDir, nombreUnico);
            FileUtils.copyFile(archivo, destino);
            
            // Registrar en BD
            Evidencia evidencia = new Evidencia();
            evidencia.setPedidoId(pedidoId);
            evidencia.setTipoEvidencia(tipoEvidencia);
            evidencia.setNombreArchivo(archivo.getName());
            evidencia.setRutaArchivo("uploads/" + nombreUnico);
            evidencia.setDescripcion(StringUtils.defaultString(descripcion));
            evidencia.setUsuarioId(usuarioId);
            
            boolean guardado = evidenciaDAO.guardar(evidencia);
            
            if (guardado) {
                logger.info("Evidencia guardada correctamente: {}", nombreUnico);
            }
            
            return guardado;
            
        } catch (IOException e) {
            logger.error("Error al guardar evidencia", e);
            throw new FileUploadException("Error al guardar el archivo", e);
        }
    }
    
    /**
     * Valida que el archivo cumpla con los requisitos
     */
    private void validarArchivo(File archivo) throws FileUploadException {
        if (!archivo.exists()) {
            throw new FileUploadException("El archivo no existe");
        }
        
        if (archivo.length() > MAX_FILE_SIZE) {
            throw new FileUploadException(
                String.format("El archivo excede el tamaño máximo permitido (%d MB)", 
                             MAX_FILE_SIZE / (1024 * 1024))
            );
        }
        
        String extension = FilenameUtils.getExtension(archivo.getName()).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new FileUploadException(
                "Formato de archivo no permitido. Permitidos: " + ALLOWED_EXTENSIONS
            );
        }
    }
    
    /**
     * Obtiene todas las evidencias de un pedido
     */
    public List<Evidencia> obtenerEvidenciasPorPedido(int pedidoId) {
        Preconditions.checkArgument(pedidoId > 0, "ID de pedido inválido");
        return evidenciaDAO.listarPorPedido(pedidoId);
    }
}