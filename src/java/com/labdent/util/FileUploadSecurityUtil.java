package com.labdent.util;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import org.apache.tika.Tika;

/**
 * Utilidad de seguridad para validación de archivos subidos
 */
public class FileUploadSecurityUtil {
    
    // Extensiones permitidas
    private static final List<String> EXTENSIONES_PERMITIDAS = Arrays.asList(
        "jpg", "jpeg", "png", "gif", "pdf", "doc", "docx", "stl", "obj"
    );
    
    // MIME types permitidos
    private static final List<String> MIME_TYPES_PERMITIDOS = Arrays.asList(
        "image/jpeg", "image/png", "image/gif",
        "application/pdf",
        "application/msword",
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
        "model/stl", "model/obj"
    );
    
    // Tamaño máximo: 10MB
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;
    
    private static final Tika tika = new Tika();
    
    /**
     * Valida la extensión del archivo
     */
    public static boolean validarExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return false;
        }
        
        String extension = obtenerExtension(filename).toLowerCase();
        return EXTENSIONES_PERMITIDAS.contains(extension);
    }
    
    /**
     * Valida el MIME type del archivo (validación real, no basada en extensión)
     */
    public static boolean validarMimeType(File file) {
        try {
            String mimeType = tika.detect(file);
            return MIME_TYPES_PERMITIDOS.stream()
                    .anyMatch(mime -> mimeType.startsWith(mime));
        } catch (IOException e) {
            return false;
        }
    }
    
    /**
     * Valida el tamaño del archivo
     */
    public static boolean validarTamano(File file) {
        return file.length() <= MAX_FILE_SIZE;
    }
    
    /**
     * Sanitiza el nombre del archivo
     * Remueve caracteres peligrosos y path traversal
     */
    public static String sanitizarNombreArchivo(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "archivo_" + System.currentTimeMillis();
        }
        
        // Remover path traversal
        filename = filename.replaceAll("\\.\\.", "");
        filename = filename.replaceAll("[/\\\\]", "");
        
        // Remover caracteres especiales
        filename = filename.replaceAll("[^a-zA-Z0-9._-]", "_");
        
        // Limitar longitud
        if (filename.length() > 100) {
            String extension = obtenerExtension(filename);
            filename = filename.substring(0, 95) + "." + extension;
        }
        
        return filename;
    }
    
    /**
     * Genera un nombre único para el archivo
     */
    public static String generarNombreUnico(String originalFilename) {
        String extension = obtenerExtension(originalFilename);
        String timestamp = String.valueOf(System.currentTimeMillis());
        String random = String.valueOf((int)(Math.random() * 10000));
        
        return timestamp + "_" + random + "." + extension;
    }
    
    /**
     * Valida que no sea un archivo ejecutable o script
     */
    public static boolean validarNoEjecutable(String filename) {
        String extension = obtenerExtension(filename).toLowerCase();
        
        List<String> extensionesPeligrosas = Arrays.asList(
            "exe", "bat", "sh", "cmd", "com", "pif", "scr",
            "js", "jar", "php", "jsp", "asp", "aspx",
            "py", "rb", "pl", "cgi"
        );
        
        return !extensionesPeligrosas.contains(extension);
    }
    
    /**
     * Escanea el contenido del archivo en busca de patrones maliciosos
     */
    public static boolean escanearContenidoMalicioso(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            
            // Patrones sospechosos
            List<String> patrones = Arrays.asList(
                "<?php", "<% ", "<%=", "eval(", "exec(", 
                "system(", "shell_exec", "passthru", "base64_decode"
            );
            
            while ((line = reader.readLine()) != null) {
                String lineLower = line.toLowerCase();
                for (String patron : patrones) {
                    if (lineLower.contains(patron.toLowerCase())) {
                        return false; // Contenido malicioso detectado
                    }
                }
            }
            
            return true; // Sin contenido malicioso
            
        } catch (IOException e) {
            return false; // Error al escanear
        }
    }
    
    /**
     * Validación completa del archivo
     */
    public static ValidationResult validarArchivo(File file, String originalFilename) {
        ValidationResult result = new ValidationResult();
        
        // 1. Validar extensión
        if (!validarExtension(originalFilename)) {
            result.setValido(false);
            result.setMensaje("Extensión de archivo no permitida");
            return result;
        }
        
        // 2. Validar que no sea ejecutable
        if (!validarNoEjecutable(originalFilename)) {
            result.setValido(false);
            result.setMensaje("Tipo de archivo no permitido (ejecutable)");
            return result;
        }
        
        // 3. Validar tamaño
        if (!validarTamano(file)) {
            result.setValido(false);
            result.setMensaje("El archivo excede el tamaño máximo de 10MB");
            return result;
        }
        
        // 4. Validar MIME type real
        if (!validarMimeType(file)) {
            result.setValido(false);
            result.setMensaje("Tipo MIME del archivo no permitido");
            return result;
        }
        
        // 5. Escanear contenido malicioso (solo para archivos de texto)
        if (esArchivoTexto(originalFilename)) {
            if (!escanearContenidoMalicioso(file)) {
                result.setValido(false);
                result.setMensaje("Contenido malicioso detectado en el archivo");
                return result;
            }
        }
        
        result.setValido(true);
        result.setMensaje("Archivo válido");
        return result;
    }
    
    /**
     * Verifica si es un archivo de texto
     */
    private static boolean esArchivoTexto(String filename) {
        String extension = obtenerExtension(filename).toLowerCase();
        return Arrays.asList("txt", "csv", "log", "json", "xml").contains(extension);
    }
    
    /**
     * Obtiene la extensión del archivo
     */
    private static String obtenerExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1);
    }
    
    /**
     * Clase para resultado de validación
     */
    public static class ValidationResult {
        private boolean valido;
        private String mensaje;
        
        public boolean isValido() { return valido; }
        public void setValido(boolean valido) { this.valido = valido; }
        public String getMensaje() { return mensaje; }
        public void setMensaje(String mensaje) { this.mensaje = mensaje; }
    }
}