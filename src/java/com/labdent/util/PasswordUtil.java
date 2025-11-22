package com.labdent.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Utilidad para encriptar y verificar contraseñas usando BCrypt
 */
public class PasswordUtil {
    
    // Factor de trabajo para BCrypt (entre 4 y 31, recomendado: 10-12)
    private static final int WORK_FACTOR = 12;
    
    /**
     * Encripta una contraseña en texto plano usando BCrypt
     * @param plainPassword Contraseña en texto plano
     * @return Contraseña encriptada (hash)
     */
    public static String encriptar(String plainPassword) {
        if (plainPassword == null || plainPassword.isEmpty()) {
            throw new IllegalArgumentException("La contraseña no puede estar vacía");
        }
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(WORK_FACTOR));
    }
    
    /**
     * Verifica si una contraseña en texto plano coincide con el hash almacenado
     * @param plainPassword Contraseña en texto plano
     * @param hashedPassword Hash almacenado en la base de datos
     * @return true si la contraseña es correcta, false en caso contrario
     */
    public static boolean verificar(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) {
            return false;
        }
        try {
            return BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (IllegalArgumentException e) {
            // Hash inválido
            return false;
        }
    }
    
    /**
     * Verifica si un hash necesita ser actualizado (por cambio en work factor)
     * @param hashedPassword Hash a verificar
     * @return true si necesita actualización
     */
    public static boolean necesitaActualizacion(String hashedPassword) {
        try {
            return !hashedPassword.startsWith("$2a$" + WORK_FACTOR + "$");
        } catch (Exception e) {
            return true;
        }
    }
}