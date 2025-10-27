<%@ page contentType="text/html;charset=UTF-8" language="java" session="true" %>
<%@ page import="java.util.List" %>
<%@ page import="com.labdent.model.Pedido" %>

<%
    List<Pedido> pedidosRecepcion = (List<Pedido>) request.getAttribute("pedidosRecepcion");
    List<Pedido> pedidosParalelizado = (List<Pedido>) request.getAttribute("pedidosParalelizado");
    List<Pedido> pedidosDisenoCad = (List<Pedido>) request.getAttribute("pedidosDisenoCad");
    List<Pedido> pedidosProduccionCam = (List<Pedido>) request.getAttribute("pedidosProduccionCam");
    List<Pedido> pedidosCeramica = (List<Pedido>) request.getAttribute("pedidosCeramica");
    List<Pedido> pedidosControlCalidad = (List<Pedido>) request.getAttribute("pedidosControlCalidad");
    List<Pedido> pedidosListoEntrega = (List<Pedido>) request.getAttribute("pedidosListoEntrega");
    
%>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Mis Pedidos</title>
    <link rel="stylesheet" href="css/kanban.css">
</head>
<body>

<h2 style="text-align:center;">🔥 Mis Pedidos en Proceso 🔥</h2>

<div class="kanban-board">

    <%-- === COLUMNA RECEPCIÓN === --%>
    <div class="kanban-column">
        <h3>Recepción</h3>
        <%
            if (pedidosRecepcion != null && !pedidosRecepcion.isEmpty()) {
                for (Pedido p : pedidosRecepcion) {
        %>
                    <div class="pedido-card">
                        <strong><%= p.getCodigoUnico() %></strong><br>
                        Cliente: <%= p.getNombrePaciente() %><br>
                        Trabajo: <%= p.getTipoProtesis() %><br>
                        Compromiso: <%= p.getFechaCompromiso() %><br>
                    </div>
        <%
                }
            } else {
        %>
            <p>Sin pedidos aquí 😴</p>
        <%
            }
        %>
    </div>

    <%-- === COLUMNA PARALELIZADO === --%>
    <div class="kanban-column">
        <h3>Paralelizado</h3>
        <%
            if (pedidosParalelizado != null && !pedidosParalelizado.isEmpty()) {
                for (Pedido p : pedidosParalelizado) {
        %>
                    <div class="pedido-card">
                        <strong><%= p.getCodigoUnico() %></strong><br>
                        Cliente: <%= p.getNombrePaciente() %><br>
                        Trabajo: <%= p.getTipoProtesis() %><br>
                        Compromiso: <%= p.getFechaCompromiso() %><br>
                    </div>
        <%
                }
            } else {
        %>
            <p>Sin pedidos aquí 😴</p>
        <%
            }
        %>
    </div>

    <%-- === COLUMNA DISEÑO CAD === --%>
    <div class="kanban-column">
        <h3>Diseño CAD</h3>
        <%
            if (pedidosDisenoCad != null && !pedidosDisenoCad.isEmpty()) {
                for (Pedido p : pedidosDisenoCad) {
        %>
                    <div class="pedido-card">
                        <strong><%= p.getCodigoUnico() %></strong><br>
                        Cliente: <%= p.getNombrePaciente() %><br>
                        Trabajo: <%= p.getTipoProtesis() %><br>
                        Compromiso: <%= p.getFechaCompromiso() %><br>
                    </div>
        <%
                }
            } else {
        %>
            <p>Sin pedidos aquí 😴</p>
        <%
            }
        %>
    </div>

    <%-- === COLUMNA PRODUCCIÓN CAM === --%>
    <div class="kanban-column">
        <h3>Producción CAM</h3>
        <%
            if (pedidosProduccionCam != null && !pedidosProduccionCam.isEmpty()) {
                for (Pedido p : pedidosProduccionCam) {
        %>
                    <div class="pedido-card">
                        <strong><%= p.getCodigoUnico() %></strong><br>
                        Cliente: <%= p.getNombrePaciente() %><br>
                        Trabajo: <%= p.getTipoProtesis() %><br>
                        Compromiso: <%= p.getFechaCompromiso() %><br>
                    </div>
        <%
                }
            } else {
        %>
            <p>Sin pedidos aquí 😴</p>
        <%
            }
        %>
    </div>

    <%-- === COLUMNA CERÁMICA === --%>
    <div class="kanban-column">
        <h3>Cerámica</h3>
        <%
            if (pedidosCeramica != null && !pedidosCeramica.isEmpty()) {
                for (Pedido p : pedidosCeramica) {
        %>
                    <div class="pedido-card">
                        <strong><%= p.getCodigoUnico() %></strong><br>
                        Cliente: <%= p.getNombrePaciente() %><br>
                        Trabajo: <%= p.getTipoProtesis() %><br>
                        Compromiso: <%= p.getFechaCompromiso() %><br>
                    </div>
        <%
                }
            } else {
        %>
            <p>Sin pedidos aquí 😴</p>
        <%
            }
        %>
    </div>

    <%-- === COLUMNA CONTROL DE CALIDAD === --%>
    <div class="kanban-column">
        <h3>Control Calidad</h3>
        <%
            if (pedidosControlCalidad != null && !pedidosControlCalidad.isEmpty()) {
                for (Pedido p : pedidosControlCalidad) {
        %>
                    <div class="pedido-card">
                        <strong><%= p.getCodigoUnico() %></strong><br>
                        Cliente: <%= p.getNombrePaciente() %><br>
                        Trabajo: <%= p.getTipoProtesis() %><br>
                        Compromiso: <%= p.getFechaCompromiso() %><br>
                    </div>
        <%
                }
            } else {
        %>
            <p>Sin pedidos aquí 😴</p>
        <%
            }
        %>
    </div>

    <%-- === COLUMNA LISTO PARA ENTREGA === --%>
    <div class="kanban-column">
        <h3>Listo para Entrega</h3>
        <%
            if (pedidosListoEntrega != null && !pedidosListoEntrega.isEmpty()) {
                for (Pedido p : pedidosListoEntrega) {
        %>
                    <div class="pedido-card">
                        <strong><%= p.getCodigoUnico() %></strong><br>
                        Cliente: <%= p.getNombrePaciente() %><br>
                        Trabajo: <%= p.getTipoProtesis() %><br>
                        Compromiso: <%= p.getFechaCompromiso() %><br>
                    </div>
        <%
                }
            } else {
        %>
            <p>Sin pedidos aquí 😴</p>
        <%
            }
        %>
    </div>

</div>

</body>
</html>
