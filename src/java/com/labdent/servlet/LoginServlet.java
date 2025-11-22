package com.labdent.servlet;

import com.labdent.dao.UsuarioDAO;
import com.labdent.model.Usuario;
import com.labdent.util.JWTUtil;
import com.labdent.util.SessionDataUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.logging.Logger;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    
    private static final Logger LOGGER = Logger.getLogger(LoginServlet.class.getName());
    private static final int MAX_LOGIN_ATTEMPTS = 5;
    private UsuarioDAO usuarioDAO;

    @Override
    public void init() {
        usuarioDAO = new UsuarioDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Si ya hay sesión activa con token válido, redirigir
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("usuario") != null) {
            String token = (String) session.getAttribute("token");
            if (token != null && JWTUtil.validarToken(token) && !JWTUtil.isTokenExpirado(token)) {
                Usuario usuario = (Usuario) session.getAttribute("usuario");
                redirigirPorRol(response, usuario.getRol());
                return;
            }
        }
        
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // ✅ Configurar headers de seguridad
        configurarHeadersSeguridad(response);
        
        // ✅ Validación de rate limiting
        HttpSession session = request.getSession(true);
        Integer intentos = (Integer) session.getAttribute("loginAttempts");
        if (intentos == null) intentos = 0;
        
        if (intentos >= MAX_LOGIN_ATTEMPTS) {
            Long tiempoBloqueo = (Long) session.getAttribute("blockTime");
            if (tiempoBloqueo != null && System.currentTimeMillis() < tiempoBloqueo) {
                long minutosRestantes = (tiempoBloqueo - System.currentTimeMillis()) / (60 * 1000);
                request.setAttribute("error", "Demasiados intentos fallidos. Intente en " + minutosRestantes + " minutos.");
                request.getRequestDispatcher("/login.jsp").forward(request, response);
                return;
            } else {
                // Reiniciar intentos después del bloqueo
                session.setAttribute("loginAttempts", 0);
                session.removeAttribute("blockTime");
                intentos = 0;
            }
        }
        
        // ✅ Sanitizar entradas
        String email = sanitizarInput(request.getParameter("email"));
        String password = request.getParameter("password");
        boolean recordar = "on".equals(request.getParameter("recordar"));

        // Validar campos vacíos
        if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
            request.setAttribute("error", "Por favor complete todos los campos");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }

        // ✅ Validar formato de email
        if (!validarEmail(email)) {
            request.setAttribute("error", "Formato de email inválido");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }

        // Autenticar usuario
        Usuario usuario = usuarioDAO.autenticar(email, password);

        if (usuario != null) {
            // ✅ Generar token JWT
            String token = JWTUtil.generarToken(usuario.getId(), usuario.getEmail(), usuario.getRol());
            
            // ✅ Guardar token en la base de datos
            if (!usuarioDAO.guardarToken(usuario.getId(), token)) {
                request.setAttribute("error", "Error al iniciar sesión. Intente nuevamente.");
                request.getRequestDispatcher("/login.jsp").forward(request, response);
                return;
            }
            
            // ✅ Crear sesión segura
            session.invalidate(); // Invalidar sesión anterior
            session = request.getSession(true);
            
            // ✅ Regenerar ID de sesión (previene session fixation)
            request.changeSessionId();
            
            session.setAttribute("usuario", usuario);
            session.setAttribute("usuarioId", usuario.getId());
            session.setAttribute("usuarioNombre", usuario.getNombreCompleto());
            session.setAttribute("usuarioRol", usuario.getRol());
            session.setAttribute("token", token);
            session.setMaxInactiveInterval(8 * 60 * 60); // 8 horas

            // ✅ Guardar datos del usuario en cookies encriptadas (reemplazo de localStorage)
            SessionDataUtil.guardarUsuarioIdEnCookie(response, usuario.getId(), recordar);
            SessionDataUtil.guardarUsuarioNombreEnCookie(response, usuario.getNombreCompleto(), recordar);
            SessionDataUtil.guardarUsuarioRolEnCookie(response, usuario.getRol(), recordar);
            
            // Cookie para recordar email
            if (recordar) {
                Cookie cookieEmail = new Cookie("labdent_email", email);
                cookieEmail.setMaxAge(7 * 24 * 60 * 60); // 7 días
                cookieEmail.setHttpOnly(true); // ✅ Previene XSS
                cookieEmail.setSecure(request.isSecure()); // ✅ Solo HTTPS
                cookieEmail.setPath(request.getContextPath());
                response.addCookie(cookieEmail);
            }

            // ✅ Registrar login exitoso
            LOGGER.info("Login exitoso - Usuario: " + usuario.getEmail() + " | ID: " + usuario.getId() + " | IP: " + obtenerIP(request));
            
            // Resetear intentos fallidos
            session.removeAttribute("loginAttempts");
            session.removeAttribute("blockTime");
            
            // Redirigir según el rol
            redirigirPorRol(response, usuario.getRol());
            
        } else {
            // ✅ Incrementar intentos fallidos
            intentos++;
            session.setAttribute("loginAttempts", intentos);
            
            if (intentos >= MAX_LOGIN_ATTEMPTS) {
                // Bloquear por 15 minutos
                long tiempoBloqueo = System.currentTimeMillis() + (15 * 60 * 1000);
                session.setAttribute("blockTime", tiempoBloqueo);
                LOGGER.warning("Cuenta bloqueada temporalmente - Email: " + email + " | IP: " + obtenerIP(request));
                request.setAttribute("error", "Demasiados intentos fallidos. Cuenta bloqueada por 15 minutos.");
            } else {
                int intentosRestantes = MAX_LOGIN_ATTEMPTS - intentos;
                request.setAttribute("error", "Credenciales inválidas. Intentos restantes: " + intentosRestantes);
            }
            
            LOGGER.warning("Login fallido - Email: " + email + " | Intentos: " + intentos + " | IP: " + obtenerIP(request));
            request.setAttribute("email", email);
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }

    /**
     * ✅ Configurar headers de seguridad HTTP
     */
    private void configurarHeadersSeguridad(HttpServletResponse response) {
        response.setHeader("X-Content-Type-Options", "nosniff");
        response.setHeader("X-Frame-Options", "DENY");
        response.setHeader("X-XSS-Protection", "1; mode=block");
        response.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
        response.setHeader("Content-Security-Policy", "default-src 'self'; script-src 'self' 'unsafe-inline'; style-src 'self' 'unsafe-inline'");
    }
    
    /**
     * ✅ Sanitizar entrada del usuario
     */
    private String sanitizarInput(String input) {
        if (input == null) return null;
        return input.trim()
                   .replaceAll("<", "&lt;")
                   .replaceAll(">", "&gt;")
                   .replaceAll("\"", "&quot;")
                   .replaceAll("'", "&#x27;")
                   .replaceAll("/", "&#x2F;");
    }
    
    /**
     * ✅ Validar formato de email
     */
    private boolean validarEmail(String email) {
        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(regex);
    }
    
    /**
     * Obtener IP real del cliente
     */
    private String obtenerIP(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    private void redirigirPorRol(HttpServletResponse response, String rol) throws IOException {
        switch (rol) {
            case "ADMIN":
                response.sendRedirect("dashboard");
                break;
            case "ODONTOLOGO":
                response.sendRedirect("misPedidos");
                break;
            case "DELIVERISTA":
                response.sendRedirect("panelDeliverista.jsp");
                break;
            case "TECNICO":
            case "CERAMISTA":
                response.sendRedirect("panelTecnico.jsp");
                break;
            case "CLIENTE":
                response.sendRedirect("PanelUsuario.jsp");
                break;
            default:
                response.sendRedirect("index.jsp");
        }
    }
}