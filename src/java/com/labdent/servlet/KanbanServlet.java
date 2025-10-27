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
import java.util.ArrayList;
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

        Usuario usuario = (Usuario) session.getAttribute("usuario");
        System.out.println("Usuario logueado -> ID: " + usuario.getId() +
                " | Rol: " + usuario.getRol());

        List<Pedido> pedidos = recuperarPedidosSegunRol(usuario);

        asignarPedidosPorEstado(request, pedidos);

        request.getRequestDispatcher("/kanban.jsp").forward(request, response);
    }

    private List<Pedido> recuperarPedidosSegunRol(Usuario usuario) {
        if ("CLIENTE".equalsIgnoreCase(usuario.getRol())) {
            System.out.println("Filtrando pedidos para CLIENTE: " + usuario.getId());
            return pedidoDAO.obtenerPedidosPorCliente(usuario.getId());
        } else {
            System.out.println("Rol administrativo, mostrando todos los pedidos");
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

        if ("actualizar_estado".equals(accion)) {
            procesarTransicionEstado(request, usuario);
        }

        doGet(request, response);
    }

    private void procesarTransicionEstado(HttpServletRequest request, Usuario usuario) {
        try {
            int pedidoId = Integer.parseInt(request.getParameter("pedidoId"));
            String nuevoEstado = request.getParameter("nuevoEstado");
            String observaciones = request.getParameter("observaciones");
            boolean checklistCompletado =
                    "on".equals(request.getParameter("checklistCompletado"));

            Pedido pedido = pedidoDAO.obtenerPorId(pedidoId);
            if (pedido != null) {
                String estadoAnterior = pedido.getEstadoActual();
                int tiempoEnEstado = transicionDAO.calcularTiempoEnEstado(pedidoId, estadoAnterior);

                boolean actualizado =
                        pedidoDAO.actualizarEstado(pedidoId, nuevoEstado, usuario.getId());

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
                    request.setAttribute("success", "Estado actualizado correctamente");
                } else {
                    request.setAttribute("error", "Error al actualizar el estado");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error en la actualización: " + e.getMessage());
        }
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
