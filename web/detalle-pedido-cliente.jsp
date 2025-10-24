<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.labdent.dao.PedidoDetalleDAO" %>
<%@ page import="com.labdent.model.PedidoArchivo" %>
<%@ page import="com.labdent.model.PedidoHistorial" %>
<%@ page import="com.labdent.dao.PedidoDAO" %>
<%@ page import="com.labdent.model.Pedido" %>

<%
    // Verificar sesión
    Object usuarioIdObj = session.getAttribute("idUsuario");
    String rolUsuario = (String) session.getAttribute("rol");
    if (usuarioIdObj == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    int idUsuarioLogueado = (int) usuarioIdObj;

    // Obtener parámetro pedidoId
    String pedidoIdParam = request.getParameter("pedidoId");
    if (pedidoIdParam == null) {
        response.sendRedirect("mis-pedidos.jsp?error=sin_id");
        return;
    }
    int pedidoId = Integer.parseInt(pedidoIdParam);

    // Buscar pedido
    PedidoDAO pedidoDAO = new PedidoDAO();
    Pedido pedido = pedidoDAO.obtenerPedidoPorId(pedidoId);
    if (pedido == null) {
        response.sendRedirect("mis-pedidos.jsp?error=no_existe");
        return;
    }

    // Validar acceso por rol
    boolean autorizado = "ADMIN".equals(rolUsuario) || 
        ("ODONTOLOGO".equals(rolUsuario) && pedido.getOdontologoId() == idUsuarioLogueado);
    if (!autorizado) {
        response.sendRedirect("mis-pedidos.jsp?error=no_autorizado");
        return;
    }

    // Obtener historial y archivos usando DAO
    PedidoDetalleDAO detalleDAO = new PedidoDetalleDAO();
    List<PedidoHistorial> historial = detalleDAO.obtenerHistorial(pedidoId);
    List<PedidoArchivo> archivos = detalleDAO.obtenerArchivos(pedidoId);
%>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Detalle del Pedido</title>
    <link rel="stylesheet" href="css/detallePedido.css">
</head>
<body>
<div class="container">
    <h2>Detalle del Pedido: <%= pedido.getCodigoUnico() %></h2>

    <div class="card">
        <p><strong>Paciente:</strong> <%= pedido.getNombrePaciente() %></p>
        <p><strong>Tipo:</strong> <%= pedido.getTipoProtesis() %></p>
        <p><strong>Material:</strong> <%= pedido.getMaterial() %></p>
        <p><strong>Fecha Compromiso:</strong> <%= pedido.getFechaCompromiso() %></p>
        <p><strong>Estado Actual:</strong>
            <span class="estado"><%= pedido.getEstadoActual() %></span>
        </p>
    </div>

    <section class="card">
        <h3>Historial del Proceso</h3>
        <table>
            <tr>
                <th>Fecha</th>
                <th>Etapa</th>
                <th>Comentario</th>
            </tr>
            <% if (historial == null || historial.isEmpty()) { %>
                <tr><td colspan="3">Aún no hay historial</td></tr>
            <% } else {
                for (PedidoHistorial h : historial) { %>
                <tr>
                    <td><%= h.getFecha() %></td>
                    <td><%= h.getEstado() %></td>
                    <td><%= h.getComentario() %></td>
                </tr>
                <% }
            } %>
        </table>
    </section>

    <section class="card">
        <h3>Archivos / Evidencias</h3>
        <% if (archivos == null || archivos.isEmpty()) { %>
            <p>No hay archivos adjuntos.</p>
        <% } else { %>
            <ul>
                <% for (PedidoArchivo a : archivos) { %>
                <li>
                    <a href="<%= a.getRutaArchivo() %>" target="_blank">
                        <%= a.getNombreArchivo() %>
                    </a>
                    <span>(<%= a.getTipo() %>)</span>
                </li>
                <% } %>
            </ul>
        <% } %>
    </section>

    <a href="panel-usuario.jsp" class="btn-volver">Volver</a>
</div>
</body>
</html>
