package com.labdent.servlet;

import com.labdent.dao.PedidoDAO;
import com.labdent.dao.TransicionDAO;
import com.labdent.dao.UsuarioDAO;
import com.labdent.model.Pedido;
import com.labdent.model.TransicionEstado;
import com.labdent.model.Usuario;
import com.labdent.util.JWTUtil;
import com.labdent.util.SessionDataUtil;
import com.labdent.util.XSSProtectionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@WebServlet("/kanban")
public class KanbanServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(KanbanServlet.class.getName());
    private PedidoDAO pedidoDAO;
    private TransicionDAO transicionDAO;
    private UsuarioDAO usuarioDAO;

    @Override
    public void init() {
        pedidoDAO = new PedidoDAO();
        transicionDAO = new TransicionDAO();
        usuarioDAO = new UsuarioDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // ✅ Validar sesión
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuario") == null) {
            response.sendRedirect("login?error=session_expired");
            return;
        }

        // ✅ Validar token JWT
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        String token = (String) session.getAttribute("token");
        
        if (token == null || !JWTUtil.validarToken(token) || JWTUtil.isTokenExpirado(token)) {
            session.invalidate();
            response.sendRedirect("login?error=token_expired");
            return;
        }

        // ✅ Validar token en BD
        if (!usuarioDAO.validarTokenEnBD(usuario.getId(), token)) {
            session.invalidate();
            response.sendRedirect("login?error=invalid_token");
            return;
        }

        LOGGER.info("Usuario " + usuario.getId() + " (" + usuario.getRol() + ") accede a Kanban");

        List<Pedido> pedidos = recuperarPedidosSegunRol(usuario);
        asignarPedidosPorEstado(request, pedidos);

        request.getRequestDispatcher("/kanban.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuario") == null) {
            response.sendRedirect("login?error=session_expired");
            return;
        }

        Usuario usuario = (Usuario) session.getAttribute("usuario");
        
        // ✅ Validar token JWT
        String token = (String) session.getAttribute("token");
        if (token == null || !JWTUtil.validarToken(token) || JWTUtil.isTokenExpirado(token)) {
            session.invalidate();
            response.sendRedirect("login?error=token_expired");
            return;
        }

        // ✅ Validar CSRF
        String csrfToken = request.getParameter("csrf_token");
        if (!SessionDataUtil.validarCSRFToken(session, csrfToken)) {
            LOGGER.warning("⚠️ CSRF token inválido - Usuario: " + usuario.getId());
            session.setAttribute("error", "Token de seguridad inválido");
            response.sendRedirect("kanban");
            return;
        }

        String accion = XSSProtectionUtil.sanitizeHTML(request.getParameter("accion"));

        if ("actualizar_estado".equals(accion)) {
            procesarTransicionEstado(request, session, usuario);
        }

        response.sendRedirect("kanban");
    }

    private void procesarTransicionEstado(HttpServletRequest request, HttpSession session, Usuario usuario) {
        try {
            // ✅ Validar y sanitizar parámetros
            String pedidoIdStr = request.getParameter("pedidoId");
            if (!XSSProtectionUtil.isPositiveInteger(pedidoIdStr)) {
                session.setAttribute("error", "ID de pedido inválido");
                return;
            }
            
            int pedidoId = Integer.parseInt(pedidoIdStr);
            
            // ✅ Sanitizar entradas
            String nuevoEstado = XSSProtectionUtil.sanitizeHTML(request.getParameter("nuevoEstado"));
            String observaciones = XSSProtectionUtil.sanitizeHTML(request.getParameter("observaciones"));
            boolean checklistCompletado = "on".equals(request.getParameter("checklistCompletado"));

            // ✅ Validar que el estado es válido
            if (!esEstadoValido(nuevoEstado)) {
                session.setAttribute("error", "Estado no válido");
                return;
            }

            Pedido pedido = pedidoDAO.obtenerPorId(pedidoId);
            if (pedido == null) {
                session.setAttribute("error", "Pedido no encontrado");
                return;
            }

            String estadoAnterior = pedido.getEstadoActual();
            int tiempoEnEstado = transicionDAO.calcularTiempoEnEstado(pedidoId, estadoAnterior);

            boolean actualizado = pedidoDAO.actualizarEstado(pedidoId, nuevoEstado, usuario.getId());

            if (actualizado) {
                TransicionEstado transicion = new TransicionEstado();
                transicion.setPedidoId(pedidoId);
                transicion.setEstadoAnterior(estadoAnterior);
                transicion.setEstadoNuevo(nuevoEstado);
                transicion.setUsuarioId(usuario.getId());
                transicion.setObservaciones(observaciones);
                transicion.setChecklistCompletado(checklistCompletado);
                transicion.setTiempoEnEstado(tiempoEnEstado);

                transicionDAO.registrar(transicion);
                
                LOGGER.info("✅ Estado actualizado - Pedido: " + pedidoId + " | " + 
                           estadoAnterior + " → " + nuevoEstado);
                session.setAttribute("success", "Estado actualizado correctamente");
            } else {
                session.setAttribute("error", "Error al actualizar el estado");
            }
        } catch (Exception e) {
            LOGGER.severe("❌ Error: " + e.getMessage());
            e.printStackTrace();
            session.setAttribute("error", "Error en la actualización");
        }
    }

    private boolean esEstadoValido(String estado) {
        List<String> estadosValidos = List.of(
            "RECEPCION", "PARALELIZADO", "DISENO_CAD", "PRODUCCION_CAM",
            "CERAMICA", "CONTROL_CALIDAD", "LISTO_ENTREGA", "ENTREGADO"
        );
        return estadosValidos.contains(estado);
    }

    private List<Pedido> recuperarPedidosSegunRol(Usuario usuario) {
        if ("CLIENTE".equalsIgnoreCase(usuario.getRol())) {
            LOGGER.info("Filtrando pedidos para CLIENTE: " + usuario.getId());
            return pedidoDAO.obtenerPedidosPorCliente(usuario.getId());
        } else {
            LOGGER.info("Rol administrativo, mostrando todos los pedidos");
            return pedidoDAO.listarTodos();
        }
    }

    private void asignarPedidosPorEstado(HttpServletRequest request, List<Pedido> pedidos) {
        request.setAttribute("pedidosRecepcion", filtrarPorEstado(pedidos, "RECEPCION"));
        request.setAttribute("pedidosParalelizado", filtrarPorEstado(pedidos, "PARALELIZADO"));
        request.setAttribute("pedidosDisenoCad", filtrarPorEstado(pedidos, "DISENO_CAD"));
        request.setAttribute("pedidosProduccionCam", filtrarPorEstado(pedidos, "PRODUCCION_CAM"));
        request.setAttribute("pedidosCeramica", filtrarPorEstado(pedidos, "CERAMICA"));
        request.setAttribute("pedidosControlCalidad", filtrarPorEstado(pedidos, "CONTROL_CALIDAD"));
        request.setAttribute("pedidosListoEntrega", filtrarPorEstado(pedidos, "LISTO_ENTREGA"));
    }

    private List<Pedido> filtrarPorEstado(List<Pedido> pedidos, String estado) {
        List<Pedido> filtrados = new ArrayList<>();
        for (Pedido pedido : pedidos) {
            if (estado.equals(pedido.getEstadoActual())) {
                filtrados.add(pedido);
            }
        }
        return filtrados;
    }
}