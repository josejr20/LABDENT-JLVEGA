<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.labdent.model.Usuario" %>
<%@ page import="com.labdent.model.Pedido" %>
<%@ page import="java.util.List" %>
<%
    Usuario usuario = (Usuario) session.getAttribute("usuario");
    if (usuario == null) {
        response.sendRedirect("login");
        return;
    }
    
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
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Tablero Kanban - LABDENT JLVEGA</title>
    <link rel="stylesheet" href="css/styles.css">
    <style>
        .kanban-board {
            display: flex;
            gap: 20px;
            overflow-x: auto;
            padding: 20px 0;
        }
        .kanban-column {
            min-width: 280px;
            background: #f8f9fa;
            border-radius: 8px;
            padding: 15px;
        }
        .kanban-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 15px;
            padding-bottom: 10px;
            border-bottom: 3px solid;
        }
        .kanban-title {
            font-weight: bold;
            font-size: 14px;
        }
        .kanban-count {
            background: #fff;
            padding: 3px 10px;
            border-radius: 12px;
            font-size: 12px;
            font-weight: bold;
        }
        .kanban-card {
            background: white;
            border-radius: 6px;
            padding: 12px;
            margin-bottom: 10px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            cursor: pointer;
            transition: transform 0.2s;
        }
        .kanban-card:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 8px rgba(0,0,0,0.15);
        }
        .card-code {
            font-weight: bold;
            color: #2c3e50;
            margin-bottom: 5px;
        }
        .card-patient {
            font-size: 13px;
            color: #555;
            margin-bottom: 3px;
        }
        .card-type {
            font-size: 12px;
            color: #777;
        }
        .card-date {
            font-size: 11px;
            color: #999;
            margin-top: 8px;
            padding-top: 8px;
            border-top: 1px solid #eee;
        }
        .card-atrasado {
            border-left: 4px solid #e74c3c;
        }
        .card-urgente {
            border-left: 4px solid #f39c12;
        }
    </style>
</head>
<body>
    <!-- Header -->
    <header class="header-dashboard">
        <div class="container-fluid">
            <div class="logo">
                <h1>LABDENT JLVEGA</h1>
            </div>
            <nav class="nav">
                <% if (usuario.isAdmin()) { %>
                    <a href="dashboard">Dashboard</a>
                <% } %>
                <a href="kanban" class="active">Tablero Kanban</a>
                <% if (usuario.isOdontologo()) { %>
                    <a href="mis-pedidos">Mis Pedidos</a>
                <% } %>
                <% if (usuario.isAdmin() || usuario.isOdontologo()) { %>
                    <a href="registro-pedido">Nuevo Pedido</a>
                <% } %>
                <div class="user-menu">
                    <span>👤 <%= usuario.getNombreCompleto() %> (<%= usuario.getRol() %>)</span>
                    <a href="logout" class="btn-logout">Cerrar Sesión</a>
                </div>
            </nav>
        </div>
    </header>

    <!-- Contenido Principal -->
    <main class="main-content">
        <div class="container-fluid">
            <h2>Tablero Kanban - Gestión de Pedidos (FIFO)</h2>
            <p class="subtitle">Los pedidos se organizan por orden de llegada en cada etapa</p>

            <!-- Mensajes -->
            <% if (request.getAttribute("success") != null) { %>
                <div class="alert alert-success">
                    <%= request.getAttribute("success") %>
                </div>
            <% } %>

            <!-- Tablero Kanban -->
            <div class="kanban-board">
                <!-- Columna 1: Recepción -->
                <div class="kanban-column">
                    <div class="kanban-header" style="border-color: #3498db;">
                        <span class="kanban-title">📥 RECEPCIÓN</span>
                        <span class="kanban-count"><%= pedidosRecepcion != null ? pedidosRecepcion.size() : 0 %></span>
                    </div>
                    <% if (pedidosRecepcion != null && !pedidosRecepcion.isEmpty()) { %>
                        <% for (Pedido p : pedidosRecepcion) { %>
                            <div class="kanban-card <%= p.isAtrasado() ? "card-atrasado" : "" %> <%= "URGENTE".equals(p.getPrioridad()) ? "card-urgente" : "" %>"
                                 onclick="verDetalle(<%= p.getId() %>)">
                                <div class="card-code"><%= p.getCodigoUnico() %></div>
                                <div class="card-patient"><%= p.getNombrePaciente() %></div>
                                <div class="card-type"><%= p.getTipoProtesis() %> - <%= p.getMaterial() %></div>
                                <div class="card-date">
                                    Compromiso: <%= p.getFechaCompromiso() %>
                                    <% if (p.isAtrasado()) { %>
                                        <br><strong style="color: #e74c3c;">⚠️ ATRASADO</strong>
                                    <% } %>
                                </div>
                            </div>
                        <% } %>
                    <% } else { %>
                        <p class="text-muted">No hay pedidos</p>
                    <% } %>
                </div>

