<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.labdent.model.Usuario" %>
<%@ page import="com.labdent.model.Pedido" %>
<%@ page import="java.util.List" %>
<%
    Usuario usuario = (Usuario) session.getAttribute("usuario");
    if (usuario == null || !"DELIVERISTA".equals(usuario.getRol())) {
        response.sendRedirect("login");
        return;
    }
    
    List<Pedido> pedidosRecepcion = (List<Pedido>) request.getAttribute("pedidosRecepcion");
    
    if (pedidosRecepcion == null) {
        response.sendRedirect("KanbanDeliverista");
        return;
    }
    
    List<Pedido> pedidosParalelizado = (List<Pedido>) request.getAttribute("pedidosParalelizado");
    List<Pedido> pedidosDisenoCad = (List<Pedido>) request.getAttribute("pedidosDisenoCad");
    List<Pedido> pedidosProduccionCam = (List<Pedido>) request.getAttribute("pedidosProduccionCam");
    List<Pedido> pedidosCeramica = (List<Pedido>) request.getAttribute("pedidosCeramica");
    List<Pedido> pedidosControlCalidad = (List<Pedido>) request.getAttribute("pedidosControlCalidad");
    List<Pedido> pedidosListoEntrega = (List<Pedido>) request.getAttribute("pedidosListoEntrega");
    
    int totalPedidos = pedidosRecepcion.size() + pedidosParalelizado.size() + 
                       pedidosDisenoCad.size() + pedidosProduccionCam.size() + 
                       pedidosCeramica.size() + pedidosControlCalidad.size() + 
                       pedidosListoEntrega.size();
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Kanban - Deliverista</title>
    <link rel="stylesheet" href="css/kanbanDeliverista.css">
</head>
<body>

<header class="header">
    <div class="container">
        <div class="header-content">
            <div class="logo">
                <h1>🦷 LABDENT</h1>
            </div>
            <nav class="nav">
                <a href="PanelDeliverista" class="nav-link">
                    <span class="icon">🏠</span>
                    <span>Panel Principal</span>
                </a>
                <a href="KanbanDeliverista" class="nav-link active">
                    <span class="icon">📊</span>
                    <span>Kanban General</span>
                </a>
            </nav>
            <div class="user-menu">
                <div class="user-info">
                    <span class="user-avatar">🚚</span>
                    <div class="user-details">
                        <span class="user-name"><%= usuario.getNombreCompleto()%></span>
                        <span class="user-role">Deliverista</span>
                    </div>
                </div>
                <a href="logout" class="btn-logout">Cerrar Sesión</a>
            </div>
        </div>
    </div>
</header>

