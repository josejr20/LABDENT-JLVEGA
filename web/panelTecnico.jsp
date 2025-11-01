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
    
    // Validar roles permitidos
    String rol = usuario.getRol();
    boolean esRolPermitido = "ODONTOLOGO".equals(rol) || "TECNICO".equals(rol) || 
                             "CERAMISTA".equals(rol) || "ADMIN".equals(rol);
    
    if (!esRolPermitido) {
        response.sendRedirect("dashboard");
        return;
    }
    
    List<Pedido> pedidosRecepcion = (List<Pedido>) request.getAttribute("pedidosRecepcion");
    
    if (pedidosRecepcion == null) {
        response.sendRedirect("PanelTecnico");
        return;
    }
    
    List<Pedido> pedidosParalelizado = (List<Pedido>) request.getAttribute("pedidosParalelizado");
    List<Pedido> pedidosDisenoCad = (List<Pedido>) request.getAttribute("pedidosDisenoCad");
    List<Pedido> pedidosProduccionCam = (List<Pedido>) request.getAttribute("pedidosProduccionCam");
    List<Pedido> pedidosCeramica = (List<Pedido>) request.getAttribute("pedidosCeramica");
    List<Pedido> pedidosControlCalidad = (List<Pedido>) request.getAttribute("pedidosControlCalidad");
    List<Pedido> pedidosListoEntrega = (List<Pedido>) request.getAttribute("pedidosListoEntrega");
    
    Integer totalEnProceso = (Integer) request.getAttribute("totalEnProceso");
    Integer misPedidosAsignados = (Integer) request.getAttribute("misPedidosAsignados");
    
    int totalPedidos = pedidosRecepcion.size() + pedidosParalelizado.size() + 
                       pedidosDisenoCad.size() + pedidosProduccionCam.size() + 
                       pedidosCeramica.size() + pedidosControlCalidad.size() + 
                       pedidosListoEntrega.size();
    
    String success = (String) session.getAttribute("success");
    String error = (String) session.getAttribute("error");
    session.removeAttribute("success");
    session.removeAttribute("error");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Panel Técnico - LABDENT</title>
    <link rel="stylesheet" href="css/panelTecnico.css">
</head>
<body>

<header class="header">
    <div class="container">
        <div class="header-content">
            <div class="logo">
                <h1>🦷 LABDENT</h1>
            </div>
            <nav class="nav">
                <% if ("ADMIN".equals(rol)) { %>
                <a href="dashboard" class="nav-link">
                    <span class="icon">📊</span>
                    <span>Dashboard</span>
                </a>
                <% } %>
                <a href="PanelTecnico" class="nav-link active">
                    <span class="icon">🔧</span>
                    <span>Panel de Producción</span>
                </a>
                <% if ("ODONTOLOGO".equals(rol)) { %>
                <a href="mis-pedidos" class="nav-link">
                    <span class="icon">📋</span>
                    <span>Mis Pedidos</span>
                </a>
                <a href="registro-pedido" class="nav-link">
                    <span class="icon">➕</span>
                    <span>Nuevo Pedido</span>
                </a>
                <% } %>
            </nav>
            <div class="user-menu">
                <div class="user-info">
                    <span class="user-avatar">
                        <% if ("TECNICO".equals(rol)) { %>🔧
                        <% } else if ("CERAMISTA".equals(rol)) { %>🎨
                        <% } else if ("ODONTOLOGO".equals(rol)) { %>👨‍⚕️
                        <% } else { %>👤<% } %>
                    </span>
                    <div class="user-details">
                        <span class="user-name"><%= usuario.getNombreCompleto()%></span>
                        <span class="user-role">
                            <% if ("TECNICO".equals(rol)) { %>Técnico
                            <% } else if ("CERAMISTA".equals(rol)) { %>Ceramista
                            <% } else if ("ODONTOLOGO".equals(rol)) { %>Odontólogo
                            <% } else { %>Administrador<% } %>
                        </span>
                    </div>
                </div>
                <a href="logout" class="btn-logout">Cerrar Sesión</a>
            </div>
        </div>
    </div>
