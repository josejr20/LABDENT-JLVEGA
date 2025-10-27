<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.labdent.model.Usuario" %>
<%@ page import="com.labdent.model.Pedido" %>
<%@ page import="java.util.List" %>
<%
    Usuario usuario = (Usuario) session.getAttribute("usuario");
    if (usuario == null || !usuario.isOdontologo()) {
        response.sendRedirect("login");
        return;
    }
    
    List<Pedido> pedidos = (List<Pedido>) request.getAttribute("pedidos");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Mis Pedidos - LABDENT JLVEGA</title>
    <link rel="stylesheet" href="css/styles.css">
</head>
<body>
    <!-- Header -->
    <header class="header-dashboard">
        <div class="container">
            <div class="logo">
                <h1>LABDENT JLVEGA</h1>
            </div>
            <nav class="nav">
                <a href="misPedidos" class="active">Mis Pedidos</a>
                <a href="registro-pedido">Nuevo Pedido</a>
                <div class="user-menu">
                    <span><%= usuario.getNombreCompleto() %></span>
                    <a href="logout" class="btn-logout">Cerrar Sesión</a>
                </div>
            </nav>
        </div>
    </header>

    <!-- Contenido Principal -->
    <main class="main-content">
        <div class="container">
            <h2>Mis Pedidos</h2>
            <p class="subtitle">Seguimiento en tiempo real de todos tus trabajos</p>

            <div class="actions-bar">
                <a href="registro-pedido" class="btn btn-primary">+ Nuevo Pedido</a>
            </div>

            <!-- Tabla de Pedidos -->
            <div class="table-container">
                <table class="table-pedidos">
                    <thead>
                        <tr>
                            <th>Código</th>
                            <th>Paciente</th>
                            <th>Tipo/Material</th>
                            <th>Estado</th>
                            <th>Fecha Ingreso</th>
                            <th>Fecha Compromiso</th>
                            <th>Estado Entrega</th>
                            <th>Acciones</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% if (pedidos != null && !pedidos.isEmpty()) { %>
                            <% for (Pedido p : pedidos) { %>
                            <tr>
                                <td><strong><%= p.getCodigoUnico() %></strong></td>
                                <td><%= p.getNombrePaciente() %></td>
                                <td>
                                    <%= p.getTipoProtesis() %><br>
                                    <small><%= p.getMaterial() %></small>
                                </td>
                                <td>
                                    <span class="badge-estado" style="background-color: <%= p.getColorEstado() %>;">
                                        <%= p.getEstadoDescripcion() %>
                                    </span>
                                </td>
                                <td><%= p.getFechaIngreso() %></td>
                                <td><%= p.getFechaCompromiso() %></td>
                                <td>
                                    <% if ("ENTREGADO".equals(p.getEstadoActual())) { %>
                                        <span class="badge-success">Entregado</span>
                                    <% } else if (p.isAtrasado()) { %>
                                        <span class="badge-danger">Atrasado (<%= Math.abs(p.getDiasRestantes()) %> días)</span>
                                    <% } else { %>
                                        <span class="badge-info"><%= p.getDiasRestantes() %> días</span>
                                    <% } %>
                                </td>
                                <td>
                                    <a href="detalle-pedido?id=<%= p.getId() %>" class="btn-small">Ver Detalle</a>
                                </td>
                            </tr>
                            <% } %>
                        <% } else { %>
                            <tr>
                                <td colspan="8" class="text-center">
                                    No tienes pedidos registrados.<br>
                                    <a href="registro-pedido" class="btn btn-primary">Registrar Primer Pedido</a>
                                </td>
                            </tr>
                        <% } %>
                    </tbody>
                </table>
            </div>
        </div>
    </main>

    <!-- Footer -->
    <footer class="footer">
        <div class="container">
            <p>&copy; 2025 LABDENT JLVEGA</p>
        </div>
    </footer>
</body>
</html>