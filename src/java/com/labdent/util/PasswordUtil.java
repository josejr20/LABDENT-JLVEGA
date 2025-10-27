package com.labdent.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Utilidad para manejo seguro de contraseñas
 * Implementa hashing con SHA-256 y salt
 */
public class PasswordUtil {

    private static final String ALGORITHM = "SHA-256";
    private static final int SALT_LENGTH = 16;

    /**
     * Genera un salt aleatorio
     * @return Salt en formato Base64
     */
    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    /**
     * Hashea una contraseña con salt usando SHA-256
     * @param password Contraseña en texto plano
     * @param salt Salt para el hash
     * @return Contraseña hasheada en Base64
     */
    public static String hashPassword(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            md.update(Base64.getDecoder().decode(salt));
            byte[] hashedPassword = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hashedPassword);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al hashear contraseña", e);
        }
    }

    /**
     * Verifica si una contraseña coincide con su hash
     * @param password Contraseña en texto plano
     * @param salt Salt utilizado en el hash original
     * @param hashedPassword Hash almacenado
     * @return true si coinciden, false en caso contrario
     */
    public static boolean verifyPassword(String password, String salt, String hashedPassword) {
        String newHash = hashPassword(password, salt);
        return newHash.equals(hashedPassword);
    }

    /**
     * Método simple para hashear sin salt (no recomendado para producción)
     * Solo para desarrollo/pruebas
     * @param password Contraseña en texto plano
     * @return Hash SHA-256 en formato hexadecimal
     */
    public static String simpleHash(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            byte[] hashedPassword = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedPassword) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al hashear contraseña", e);
        }
    }

    /**
     * Valida la fortaleza de una contraseña
     * @param password Contraseña a validar
     * @return true si cumple los requisitos mínimos
     */
    public static boolean isPasswordStrong(String password) {
        if (password == null || password.length() < 6) {
            return false;
        }

        // Requisitos: al menos 6 caracteres
        // Para producción, agregar más validaciones:
        // - Mayúsculas, minúsculas, números, caracteres especiales
        boolean hasLetter = password.matches(".*[a-zA-Z].*");
        boolean hasDigit = password.matches(".*\\d.*");

        return hasLetter || hasDigit;
    }

    /**
     * Genera una contraseña temporal aleatoria
     * @param length Longitud de la contraseña
     * @return Contraseña temporal
     */
    public static String generateTemporaryPassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();

        for (int i = 0; i < length; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }

        return password.toString();
    }
}