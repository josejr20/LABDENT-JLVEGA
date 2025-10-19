package com.labdent.servlet;

import com.labdent.dao.PedidoDAO;
import com.labdent.dao.TransicionDAO;
import com.labdent.model.Pedido;
import com.labdent.model.TransicionEstado;
import com.labdent.model.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/kanban")
public class KanbanServlet extends HttpServlet {

    private PedidoDAO pedidoDAO;
    private TransicionDAO transicionDAO;

    @Override
    public void init() {
        pedidoDAO = new PedidoDAO();
        transicionDAO = new TransicionDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuario") == null) {
            response.sendRedirect("login");
            return;
        }

        // Obtener todos los pedidos FIFO
        List<Pedido> todosPedidos = pedidoDAO.listarTodos();
        
        // Separar por estado para el tablero Kanban
        request.setAttribute("pedidosRecepcion", filtrarPorEstado(todosPedidos, "RECEPCION"));
        request.setAttribute("pedidosParalelizado", filtrarPorEstado(todosPedidos, "PARALELIZADO"));
        request.setAttribute("pedidosDisenoCad", filtrarPorEstado(todosPedidos, "DISENO_CAD"));
        request.setAttribute("pedidosProduccionCam", filtrarPorEstado(todosPedidos, "PRODUCCION_CAM"));
        request.setAttribute("pedidosCeramica", filtrarPorEstado(todosPedidos, "CERAMICA"));
        request.setAttribute("pedidosControlCalidad", filtrarPorEstado(todosPedidos, "CONTROL_CALIDAD"));
        request.setAttribute("pedidosListoEntrega", filtrarPorEstado(todosPedidos, "LISTO_ENTREGA"));

        request.getRequestDispatcher("/kanban.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuario") == null) {
            response.sendRedirect("login");
            return;
        }

        Usuario usuario = (Usuario) session.getAttribute("usuario");
        String accion = request.getParameter("accion");

        try {
            if ("actualizar_estado".equals(accion)) {
                int pedidoId = Integer.parseInt(request.getParameter("pedidoId"));
                String nuevoEstado = request.getParameter("nuevoEstado");
                String observaciones = request.getParameter("observaciones");
                boolean checklistCompletado = "on".equals(request.getParameter("checklistCompletado"));

                // Obtener pedido actual
                Pedido pedido = pedidoDAO.obtenerPorId(pedidoId);
                if (pedido != null) {
                    String estadoAnterior = pedido.getEstadoActual();
                    
                    // Calcular tiempo en estado anterior
                    int tiempoEnEstado = transicionDAO.calcularTiempoEnEstado(pedidoId, estadoAnterior);

                    // Actualizar estado del pedido
                    if (pedidoDAO.actualizarEstado(pedidoId, nuevoEstado, usuario.getId())) {
                        // Registrar transición
                        TransicionEstado transicion = new TransicionEstado();
                        transicion.setPedidoId(pedidoId);
                        transicion.setEstadoAnterior(estadoAnterior);
                        transicion.setEstadoNuevo(nuevoEstado);
                        transicion.setUsuarioId(usuario.getId());
                        transicion.setObservaciones(observaciones);
                        transicion.setChecklistCompletado(checklistCompletado);
                        transicion.setTiempoEnEstado(tiempoEnEstado);
                        transicionDAO.registrar(transicion);

                        request.setAttribute("success", "Estado actualizado correctamente");
                    } else {
                        request.setAttribute("error", "Error al actualizar el estado");
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error: " + e.getMessage());
        }

        doGet(request, response);
    }

    private List<Pedido> filtrarPorEstado(List<Pedido> pedidos, String estado) {
        return pedidos.stream()
                .filter(p -> estado.equals(p.getEstadoActual()))
                .collect(java.util.stream.Collectors.toList());
    }
}