package com.labdent.util;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Utilidad para manejo de archivos
 */
public class FileUploadUtil {
    
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    
    private FileUploadUtil() {
        // Utility class, no debe ser instanciada
    }
    
    /**
     * Genera un nombre de archivo único
     * @param prefix Prefijo para el nombre
     * @param extension Extensión del archivo
     * @return Nombre único del archivo
     */
    public static String generateUniqueFileName(String prefix, String extension) {
        Preconditions.checkArgument(!StringUtils.isEmpty(extension), "La extensión no puede estar vacía");
        
        String timestamp = LocalDateTime.now().format(FORMATTER);
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        
        String cleanPrefix = StringUtils.isNotEmpty(prefix) 
            ? StringUtils.replaceChars(prefix, " ", "_") + "_" 
            : "";
        
        return String.format("%s%s_%s.%s", cleanPrefix, timestamp, uniqueId, extension);
    }
    
    /**
     * Valida si una extensión es una imagen
     */
    public static boolean isImageFile(String extension) {
        return StringUtils.equalsAnyIgnoreCase(extension, "jpg", "jpeg", "png", "gif");
    }
    
    /**
     * Valida si una extensión es un documento
     */
    public static boolean isDocumentFile(String extension) {
        return StringUtils.equalsAnyIgnoreCase(extension, "pdf", "doc", "docx");
    }
    
    /**
     * Valida si una extensión es un archivo CAD
     */
    public static boolean isCadFile(String extension) {
        return StringUtils.equalsAnyIgnoreCase(extension, "stl", "obj", "3ds");
    }
}