</header>

<main class="main-content">
    <div class="container-wide">
        
        <!-- Hero Section -->
        <section class="hero">
            <div class="hero-content">
                <h2 class="hero-title">Panel de Producción 🔧</h2>
                <p class="hero-subtitle">Gestiona el proceso de fabricación de pedidos dentales</p>
            </div>
            <div class="hero-actions">
                <button onclick="window.location.reload()" class="btn btn-primary">
                    <span>🔄</span> Actualizar
                </button>
                <button onclick="exportarReporte()" class="btn btn-secondary">
                    <span>📊</span> Exportar Reporte
                </button>
            </div>
        </section>

        <!-- Alertas -->
        <% if (success != null) { %>
        <div class="alert alert-success">
            <span class="alert-icon">✅</span>
            <span><%= success %></span>
            <button class="alert-close" onclick="this.parentElement.remove()">×</button>
        </div>
        <% } %>
        
        <% if (error != null) { %>
        <div class="alert alert-error">
            <span class="alert-icon">❌</span>
            <span><%= error %></span>
            <button class="alert-close" onclick="this.parentElement.remove()">×</button>
        </div>
        <% } %>

        <!-- Stats Cards -->
        <div class="stats-grid">
            <div class="stat-card stat-card-blue">
                <div class="stat-icon">📦</div>
                <div class="stat-info">
                    <h3 class="stat-title">Total en Producción</h3>
                    <p class="stat-number"><%= totalPedidos %></p>
                    <span class="stat-desc">Pedidos activos</span>
                </div>
            </div>
            
            <div class="stat-card stat-card-green">
                <div class="stat-icon">✅</div>
                <div class="stat-info">
                    <h3 class="stat-title">Mis Pedidos Asignados</h3>
                    <p class="stat-number"><%= misPedidosAsignados != null ? misPedidosAsignados : 0 %></p>
                    <span class="stat-desc">Responsable directo</span>
                </div>
            </div>
            
            <div class="stat-card stat-card-orange">
                <div class="stat-icon">⏱️</div>
                <div class="stat-info">
                    <h3 class="stat-title">En Control de Calidad</h3>
                    <p class="stat-number"><%= pedidosControlCalidad.size() %></p>
                    <span class="stat-desc">Pendientes de revisión</span>
                </div>
            </div>
            
            <div class="stat-card stat-card-purple">
                <div class="stat-icon">📋</div>
                <div class="stat-info">
                    <h3 class="stat-title">Listos para Entrega</h3>
                    <p class="stat-number"><%= pedidosListoEntrega.size() %></p>
                    <span class="stat-desc">Completados hoy</span>
                </div>
            </div>
        </div>

        <!-- Kanban Board -->
        <div class="kanban-header-section">
            <h2 class="section-title">
                <span class="section-icon">📋</span>
                Tablero de Producción Kanban
            </h2>
            <div class="kanban-filters">
                <select id="filterPrioridad" class="filter-select" onchange="filtrarPedidos()">
                    <option value="">Todas las prioridades</option>
                    <option value="NORMAL">Normal</option>
                    <option value="URGENTE">Urgente</option>
                    <option value="EMERGENCIA">Emergencia</option>
                </select>
                <input type="text" id="searchPedido" class="search-input" 
                       placeholder="🔍 Buscar por código o paciente..." 
                       onkeyup="buscarPedidos()">
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
                        <div class="kanban-card <%= p.isAtrasado() ? "card-delayed" : "" %>" 
                             data-prioridad="<%= p.getPrioridad() %>"
                             data-codigo="<%= p.getCodigoUnico() %>"
                             data-paciente="<%= p.getNombrePaciente() %>"
                             onclick="abrirModalActualizar(<%= p.getId() %>, '<%= p.getCodigoUnico() %>', '<%= p.getEstadoActual() %>')">
                            <div class="card-header">
                                <span class="card-code"><%= p.getCodigoUnico()%></span>
                                <% if ("URGENTE".equals(p.getPrioridad())) { %>
                                <span class="badge badge-urgent">🔥</span>
                                <% } else if ("EMERGENCIA".equals(p.getPrioridad())) { %>
                                <span class="badge badge-emergency">⚡</span>
                                <% } %>
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
                                <% if (p.isAtrasado()) { %>
                                <span class="card-status-delayed">⚠️ Atrasado</span>
                                <% } %>
                            </div>
                            <% if (p.getResponsableActual() != null && p.getNombreResponsable() != null) { %>
                            <div class="card-responsible">
                                <span class="responsible-icon">👷</span>
                                <span class="responsible-name"><%= p.getNombreResponsable() %></span>
                            </div>
                            <% } %>
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
                        <div class="kanban-card" 
                             data-prioridad="<%= p.getPrioridad() %>"
                             data-codigo="<%= p.getCodigoUnico() %>"
                             data-paciente="<%= p.getNombrePaciente() %>"
                             onclick="abrirModalActualizar(<%= p.getId() %>, '<%= p.getCodigoUnico() %>', '<%= p.getEstadoActual() %>')">
                            <div class="card-header">
                                <span class="card-code"><%= p.getCodigoUnico()%></span>
                                <% if ("URGENTE".equals(p.getPrioridad())) { %>
                                <span class="badge badge-urgent">🔥</span>
                                <% } else if ("EMERGENCIA".equals(p.getPrioridad())) { %>
                                <span class="badge badge-emergency">⚡</span>
                                <% } %>
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
                            <% if (p.getResponsableActual() != null && p.getNombreResponsable() != null) { %>
                            <div class="card-responsible">
                                <span class="responsible-icon">👷</span>
                                <span class="responsible-name"><%= p.getNombreResponsable() %></span>
                            </div>
                            <% } %>
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
                        <div class="kanban-card" 
                             data-prioridad="<%= p.getPrioridad() %>"
                             data-codigo="<%= p.getCodigoUnico() %>"
                             data-paciente="<%= p.getNombrePaciente() %>"
                             onclick="abrirModalActualizar(<%= p.getId() %>, '<%= p.getCodigoUnico() %>', '<%= p.getEstadoActual() %>')">
                            <div class="card-header">
                                <span class="card-code"><%= p.getCodigoUnico()%></span>
                                <% if ("URGENTE".equals(p.getPrioridad())) { %>
                                <span class="badge badge-urgent">🔥</span>
                                <% } else if ("EMERGENCIA".equals(p.getPrioridad())) { %>
                                <span class="badge badge-emergency">⚡</span>
                                <% } %>
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
                            <% if (p.getResponsableActual() != null && p.getNombreResponsable() != null) { %>
                            <div class="card-responsible">
                                <span class="responsible-icon">👷</span>
                                <span class="responsible-name"><%= p.getNombreResponsable() %></span>
                            </div>
                            <% } %>
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
                        <div class="kanban-card" 
                             data-prioridad="<%= p.getPrioridad() %>"
                             data-codigo="<%= p.getCodigoUnico() %>"
                             data-paciente="<%= p.getNombrePaciente() %>"
                             onclick="abrirModalActualizar(<%= p.getId() %>, '<%= p.getCodigoUnico() %>', '<%= p.getEstadoActual() %>')">
                            <div class="card-header">
                                <span class="card-code"><%= p.getCodigoUnico()%></span>
                                <% if ("URGENTE".equals(p.getPrioridad())) { %>
                                <span class="badge badge-urgent">🔥</span>
                                <% } else if ("EMERGENCIA".equals(p.getPrioridad())) { %>
                                <span class="badge badge-emergency">⚡</span>
                                <% } %>
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
                            <% if (p.getResponsableActual() != null && p.getNombreResponsable() != null) { %>
                            <div class="card-responsible">
                                <span class="responsible-icon">👷</span>
                                <span class="responsible-name"><%= p.getNombreResponsable() %></span>
                            </div>
                            <% } %>
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
                        <div class="kanban-card" 
                             data-prioridad="<%= p.getPrioridad() %>"
                             data-codigo="<%= p.getCodigoUnico() %>"
                             data-paciente="<%= p.getNombrePaciente() %>"
                             onclick="abrirModalActualizar(<%= p.getId() %>, '<%= p.getCodigoUnico() %>', '<%= p.getEstadoActual() %>')">
                            <div class="card-header">
                                <span class="card-code"><%= p.getCodigoUnico()%></span>
                                <% if ("URGENTE".equals(p.getPrioridad())) { %>
                                <span class="badge badge-urgent">🔥</span>
                                <% } else if ("EMERGENCIA".equals(p.getPrioridad())) { %>
                                <span class="badge badge-emergency">⚡</span>
                                <% } %>
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
                            <% if (p.getResponsableActual() != null && p.getNombreResponsable() != null) { %>
                            <div class="card-responsible">
                                <span class="responsible-icon">👷</span>
                                <span class="responsible-name"><%= p.getNombreResponsable() %></span>
                            </div>
                            <% } %>
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
                        <div class="kanban-card" 
                             data-prioridad="<%= p.getPrioridad() %>"
                             data-codigo="<%= p.getCodigoUnico() %>"
                             data-paciente="<%= p.getNombrePaciente() %>"
                             onclick="abrirModalActualizar(<%= p.getId() %>, '<%= p.getCodigoUnico() %>', '<%= p.getEstadoActual() %>')">
                            <div class="card-header">
                                <span class="card-code"><%= p.getCodigoUnico()%></span>
                                <% if ("URGENTE".equals(p.getPrioridad())) { %>
                                <span class="badge badge-urgent">🔥</span>
                                <% } else if ("EMERGENCIA".equals(p.getPrioridad())) { %>
                                <span class="badge badge-emergency">⚡</span>
                                <% } %>
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
                            <% if (p.getResponsableActual() != null && p.getNombreResponsable() != null) { %>
                            <div class="card-responsible">
                                <span class="responsible-icon">👷</span>
                                <span class="responsible-name"><%= p.getNombreResponsable() %></span>
                            </div>
                            <% } %>
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
                        <div class="kanban-card card-ready" 
                             data-prioridad="<%= p.getPrioridad() %>"
                             data-codigo="<%= p.getCodigoUnico() %>"
                             data-paciente="<%= p.getNombrePaciente() %>"
                             onclick="abrirModalDetalle(<%= p.getId() %>)">
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
        <p>&copy; 2025 LABDENT JLVEGA | Sistema de Gestión Dental con MVC + SOLID + TDD</p>
    </div>
