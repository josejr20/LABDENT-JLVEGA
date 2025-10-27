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
    
    // Estadísticas
    int totalPedidos = pedidos != null ? pedidos.size() : 0;
    int enProceso = 0;
    int completados = 0;
    int atrasados = 0;
    int urgentes = 0;
    
    if (pedidos != null) {
        for (Pedido p : pedidos) {
            if ("ENTREGADO".equals(p.getEstadoActual())) {
                completados++;
            } else {
                enProceso++;
                if (p.isAtrasado()) {
                    atrasados++;
                }
            }
            if ("URGENTE".equals(p.getPrioridad())) {
                urgentes++;
            }
        }
    }
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Mis Pedidos - LABDENT JLVEGA</title>
    <link rel="stylesheet" href="css/misPedidos.css">
</head>
<body>

<!-- Header -->
<header class="header">
    <div class="container">
        <div class="header-content">
            <div class="logo">
                <h1>🦷 LABDENT</h1>
            </div>
            <nav class="nav">
                <% if (usuario.isAdmin()) { %>
                <a href="dashboard" class="nav-link">
                    <span class="icon">📊</span>
                    <span>Dashboard</span>
                </a>
                <% } %>
                <a href="kanban" class="nav-link">
                    <span class="icon">📋</span>
                    <span>Tablero Kanban</span>
                </a>
                <a href="misPedidos" class="nav-link active">
                    <span class="icon">📦</span>
                    <span>Mis Pedidos</span>
                </a>
                <a href="registro-pedido" class="nav-link">
                    <span class="icon">➕</span>
                    <span>Nuevo Pedido</span>
                </a>
            </nav>
            <div class="user-menu">
                <div class="user-info">
                    <span class="user-avatar">👤</span>
                    <div class="user-details">
                        <span class="user-name"><%= usuario.getNombreCompleto() %></span>
                        <span class="user-role"><%= usuario.getRol() %></span>
                    </div>
                </div>
                <a href="logout" class="btn-logout">Cerrar Sesión</a>
            </div>
        </div>
    </div>
</header>

