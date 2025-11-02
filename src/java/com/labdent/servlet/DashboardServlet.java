package com.labdent.servlet;

import com.labdent.dao.PedidoDAO;
import com.labdent.dao.ReporteDAO;
import com.labdent.model.Pedido;
import com.labdent.model.Reporte;
import com.labdent.model.Usuario;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {

    private PedidoDAO pedidoDAO;
    private ReporteDAO reporteDAO;

    @Override
    public void init() throws ServletException {
        pedidoDAO = new PedidoDAO();
        reporteDAO = new ReporteDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        // Validación de sesión
        if (session == null || session.getAttribute("usuario") == null) {
            response.sendRedirect("login");
            return;
        }

        Usuario usuario = (Usuario) session.getAttribute("usuario");

        // Solo el administrador puede acceder al dashboard
        if (!"ADMIN".equals(usuario.getRol())) {
            response.sendRedirect("index.jsp");
            return;
        }

        try {
            // === 1️⃣ Reporte general ===
            Reporte reporte = reporteDAO.generarReporteGeneral();
            request.setAttribute("reporte", reporte);

            // === 2️⃣ Pedidos atrasados ===
            List<Pedido> pedidosAtrasados = pedidoDAO.listarAtrasados();
            request.setAttribute("pedidosAtrasados", pedidosAtrasados);

            // === 3️⃣ Últimos pedidos ===
            List<Pedido> ultimosPedidos = pedidoDAO.listarTodos();
            if (ultimosPedidos != null && ultimosPedidos.size() > 10) {
                ultimosPedidos = ultimosPedidos.subList(0, 10);
            }
            request.setAttribute("ultimosPedidos", ultimosPedidos);

            // === 4️⃣ Pedidos por estado ===
            List<Pedido> pedidosRecepcion = pedidoDAO.listarPorEstado("RECEPCION");
            List<Pedido> pedidosParalelizado = pedidoDAO.listarPorEstado("PARALELIZADO");
            List<Pedido> pedidosDisenoCad = pedidoDAO.listarPorEstado("DISENO_CAD");
            List<Pedido> pedidosProduccionCam = pedidoDAO.listarPorEstado("PRODUCCION_CAM");
            List<Pedido> pedidosCeramica = pedidoDAO.listarPorEstado("CERAMICA");
            List<Pedido> pedidosControlCalidad = pedidoDAO.listarPorEstado("CONTROL_CALIDAD");
            List<Pedido> pedidosListoEntrega = pedidoDAO.listarPorEstado("LISTO_ENTREGA");

            // Evitar nulls
            if (pedidosRecepcion == null) pedidosRecepcion = new ArrayList<>();
            if (pedidosParalelizado == null) pedidosParalelizado = new ArrayList<>();
            if (pedidosDisenoCad == null) pedidosDisenoCad = new ArrayList<>();
            if (pedidosProduccionCam == null) pedidosProduccionCam = new ArrayList<>();
            if (pedidosCeramica == null) pedidosCeramica = new ArrayList<>();
            if (pedidosControlCalidad == null) pedidosControlCalidad = new ArrayList<>();
            if (pedidosListoEntrega == null) pedidosListoEntrega = new ArrayList<>();

            // Set de atributos para JSP
            request.setAttribute("pedidosRecepcion", pedidosRecepcion);
            request.setAttribute("pedidosParalelizado", pedidosParalelizado);
            request.setAttribute("pedidosDisenoCad", pedidosDisenoCad);
            request.setAttribute("pedidosProduccionCam", pedidosProduccionCam);
            request.setAttribute("pedidosCeramica", pedidosCeramica);
            request.setAttribute("pedidosControlCalidad", pedidosControlCalidad);
            request.setAttribute("pedidosListoEntrega", pedidosListoEntrega);

            // Logs opcionales para depuración
            System.out.println("📊 Dashboard - Datos cargados:");
            System.out.println("  📥 Recepción: " + pedidosRecepcion.size());
            System.out.println("  🔄 Paralelizado: " + pedidosParalelizado.size());
            System.out.println("  💻 Diseño CAD: " + pedidosDisenoCad.size());
            System.out.println("  ⚙️ Producción CAM: " + pedidosProduccionCam.size());
            System.out.println("  🎨 Cerámica: " + pedidosCeramica.size());
            System.out.println("  ✓ Control Calidad: " + pedidosControlCalidad.size());
            System.out.println("  📦 Listo Entrega: " + pedidosListoEntrega.size());
            System.out.println("  ⚠️ Atrasados: " + pedidosAtrasados.size());

            // === 5️⃣ Forward al JSP ===
            request.getRequestDispatcher("/dashboard.jsp")
                   .forward(request, response);

        } catch (Exception e) {
            System.err.println("❌ Error al cargar el dashboard: " + e.getMessage());
            e.printStackTrace();
            session.setAttribute("error", "Error al cargar el panel: " + e.getMessage());
            response.sendRedirect("error.jsp");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}