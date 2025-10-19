<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.labdent.model.Usuario" %>
<%@ page import="com.labdent.model.Reporte" %>
<%@ page import="java.text.DecimalFormat" %>
<%
    Usuario usuario = (Usuario) session.getAttribute("usuario");
    if (usuario == null || !usuario.isAdmin()) {
        response.sendRedirect("login");
        return;
    }
    
    Reporte reporte = (Reporte) request.getAttribute("reporte");
    DecimalFormat df = new DecimalFormat("#.##");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Reportes - LABDENT JLVEGA</title>
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
                <a href="dashboard">Dashboard</a>
                <a href="kanban">Kanban</a>
                <a href="reportes" class="active">Reportes</a>
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
            <h2>Reportes de Productividad</h2>

            <!-- Métricas Principales -->
            <div class="reportes-grid">
                <div class="reporte-card">
                    <h3>Total de Pedidos</h3>
                    <p class="reporte-numero"><%= reporte.getTotalPedidos() %></p>
                </div>
                <div class="reporte-card">
                    <h3>En Proceso</h3>
                    <p class="reporte-numero"><%= reporte.getPedidosEnProceso() %></p>
                </div>
                <div class="reporte-card">
                    <h3>Entregados</h3>
                    <p class="reporte-numero"><%= reporte.getPedidosEntregados() %></p>
                </div>
                <div class="reporte-card alert">
                    <h3>Atrasados</h3>
                    <p class="reporte-numero"><%= reporte.getPedidosAtrasados() %></p>
                </div>
            </div>

            <!-- Indicadores de Rendimiento -->
            <div class="kpi-section">
                <h3>Indicadores de Rendimiento (KPIs)</h3>
                <div class="kpi-grid">
                    <div class="kpi-card">
                        <h4>Tiempo Promedio de Entrega</h4>
                        <p class="kpi-value"><%= df.format(reporte.getTiempoPromedioEntrega()) %> días</p>
                        <p class="kpi-desc">Desde ingreso hasta entrega</p>
                    </div>
                    <div class="kpi-card">
                        <h4>Tasa de Cumplimiento</h4>
                        <p class="kpi-value"><%= df.format(reporte.getTasaCumplimiento()) %>%</p>
                        <p class="kpi-desc">Pedidos entregados a tiempo</p>
                    </div>
                </div>
            </div>

            <!-- Gráfico de Distribución -->
            <div class="chart-section">
                <h3>Distribución por Estado</h3>
                <canvas id="chartEstados" width="400" height="200"></canvas>
            </div>

            <div class="export-section">
                <button onclick="window.print()" class="btn btn-secondary">Imprimir Reporte</button>
                <button onclick="alert('Funcionalidad de exportación en desarrollo')" class="btn btn-outline">Exportar PDF</button>
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