<!-- Columna 2: Paralelizado -->
                <div class="kanban-column">
                    <div class="kanban-header" style="border-color: #9b59b6;">
                        <span class="kanban-title">🔄 PARALELIZADO</span>
                        <span class="kanban-count"><%= pedidosParalelizado != null ? pedidosParalelizado.size() : 0 %></span>
                    </div>
                    <% if (pedidosParalelizado != null && !pedidosParalelizado.isEmpty()) { %>
                        <% for (Pedido p : pedidosParalelizado) { %>
                            <div class="kanban-card <%= p.isAtrasado() ? "card-atrasado" : "" %>"
                                 onclick="verDetalle(<%= p.getId() %>)">
                                <div class="card-code"><%= p.getCodigoUnico() %></div>
                                <div class="card-patient"><%= p.getNombrePaciente() %></div>
                                <div class="card-type"><%= p.getTipoProtesis() %> - <%= p.getMaterial() %></div>
                                <div class="card-date">Compromiso: <%= p.getFechaCompromiso() %></div>
                            </div>
                        <% } %>
                    <% } else { %>
                        <p class="text-muted">No hay pedidos</p>
                    <% } %>
                </div>

                <!-- Columna 3: Diseño CAD -->
                <div class="kanban-column">
                    <div class="kanban-header" style="border-color: #1abc9c;">
                        <span class="kanban-title">💻 DISEÑO CAD</span>
                        <span class="kanban-count"><%= pedidosDisenoCad != null ? pedidosDisenoCad.size() : 0 %></span>
                    </div>
                    <% if (pedidosDisenoCad != null && !pedidosDisenoCad.isEmpty()) { %>
                        <% for (Pedido p : pedidosDisenoCad) { %>
                            <div class="kanban-card <%= p.isAtrasado() ? "card-atrasado" : "" %>"
                                 onclick="verDetalle(<%= p.getId() %>)">
                                <div class="card-code"><%= p.getCodigoUnico() %></div>
                                <div class="card-patient"><%= p.getNombrePaciente() %></div>
                                <div class="card-type"><%= p.getTipoProtesis() %> - <%= p.getMaterial() %></div>
                                <div class="card-date">Compromiso: <%= p.getFechaCompromiso() %></div>
                            </div>
                        <% } %>
                    <% } else { %>
                        <p class="text-muted">No hay pedidos</p>
                    <% } %>
                </div>

                <!-- Columna 4: Producción CAM -->
                <div class="kanban-column">
                    <div class="kanban-header" style="border-color: #f39c12;">
                        <span class="kanban-title">⚙️ PRODUCCIÓN CAM</span>
                        <span class="kanban-count"><%= pedidosProduccionCam != null ? pedidosProduccionCam.size() : 0 %></span>
                    </div>
                    <% if (pedidosProduccionCam != null && !pedidosProduccionCam.isEmpty()) { %>
                        <% for (Pedido p : pedidosProduccionCam) { %>
                            <div class="kanban-card <%= p.isAtrasado() ? "card-atrasado" : "" %>"
                                 onclick="verDetalle(<%= p.getId() %>)">
                                <div class="card-code"><%= p.getCodigoUnico() %></div>
                                <div class="card-patient"><%= p.getNombrePaciente() %></div>
                                <div class="card-type"><%= p.getTipoProtesis() %> - <%= p.getMaterial() %></div>
                                <div class="card-date">Compromiso: <%= p.getFechaCompromiso() %></div>
                            </div>
                        <% } %>
                    <% } else { %>
                        <p class="text-muted">No hay pedidos</p>
                    <% } %>
                </div>

                <!-- Columna 5: Cerámica -->
                <div class="kanban-column">
                    <div class="kanban-header" style="border-color: #e74c3c;">
                        <span class="kanban-title">🎨 CERÁMICA</span>
                        <span class="kanban-count"><%= pedidosCeramica != null ? pedidosCeramica.size() : 0 %></span>
                    </div>
                    <% if (pedidosCeramica != null && !pedidosCeramica.isEmpty()) { %>
                        <% for (Pedido p : pedidosCeramica) { %>
                            <div class="kanban-card <%= p.isAtrasado() ? "card-atrasado" : "" %>"
                                 onclick="verDetalle(<%= p.getId() %>)">
                                <div class="card-code"><%= p.getCodigoUnico() %></div>
                                <div class="card-patient"><%= p.getNombrePaciente() %></div>
                                <div class="card-type"><%= p.getTipoProtesis() %> - <%= p.getMaterial() %></div>
                                <div class="card-date">Compromiso: <%= p.getFechaCompromiso() %></div>
                            </div>
                        <% } %>
                    <% } else { %>
                        <p class="text-muted">No hay pedidos</p>
                    <% } %>
                </div>

                <!-- Columna 6: Control de Calidad -->
                <div class="kanban-column">
                    <div class="kanban-header" style="border-color: #16a085;">
                        <span class="kanban-title">✓ CONTROL CALIDAD</span>
                        <span class="kanban-count"><%= pedidosControlCalidad != null ? pedidosControlCalidad.size() : 0 %></span>
                    </div>
                    <% if (pedidosControlCalidad != null && !pedidosControlCalidad.isEmpty()) { %>
                        <% for (Pedido p : pedidosControlCalidad) { %>
                            <div class="kanban-card <%= p.isAtrasado() ? "card-atrasado" : "" %>"
                                 onclick="verDetalle(<%= p.getId() %>)">
                                <div class="card-code"><%= p.getCodigoUnico() %></div>
                                <div class="card-patient"><%= p.getNombrePaciente() %></div>
                                <div class="card-type"><%= p.getTipoProtesis() %> - <%= p.getMaterial() %></div>
                                <div class="card-date">Compromiso: <%= p.getFechaCompromiso() %></div>
                            </div>
                        <% } %>
                    <% } else { %>
                        <p class="text-muted">No hay pedidos</p>
                    <% } %>
                </div>

                <!-- Columna 7: Listo para Entrega -->
                <div class="kanban-column">
                    <div class="kanban-header" style="border-color: #27ae60;">
                        <span class="kanban-title">📦 LISTO ENTREGA</span>
                        <span class="kanban-count"><%= pedidosListoEntrega != null ? pedidosListoEntrega.size() : 0 %></span>
                    </div>
                    <% if (pedidosListoEntrega != null && !pedidosListoEntrega.isEmpty()) { %>
                        <% for (Pedido p : pedidosListoEntrega) { %>
                            <div class="kanban-card"
                                 onclick="verDetalle(<%= p.getId() %>)">
                                <div class="card-code"><%= p.getCodigoUnico() %></div>
                                <div class="card-patient"><%= p.getNombrePaciente() %></div>
                                <div class="card-type"><%= p.getTipoProtesis() %> - <%= p.getMaterial() %></div>
                                <div class="card-date">Compromiso: <%= p.getFechaCompromiso() %></div>
                            </div>
                        <% } %>
                    <% } else { %>
                        <p class="text-muted">No hay pedidos</p>
                    <% } %>
                </div>
            </div>
        </div>
    </main>

    <!-- Footer -->
    <footer class="footer">
        <div class="container">
            <p>&copy; 2025 LABDENT JLVEGA - Tablero Kanban FIFO</p>
        </div>
    </footer>

    <script>
        function verDetalle(pedidoId) {
            window.location.href = 'detalle-pedido?id=' + pedidoId;
        }
    </script>
</body>
</html>