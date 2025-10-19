<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.labdent.model.Usuario" %>
<%@ page import="com.labdent.model.Reporte" %>
<%@ page import="com.labdent.model.Pedido" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.DecimalFormat" %>
<%
    Usuario usuario = (Usuario) session.getAttribute("usuario");
    if (usuario == null || !"ADMIN".equals(usuario.getRol())) {
        response.sendRedirect("login");
        return;
    }
    
    Reporte reporte = (Reporte) request.getAttribute("reporte");
    List<Pedido> pedidosAtrasados = (List<Pedido>) request.getAttribute("pedidosAtrasados");
    List<Pedido> ultimosPedidos = (List<Pedido>) request.getAttribute("ultimosPedidos");
    
    DecimalFormat df = new DecimalFormat("#.##");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard - LABDENT JLVEGA</title>
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
                <a href="dashboard" class="active">Dashboard</a>
                <a href="kanban">Tablero Kanban</a>
                <a href="reportes">Reportes</a>
                <a href="admin-usuarios.jsp">Usuarios</a>
                <div class="user-menu">
                    <span>👤 <%= usuario.getNombreCompleto() %></span>
                    <a href="logout" class="btn-logout">Cerrar Sesión</a>
                </div>
            </nav>
        </div>
    </header>

    <!-- Contenido Principal -->
    <main class="dashboard-content">
        <div class="container">
            <h2>Panel de Control - Administrador</h2>

            <!-- Tarjetas de Resumen -->
            <div class="stats-grid">
                <div class="stat-card">
                    <div class="stat-icon">📦</div>
                    <div class="stat-info">
                        <h3>Total Pedidos</h3>
                        <p class="stat-number"><%= reporte.getTotalPedidos() %></p>
                    </div>
                </div>

                <div class="stat-card">
                    <div class="stat-icon">⏳</div>
                    <div class="stat-info">
                        <h3>En Proceso</h3>
                        <p class="stat-number"><%= reporte.getPedidosEnProceso() %></p>
                    </div>
                </div>

                <div class="stat-card">
                    <div class="stat-icon">✅</div>
                    <div class="stat-info">
                        <h3>Entregados</h3>
                        <p class="stat-number"><%= reporte.getPedidosEntregados() %></p>
                    </div>
                </div>

                <div class="stat-card alert-card">
                    <div class="stat-icon">⚠️</div>
                    <div class="stat-info">
                        <h3>Atrasados</h3>
                        <p class="stat-number"><%= reporte.getPedidosAtrasados() %></p>
                    </div>
                </div>

                <div class="stat-card">
                    <div class="stat-icon">⏱️</div>
                    <div class="stat-info">
                        <h3>Tiempo Promedio</h3>
                        <p class="stat-number"><%= df.format(reporte.getTiempoPromedioEntrega()) %> días</p>
                    </div>
                </div>

                <div class="stat-card">
                    <div class="stat-icon">📊</div>
                    <div class="stat-info">
                        <h3>Cumplimiento</h3>
                        <p class="stat-number"><%= df.format(reporte.getTasaCumplimiento()) %>%</p>
                    </div>
                </div>
            </div>

            <!-- Distribución por Estado -->
            <div class="section-card">
                <h3>Distribución de Pedidos por Estado</h3>
                <div class="estado-grid">
                    <div class="estado-item">
                        <span class="estado-label" style="background-color: #3498db;">Recepción</span>
                        <span class="estado-count"><%= reporte.getPedidosRecepcion() %></span>
                    </div>
                    <div class="estado-item">
                        <span class="estado-label" style="background-color: #9b59b6;">Paralelizado</span>
                        <span class="estado-count"><%= reporte.getPedidosParalelizado() %></span>
                    </div>
                    <div class="estado-item">
                        <span class="estado-label" style="background-color: #1abc9c;">Diseño CAD</span>
                        <span class="estado-count"><%= reporte.getPedidosDisenoCad() %></span>
                    </div>
                    <div class="estado-item">
                        <span class="estado-label" style="background-color: #f39c12;">Producción CAM</span>
                        <span class="estado-count"><%= reporte.getPedidosProduccionCam() %></span>
                    </div>
                    <div class="estado-item">
                        <span class="estado-label" style="background-color: #e74c3c;">Cerámica</span>
                        <span class="estado-count"><%= reporte.getPedidosCeramica() %></span>
                    </div>
                    <div class="estado-item">
                        <span class="estado-label" style="background-color: #16a085;">Control Calidad</span>
                        <span class="estado-count"><%= reporte.getPedidosControlCalidad() %></span>
                    </div>
                    <div class="estado-item">
                        <span class="estado-label" style="background-color: #27ae60;">Listo Entrega</span>
                        <span class="estado-count"><%= reporte.getPedidosListoEntrega() %></span>
                    </div>
                </div>
            </div>

            <!-- Pedidos Atrasados -->
            <% if (pedidosAtrasados != null && !pedidosAtrasados.isEmpty()) { %>
            <div class="section-card alert-section">
                <h3>⚠️ Pedidos Atrasados (Requieren Atención Inmediata)</h3>
                <table class="table-pedidos">
                    <thead>
                        <tr>
                            <th>Código</th>
                            <th>Paciente</th>
                            <th>Odontólogo</th>
                            <th>Estado</th>
                            <th>Fecha Compromiso</th>
                            <th>Días Atrasados</th>
                            <th>Acciones</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% for (Pedido p : pedidosAtrasados) { %>
                        <tr>
                            <td><strong><%= p.getCodigoUnico() %></strong></td>
                            <td><%= p.getNombrePaciente() %></td>
                            <td><%= p.getNombreOdontologo() %></td>
                            <td><span class="badge-estado" style="background-color: <%= p.getColorEstado() %>;">
                                <%= p.getEstadoDescripcion() %>
                            </span></td>
                            <td><%= p.getFechaCompromiso() %></td>
                            <td class="text-danger"><strong><%= Math.abs(p.getDiasRestantes()) %> días</strong></td>
                            <td>
                                <a href="detalle-pedido?id=<%= p.getId() %>" class="btn-small">Ver Detalle</a>
                            </td>
                        </tr>
                        <% } %>
                    </tbody>
                </table>
            </div>
            <% } %>

            <!-- Últimos Pedidos -->
            <div class="section-card">
                <h3>Últimos Pedidos Registrados</h3>
                <table class="table-pedidos">
                    <thead>
                        <tr>
                            <th>Código</th>
                            <th>Paciente</th>
                            <th>Tipo</th>
                            <th>Material</th>
                            <th>Estado</th>
                            <th>Fecha Ingreso</th>
                            <th>Acciones</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% if (ultimosPedidos != null && !ultimosPedidos.isEmpty()) { %>
                            <% for (Pedido p : ultimosPedidos) { %>
                            <tr>
                                <td><strong><%= p.getCodigoUnico() %></strong></td>
                                <td><%= p.getNombrePaciente() %></td>
                                <td><%= p.getTipoProtesis() %></td>
                                <td><%= p.getMaterial() %></td>
                                <td><span class="badge-estado" style="background-color: <%= p.getColorEstado() %>;">
                                    <%= p.getEstadoDescripcion() %>
                                </span></td>
                                <td><%= p.getFechaIngreso() %></td>
                                <td>
                                    <a href="detalle-pedido?id=<%= p.getId() %>" class="btn-small">Ver</a>
                                </td>
                            </tr>
                            <% } %>
                        <% } else { %>
                            <tr>
                                <td colspan="7" class="text-center">No hay pedidos registrados</td>
                            </tr>
                        <% } %>
                    </tbody>
                </table>
            </div>

            <!-- Accesos Rápidos -->
            <div class="quick-actions">
                <h3>Accesos Rápidos</h3>
                <div class="actions-grid">
                    <a href="kanban" class="action-card">
                        <div class="action-icon">📋</div>
                        <h4>Tablero Kanban</h4>
                        <p>Gestionar flujo de trabajo</p>
                    </a>
                    <a href="registro-pedido" class="action-card">
                        <div class="action-icon">➕</div>
                        <h4>Nuevo Pedido</h4>
                        <p>Registrar trabajo nuevo</p>
                    </a>
                    <a href="reportes" class="action-card">
                        <div class="action-icon">📊</div>
                        <h4>Reportes</h4>
                        <p>Ver estadísticas detalladas</p>
                    </a>
                    <a href="admin-usuarios.jsp" class="action-card">
                        <div class="action-icon">👥</div>
                        <h4>Usuarios</h4>
                        <p>Administrar usuarios</p>
                    </a>
                </div>
            </div>
        </div>
    </main>

    <!-- Footer -->
    <footer class="footer">
        <div class="container">
            <p>&copy; 2025 LABDENT JLVEGA - Sistema de Gestión de Pedidos v2.0</p>
        </div>
    </footer>
</body>
</html>