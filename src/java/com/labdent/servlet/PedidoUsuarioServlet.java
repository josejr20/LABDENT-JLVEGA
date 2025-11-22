package com.labdent.servlet;

import com.google.gson.Gson;
import com.labdent.dao.PedidoDAO;
import com.labdent.dao.UsuarioDAO;
import com.labdent.model.Pedido;
import com.labdent.model.Usuario;
import com.labdent.util.JWTUtil;
import com.labdent.util.XSSProtectionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

@WebServlet("/pedido/listarPorUsuario")
public class PedidoUsuarioServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(PedidoUsuarioServlet.class.getName());
    private final PedidoDAO pedidoDAO = new PedidoDAO();
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");

        // ✅ 1. Validar sesión
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuario") == null) {
            enviarError(response, HttpServletResponse.SC_UNAUTHORIZED, "Sesión no válida");
            return;
        }

        // ✅ 2. Validar token JWT
        String token = (String) session.getAttribute("token");
        if (token == null || !JWTUtil.validarToken(token) || JWTUtil.isTokenExpirado(token)) {
            session.invalidate();
            enviarError(response, HttpServletResponse.SC_UNAUTHORIZED, "Token expirado");
            return;
        }

        // ✅ 3. Obtener usuario de sesión
        Usuario usuarioSesion = (Usuario) session.getAttribute("usuario");
        
        // ✅ 4. Validar token en BD
        if (!usuarioDAO.validarTokenEnBD(usuarioSesion.getId(), token)) {
            session.invalidate();
            enviarError(response, HttpServletResponse.SC_UNAUTHORIZED, "Token no válido");
            return;
        }

        // ✅ 5. Validar y sanitizar ID
        String idParam = request.getParameter("id");
        if (!XSSProtectionUtil.isPositiveInteger(idParam)) {
            enviarError(response, HttpServletResponse.SC_BAD_REQUEST, "ID inválido");
            return;
        }

        int usuarioId = Integer.parseInt(idParam);

        // ✅ 6. CRITICAL: Validar que el usuario solo pueda ver SUS pedidos
        // (excepto ADMIN que puede ver todos)
        if (!usuarioSesion.isAdmin() && usuarioSesion.getId() != usuarioId) {
            LOGGER.warning("⚠️ Intento de acceso no autorizado - Usuario " + 
                          usuarioSesion.getId() + " intentó acceder a pedidos de " + usuarioId);
            enviarError(response, HttpServletResponse.SC_FORBIDDEN, 
                       "No tiene permisos para ver estos pedidos");
            return;
        }

        try {
            LOGGER.info("✅ Usuario " + usuarioSesion.getId() + " consultando pedidos de " + usuarioId);
            
            List<Pedido> pedidos = pedidoDAO.obtenerPedidosPorCliente(usuarioId);

            if (pedidos == null || pedidos.isEmpty()) {
                LOGGER.info("📭 No hay pedidos para el usuario " + usuarioId);
            } else {
                LOGGER.info("📦 Pedidos encontrados: " + pedidos.size());
            }

            respuestaJsonExitosa(response, pedidos);

        } catch (Exception ex) {
            LOGGER.severe("❌ ERROR: " + ex.getMessage());
            ex.printStackTrace();
            enviarError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Error al obtener pedidos");
        }
    }

    private void respuestaJsonExitosa(HttpServletResponse response, List<Pedido> pedidos)
            throws IOException {
        JsonRespuesta jsonRespuesta = new JsonRespuesta("success", pedidos);
        String json = gson.toJson(jsonRespuesta);
        response.getWriter().write(json);
    }

    private void enviarError(HttpServletResponse response, int status, String mensaje)
            throws IOException {
        response.setStatus(status);
        JsonRespuesta jsonError = new JsonRespuesta("error", mensaje);
        response.getWriter().write(gson.toJson(jsonError));
    }

    private static class JsonRespuesta {
        private final String status;
        private final Object data;

        public JsonRespuesta(String status, Object data) {
            this.status = status;
            this.data = data;
        }
    }
}