package com.labdent.util;

import com.labdent.model.Usuario;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.Base64;

/**
 * Utilidad para gestionar datos de sesión de forma segura
 * Proporciona compatibilidad con localStorage mediante cookies seguras
 */
public class SessionDataUtil {
    
    /**
     * Guarda el ID del usuario en una cookie (solo para referencia, NO sensible)
     */
    public static void guardarUsuarioIdEnCookie(HttpServletResponse response, int usuarioId, boolean recordar) {
        try {
            // Codificar en Base64 (ofuscación básica, no encriptación)
            String encodedId = Base64.getEncoder().encodeToString(String.valueOf(usuarioId).getBytes());
            
            Cookie cookie = new Cookie("labdent_uid", encodedId);
            cookie.setMaxAge(recordar ? 7 * 24 * 60 * 60 : -1); // 7 días o sesión
            cookie.setHttpOnly(true); // ✅ No accesible desde JavaScript
            // cookie.setSecure(true); // ✅ Descomentar en producción con HTTPS
            cookie.setPath("/");
            
            response.addCookie(cookie);
        } catch (Exception e) {
            System.err.println("Error guardando cookie de usuario: " + e.getMessage());
        }
    }
    
    /**
     * Guarda el nombre del usuario en una cookie
     */
    public static void guardarUsuarioNombreEnCookie(HttpServletResponse response, String nombre, boolean recordar) {
        try {
            String encodedNombre = Base64.getEncoder().encodeToString(nombre.getBytes("UTF-8"));
            
            Cookie cookie = new Cookie("labdent_uname", encodedNombre);
            cookie.setMaxAge(recordar ? 7 * 24 * 60 * 60 : -1);
            cookie.setHttpOnly(false); // Accesible desde JS si es necesario
            // cookie.setSecure(true);
            cookie.setPath("/");
            
            response.addCookie(cookie);
        } catch (Exception e) {
            System.err.println("Error guardando cookie de nombre: " + e.getMessage());
        }
    }
    
    /**
     * Guarda el rol del usuario en una cookie
     */
    public static void guardarUsuarioRolEnCookie(HttpServletResponse response, String rol, boolean recordar) {
        try {
            String encodedRol = Base64.getEncoder().encodeToString(rol.getBytes());
            
            Cookie cookie = new Cookie("labdent_urol", encodedRol);
            cookie.setMaxAge(recordar ? 7 * 24 * 60 * 60 : -1);
            cookie.setHttpOnly(true);
            // cookie.setSecure(true);
            cookie.setPath("/");
            
            response.addCookie(cookie);
        } catch (Exception e) {
            System.err.println("Error guardando cookie de rol: " + e.getMessage());
        }
    }
    
    /**
     * Lee el ID del usuario desde la cookie
     */
    public static Integer leerUsuarioIdDesdeCookie(HttpServletRequest request) {
        try {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("labdent_uid".equals(cookie.getName())) {
                        String decoded = new String(Base64.getDecoder().decode(cookie.getValue()));
                        return Integer.parseInt(decoded);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error leyendo cookie de usuario: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Lee el nombre del usuario desde la cookie
     */
    public static String leerUsuarioNombreDesdeCookie(HttpServletRequest request) {
        try {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("labdent_uname".equals(cookie.getName())) {
                        return new String(Base64.getDecoder().decode(cookie.getValue()), "UTF-8");
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error leyendo cookie de nombre: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Elimina todas las cookies de usuario
     */
    public static void eliminarCookiesUsuario(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().startsWith("labdent_")) {
                    cookie.setValue("");
                    cookie.setMaxAge(0);
                    cookie.setPath("/");
                    response.addCookie(cookie);
                }
            }
        }
    }
    
    /**
     * Obtiene los datos del usuario desde la sesión HTTP de forma segura
     */
    public static Usuario obtenerUsuarioDesdeSesion(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            return (Usuario) session.getAttribute("usuario");
        }
        return null;
    }
    
    /**
     * Verifica si hay una sesión válida con token JWT
     */
    public static boolean tieneSesionValida(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return false;
        
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        String token = (String) session.getAttribute("token");
        
        if (usuario == null || token == null) return false;
        
        return JWTUtil.validarToken(token) && !JWTUtil.isTokenExpirado(token);
    }
    
    /**
     * Genera un CSRF token para formularios
     */
    public static String generarCSRFToken(HttpSession session) {
        String token = Base64.getEncoder().encodeToString(
            String.valueOf(System.currentTimeMillis()).getBytes()
        );
        session.setAttribute("csrf_token", token);
        return token;
    }
    
    /**
     * Valida un CSRF token
     */
    public static boolean validarCSRFToken(HttpSession session, String token) {
        if (session == null || token == null) return false;
        
        String sessionToken = (String) session.getAttribute("csrf_token");
        return token.equals(sessionToken);
    }
}