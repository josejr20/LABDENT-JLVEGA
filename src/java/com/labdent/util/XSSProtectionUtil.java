package com.labdent.util;

import org.owasp.encoder.Encode;

/**
 * Utilidad para protección contra XSS (Cross-Site Scripting)
 * Usa OWASP Java Encoder para sanitización segura
 */
public class XSSProtectionUtil {
    
    /**
     * Sanitiza texto para usar en HTML
     * Escapa: < > & " ' /
     */
    public static String sanitizeHTML(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }
        return Encode.forHtml(input);
    }
    
    /**
     * Sanitiza para atributos HTML
     */
    public static String sanitizeHTMLAttribute(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }
        return Encode.forHtmlAttribute(input);
    }
    
    /**
     * Sanitiza para JavaScript
     */
    public static String sanitizeJavaScript(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }
        return Encode.forJavaScript(input);
    }
    
    /**
     * Sanitiza para URLs
     */
    public static String sanitizeURL(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }
        return Encode.forUriComponent(input);
    }
    
    /**
     * Sanitiza para CSS
     */
    public static String sanitizeCSS(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }
        return Encode.forCssString(input);
    }
    
    /**
     * Remueve todos los tags HTML
     */
    public static String stripHTMLTags(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }
        return input.replaceAll("<[^>]*>", "");
    }
    
    /**
     * Valida que solo contenga caracteres alfanuméricos
     */
    public static boolean isAlphanumeric(String input) {
        return input != null && input.matches("^[a-zA-Z0-9]+$");
    }
    
    /**
     * Valida formato de email
     */
    public static boolean isValidEmail(String email) {
        if (email == null) return false;
        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(regex);
    }
    
    /**
     * Valida que sea un número entero positivo
     */
    public static boolean isPositiveInteger(String input) {
        if (input == null || input.isEmpty()) return false;
        try {
            return Integer.parseInt(input) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}