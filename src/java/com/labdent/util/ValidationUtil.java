package com.labdent.util;

import com.google.common.base.Strings;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

/**
 * Utilidad para validaciones
 */
public class ValidationUtil {
    
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    
    private ValidationUtil() {
        // Utility class
    }
    
    /**
     * Valida si un email es válido
     */
    public static boolean isValidEmail(String email) {
        return !Strings.isNullOrEmpty(email) && EMAIL_PATTERN.matcher(email).matches();
    }
    
    /**
     * Valida si un ID es válido
     */
    public static boolean isValidId(Integer id) {
        return id != null && id > 0;
    }
    
    /**
     * Valida si un texto no está vacío
     */
    public static boolean isNotEmpty(String text) {
        return StringUtils.isNotBlank(text);
    }
    
    /**
     * Sanitiza un texto para evitar XSS
     */
    public static String sanitizeHtml(String text) {
        if (Strings.isNullOrEmpty(text)) {
            return "";
        }
        return text.replaceAll("<", "&lt;")
                   .replaceAll(">", "&gt;")
                   .replaceAll("\"", "&quot;")
                   .replaceAll("'", "&#x27;");
    }
}