package com.labdent.servlet;

import com.labdent.dao.PedidoDAO;
import com.labdent.dao.TransicionEstadoDAO;
import com.labdent.dao.EvidenciaDAO;
import com.labdent.model.Pedido;
import com.labdent.model.Usuario;
import com.labdent.model.TransicionEstado;
import com.labdent.model.Evidencia;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/ver-pedido")
public class VerPedidoServlet extends HttpServlet {

    private final PedidoDAO pedidoDAO = new PedidoDAO();
    private final TransicionEstadoDAO transicionDAO = new TransicionEstadoDAO();
    private final EvidenciaDAO evidenciaDAO = new EvidenciaDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String idParam = request.getParameter("id");
        if (idParam == null || idParam.trim().isEmpty()) {
            System.err.println("❌ [VerPedido] ID del pedido no proporcionado");
            response.sendRedirect("KanbanUsuarioServlet");
            return;
        }

        try {
            int pedidoId = Integer.parseInt(idParam);
            System.out.println("🔍 [VerPedido] Buscando pedido ID: " + pedidoId);

            // Obtener el pedido
            Pedido pedido = pedidoDAO.obtenerPorId(pedidoId);

            if (pedido == null) {
                System.err.println("❌ [VerPedido] Pedido no encontrado");
                request.setAttribute("error", "Pedido no encontrado");
                response.sendRedirect("KanbanUsuarioServlet");
                return;
            }

            // Validar que el usuario tenga acceso al pedido
            Integer usuarioId = (Integer) session.getAttribute("usuarioId");
            boolean tieneAcceso = false;

            if (usuario.isAdmin() || usuario.isTecnico() || usuario.isCeramista()) {
                tieneAcceso = true;
            } else if (usuario.isOdontologo()) {
                tieneAcceso = (pedido.getOdontologoId() == usuario.getId());
            } else if (usuario.isCliente()) {
                tieneAcceso = (pedido.getUsuarioId() == usuarioId);
            }

            if (!tieneAcceso) {
                System.err.println("⚠️ [VerPedido] Usuario sin acceso al pedido");
                request.setAttribute("error", "No tienes acceso a este pedido");
                response.sendRedirect("KanbanUsuarioServlet");
                return;
            }

            System.out.println("✅ [VerPedido] Pedido encontrado: " + pedido.getCodigoUnico());

            // Obtener historial de transiciones
            List<TransicionEstado> historial = transicionDAO.obtenerHistorialPorPedido(pedidoId);
            System.out.println("📝 [VerPedido] Transiciones encontradas: " + 
                             (historial != null ? historial.size() : 0));

            // Obtener evidencias/archivos
            List<Evidencia> evidencias = evidenciaDAO.listarPorPedido(pedidoId);
            System.out.println("📎 [VerPedido] Evidencias encontradas: " + 
                             (evidencias != null ? evidencias.size() : 0));

            // Enviar datos al JSP
            request.setAttribute("pedido", pedido);
            request.setAttribute("historial", historial);
            request.setAttribute("evidencias", evidencias);

            request.getRequestDispatcher("detallePedido.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            System.err.println("❌ [VerPedido] ID inválido: " + idParam);
            response.sendRedirect("KanbanUsuarioServlet");
        } catch (Exception e) {
            System.err.println("❌ [VerPedido] Error: " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect("KanbanUsuarioServlet");
        }
    }
}