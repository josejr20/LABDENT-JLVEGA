package com.labdent.servlet;

import com.labdent.dao.PedidoDAO;
import com.labdent.model.Pedido;
import com.labdent.model.Usuario;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/kanbanDeliverista")
public class KanbanDeliveristaServlet extends HttpServlet {

    private final PedidoDAO pedidoDAO = new PedidoDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null || !"DELIVERISTA".equals(usuario.getRol())) {
            response.sendRedirect("login.jsp");
            return;
        }

        System.out.println("🚚 [KanbanDeliverista] Usuario: " + usuario.getNombreCompleto());

        try {
            // Obtener todos los pedidos hasta LISTO_ENTREGA
            List<Pedido> todosPedidos = pedidoDAO.listarTodos();

            // Separar por estado (solo hasta LISTO_ENTREGA)
            List<Pedido> pedidosRecepcion = new ArrayList<>();
            List<Pedido> pedidosParalelizado = new ArrayList<>();
            List<Pedido> pedidosDisenoCad = new ArrayList<>();
            List<Pedido> pedidosProduccionCam = new ArrayList<>();
            List<Pedido> pedidosCeramica = new ArrayList<>();
            List<Pedido> pedidosControlCalidad = new ArrayList<>();
            List<Pedido> pedidosListoEntrega = new ArrayList<>();

            if (todosPedidos != null) {
                for (Pedido pedido : todosPedidos) {
                    String estado = pedido.getEstadoActual();
                    if (estado == null) continue;

                    // Solo mostrar pedidos que NO estén entregados
                    if ("ENTREGADO".equals(estado)) {
                        continue;
                    }

                    switch (estado.trim()) {
                        case "RECEPCION":
                            pedidosRecepcion.add(pedido);
                            break;
                        case "PARALELIZADO":
                            pedidosParalelizado.add(pedido);
                            break;
                        case "DISENO_CAD":
                            pedidosDisenoCad.add(pedido);
                            break;
                        case "PRODUCCION_CAM":
                            pedidosProduccionCam.add(pedido);
                            break;
                        case "CERAMICA":
                            pedidosCeramica.add(pedido);
                            break;
                        case "CONTROL_CALIDAD":
                            pedidosControlCalidad.add(pedido);
                            break;
                        case "LISTO_ENTREGA":
                            pedidosListoEntrega.add(pedido);
                            break;
                    }
                }
            }

            System.out.println("📊 [KanbanDeliverista] Pedidos Listos para Entrega: " + pedidosListoEntrega.size());

            // Enviar las listas al JSP
            request.setAttribute("pedidosRecepcion", pedidosRecepcion);
            request.setAttribute("pedidosParalelizado", pedidosParalelizado);
            request.setAttribute("pedidosDisenoCad", pedidosDisenoCad);
            request.setAttribute("pedidosProduccionCam", pedidosProduccionCam);
            request.setAttribute("pedidosCeramica", pedidosCeramica);
            request.setAttribute("pedidosControlCalidad", pedidosControlCalidad);
            request.setAttribute("pedidosListoEntrega", pedidosListoEntrega);

            request.getRequestDispatcher("kanbanDeliverista.jsp").forward(request, response);

        } catch (Exception e) {
            System.err.println("❌ Error en KanbanDeliverista: " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect("panelDeliverista.jsp");
        }
    }
}