</footer>

<!-- Modal Actualizar Estado -->
<div id="modalActualizar" class="modal">
    <div class="modal-content modal-large">
        <div class="modal-header">
            <h3 class="modal-title">🔄 Actualizar Estado del Pedido</h3>
            <button class="modal-close" onclick="cerrarModalActualizar()">&times;</button>
        </div>
        
        <div class="modal-body">
            <form id="formActualizar" action="ActualizarEstadoPedido" method="post" enctype="multipart/form-data">
                <input type="hidden" name="pedidoId" id="pedidoId">
                
                <div class="form-row">
                    <div class="form-group">
                        <label class="form-label">Código del Pedido:</label>
                        <div class="form-value" id="codigoPedido"></div>
                    </div>
                    
                    <div class="form-group">
                        <label class="form-label">Estado Actual:</label>
                        <div class="form-value" id="estadoActual"></div>
                    </div>
                </div>
                
                <div class="form-group">
                    <label class="form-label">Nuevo Estado: *</label>
                    <select name="nuevoEstado" id="nuevoEstado" class="form-select" required>
                        <option value="">Seleccionar siguiente estado...</option>
                    </select>
                    <small class="form-help">Solo puedes avanzar al siguiente estado permitido</small>
                </div>
                
                <div class="form-group">
                    <label class="form-label">Observaciones:</label>
                    <textarea name="observaciones" id="observaciones" class="form-textarea" rows="4" 
                              placeholder="Agrega comentarios sobre el proceso realizado..."></textarea>
                </div>
                
                <!-- Sección de Evidencias -->
                <div class="evidencias-section">
                    <h4 class="evidencias-title">
                        <span class="evidencias-icon">📎</span>
                        Subir Evidencias (Opcional)
                    </h4>
                    
                    <div id="evidenciasContainer">
                        <div class="evidencia-item" id="evidenciaItem1">
                            <div class="form-row">
                                <div class="form-group">
                                    <label class="form-label">Tipo de Evidencia:</label>
                                    <select name="tipoEvidencia[]" class="form-select">
                                        <option value="">Sin evidencia</option>
                                        <option value="FOTO_INICIAL">📷 Foto Inicial</option>
                                        <option value="FOTO_PROCESO">📸 Foto del Proceso</option>
                                        <option value="FOTO_FINAL">🖼️ Foto Final</option>
                                        <option value="DOCUMENTO">📄 Documento</option>
                                        <option value="CAD_FILE">💾 Archivo CAD/STL</option>
                                    </select>
                                </div>
                                
                                <div class="form-group">
                                    <label class="form-label">Archivo:</label>
                                    <input type="file" name="evidenciaArchivo[]" class="form-file" 
                                           accept="image/*,.pdf,.doc,.docx,.stl,.obj">
                                    <small class="form-help">Max 10MB - JPG, PNG, PDF, DOC, STL</small>
                                </div>
                            </div>
                            
                            <div class="form-group">
                                <label class="form-label">Descripción:</label>
                                <input type="text" name="evidenciaDescripcion[]" class="form-input" 
                                       placeholder="Descripción breve del archivo...">
                            </div>
                        </div>
                    </div>
                    
                    <button type="button" class="btn btn-secondary btn-sm" onclick="agregarEvidencia()">
                        ➕ Agregar Otra Evidencia
                    </button>
                </div>
                
            </form>
        </div>
        
        <div class="modal-footer">
            <button type="button" class="btn btn-secondary" onclick="cerrarModalActualizar()">
                Cancelar
            </button>
            <button type="button" class="btn btn-primary" onclick="enviarActualizacion()">
                💾 Guardar y Actualizar
            </button>
        </div>
    </div>
