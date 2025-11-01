package com.labdent.controller;

import com.labdent.dao.PedidoDAO;
import com.labdent.dao.TransicionEstadoDAO;
import com.labdent.dao.NotificacionDAO;
import com.labdent.model.Pedido;
import com.labdent.model.Usuario;
import com.labdent.service.NotificacionService;
import com.labdent.service.PedidoService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller para el panel de técnicos, odontólogos y ceramistas
 * Implementa el patrón MVC
 */
@WebServlet("/PanelTecnico")
public class PanelTecnicoController extends HttpServlet {
    
    private static final Logger logger = LoggerFactory.getLogger(PanelTecnicoController.class);
    
    private PedidoService pedidoService;
    
    @Override
    public void init() throws ServletException {
        super.init();
        
        // Dependency Injection (manual para simplificar, podrías usar un framework DI)
        PedidoDAO pedidoDAO = new PedidoDAO();
        TransicionEstadoDAO transicionDAO = new TransicionEstadoDAO();
        NotificacionDAO notificacionDAO = new NotificacionDAO();
        NotificacionService notificacionService = new NotificacionService(notificacionDAO);
        
        this.pedidoService = new PedidoService(pedidoDAO, transicionDAO, notificacionService);
        
        logger.info("PanelTecnicoController inicializado correctamente");
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null) {
            logger.warn("Sesión no encontrada, redirigiendo a login");
            response.sendRedirect("login.jsp");
            return;
        }
        
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            logger.warn("Usuario no encontrado en sesión");
            response.sendRedirect("login.jsp");
            return;
        }
        
        // Validar roles permitidos
        String rol = usuario.getRol();
        if (!esRolPermitido(rol)) {
            logger.warn("Acceso denegado para rol: {}", rol);
            response.sendRedirect("dashboard");
            return;
        }
        
        logger.info("Usuario {} ({}) accediendo al panel técnico", 
                   usuario.getNombreCompleto(), rol);
        
        try {
            // Obtener pedidos según el estado actual de cada uno
            PedidoDAO pedidoDAO = new PedidoDAO();
            List<Pedido> todosPedidos = pedidoDAO.listarTodos();
            
            // Separar por estado
            List<Pedido> pedidosRecepcion = new ArrayList<>();
            List<Pedido> pedidosParalelizado = new ArrayList<>();
            List<Pedido> pedidosDisenoCad = new ArrayList<>();
            List<Pedido> pedidosProduccionCam = new ArrayList<>();
            List<Pedido> pedidosCeramica = new ArrayList<>();
            List<Pedido> pedidosControlCalidad = new ArrayList<>();
            List<Pedido> pedidosListoEntrega = new ArrayList<>();
            
            for (Pedido p : todosPedidos) {
                if (p.getEstadoActual() == null || "ENTREGADO".equals(p.getEstadoActual())) {
                    continue;
                }
                
                switch (p.getEstadoActual()) {
                    case "RECEPCION":
                        pedidosRecepcion.add(p);
                        break;
                    case "PARALELIZADO":
                        pedidosParalelizado.add(p);
                        break;
                    case "DISENO_CAD":
                        pedidosDisenoCad.add(p);
                        break;
                    case "PRODUCCION_CAM":
                        pedidosProduccionCam.add(p);
                        break;
                    case "CERAMICA":
                        pedidosCeramica.add(p);
                        break;
                    case "CONTROL_CALIDAD":
                        pedidosControlCalidad.add(p);
                        break;
                    case "LISTO_ENTREGA":
                        pedidosListoEntrega.add(p);
                        break;
                }
            }
            
            // Estadísticas
            int totalEnProceso = todosPedidos.size();
            int misPedidosAsignados = contarPedidosAsignados(todosPedidos, usuario.getId());
            
            logger.debug("Total pedidos: {}, Asignados: {}", totalEnProceso, misPedidosAsignados);
            
            // Enviar datos a la vista
            request.setAttribute("pedidosRecepcion", pedidosRecepcion);
            request.setAttribute("pedidosParalelizado", pedidosParalelizado);
            request.setAttribute("pedidosDisenoCad", pedidosDisenoCad);
            request.setAttribute("pedidosProduccionCam", pedidosProduccionCam);
            request.setAttribute("pedidosCeramica", pedidosCeramica);
            request.setAttribute("pedidosControlCalidad", pedidosControlCalidad);
            request.setAttribute("pedidosListoEntrega", pedidosListoEntrega);
            request.setAttribute("totalEnProceso", totalEnProceso);
            request.setAttribute("misPedidosAsignados", misPedidosAsignados);
            
            request.getRequestDispatcher("panelTecnico.jsp").forward(request, response);
            
        } catch (Exception e) {
            logger.error("Error al cargar panel técnico", e);
            request.setAttribute("error", "Error al cargar los datos: " + e.getMessage());
            request.getRequestDispatcher("error.jsp").forward(request, response);
        }
    }
    
    private boolean esRolPermitido(String rol) {
        return "ODONTOLOGO".equals(rol) || 
               "TECNICO".equals(rol) || 
               "CERAMISTA".equals(rol) ||
               "ADMIN".equals(rol);
    }
    
    private int contarPedidosAsignados(List<Pedido> pedidos, int usuarioId) {
        return (int) pedidos.stream()
            .filter(p -> p.getResponsableActual() != null && p.getResponsableActual() == usuarioId)
            .count();
    }
}