<!-- Main Content -->
<main class="main-content">
    <div class="container">
        
        <!-- Breadcrumb -->
        <div class="breadcrumb">
            <a href="dashboard" class="breadcrumb-link">
                <span>🏠</span> Inicio
            </a>
            <span class="breadcrumb-separator">›</span>
            <span class="breadcrumb-current">Mis Pedidos</span>
        </div>

        <!-- Page Header -->
        <div class="page-header">
            <div>
                <h1 class="page-title">📦 Mis Pedidos</h1>
                <p class="page-subtitle">Seguimiento completo de todos tus trabajos dentales</p>
            </div>
            <div class="page-actions">
                <button onclick="window.location.reload()" class="btn-icon" title="Actualizar">
                    🔄
                </button>
                <a href="registro-pedido" class="btn btn-primary">
                    <span>➕</span> Nuevo Pedido
                </a>
            </div>
        </div>

        <!-- Stats Cards -->
        <div class="stats-grid">
            <div class="stat-card stat-card-blue">
                <div class="stat-icon">📊</div>
                <div class="stat-info">
                    <h3 class="stat-title">Total Pedidos</h3>
                    <p class="stat-number"><%= totalPedidos %></p>
                </div>
            </div>
            
            <div class="stat-card stat-card-orange">
                <div class="stat-icon">⏳</div>
                <div class="stat-info">
                    <h3 class="stat-title">En Proceso</h3>
                    <p class="stat-number"><%= enProceso %></p>
                </div>
            </div>
            
            <div class="stat-card stat-card-green">
                <div class="stat-icon">✅</div>
                <div class="stat-info">
                    <h3 class="stat-title">Completados</h3>
                    <p class="stat-number"><%= completados %></p>
                </div>
            </div>
            
            <div class="stat-card stat-card-red">
                <div class="stat-icon">⚠️</div>
                <div class="stat-info">
                    <h3 class="stat-title">Atrasados</h3>
                    <p class="stat-number"><%= atrasados %></p>
                </div>
            </div>
        </div>

        <!-- Filters -->
        <div class="filters-bar">
            <div class="search-box">
                <span class="search-icon">🔍</span>
                <input type="text" id="searchInput" placeholder="Buscar por código, paciente..." class="search-input">
            </div>
            <div class="filter-group">
                <select id="filterEstado" class="filter-select">
                    <option value="">Todos los estados</option>
                    <option value="RECEPCION">Recepción</option>
                    <option value="PARALELIZADO">Paralelizado</option>
                    <option value="DISENO_CAD">Diseño CAD</option>
                    <option value="PRODUCCION_CAM">Producción CAM</option>
                    <option value="CERAMICA">Cerámica</option>
                    <option value="CONTROL_CALIDAD">Control Calidad</option>
                    <option value="LISTO_ENTREGA">Listo Entrega</option>
                    <option value="ENTREGADO">Entregado</option>
                </select>
                <select id="filterPrioridad" class="filter-select">
                    <option value="">Todas las prioridades</option>
                    <option value="NORMAL">Normal</option>
                    <option value="URGENTE">Urgente</option>
                    <option value="EMERGENCIA">Emergencia</option>
                </select>
            </div>
        </div>

        <!-- Table Container -->
        <div class="table-container">
            <% if (pedidos != null && !pedidos.isEmpty()) { %>
            <table class="table-pedidos" id="tablePedidos">
                <thead>
                    <tr>
                        <th>Código</th>
                        <th>Paciente</th>
                        <th>Trabajo</th>
                        <th>Estado Actual</th>
                        <th>Prioridad</th>
                        <th>Fecha Ingreso</th>
                        <th>Fecha Compromiso</th>
                        <th>Tiempo Restante</th>
                        <th>Acciones</th>
                    </tr>
                </thead>
                <tbody>
                    <% for (Pedido p : pedidos) { %>
                    <tr class="table-row" data-estado="<%= p.getEstadoActual() %>" data-prioridad="<%= p.getPrioridad() %>">
                        <td>
                            <span class="pedido-code"><%= p.getCodigoUnico() %></span>
                        </td>
                        <td>
                            <div class="patient-info">
                                <span class="patient-icon">👤</span>
                                <strong><%= p.getNombrePaciente() %></strong>
                            </div>
                        </td>
                        <td>
                            <div class="work-info">
                                <strong class="work-type">🦷 <%= p.getTipoProtesis() %></strong>
                                <span class="work-material">💎 <%= p.getMaterial() %></span>
                            </div>
                        </td>
                        <td>
                            <span class="badge badge-<%= p.getEstadoActual().toLowerCase() %>">
                                <%= p.getEstadoDescripcion() %>
                            </span>
                        </td>
                        <td>
                            <% if ("URGENTE".equals(p.getPrioridad())) { %>
                                <span class="badge badge-priority-urgent">🔥 Urgente</span>
                            <% } else if ("EMERGENCIA".equals(p.getPrioridad())) { %>
                                <span class="badge badge-priority-emergency">⚡ Emergencia</span>
                            <% } else { %>
                                <span class="badge badge-priority-normal">Normal</span>
                            <% } %>
                        </td>
                        <td>
                            <div class="date-info">
                                <span class="date-icon">📅</span>
                                <%= p.getFechaIngreso() %>
                            </div>
                        </td>
                        <td>
                            <div class="date-info">
                                <span class="date-icon">🎯</span>
                                <%= p.getFechaCompromiso() %>
                            </div>
                        </td>
                        <td>
                            <% if ("ENTREGADO".equals(p.getEstadoActual())) { %>
                                <span class="status-chip status-delivered">
                                    <span class="status-icon">✅</span>
                                    <span>Entregado</span>
                                </span>
                            <% } else if (p.isAtrasado()) { %>
                                <span class="status-chip status-delayed">
                                    <span class="status-icon">⚠️</span>
                                    <span>Atrasado <%= Math.abs(p.getDiasRestantes()) %>d</span>
                                </span>
                            <% } else { %>
                                <span class="status-chip status-ontime">
                                    <span class="status-icon">⏱️</span>
                                    <span><%= p.getDiasRestantes() %> días</span>
                                </span>
                            <% } %>
                        </td>
                        <td>
                            <div class="action-buttons">
                                <a href="ver-pedido?id=<%= p.getId() %>" class="btn-action btn-view" title="Ver detalle">
                                    👁️
                                </a>
                                <% if (!"ENTREGADO".equals(p.getEstadoActual())) { %>
                                <a href="editar-pedido?id=<%= p.getId() %>" class="btn-action btn-edit" title="Editar">
                                    ✏️
                                </a>
                                <% } %>
                            </div>
                        </td>
                    </tr>
                    <% } %>
                </tbody>
            </table>
            <% } else { %>
            <div class="empty-state-large">
                <div class="empty-icon">📦</div>
                <h3>No tienes pedidos registrados</h3>
                <p>Comienza creando tu primer pedido para dar seguimiento a tus trabajos dentales</p>
                <a href="registro-pedido" class="btn btn-primary btn-large">
                    <span>➕</span> Registrar Primer Pedido
                </a>
            </div>
            <% } %>
        </div>

    </div>
</main>

<!-- Footer -->
<footer class="footer">
    <div class="container">
        <p>&copy; 2025 LABDENT JLVEGA | Sistema de Gestión Dental</p>
    </div>
</footer>

<script>
    // Búsqueda en tiempo real
    const searchInput = document.getElementById('searchInput');
    const filterEstado = document.getElementById('filterEstado');
    const filterPrioridad = document.getElementById('filterPrioridad');
    const tableRows = document.querySelectorAll('.table-row');

    function filterTable() {
        const searchTerm = searchInput.value.toLowerCase();
        const estadoFilter = filterEstado.value;
        const prioridadFilter = filterPrioridad.value;

        tableRows.forEach(row => {
            const text = row.textContent.toLowerCase();
            const estado = row.dataset.estado;
            const prioridad = row.dataset.prioridad;

            const matchSearch = text.includes(searchTerm);
            const matchEstado = !estadoFilter || estado === estadoFilter;
            const matchPrioridad = !prioridadFilter || prioridad === prioridadFilter;

            if (matchSearch && matchEstado && matchPrioridad) {
                row.style.display = '';
            } else {
                row.style.display = 'none';
            }
        });
    }

    searchInput.addEventListener('input', filterTable);
    filterEstado.addEventListener('change', filterTable);
    filterPrioridad.addEventListener('change', filterTable);

    console.log('✅ Mis Pedidos cargado - <%= totalPedidos %> pedidos');
</script>

</body>
</html>