</div>

<script>
    let contadorEvidencias = 1;
    
    // Mapa de transiciones permitidas (debe coincidir con el backend)
    const TRANSICIONES_PERMITIDAS = {
        'RECEPCION': ['PARALELIZADO'],
        'PARALELIZADO': ['DISENO_CAD'],
        'DISENO_CAD': ['PRODUCCION_CAM'],
        'PRODUCCION_CAM': ['CERAMICA', 'CONTROL_CALIDAD'],
        'CERAMICA': ['CONTROL_CALIDAD'],
        'CONTROL_CALIDAD': ['LISTO_ENTREGA'],
        'LISTO_ENTREGA': ['ENTREGADO']
    };
    
    const ESTADO_DESCRIPCION = {
        'RECEPCION': 'Recepción',
        'PARALELIZADO': 'Paralelizado',
        'DISENO_CAD': 'Diseño CAD',
        'PRODUCCION_CAM': 'Producción CAM',
        'CERAMICA': 'Cerámica',
        'CONTROL_CALIDAD': 'Control de Calidad',
        'LISTO_ENTREGA': 'Listo para Entrega',
        'ENTREGADO': 'Entregado'
    };
    
    function abrirModalActualizar(pedidoId, codigoPedido, estadoActual) {
        document.getElementById('pedidoId').value = pedidoId;
        document.getElementById('codigoPedido').textContent = codigoPedido;
        document.getElementById('estadoActual').textContent = ESTADO_DESCRIPCION[estadoActual] || estadoActual;
        
        // Cargar opciones de siguiente estado
        cargarSiguientesEstados(estadoActual);
        
        // Limpiar evidencias
        contadorEvidencias = 1;
        document.getElementById('evidenciasContainer').innerHTML = `
            <div class="evidencia-item" id="evidenciaItem1">
                <div class="form-row">
                    <div class="form-group">
                        <label class="form-label">Tipo de Evidencia:</label>
                        <select name="tipoEvidencia[]" class="form-select">
                            <option value="">Sin evidencia</option>
                            <option value="FOTO_INICIAL">📷 Foto Inicial</option>
                            <option value="FOTO_PROCESO">📸 Foto del Proceso</option>
                            <option value="FOTO_FINAL">🖼️ Foto Final</option>
                            <option value="DOCUMENTO">📄 Documento</option>
                            <option value="CAD_FILE">💾 Archivo CAD/STL</option>
                        </select>
                    </div>
                    
                    <div class="form-group">
                        <label class="form-label">Archivo:</label>
                        <input type="file" name="evidenciaArchivo[]" class="form-file" 
                               accept="image/*,.pdf,.doc,.docx,.stl,.obj">
                        <small class="form-help">Max 10MB - JPG, PNG, PDF, DOC, STL</small>
                    </div>
                </div>
                
                <div class="form-group">
                    <label class="form-label">Descripción:</label>
                    <input type="text" name="evidenciaDescripcion[]" class="form-input" 
                           placeholder="Descripción breve del archivo...">
                </div>
            </div>
        `;
        
        document.getElementById('modalActualizar').style.display = 'flex';
    }
    
    function cerrarModalActualizar() {
        document.getElementById('modalActualizar').style.display = 'none';
        document.getElementById('formActualizar').reset();
    }
    
    function cargarSiguientesEstados(estadoActual) {
        const select = document.getElementById('nuevoEstado');
        select.innerHTML = '<option value="">Seleccionar siguiente estado...</option>';
        
        const estadosPermitidos = TRANSICIONES_PERMITIDAS[estadoActual] || [];
        
        estadosPermitidos.forEach(estado => {
            const option = document.createElement('option');
            option.value = estado;
            option.textContent = ESTADO_DESCRIPCION[estado] || estado;
            select.appendChild(option);
        });
        
        if (estadosPermitidos.length === 0) {
            select.innerHTML = '<option value="">No hay estados siguientes disponibles</option>';
            select.disabled = true;
        } else {
            select.disabled = false;
        }
    }
    
    function agregarEvidencia() {
        contadorEvidencias++;
        const container = document.getElementById('evidenciasContainer');
        
        const nuevaEvidencia = document.createElement('div');
        nuevaEvidencia.className = 'evidencia-item';
        nuevaEvidencia.id = 'evidenciaItem' + contadorEvidencias;
        nuevaEvidencia.innerHTML = `
            <div class="evidencia-header">
                <h5>Evidencia ${contadorEvidencias}</h5>
                <button type="button" class="btn-remove" onclick="eliminarEvidencia(${contadorEvidencias})">
                    🗑️
                </button>
            </div>
            <div class="form-row">
                <div class="form-group">
                    <label class="form-label">Tipo de Evidencia:</label>
                    <select name="tipoEvidencia[]" class="form-select">
                        <option value="">Sin evidencia</option>
                        <option value="FOTO_INICIAL">📷 Foto Inicial</option>
                        <option value="FOTO_PROCESO">📸 Foto del Proceso</option>
                        <option value="FOTO_FINAL">🖼️ Foto Final</option>
                        <option value="DOCUMENTO">📄 Documento</option>
                        <option value="CAD_FILE">💾 Archivo CAD/STL</option>
                    </select>
                </div>
                
                <div class="form-group">
                    <label class="form-label">Archivo:</label>
                    <input type="file" name="evidenciaArchivo[]" class="form-file" 
                           accept="image/*,.pdf,.doc,.docx,.stl,.obj">
                    <small class="form-help">Max 10MB - JPG, PNG, PDF, DOC, STL</small>
                </div>
            </div>
            
            <div class="form-group">
                <label class="form-label">Descripción:</label>
                <input type="text" name="evidenciaDescripcion[]" class="form-input" 
                       placeholder="Descripción breve del archivo...">
            </div>
        `;
        
        container.appendChild(nuevaEvidencia);
    }
    
    function eliminarEvidencia(numero) {
        const elemento = document.getElementById('evidenciaItem' + numero);
        if (elemento) {
            elemento.remove();
        }
    }
    
    async function enviarActualizacion() {
        const form = document.getElementById('formActualizar');
        
        // Validar formulario
        const nuevoEstado = document.getElementById('nuevoEstado').value;
        if (!nuevoEstado) {
            alert('⚠️ Debes seleccionar un nuevo estado');
            return;
        }
        
        // Mostrar loading
        const btnGuardar = event.target;
        btnGuardar.disabled = true;
        btnGuardar.textContent = '⏳ Procesando...';
        
        try {
            const formData = new FormData(form);
            
            // Enviar actualización de estado primero
            const responseEstado = await fetch('ActualizarEstadoPedido', {
                method: 'POST',
                body: new URLSearchParams({
                    pedidoId: formData.get('pedidoId'),
                    nuevoEstado: formData.get('nuevoEstado'),
                    observaciones: formData.get('observaciones')
                })
            });
            
            if (!responseEstado.ok) {
                throw new Error('Error al actualizar el estado');
            }
            
            // Enviar evidencias si existen
            const archivos = form.querySelectorAll('input[type="file"]');
            let evidenciasSubidas = 0;
            
            for (let i = 0; i < archivos.length; i++) {
                if (archivos[i].files.length > 0) {
                    const evidenciaFormData = new FormData();
                    evidenciaFormData.append('pedidoId', formData.get('pedidoId'));
                    evidenciaFormData.append('tipoEvidencia', form.querySelectorAll('select[name="tipoEvidencia[]"]')[i].value);
                    evidenciaFormData.append('archivo', archivos[i].files[0]);
                    evidenciaFormData.append('descripcion', form.querySelectorAll('input[name="evidenciaDescripcion[]"]')[i].value);
                    
                    const responseEvidencia = await fetch('SubirEvidencia', {
                        method: 'POST',
                        body: evidenciaFormData
                    });
                    
                    if (responseEvidencia.ok) {
                        evidenciasSubidas++;
                    }
                }
            }
            
            console.log(`✅ Estado actualizado. ${evidenciasSubidas} evidencias subidas.`);
            
            // Cerrar modal y recargar
            cerrarModalActualizar();
            window.location.reload();
            
        } catch (error) {
            console.error('Error:', error);
            alert('❌ Error al actualizar: ' + error.message);
            btnGuardar.disabled = false;
            btnGuardar.textContent = '💾 Guardar y Actualizar';
        }
    }
    
    function abrirModalDetalle(pedidoId) {
        window.location.href = 'ver-pedido?id=' + pedidoId;
    }
    
    function filtrarPedidos() {
        const prioridadSeleccionada = document.getElementById('filterPrioridad').value;
        const cards = document.querySelectorAll('.kanban-card');
        
        cards.forEach(card => {
            const prioridadCard = card.getAttribute('data-prioridad');
            
            if (!prioridadSeleccionada || prioridadCard === prioridadSeleccionada) {
                card.style.display = '';
            } else {
                card.style.display = 'none';
            }
        });
    }
    
    function buscarPedidos() {
        const searchTerm = document.getElementById('searchPedido').value.toLowerCase();
        const cards = document.querySelectorAll('.kanban-card');
        
        cards.forEach(card => {
            const codigo = card.getAttribute('data-codigo').toLowerCase();
            const paciente = card.getAttribute('data-paciente').toLowerCase();
            
            if (codigo.includes(searchTerm) || paciente.includes(searchTerm)) {
                card.style.display = '';
            } else {
                card.style.display = 'none';
            }
        });
    }
    
    function exportarReporte() {
        // Implementar exportación a Excel usando Apache POI
        window.location.href = 'ExportarReporte?formato=excel';
    }
    
    // Cerrar modal al hacer clic fuera
    window.onclick = function(event) {
        const modal = document.getElementById('modalActualizar');
        if (event.target === modal) {
            cerrarModalActualizar();
        }
    }
    
    // Auto-cerrar alertas después de 5 segundos
    setTimeout(() => {
        document.querySelectorAll('.alert').forEach(alert => {
            alert.style.animation = 'slideOut 0.3s ease';
            setTimeout(() => alert.remove(), 300);
        });
    }, 5000);
    
    console.log('✅ Panel Técnico cargado - <%= totalPedidos %> pedidos en producción');
</script>

</body>
</html>