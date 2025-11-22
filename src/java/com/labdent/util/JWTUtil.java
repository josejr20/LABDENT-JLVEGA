package com.labdent.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Utilidad para generar y validar tokens JWT
 */
public class JWTUtil {
    
    // ⚠️ CAMBIAR ESTA CLAVE EN PRODUCCIÓN - Debe ser de al menos 256 bits
    private static final String SECRET_KEY = "LabDent_JLVega_Secret_Key_2025_SuperSecure_MinLength32Chars!@#$%";
    private static final SecretKey KEY = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    
    // Duración del token: 8 horas (en milisegundos)
    private static final long TOKEN_VALIDITY = 8 * 60 * 60 * 1000;
    
    /**
     * Genera un token JWT para un usuario
     */
    public static String generarToken(int usuarioId, String email, String rol) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("usuarioId", usuarioId);
        claims.put("email", email);
        claims.put("rol", rol);
        
        Date now = new Date();
        Date expiration = new Date(now.getTime() + TOKEN_VALIDITY);
        
        return Jwts.builder()
                .claims(claims)
                .subject(email)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(KEY)
                .compact();
    }
    
    /**
     * Valida un token JWT
     */
    public static boolean validarToken(String token) {
        try {
            Jwts.parser()
                .verifyWith(KEY)
                .build()
                .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            System.err.println("Token inválido: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Verifica si un token ha expirado
     */
    public static boolean isTokenExpirado(String token) {
        try {
            Claims claims = obtenerClaims(token);
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }
    
    /**
     * Obtiene el ID del usuario desde el token
     */
    public static Integer obtenerUsuarioId(String token) {
        try {
            Claims claims = obtenerClaims(token);
            return claims.get("usuarioId", Integer.class);
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Obtiene el email del usuario desde el token
     */
    public static String obtenerEmail(String token) {
        try {
            Claims claims = obtenerClaims(token);
            return claims.getSubject();
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Obtiene el rol del usuario desde el token
     */
    public static String obtenerRol(String token) {
        try {
            Claims claims = obtenerClaims(token);
            return claims.get("rol", String.class);
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Obtiene la fecha de expiración del token
     */
    public static Date obtenerExpiracion(String token) {
        try {
            Claims claims = obtenerClaims(token);
            return claims.getExpiration();
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Obtiene todos los claims del token
     */
    private static Claims obtenerClaims(String token) {
        return Jwts.parser()
                .verifyWith(KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    
    /**
     * Calcula el tiempo restante del token en minutos
     */
    public static long obtenerTiempoRestante(String token) {
        try {
            Date expiracion = obtenerExpiracion(token);
            if (expiracion == null) return 0;
            
            long diff = expiracion.getTime() - System.currentTimeMillis();
            return diff > 0 ? diff / (60 * 1000) : 0;
        } catch (Exception e) {
            return 0;
        }
    }
}