package com.labdent.servlet;

import com.labdent.dao.PedidoDAO;
import com.labdent.model.Pedido;
import com.labdent.model.PedidoArchivo;
import com.labdent.model.PedidoHistorial;
import com.labdent.dao.PedidoHistorialDAO;
import com.labdent.dao.PedidoArchivoDAO;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/DetallePedidoCliente")
public class DetallePedidoClienteServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pedidoIdStr = request.getParameter("pedidoId");

        if (pedidoIdStr == null) {
            response.sendRedirect("panel-cliente.jsp");
            return;
        }

        int pedidoId = Integer.parseInt(pedidoIdStr);

        PedidoDAO pedidoDAO = new PedidoDAO();
        Pedido pedido = pedidoDAO.obtenerPedidoPorId(pedidoId);
        PedidoHistorialDAO historialDAO = new PedidoHistorialDAO();
        List<PedidoHistorial> historial = historialDAO.listarHistorialPorPedido(pedidoId);

        PedidoArchivoDAO archivoDAO = new PedidoArchivoDAO();
        List<PedidoArchivo> archivos = archivoDAO.listarArchivosPorPedido(pedidoId);

        request.setAttribute("pedido", pedido);
        request.setAttribute("historial", historial);
        request.setAttribute("archivos", archivos);

        RequestDispatcher dispatcher = request.getRequestDispatcher("detalle-pedido-cliente.jsp");
        dispatcher.forward(request, response);
    }
}
