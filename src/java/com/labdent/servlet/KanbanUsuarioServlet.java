package com.labdent.servlet;

import com.labdent.dao.PedidoDAO;
import com.labdent.model.Pedido;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/KanbanUsuarioServlet")
public class KanbanUsuarioServlet extends HttpServlet {

    private final PedidoDAO pedidoDAO = new PedidoDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuarioId") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        int usuarioId = (int) session.getAttribute("usuarioId");
        
        // 🔍 DEBUG: Ver qué usuario está consultando
        System.out.println("🔍 [KanbanUsuarioServlet] Usuario ID: " + usuarioId);

        // Obtener todos los pedidos del usuario
        List<Pedido> todosPedidos = pedidoDAO.obtenerPedidosPorCliente(usuarioId);
        
        // 🔍 DEBUG: Ver cuántos pedidos se encontraron
        System.out.println("🔍 [KanbanUsuarioServlet] Total pedidos encontrados: " + 
                          (todosPedidos != null ? todosPedidos.size() : 0));
        
        if (todosPedidos != null && !todosPedidos.isEmpty()) {
            for (Pedido p : todosPedidos) {
                System.out.println("  📦 Pedido: " + p.getCodigoUnico() + 
                                 " | Estado: " + p.getEstadoActual() + 
                                 " | Usuario ID: " + p.getUsuarioId());
            }
        }

        // Separar por estado
        List<Pedido> pedidosRecepcion = new ArrayList<>();
        List<Pedido> pedidosParalelizado = new ArrayList<>();
        List<Pedido> pedidosDisenoCad = new ArrayList<>();
        List<Pedido> pedidosProduccionCam = new ArrayList<>();
        List<Pedido> pedidosCeramica = new ArrayList<>();
        List<Pedido> pedidosControlCalidad = new ArrayList<>();
        List<Pedido> pedidosListoEntrega = new ArrayList<>();

        // Clasificar pedidos según su estado
        if (todosPedidos != null) {
            for (Pedido pedido : todosPedidos) {
                String estado = pedido.getEstadoActual();
                
                // 🔍 DEBUG: Ver el estado antes del switch
                System.out.println("  🔄 Clasificando pedido " + pedido.getCodigoUnico() + 
                                 " con estado: [" + estado + "]");
                
                if (estado == null) {
                    System.out.println("  ⚠️ Estado NULL para pedido " + pedido.getCodigoUnico());
                    continue;
                }

                switch (estado.trim()) {  // 👈 IMPORTANTE: trim() para eliminar espacios
                    case "RECEPCION":
                        pedidosRecepcion.add(pedido);
                        System.out.println("    ✅ Añadido a RECEPCION");
                        break;
                    case "PARALELIZADO":
                        pedidosParalelizado.add(pedido);
                        System.out.println("    ✅ Añadido a PARALELIZADO");
                        break;
                    case "DISENO_CAD":
                        pedidosDisenoCad.add(pedido);
                        System.out.println("    ✅ Añadido a DISENO_CAD");
                        break;
                    case "PRODUCCION_CAM":
                        pedidosProduccionCam.add(pedido);
                        System.out.println("    ✅ Añadido a PRODUCCION_CAM");
                        break;
                    case "CERAMICA":
                        pedidosCeramica.add(pedido);
                        System.out.println("    ✅ Añadido a CERAMICA");
                        break;
                    case "CONTROL_CALIDAD":
                        pedidosControlCalidad.add(pedido);
                        System.out.println("    ✅ Añadido a CONTROL_CALIDAD");
                        break;
                    case "LISTO_ENTREGA":
                        pedidosListoEntrega.add(pedido);
                        System.out.println("    ✅ Añadido a LISTO_ENTREGA");
                        break;
                    default:
                        System.out.println("    ❌ Estado no reconocido: [" + estado + "]");
                        break;
                }
            }
        }

        // 🔍 DEBUG: Ver cuántos pedidos hay en cada columna
        System.out.println("\n📊 Resumen por columna:");
        System.out.println("  RECEPCION: " + pedidosRecepcion.size());
        System.out.println("  PARALELIZADO: " + pedidosParalelizado.size());
        System.out.println("  DISENO_CAD: " + pedidosDisenoCad.size());
        System.out.println("  PRODUCCION_CAM: " + pedidosProduccionCam.size());
        System.out.println("  CERAMICA: " + pedidosCeramica.size());
        System.out.println("  CONTROL_CALIDAD: " + pedidosControlCalidad.size());
        System.out.println("  LISTO_ENTREGA: " + pedidosListoEntrega.size());

        // Enviar las listas separadas al JSP
        request.setAttribute("pedidosRecepcion", pedidosRecepcion);
        request.setAttribute("pedidosParalelizado", pedidosParalelizado);
        request.setAttribute("pedidosDisenoCad", pedidosDisenoCad);
        request.setAttribute("pedidosProduccionCam", pedidosProduccionCam);
        request.setAttribute("pedidosCeramica", pedidosCeramica);
        request.setAttribute("pedidosControlCalidad", pedidosControlCalidad);
        request.setAttribute("pedidosListoEntrega", pedidosListoEntrega);

        request.getRequestDispatcher("kanbanUsuario.jsp").forward(request, response);
    }
}