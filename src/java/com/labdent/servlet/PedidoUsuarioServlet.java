package com.labdent.servlet;

import com.google.gson.Gson;
import com.labdent.dao.PedidoDAO;
import com.labdent.model.Pedido;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/pedido/listarPorUsuario")
public class PedidoUsuarioServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final PedidoDAO pedidoDAO = new PedidoDAO();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");

        String idParam = request.getParameter("id");
        if (!esIdValido(idParam)) {
            enviarError(response, HttpServletResponse.SC_BAD_REQUEST, "ID inválido");
            return;
        }

        int usuarioId = Integer.parseInt(idParam);

        try {
            System.out.println("📌 [DEBUG] Consultando pedidos del cliente ID → " + usuarioId);

            List<Pedido> pedidos = pedidoDAO.obtenerPedidosPorCliente(usuarioId);

            if (pedidos == null || pedidos.isEmpty()) {
                System.out.println("⚠ [DEBUG] No hay pedidos para este usuario");
            } else {
                System.out.println("✅ [DEBUG] Pedidos encontrados: " + pedidos.size());
            }

            respuestaJsonExitosa(response, pedidos);

        } catch (Exception ex) {
            System.err.println("❌ ERROR en PedidoUsuarioServlet: " + ex.getMessage());
            ex.printStackTrace();
            enviarError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Error al obtener pedidos del cliente");
        }
    }

    private boolean esIdValido(String id) {
        return id != null && id.matches("\\d+");
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