<main class="main-content">
    <div class="container-wide">
        
        <div class="page-header">
            <div class="breadcrumb">
                <a href="PanelDeliverista" class="breadcrumb-link">
                    <span>🏠</span> Panel
                </a>
                <span class="breadcrumb-separator">›</span>
                <span class="breadcrumb-current">Kanban General</span>
            </div>
            <div class="page-actions">
                <button onclick="window.location.reload()" class="btn-icon" title="Actualizar">
                    🔄
                </button>
                <a href="PanelDeliverista" class="btn btn-secondary">
                    ← Volver al Panel
                </a>
            </div>
        </div>

        <div class="kanban-header">
            <div class="kanban-title-section">
                <h1 class="kanban-title">📊 Kanban General de Producción</h1>
                <p class="kanban-subtitle">Vista de solo lectura del estado de todos los pedidos en producción</p>
            </div>
            <div class="kanban-stats">
                <div class="stat-badge stat-badge-blue">
                    <span class="stat-label">Total en Producción</span>
                    <span class="stat-value"><%= totalPedidos %></span>
                </div>
                <div class="stat-badge stat-badge-green">
                    <span class="stat-label">Listos para Entrega</span>
                    <span class="stat-value"><%= pedidosListoEntrega.size() %></span>
                </div>
            </div>
        </div>

        <div class="info-banner">
            <span class="info-icon">ℹ️</span>
            <div class="info-text">
                <strong>Modo de Solo Lectura:</strong> Este kanban es solo para visualización. 
                Los pedidos en "Listo para Entrega" están disponibles para ser tomados desde tu Panel Principal.
            </div>
        </div>

        <div class="kanban-board">

            <!-- RECEPCIÓN -->
            <div class="kanban-column">
                <div class="column-header header-blue">
                    <div class="column-title">
                        <span class="column-icon">📥</span>
                        <span>Recepción</span>
                    </div>
                    <span class="column-count"><%= pedidosRecepcion.size()%></span>
                </div>
                <div class="column-body">
                    <% if (!pedidosRecepcion.isEmpty()) { %>
                        <% for (Pedido p : pedidosRecepcion) {%>
                        <div class="kanban-card kanban-card-readonly">
                            <div class="card-header">
                                <span class="card-code"><%= p.getCodigoUnico()%></span>
                            </div>
                            <div class="card-body">
                                <div class="card-patient">
                                    <strong>👤</strong> <%= p.getNombrePaciente()%>
                                </div>
                                <div class="card-work">
                                    <strong>🦷</strong> <%= p.getTipoProtesis()%>
                                </div>
                                <div class="card-material">
                                    <strong>💎</strong> <%= p.getMaterial()%>
                                </div>
                            </div>
                            <div class="card-footer">
                                <div class="card-date">
                                    <strong>📅</strong> <%= p.getFechaCompromiso()%>
                                </div>
                            </div>
                        </div>
                        <% } %>
                    <% } else { %>
                        <div class="empty-state">
                            <div class="empty-icon">📭</div>
                            <p>Sin pedidos</p>
                        </div>
                    <% }%>
                </div>
            </div>

            <!-- PARALELIZADO -->
            <div class="kanban-column">
                <div class="column-header header-purple">
                    <div class="column-title">
                        <span class="column-icon">🔄</span>
                        <span>Paralelizado</span>
                    </div>
                    <span class="column-count"><%= pedidosParalelizado.size()%></span>
                </div>
                <div class="column-body">
                    <% if (!pedidosParalelizado.isEmpty()) { %>
                        <% for (Pedido p : pedidosParalelizado) {%>
                        <div class="kanban-card kanban-card-readonly">
                            <div class="card-header">
                                <span class="card-code"><%= p.getCodigoUnico()%></span>
                            </div>
                            <div class="card-body">
                                <div class="card-patient">
                                    <strong>👤</strong> <%= p.getNombrePaciente()%>
                                </div>
                                <div class="card-work">
                                    <strong>🦷</strong> <%= p.getTipoProtesis()%>
                                </div>
                                <div class="card-material">
                                    <strong>💎</strong> <%= p.getMaterial()%>
                                </div>
                            </div>
                            <div class="card-footer">
                                <div class="card-date">
                                    <strong>📅</strong> <%= p.getFechaCompromiso()%>
                                </div>
                            </div>
                        </div>
                        <% } %>
                    <% } else { %>
                        <div class="empty-state">
                            <div class="empty-icon">📭</div>
                            <p>Sin pedidos</p>
                        </div>
                    <% }%>
                </div>
            </div>

            <!-- DISEÑO CAD -->
            <div class="kanban-column">
                <div class="column-header header-teal">
                    <div class="column-title">
                        <span class="column-icon">💻</span>
                        <span>Diseño CAD</span>
                    </div>
                    <span class="column-count"><%= pedidosDisenoCad.size()%></span>
                </div>
                <div class="column-body">
                    <% if (!pedidosDisenoCad.isEmpty()) { %>
                        <% for (Pedido p : pedidosDisenoCad) {%>
                        <div class="kanban-card kanban-card-readonly">
                            <div class="card-header">
                                <span class="card-code"><%= p.getCodigoUnico()%></span>
                            </div>
                            <div class="card-body">
                                <div class="card-patient">
                                    <strong>👤</strong> <%= p.getNombrePaciente()%>
                                </div>
                                <div class="card-work">
                                    <strong>🦷</strong> <%= p.getTipoProtesis()%>
                                </div>
                                <div class="card-material">
                                    <strong>💎</strong> <%= p.getMaterial()%>
                                </div>
                            </div>
                            <div class="card-footer">
                                <div class="card-date">
                                    <strong>📅</strong> <%= p.getFechaCompromiso()%>
                                </div>
                            </div>
                        </div>
                        <% } %>
                    <% } else { %>
                        <div class="empty-state">
                            <div class="empty-icon">📭</div>
                            <p>Sin pedidos</p>
                        </div>
                    <% }%>
                </div>
            </div>

            <!-- PRODUCCIÓN CAM -->
            <div class="kanban-column">
                <div class="column-header header-orange">
                    <div class="column-title">
                        <span class="column-icon">⚙️</span>
                        <span>Producción CAM</span>
                    </div>
                    <span class="column-count"><%= pedidosProduccionCam.size()%></span>
                </div>
                <div class="column-body">
                    <% if (!pedidosProduccionCam.isEmpty()) { %>
                        <% for (Pedido p : pedidosProduccionCam) {%>
                        <div class="kanban-card kanban-card-readonly">
                            <div class="card-header">
                                <span class="card-code"><%= p.getCodigoUnico()%></span>
                            </div>
                            <div class="card-body">
                                <div class="card-patient">
                                    <strong>👤</strong> <%= p.getNombrePaciente()%>
                                </div>
                                <div class="card-work">
                                    <strong>🦷</strong> <%= p.getTipoProtesis()%>
                                </div>
                                <div class="card-material">
                                    <strong>💎</strong> <%= p.getMaterial()%>
                                </div>
                            </div>
                            <div class="card-footer">
                                <div class="card-date">
                                    <strong>📅</strong> <%= p.getFechaCompromiso()%>
                                </div>
                            </div>
                        </div>
                        <% } %>
                    <% } else { %>
                        <div class="empty-state">
                            <div class="empty-icon">📭</div>
                            <p>Sin pedidos</p>
                        </div>
                    <% }%>
                </div>
            </div>

            <!-- CERÁMICA -->
            <div class="kanban-column">
                <div class="column-header header-red">
                    <div class="column-title">
                        <span class="column-icon">🎨</span>
                        <span>Cerámica</span>
                    </div>
                    <span class="column-count"><%= pedidosCeramica.size()%></span>
                </div>
                <div class="column-body">
                    <% if (!pedidosCeramica.isEmpty()) { %>
                        <% for (Pedido p : pedidosCeramica) {%>
                        <div class="kanban-card kanban-card-readonly">
                            <div class="card-header">
                                <span class="card-code"><%= p.getCodigoUnico()%></span>
                            </div>
                            <div class="card-body">
                                <div class="card-patient">
                                    <strong>👤</strong> <%= p.getNombrePaciente()%>
                                </div>
                                <div class="card-work">
                                    <strong>🦷</strong> <%= p.getTipoProtesis()%>
                                </div>
                                <div class="card-material">
                                    <strong>💎</strong> <%= p.getMaterial()%>
                                </div>
                            </div>
                            <div class="card-footer">
                                <div class="card-date">
                                    <strong>📅</strong> <%= p.getFechaCompromiso()%>
                                </div>
                            </div>
                        </div>
                        <% } %>
                    <% } else { %>
                        <div class="empty-state">
                            <div class="empty-icon">📭</div>
                            <p>Sin pedidos</p>
                        </div>
                    <% }%>
                </div>
            </div>

            <!-- CONTROL CALIDAD -->
            <div class="kanban-column">
                <div class="column-header header-cyan">
                    <div class="column-title">
                        <span class="column-icon">✓</span>
                        <span>Control Calidad</span>
                    </div>
                    <span class="column-count"><%= pedidosControlCalidad.size()%></span>
                </div>
                <div class="column-body">
                    <% if (!pedidosControlCalidad.isEmpty()) { %>
                        <% for (Pedido p : pedidosControlCalidad) {%>
                        <div class="kanban-card kanban-card-readonly">
                            <div class="card-header">
                                <span class="card-code"><%= p.getCodigoUnico()%></span>
                            </div>
                            <div class="card-body">
                                <div class="card-patient">
                                    <strong>👤</strong> <%= p.getNombrePaciente()%>
                                </div>
                                <div class="card-work">
                                    <strong>🦷</strong> <%= p.getTipoProtesis()%>
                                </div>
                                <div class="card-material">
                                    <strong>💎</strong> <%= p.getMaterial()%>
                                </div>
                            </div>
                            <div class="card-footer">
                                <div class="card-date">
                                    <strong>📅</strong> <%= p.getFechaCompromiso()%>
                                </div>
                            </div>
                        </div>
                        <% } %>
                    <% } else { %>
                        <div class="empty-state">
                            <div class="empty-icon">📭</div>
                            <p>Sin pedidos</p>
                        </div>
                    <% }%>
                </div>
            </div>

            <!-- LISTO ENTREGA -->
            <div class="kanban-column">
                <div class="column-header header-green">
                    <div class="column-title">
                        <span class="column-icon">📦</span>
                        <span>Listo Entrega</span>
                    </div>
                    <span class="column-count"><%= pedidosListoEntrega.size()%></span>
                </div>
                <div class="column-body">
                    <% if (!pedidosListoEntrega.isEmpty()) { %>
                        <% for (Pedido p : pedidosListoEntrega) {%>
                        <div class="kanban-card card-ready kanban-card-readonly">
                            <div class="card-header">
                                <span class="card-code"><%= p.getCodigoUnico()%></span>
                                <span class="badge badge-success">✓ Listo</span>
                            </div>
                            <div class="card-body">
                                <div class="card-patient">
                                    <strong>👤</strong> <%= p.getNombrePaciente()%>
                                </div>
                                <div class="card-work">
                                    <strong>🦷</strong> <%= p.getTipoProtesis()%>
                                </div>
                                <div class="card-material">
                                    <strong>💎</strong> <%= p.getMaterial()%>
                                </div>
                            </div>
                            <div class="card-footer">
                                <div class="card-date">
                                    <strong>📅</strong> <%= p.getFechaCompromiso()%>
                                </div>
                            </div>
                            <div class="card-action">
                                <span class="card-hint">👉 Disponible en Panel</span>
                            </div>
                        </div>
                        <% } %>
                    <% } else { %>
                        <div class="empty-state">
                            <div class="empty-icon">📭</div>
                            <p>Sin pedidos</p>
                        </div>
                    <% }%>
                </div>
            </div>

        </div>
    </div>
</main>

<footer class="footer">
    <div class="container">
        <p>&copy; 2025 LABDENT JLVEGA | Sistema de Gestión Dental</p>
    </div>
</footer>

<script>
    console.log('✅ Kanban Deliverista cargado - <%= totalPedidos %> pedidos en producción');
</script>

</body>
</html>