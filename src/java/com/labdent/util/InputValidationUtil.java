package com.labdent.util;

import java.util.Arrays;
import java.util.List;

/**
 * Utilidad para validación de entradas del usuario
 */
public class InputValidationUtil {
    
    // Estados válidos del sistema
    private static final List<String> ESTADOS_VALIDOS = Arrays.asList(
        "RECEPCION", "PARALELIZADO", "DISENO_CAD", "PRODUCCION_CAM",
        "CERAMICA", "CONTROL_CALIDAD", "LISTO_ENTREGA", "ENTREGADO"
    );
    
    // Estados de delivery válidos
    private static final List<String> ESTADOS_DELIVERY_VALIDOS = Arrays.asList(
        "SALIO_EMPRESA", "EN_CURSO", "LLEGO_DESTINO", "PEDIDO_ENTREGADO"
    );
    
    // Prioridades válidas
    private static final List<String> PRIORIDADES_VALIDAS = Arrays.asList(
        "NORMAL", "URGENTE", "EMERGENCIA"
    );
    
    /**
     * Valida que un ID sea un número positivo
     */
    public static boolean esIdValido(String id) {
        if (id == null || id.trim().isEmpty()) {
            return false;
        }
        try {
            int numero = Integer.parseInt(id.trim());
            return numero > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Valida que un estado sea válido
     */
    public static boolean esEstadoValido(String estado) {
        return estado != null && ESTADOS_VALIDOS.contains(estado.trim().toUpperCase());
    }
    
    /**
     * Valida que un estado de delivery sea válido
     */
    public static boolean esEstadoDeliveryValido(String estado) {
        return estado != null && ESTADOS_DELIVERY_VALIDOS.contains(estado.trim().toUpperCase());
    }
    
    /**
     * Valida que una prioridad sea válida
     */
    public static boolean esPrioridadValida(String prioridad) {
        return prioridad != null && PRIORIDADES_VALIDAS.contains(prioridad.trim().toUpperCase());
    }
    
    /**
     * Valida que un string no esté vacío
     */
    public static boolean noEstaVacio(String valor) {
        return valor != null && !valor.trim().isEmpty();
    }
    
    /**
     * Valida longitud máxima
     */
    public static boolean longitudValida(String valor, int maxLength) {
        return valor != null && valor.length() <= maxLength;
    }
    
    /**
     * Valida formato de fecha YYYY-MM-DD
     */
    public static boolean esFechaValida(String fecha) {
        if (fecha == null) return false;
        return fecha.matches("^\\d{4}-\\d{2}-\\d{2}$");
    }
    
    /**
     * Valida que sea un rol válido
     */
    public static boolean esRolValido(String rol) {
        List<String> rolesValidos = Arrays.asList(
            "ADMIN", "ODONTOLOGO", "TECNICO", "CERAMISTA", "DELIVERISTA", "CLIENTE"
        );
        return rol != null && rolesValidos.contains(rol.trim().toUpperCase());
    }
    
    /**
     * Limpia y valida un parámetro de texto
     */
    public static String limpiarTexto(String texto, int maxLength) {
        if (texto == null) return "";
        String limpio = texto.trim();
        if (limpio.length() > maxLength) {
            limpio = limpio.substring(0, maxLength);
        }
        return XSSProtectionUtil.sanitizeHTML(limpio);
    }
}