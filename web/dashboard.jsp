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

    // Validar que sea administrador
    if (!"ADMIN".equals(usuario.getRol())) {
        response.sendRedirect("dashboard");
        return;
    }

    List<Pedido> pedidosRecepcion = (List<Pedido>) request.getAttribute("pedidosRecepcion");
    List<Pedido> pedidosParalelizado = (List<Pedido>) request.getAttribute("pedidosParalelizado");
    List<Pedido> pedidosDisenoCad = (List<Pedido>) request.getAttribute("pedidosDisenoCad");
    List<Pedido> pedidosProduccionCam = (List<Pedido>) request.getAttribute("pedidosProduccionCam");
    List<Pedido> pedidosCeramica = (List<Pedido>) request.getAttribute("pedidosCeramica");
    List<Pedido> pedidosControlCalidad = (List<Pedido>) request.getAttribute("pedidosControlCalidad");
    List<Pedido> pedidosListoEntrega = (List<Pedido>) request.getAttribute("pedidosListoEntrega");

    // Inicializar listas si son null
    if (pedidosRecepcion == null) {
        pedidosRecepcion = new java.util.ArrayList<>();
    }
    if (pedidosParalelizado == null) {
        pedidosParalelizado = new java.util.ArrayList<>();
    }
    if (pedidosDisenoCad == null) {
        pedidosDisenoCad = new java.util.ArrayList<>();
    }
    if (pedidosProduccionCam == null) {
        pedidosProduccionCam = new java.util.ArrayList<>();
    }
    if (pedidosCeramica == null) {
        pedidosCeramica = new java.util.ArrayList<>();
    }
    if (pedidosControlCalidad == null) {
        pedidosControlCalidad = new java.util.ArrayList<>();
    }
    if (pedidosListoEntrega == null) {
        pedidosListoEntrega = new java.util.ArrayList<>();
    }

    int totalPedidos = pedidosRecepcion.size() + pedidosParalelizado.size()
            + pedidosDisenoCad.size() + pedidosProduccionCam.size()
            + pedidosCeramica.size() + pedidosControlCalidad.size()
            + pedidosListoEntrega.size();

    // Calcular pedidos atrasados
    int pedidosAtrasados = 0;
    int pedidosUrgentes = 0;
    for (List<Pedido> lista : new List[]{pedidosRecepcion, pedidosParalelizado, pedidosDisenoCad,
        pedidosProduccionCam, pedidosCeramica, pedidosControlCalidad}) {
        for (Pedido p : lista) {
            if (p.isAtrasado()) {
                pedidosAtrasados++;
            }
            if ("URGENTE".equals(p.getPrioridad()) || "EMERGENCIA".equals(p.getPrioridad())) {
                pedidosUrgentes++;
            }
        }
    }

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
        <title>Panel Administrador - LABDENT</title>
        <link rel="stylesheet" href="css/dashboard.css"/>
    </head>
    <body>

        <header class="header">
            <div class="container">
                <div class="header-content">
                    <div class="logo">
                        <h1>🦷 LABDENT ADMIN</h1>
                    </div>
                    <nav class="nav">
                        <a href="dashboard" class="nav-link">
                            <span class="icon">📊</span>
                            <span>Dashboard</span>
                        </a>
                        <a href="PanelAdministrador" class="nav-link active">
                            <span class="icon">📋</span>
                            <span>Kanban Avanzado</span>
                        </a>
                        <a href="usuarios" class="nav-link">
                            <span class="icon">👥</span>
                            <span>Usuarios</span>
                        </a>
                        <a href="reportes" class="nav-link">
                            <span class="icon">📈</span>
                            <span>Reportes</span>
                        </a>
                    </nav>
                    <div class="user-menu">
                        <div class="user-info">
                            <span class="user-avatar">👑</span>
                            <div class="user-details">
                                <span class="user-name"><%= usuario.getNombreCompleto()%></span>
                                <span class="user-role">Administrador</span>
                            </div>
                        </div>
                        <a href="logout" class="btn-logout">Cerrar Sesión</a>
                    </div>
                </div>
            </div>
        </header>

        <main class="main-content">
            <div class="container">

                <section class="hero">
                    <div class="hero-content">
                        <h2 class="hero-title">Panel de Administración 👑</h2>
                        <p class="hero-subtitle">Control total del flujo de producción y gestión de recursos</p>
                    </div>
                    <div class="hero-actions">
                        <button onclick="window.location.reload()" class="btn btn-primary">
                            <span>🔄</span> Actualizar
                        </button>
                        <button onclick="abrirModalAsignacionMasiva()" class="btn btn-success">
                            <span>👥</span> Asignación Masiva
                        </button>
                        <button onclick="exportarReporte()" class="btn btn-secondary">
                            <span>📊</span> Exportar
                        </button>
                    </div>
                </section>

                <% if (success != null) {%>
                <div class="alert alert-success">
                    <span class="alert-icon">✅</span>
                    <span><%= success%></span>
                    <button class="alert-close" onclick="this.parentElement.remove()">×</button>
                </div>
                <% } %>

                <% if (error != null) {%>
                <div class="alert alert-error">
                    <span class="alert-icon">❌</span>
                    <span><%= error%></span>
                    <button class="alert-close" onclick="this.parentElement.remove()">×</button>
                </div>
                <% }%>

                <div class="stats-grid">
                    <div class="stat-card stat-card-blue">
                        <div class="stat-icon">📦</div>
                        <div class="stat-info">
                            <h3 class="stat-title">Total en Producción</h3>
                            <p class="stat-number"><%= totalPedidos%></p>
                            <span class="stat-desc">Pedidos activos</span>
                        </div>
                    </div>

                    <div class="stat-card stat-card-red">
                        <div class="stat-icon">⚠️</div>
                        <div class="stat-info">
                            <h3 class="stat-title">Pedidos Atrasados</h3>
                            <p class="stat-number"><%= pedidosAtrasados%></p>
                            <span class="stat-desc">Requieren atención</span>
                        </div>
                    </div>

                    <div class="stat-card stat-card-orange">
                        <div class="stat-icon">🔥</div>
                        <div class="stat-info">
                            <h3 class="stat-title">Urgentes/Emergencias</h3>
                            <p class="stat-number"><%= pedidosUrgentes%></p>
                            <span class="stat-desc">Prioridad alta</span>
                        </div>
                    </div>

                    <div class="stat-card stat-card-green">
                        <div class="stat-icon">✅</div>
                        <div class="stat-info">
                            <h3 class="stat-title">Listos para Entrega</h3>
                            <p class="stat-number"><%= pedidosListoEntrega.size()%></p>
                            <span class="stat-desc">Completados</span>
                        </div>
                    </div>
                </div>

                <div class="toolbar">
                    <div class="toolbar-section">
                        <select id="filterEstado" class="filter-select" onchange="filtrarPedidos()">
                            <option value="">Todos los estados</option>
                            <option value="RECEPCION">Recepción</option>
                            <option value="PARALELIZADO">Paralelizado</option>
                            <option value="DISENO_CAD">Diseño CAD</option>
                            <option value="PRODUCCION_CAM">Producción CAM</option>
                            <option value="CERAMICA">Cerámica</option>
                            <option value="CONTROL_CALIDAD">Control Calidad</option>
                            <option value="LISTO_ENTREGA">Listo Entrega</option>
                        </select>

                        <select id="filterPrioridad" class="filter-select" onchange="filtrarPedidos()">
                            <option value="">Todas las prioridades</option>
                            <option value="NORMAL">Normal</option>
                            <option value="URGENTE">Urgente</option>
                            <option value="EMERGENCIA">Emergencia</option>
                        </select>

                        <select id="filterResponsable" class="filter-select" onchange="filtrarPedidos()">
                            <option value="">Todos los responsables</option>
                            <option value="SIN_ASIGNAR">Sin asignar</option>
                            <!-- Cargar dinámicamente desde backend -->
                        </select>

                        <input type="text" id="searchPedido" class="search-input" 
                               placeholder="🔍 Buscar por código, paciente o odontólogo..." 
                               onkeyup="buscarPedidos()">
                    </div>

                    <div class="toolbar-section">
                        <div class="view-toggle">
                            <button class="view-btn active" onclick="cambiarVista('kanban')">
                                📋 Kanban
                            </button>
                            <button class="view-btn" onclick="cambiarVista('lista')">
                                📑 Lista
                            </button>
                        </div>
                    </div>
                </div>

                <div class="kanban-board" id="kanbanView">

                    <!-- RECEPCIÓN -->
                    <div class="kanban-column" data-estado="RECEPCION">
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
                            <div class="kanban-card <%= p.isAtrasado() ? "card-delayed" : ""%>" 
                                 data-prioridad="<%= p.getPrioridad()%>"
                                 data-codigo="<%= p.getCodigoUnico()%>"
                                 data-paciente="<%= p.getNombrePaciente()%>"
                                 data-id="<%= p.getId()%>">
                                <div class="card-header">
                                    <span class="card-code"><%= p.getCodigoUnico()%></span>
                                    <% if ("URGENTE".equals(p.getPrioridad())) { %>
                                    <span class="badge badge-urgent">🔥 Urgente</span>
                                    <% } else if ("EMERGENCIA".equals(p.getPrioridad())) { %>
                                    <span class="badge badge-emergency">⚡ Emergencia</span>
                                    <% }%>
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
                                    <span style="color: #e74c3c; font-weight: 600;">⚠️ Atrasado</span>
                                    <% } %>
                                </div>
                                <% if (p.getResponsableActual() != null && p.getNombreResponsable() != null) {%>
                                <div class="card-responsible">
                                    <span class="responsible-icon">👷</span>
                                    <span class="responsible-name"><%= p.getNombreResponsable()%></span>
                                </div>
                                <% }%>
                                <div class="card-actions">
                                    <button class="card-btn card-btn-assign" onclick="abrirModalAsignar(<%= p.getId()%>, '<%= p.getCodigoUnico()%>')">
                                        👤 Asignar
                                    </button>
                                    <button class="card-btn card-btn-move" 
                                            onclick="abrirModalActualizar(<%= p.getId()%>, '<%= p.getCodigoUnico()%>', '<%= p.getEstadoActual()%>')">
                                        ➡️ Mover
                                    </button>


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
                    <div class="kanban-column" data-estado="PARALELIZADO">
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
                            <div class="kanban-card <%= p.isAtrasado() ? "card-delayed" : ""%>" 
                                 data-prioridad="<%= p.getPrioridad()%>"
                                 data-codigo="<%= p.getCodigoUnico()%>"
                                 data-paciente="<%= p.getNombrePaciente()%>"
                                 data-id="<%= p.getId()%>">
                                <div class="card-header">
                                    <span class="card-code"><%= p.getCodigoUnico()%></span>
                                    <% if ("URGENTE".equals(p.getPrioridad())) { %>
                                    <span class="badge badge-urgent">🔥 Urgente</span>
                                    <% } else if ("EMERGENCIA".equals(p.getPrioridad())) { %>
                                    <span class="badge badge-emergency">⚡ Emergencia</span>
                                    <% }%>
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
                                <% if (p.getResponsableActual() != null && p.getNombreResponsable() != null) {%>
                                <div class="card-responsible">
                                    <span class="responsible-icon">👷</span>
                                    <span class="responsible-name"><%= p.getNombreResponsable()%></span>
                                </div>
                                <% }%>
                                <div class="card-actions">
                                    <button class="card-btn card-btn-assign" onclick="abrirModalAsignar(<%= p.getId()%>, '<%= p.getCodigoUnico()%>')">
                                        👤 Asignar
                                    </button>
                                    <button class="card-btn card-btn-move" 
                                            onclick="abrirModalActualizar(<%= p.getId()%>, '<%= p.getCodigoUnico()%>', '<%= p.getEstadoActual()%>')">
                                        ➡️ Mover
                                    </button>
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
                    <div class="kanban-column" data-estado="DISENO_CAD">
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
                            <div class="kanban-card <%= p.isAtrasado() ? "card-delayed" : ""%>" 
                                 data-prioridad="<%= p.getPrioridad()%>"
                                 data-codigo="<%= p.getCodigoUnico()%>"
                                 data-paciente="<%= p.getNombrePaciente()%>"
                                 data-id="<%= p.getId()%>">
                                <div class="card-header">
                                    <span class="card-code"><%= p.getCodigoUnico()%></span>
                                    <% if ("URGENTE".equals(p.getPrioridad())) { %>
                                    <span class="badge badge-urgent">🔥 Urgente</span>
                                    <% } else if ("EMERGENCIA".equals(p.getPrioridad())) { %>
                                    <span class="badge badge-emergency">⚡ Emergencia</span>
                                    <% }%>
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
                                <% if (p.getResponsableActual() != null && p.getNombreResponsable() != null) {%>
                                <div class="card-responsible">
                                    <span class="responsible-icon">👷</span>
                                    <span class="responsible-name"><%= p.getNombreResponsable()%></span>
                                </div>
                                <% }%>
                                <div class="card-actions">
                                    <button class="card-btn card-btn-assign" onclick="abrirModalAsignar(<%= p.getId()%>, '<%= p.getCodigoUnico()%>')">
                                        👤 Asignar
                                    </button>
                                    <button class="card-btn card-btn-move" 
                                            onclick="abrirModalActualizar(<%= p.getId()%>, '<%= p.getCodigoUnico()%>', '<%= p.getEstadoActual()%>')">
                                        ➡️ Mover
                                    </button>
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
                    <div class="kanban-column" data-estado="PRODUCCION_CAM">
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
                            <div class="kanban-card <%= p.isAtrasado() ? "card-delayed" : ""%>" 
                                 data-prioridad="<%= p.getPrioridad()%>"
                                 data-codigo="<%= p.getCodigoUnico()%>"
                                 data-paciente="<%= p.getNombrePaciente()%>"
                                 data-id="<%= p.getId()%>">
                                <div class="card-header">
                                    <span class="card-code"><%= p.getCodigoUnico()%></span>
                                    <% if ("URGENTE".equals(p.getPrioridad())) { %>
                                    <span class="badge badge-urgent">🔥 Urgente</span>
                                    <% } else if ("EMERGENCIA".equals(p.getPrioridad())) { %>
                                    <span class="badge badge-emergency">⚡ Emergencia</span>
                                    <% }%>
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
                                <% if (p.getResponsableActual() != null && p.getNombreResponsable() != null) {%>
                                <div class="card-responsible">
                                    <span class="responsible-icon">👷</span>
                                    <span class="responsible-name"><%= p.getNombreResponsable()%></span>
                                </div>
                                <% }%>
                                <div class="card-actions">
                                    <button class="card-btn card-btn-assign" onclick="abrirModalAsignar(<%= p.getId()%>, '<%= p.getCodigoUnico()%>')">
                                        👤 Asignar
                                    </button>
                                    <button class="card-btn card-btn-move" 
                                            onclick="abrirModalActualizar(<%= p.getId()%>, '<%= p.getCodigoUnico()%>', '<%= p.getEstadoActual()%>')">
                                        ➡️ Mover
                                    </button>
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
                    <div class="kanban-column" data-estado="CERAMICA">
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
                            <div class="kanban-card <%= p.isAtrasado() ? "card-delayed" : ""%>" 
                                 data-prioridad="<%= p.getPrioridad()%>"
                                 data-codigo="<%= p.getCodigoUnico()%>"
                                 data-paciente="<%= p.getNombrePaciente()%>"
                                 data-id="<%= p.getId()%>">
                                <div class="card-header">
                                    <span class="card-code"><%= p.getCodigoUnico()%></span>
                                    <% if ("URGENTE".equals(p.getPrioridad())) { %>
                                    <span class="badge badge-urgent">🔥 Urgente</span>
                                    <% } else if ("EMERGENCIA".equals(p.getPrioridad())) { %>
                                    <span class="badge badge-emergency">⚡ Emergencia</span>
                                    <% }%>
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
                                <% if (p.getResponsableActual() != null && p.getNombreResponsable() != null) {%>
                                <div class="card-responsible">
                                    <span class="responsible-icon">👷</span>
                                    <span class="responsible-name"><%= p.getNombreResponsable()%></span>
                                </div>
                                <% }%>
                                <div class="card-actions">
                                    <button class="card-btn card-btn-assign" onclick="abrirModalAsignar(<%= p.getId()%>, '<%= p.getCodigoUnico()%>')">
                                        👤 Asignar
                                    </button>
                                    <button class="card-btn card-btn-move" 
                                            onclick="abrirModalActualizar(<%= p.getId()%>, '<%= p.getCodigoUnico()%>', '<%= p.getEstadoActual()%>')">
                                        ➡️ Mover
                                    </button>
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
                    <div class="kanban-column" data-estado="CONTROL_CALIDAD">
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
                            <div class="kanban-card <%= p.isAtrasado() ? "card-delayed" : ""%>" 
                                 data-prioridad="<%= p.getPrioridad()%>"
                                 data-codigo="<%= p.getCodigoUnico()%>"
                                 data-paciente="<%= p.getNombrePaciente()%>"
                                 data-id="<%= p.getId()%>">
                                <div class="card-header">
                                    <span class="card-code"><%= p.getCodigoUnico()%></span>
                                    <% if ("URGENTE".equals(p.getPrioridad())) { %>
                                    <span class="badge badge-urgent">🔥 Urgente</span>
                                    <% } else if ("EMERGENCIA".equals(p.getPrioridad())) { %>
                                    <span class="badge badge-emergency">⚡ Emergencia</span>
                                    <% }%>
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
                                <% if (p.getResponsableActual() != null && p.getNombreResponsable() != null) {%>
                                <div class="card-responsible">
                                    <span class="responsible-icon">👷</span>
                                    <span class="responsible-name"><%= p.getNombreResponsable()%></span>
                                </div>
                                <% }%>
                                <div class="card-actions">
                                    <button class="card-btn card-btn-assign" onclick="abrirModalAsignar(<%= p.getId()%>, '<%= p.getCodigoUnico()%>')">
                                        👤 Asignar
                                    </button>
                                    <button class="card-btn card-btn-move" 
                                            onclick="abrirModalActualizar(<%= p.getId()%>, '<%= p.getCodigoUnico()%>', '<%= p.getEstadoActual()%>')">
                                        ➡️ Mover
                                    </button>
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
                    <div class="kanban-column" data-estado="LISTO_ENTREGA">
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
                            <div class="kanban-card" 
                                 data-prioridad="<%= p.getPrioridad()%>"
                                 data-codigo="<%= p.getCodigoUnico()%>"
                                 data-paciente="<%= p.getNombrePaciente()%>"
                                 data-id="<%= p.getId()%>"
                                 onclick="verDetalle(<%= p.getId()%>)">
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
                <p>&copy; 2025 LABDENT JLVEGA | Panel Administrador con Control Avanzado</p>
            </div>
        </footer>

        <!-- Modal Asignar Responsable -->
        <div id="modalAsignar" class="modal">
            <div class="modal-content">
                <div class="modal-header">
                    <h3 class="modal-title">👤 Asignar Responsable</h3>
                    <button class="modal-close" onclick="cerrarModalAsignar()">&times;</button>
                </div>

                <div class="modal-body">
                    <form id="formAsignar" action="AsignarResponsable" method="post">
                        <input type="hidden" name="pedidoId" id="asignarPedidoId">

                        <div class="form-group">
                            <label class="form-label">Código del Pedido:</label>
                            <div class="form-value" id="asignarCodigoPedido"></div>
                        </div>

                        <div class="form-group">
                            <label class="form-label">Responsable: *</label>
                            <select name="responsableId" id="asignarResponsable" class="form-select" required>
                                <option value="">Seleccionar responsable...</option>
                                <!-- Cargar dinámicamente desde backend -->
                            </select>
                        </div>

                        <div class="form-group">
                            <label class="form-label">Notas de asignación:</label>
                            <textarea name="notas" class="form-textarea" rows="3" 
                                      placeholder="Agregar instrucciones o comentarios..."></textarea>
                        </div>
                    </form>
                </div>

                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" onclick="cerrarModalAsignar()">
                        Cancelar
                    </button>
                    <button type="button" class="btn btn-success" onclick="enviarAsignacion()">
                        ✓ Asignar
                    </button>
                </div>
            </div>
        </div>

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



        <!-- Modal Asignación Masiva -->
        <div id="modalAsignacionMasiva" class="modal">
            <div class="modal-content">
                <div class="modal-header">
                    <h3 class="modal-title">👥 Asignación Masiva de Pedidos</h3>
                    <button class="modal-close" onclick="cerrarModalAsignacionMasiva()">&times;</button>
                </div>

                <div class="modal-body">
                    <form id="formAsignacionMasiva" action="AsignacionMasiva" method="post">
                        <div class="form-group">
                            <label class="form-label">Estado de pedidos: *</label>
                            <select name="estadoPedidos" id="masEstado" class="form-select" required onchange="cargarPedidosPorEstado()">
                                <option value="">Seleccionar estado...</option>
                                <option value="RECEPCION">📥 Recepción</option>
                                <option value="PARALELIZADO">🔄 Paralelizado</option>
                                <option value="DISENO_CAD">💻 Diseño CAD</option>
                                <option value="PRODUCCION_CAM">⚙️ Producción CAM</option>
                                <option value="CERAMICA">🎨 Cerámica</option>
                                <option value="CONTROL_CALIDAD">✓ Control Calidad</option>
                            </select>
                        </div>

                        <div class="form-group">
                            <label class="form-label">Criterio de asignación: *</label>
                            <select name="criterio" class="form-select" required>
                                <option value="">Seleccionar criterio...</option>
                                <option value="CARGA_BALANCEADA">⚖️ Distribuir equitativamente</option>
                                <option value="PRIORIDAD">🔥 Por prioridad</option>
                                <option value="FECHA_COMPROMISO">📅 Por fecha de compromiso</option>
                                <option value="ESPECIALIDAD">🎯 Por especialidad del técnico</option>
                            </select>
                        </div>

                        <div class="form-group">
                            <label class="form-label">Responsables disponibles: *</label>
                            <select name="responsables[]" id="masResponsables" class="form-select" multiple required size="5">
                                <!-- Cargar dinámicamente desde backend -->
                            </select>
                            <small class="form-help">Mantén presionado Ctrl/Cmd para seleccionar múltiples</small>
                        </div>

                        <div class="alert alert-warning">
                            <span>ℹ️</span>
                            <span>Esta acción asignará automáticamente los pedidos sin asignar del estado seleccionado a los responsables elegidos según el criterio definido.</span>
                        </div>

                        <div id="vistaPrevia" style="display: none; margin-top: 20px;">
                            <h4 style="margin-bottom: 10px;">Vista Previa de Asignación:</h4>
                            <div id="listadoPrevia" style="max-height: 200px; overflow-y: auto; border: 1px solid #e9ecef; border-radius: 8px; padding: 10px;">
                            </div>
                        </div>
                    </form>
                </div>

                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" onclick="cerrarModalAsignacionMasiva()">
                        Cancelar
                    </button>
                    <button type="button" class="btn btn-secondary" onclick="previsualizarAsignacion()">
                        👁️ Previsualizar
                    </button>
                    <button type="button" class="btn btn-success" onclick="enviarAsignacionMasiva()">
                        ✓ Asignar Todos
                    </button>
                </div>
            </div>
        </div>

        <script>
            let contadorEvidencias = 1;

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
            // ==================== FILTRADO Y BÚSQUEDA ====================

            function filtrarPedidos() {
                const estadoFilter = document.getElementById('filterEstado').value;
                const prioridadFilter = document.getElementById('filterPrioridad').value;
                const responsableFilter = document.getElementById('filterResponsable').value;
                const cards = document.querySelectorAll('.kanban-card');
                cards.forEach(card => {
                    const estadoColumna = card.closest('.kanban-column').getAttribute('data-estado');
                    const prioridad = card.getAttribute('data-prioridad');
                    const responsable = card.querySelector('.responsible-name')?.textContent || 'SIN_ASIGNAR';
                    let mostrar = true;
                    if (estadoFilter && estadoColumna !== estadoFilter) {
                        mostrar = false;
                    }

                    if (prioridadFilter && prioridad !== prioridadFilter) {
                        mostrar = false;
                    }

                    if (responsableFilter && responsableFilter !== 'SIN_ASIGNAR' && !responsable.includes(responsableFilter)) {
                        mostrar = false;
                    }

                    if (responsableFilter === 'SIN_ASIGNAR' && card.querySelector('.card-responsible')) {
                        mostrar = false;
                    }

                    card.style.display = mostrar ? '' : 'none';
                });
                actualizarContadores();
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
                actualizarContadores();
            }

            function actualizarContadores() {
                const columns = document.querySelectorAll('.kanban-column');
                columns.forEach(col => {
                    const visibleCards = col.querySelectorAll('.kanban-card:not([style*="display: none"])').length;
                    const counter = col.querySelector('.column-count');
                    if (counter) {
                        counter.textContent = visibleCards;
                    }
                });
            }

            function cambiarVista(vista) {
                const btns = document.querySelectorAll('.view-btn');
                btns.forEach(btn => btn.classList.remove('active'));
                event.target.classList.add('active');
                if (vista === 'lista') {
                    alert('Vista de lista en desarrollo. Próximamente disponible.');
                }
            }

            // ==================== MODAL ASIGNAR ====================

            function abrirModalAsignar(pedidoId, codigoPedido) {
                event.stopPropagation();
                document.getElementById('asignarPedidoId').value = pedidoId;
                document.getElementById('asignarCodigoPedido').textContent = codigoPedido;
                // Cargar responsables disponibles (simulado, debe venir del backend)
                cargarResponsables('asignarResponsable');
                document.getElementById('modalAsignar').style.display = 'flex';
            }

            function cerrarModalAsignar() {
                document.getElementById('modalAsignar').style.display = 'none';
                document.getElementById('formAsignar').reset();
            }

            async function enviarAsignacion() {
                const form = document.getElementById('formAsignar');
                const responsable = document.getElementById('asignarResponsable').value;
                if (!responsable) {
                    alert('⚠️ Debes seleccionar un responsable');
                    return;
                }

                try {
                    const formData = new FormData(form);
                    const response = await fetch('AsignarResponsable', {
                        method: 'POST',
                        body: formData
                    });
                    if (response.ok) {
                        alert('✅ Responsable asignado correctamente');
                        cerrarModalAsignar();
                        window.location.reload();
                    } else {
                        throw new Error('Error al asignar responsable');
                    }
                } catch (error) {
                    console.error('Error:', error);
                    alert('❌ Error al asignar: ' + error.message);
                }
            }

            // ==================== MODAL MOVER ====================

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

            // ==================== MODAL ASIGNACIÓN MASIVA ====================

            function abrirModalAsignacionMasiva() {
                cargarResponsables('masResponsables');
                document.getElementById('modalAsignacionMasiva').style.display = 'flex';
            }

            function cerrarModalAsignacionMasiva() {
                document.getElementById('modalAsignacionMasiva').style.display = 'none';
                document.getElementById('formAsignacionMasiva').reset();
                document.getElementById('vistaPrevia').style.display = 'none';
            }

            function cargarPedidosPorEstado() {
                const estado = document.getElementById('masEstado').value;
                if (!estado)
                    return;

                // Aquí se debería hacer una petición al backend para obtener los pedidos sin asignar
                console.log('Cargando pedidos del estado:', estado);
            }

            async function previsualizarAsignacion() {
                const estado = document.getElementById('masEstado').value;
                const criterio = document.querySelector('select[name="criterio"]').value;
                const responsables = Array.from(document.getElementById('masResponsables').selectedOptions).map(opt => opt.value);

                if (!estado || !criterio || responsables.length === 0) {
                    alert('⚠️ Completa todos los campos primero');
                    return;
                }

                try {
                    const response = await fetch('PrevisualizarAsignacion', {
                        method: 'POST',
                        headers: {'Content-Type': 'application/json'},
                        body: JSON.stringify({estado, criterio, responsables})
                    });

                    if (response.ok) {
                        const preview = await response.json();
                        mostrarVistaPrevia(preview);
                    } else {
                        throw new Error('Error al obtener vista previa');
                    }
                } catch (error) {
                    console.error('Error:', error);
                    alert('❌ Error al previsualizar: ' + error.message);
                }
            }

            function mostrarVistaPrevia(preview) {
                const container = document.getElementById('listadoPrevia');
                container.innerHTML = '';

                preview.forEach(item => {
                    const div = document.createElement('div');
                    div.style.cssText = 'padding: 8px; border-bottom: 1px solid #e9ecef; display: flex; justify-content: space-between;';
                    div.innerHTML = `
                        <span><strong>${item.codigoPedido}</strong> - ${item.paciente}</span>
                        <span style="color: #667eea; font-weight: 600;">→ ${item.responsable}</span>
                    `;
                    container.appendChild(div);
                });

                document.getElementById('vistaPrevia').style.display = 'block';
            }

            async function enviarAsignacionMasiva() {
                const form = document.getElementById('formAsignacionMasiva');
                const estado = document.getElementById('masEstado').value;
                const criterio = form.querySelector('select[name="criterio"]').value;
                const responsables = Array.from(document.getElementById('masResponsables').selectedOptions).map(opt => opt.value);

                if (!estado || !criterio || responsables.length === 0) {
                    alert('⚠️ Completa todos los campos');
                    return;
                }

                if (!confirm(`¿Confirmas la asignación masiva de pedidos en estado ${ESTADO_DESCRIPCION[estado]}?`)) {
                    return;
                }

                try {
                    const response = await fetch('AsignacionMasiva', {
                        method: 'POST',
                        headers: {'Content-Type': 'application/json'},
                        body: JSON.stringify({estado, criterio, responsables})
                    });

                    if (response.ok) {
                        const result = await response.json();
                        alert(`✅ Se asignaron ${result.asignados} pedidos correctamente`);
                        cerrarModalAsignacionMasiva();
                        window.location.reload();
                    } else {
                        throw new Error('Error al realizar asignación masiva');
                    }
                } catch (error) {
                    console.error('Error:', error);
                    alert('❌ Error al asignar: ' + error.message);
                }
            }

            // ==================== FUNCIONES AUXILIARES ====================

            function cargarResponsables(selectId) {
                const select = document.getElementById(selectId);
                // Simulado - Debe venir del backend
                const responsables = [
                    {id: 1, nombre: 'Juan Pérez - Técnico CAD'},
                    {id: 2, nombre: 'María García - Técnico CAM'},
                    {id: 3, nombre: 'Carlos López - Ceramista'},
                    {id: 4, nombre: 'Ana Martínez - Control Calidad'}
                ];

                select.innerHTML = '<option value="">Seleccionar...</option>';
                responsables.forEach(resp => {
                    const option = document.createElement('option');
                    option.value = resp.id;
                    option.textContent = resp.nombre;
                    select.appendChild(option);
                });
            }

            function verDetalle(pedidoId) {
                window.location.href = 'ver-pedido?id=' + pedidoId;
            }

            function exportarReporte() {
                if (confirm('¿Deseas exportar el reporte del estado actual del tablero?')) {
                    window.location.href = 'ExportarReporte?tipo=kanban&formato=excel';
                }
            }
            window.onclick = function (event) {
                const modal = document.getElementById('modalActualizar');
                if (event.target === modal) {
                    cerrarModalActualizar();
                }
            }

            // ==================== EVENT LISTENERS ====================

            window.onclick = function (event) {
                if (event.target.classList.contains('modal')) {
                    event.target.style.display = 'none';
                }
            }

            // Auto-cerrar alertas
            setTimeout(() => {
                document.querySelectorAll('.alert').forEach(alert => {
                    alert.style.animation = 'slideOut 0.3s ease';
                    setTimeout(() => alert.remove(), 300);
                });
            }, 5000);

            // Actualización automática cada 2 minutos
            setInterval(() => {
                console.log('🔄 Verificando actualizaciones...');
                // Aquí podrías implementar una actualización AJAX sin recargar la página
            }, 120000);

            console.log('✅ Panel Administrador cargado - <%= totalPedidos%> pedidos en producción');
        </script>

    </body>
</html>