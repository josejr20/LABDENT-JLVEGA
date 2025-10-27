<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.labdent.model.Usuario" %>
<%@ page import="com.labdent.model.Pedido" %>
<%@ page import="com.labdent.model.TransicionEstado" %>
<%@ page import="com.labdent.model.Evidencia" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%
    Usuario usuario = (Usuario) session.getAttribute("usuario");
    if (usuario == null) {
        response.sendRedirect("login");
        return;
    }
    
    Pedido pedido = (Pedido) request.getAttribute("pedido");
    if (pedido == null) {
        response.sendRedirect("KanbanUsuarioServlet");
        return;
    }
    
    List<TransicionEstado> historial = (List<TransicionEstado>) request.getAttribute("historial");
    List<Evidencia> evidencias = (List<Evidencia>) request.getAttribute("evidencias");
    
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Detalle del Pedido - <%= pedido.getCodigoUnico() %></title>
    <link rel="stylesheet" href="css/detallePedido.css">
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
                <% if (usuario.isCliente()) { %>
                <a href="PanelUsuario.jsp" class="nav-link">
                    <span class="icon">🏠</span>
                    <span>Inicio</span>
                </a>
                <a href="mis-pedidos-cliente" class="nav-link">
                    <span class="icon">📋</span>
                    <span>Mis Pedidos</span>
                </a>
                <a href="KanbanUsuarioServlet" class="nav-link">
                    <span class="icon">📊</span>
                    <span>Kanban</span>
                </a>
                <% } else if (usuario.isOdontologo()) { %>
                <a href="dashboard" class="nav-link">
                    <span class="icon">🏠</span>
                    <span>Dashboard</span>
                </a>
                <a href="mis-pedidos" class="nav-link">
                    <span class="icon">📋</span>
                    <span>Mis Pedidos</span>
                </a>
                <a href="kanban" class="nav-link">
                    <span class="icon">📊</span>
                    <span>Kanban</span>
                </a>
                <% } else { %>
                <a href="dashboard" class="nav-link">
                    <span class="icon">🏠</span>
                    <span>Dashboard</span>
                </a>
                <a href="kanban" class="nav-link">
                    <span class="icon">📊</span>
                    <span>Kanban</span>
                </a>
                <% } %>
            </nav>
            <div class="user-menu">
                <div class="user-info">
                    <span class="user-avatar">👤</span>
                    <div class="user-details">
                        <span class="user-name"><%= usuario.getNombreCompleto()%></span>
                        <span class="user-role"><%= usuario.getRol()%></span>
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
            <% if (usuario.isCliente()) { %>
            <a href="PanelUsuario.jsp" class="breadcrumb-link">
                <span>🏠</span> Panel
            </a>
            <span class="breadcrumb-separator">›</span>
            <a href="KanbanUsuarioServlet" class="breadcrumb-link">
                <span>📊</span> Kanban
            </a>
            <% } else { %>
            <a href="dashboard" class="breadcrumb-link">
                <span>🏠</span> Dashboard
            </a>
            <span class="breadcrumb-separator">›</span>
            <a href="kanban" class="breadcrumb-link">
                <span>📊</span> Kanban
            </a>
            <% } %>
            <span class="breadcrumb-separator">›</span>
            <span class="breadcrumb-current">Detalle del Pedido</span>
        </div>

        <!-- Header del Pedido -->
        <div class="pedido-header">
            <div class="pedido-header-left">
                <h1 class="pedido-title">
                    <span class="pedido-icon">📦</span>
                    <%= pedido.getCodigoUnico() %>
                </h1>
                <div class="pedido-badges">
                    <span class="badge badge-<%= pedido.getEstadoActual().toLowerCase() %>">
                        <%= pedido.getEstadoDescripcion() %>
                    </span>
                    <% if ("URGENTE".equals(pedido.getPrioridad())) { %>
                    <span class="badge badge-priority-urgent">🔥 Urgente</span>
                    <% } else if ("EMERGENCIA".equals(pedido.getPrioridad())) { %>
                    <span class="badge badge-priority-emergency">⚡ Emergencia</span>
                    <% } %>
                    <% if (pedido.isAtrasado()) { %>
                    <span class="badge badge-danger">⚠️ Atrasado</span>
                    <% } %>
                </div>
            </div>
            <div class="pedido-header-right">
                <% if (usuario.isCliente()) { %>
                <a href="KanbanUsuarioServlet" class="btn btn-secondary">
                    ← Volver al Kanban
                </a>
                <% } else { %>
                <a href="kanban" class="btn btn-secondary">
                    ← Volver al Kanban
                </a>
                <% if (usuario.isOdontologo() && !"ENTREGADO".equals(pedido.getEstadoActual())) { %>
                <a href="editar-pedido?id=<%= pedido.getId() %>" class="btn btn-primary">
                    ✏️ Editar
                </a>
                <% } %>
                <% } %>
            </div>
        </div>

        <!-- Grid Principal -->
        <div class="detail-grid">
            
            <!-- Columna Izquierda -->
            <div class="detail-column">
                
                <!-- Información del Paciente -->
                <div class="info-card">
                    <div class="card-header">
                        <h2 class="card-title">
                            <span class="card-icon">👤</span>
                            Información del Paciente
                        </h2>
                    </div>
                    <div class="card-body">
                        <div class="info-row">
                            <span class="info-label">Nombre del Paciente:</span>
                            <span class="info-value"><strong><%= pedido.getNombrePaciente() %></strong></span>
                        </div>
                        <div class="info-row">
                            <span class="info-label">Piezas Dentales:</span>
                            <span class="info-value"><%= pedido.getPiezasDentales() %></span>
                        </div>
                    </div>
                </div>

                <!-- Información del Trabajo -->
                <div class="info-card">
                    <div class="card-header">
                        <h2 class="card-title">
                            <span class="card-icon">🦷</span>
                            Detalles del Trabajo
                        </h2>
                    </div>
                    <div class="card-body">
                        <div class="info-row">
                            <span class="info-label">Tipo de Prótesis:</span>
                            <span class="info-value"><strong><%= pedido.getTipoProtesis() %></strong></span>
                        </div>
                        <div class="info-row">
                            <span class="info-label">Material:</span>
                            <span class="info-value">💎 <%= pedido.getMaterial() %></span>
                        </div>
                        <% if (pedido.getColorShade() != null) { %>
                        <div class="info-row">
                            <span class="info-label">Color/Shade:</span>
                            <span class="info-value">🎨 <%= pedido.getColorShade() %></span>
                        </div>
                        <% } %>
                    </div>
                </div>

                <!-- Fechas Importantes -->
                <div class="info-card">
                    <div class="card-header">
                        <h2 class="card-title">
                            <span class="card-icon">📅</span>
                            Fechas Importantes
                        </h2>
                    </div>
                    <div class="card-body">
                        <div class="info-row">
                            <span class="info-label">Fecha de Ingreso:</span>
                            <span class="info-value"><%= sdf.format(pedido.getFechaIngreso()) %></span>
                        </div>
                        <div class="info-row">
                            <span class="info-label">Fecha Compromiso:</span>
                            <span class="info-value date-highlight">
                                🎯 <%= pedido.getFechaCompromiso() %>
                            </span>
                        </div>
                        <% if (pedido.getFechaEntrega() != null) { %>
                        <div class="info-row">
                            <span class="info-label">Fecha de Entrega:</span>
                            <span class="info-value success-text">
                                ✅ <%= sdf.format(pedido.getFechaEntrega()) %>
                            </span>
                        </div>
                        <% } else { %>
                        <div class="info-row">
                            <span class="info-label">Tiempo Restante:</span>
                            <span class="info-value <%= pedido.isAtrasado() ? "danger-text" : "success-text" %>">
                                <% if (pedido.isAtrasado()) { %>
                                    ⚠️ Atrasado <%= Math.abs(pedido.getDiasRestantes()) %> días
                                <% } else { %>
                                    ⏱️ <%= pedido.getDiasRestantes() %> días restantes
                                <% } %>
                            </span>
                        </div>
                        <% } %>
                    </div>
                </div>

                <!-- Información del Odontólogo -->
                <% if (pedido.getNombreOdontologo() != null) { %>
                <div class="info-card">
                    <div class="card-header">
                        <h2 class="card-title">
                            <span class="card-icon">👨‍⚕️</span>
                            Odontólogo Solicitante
                        </h2>
                    </div>
                    <div class="card-body">
                        <div class="info-row">
                            <span class="info-label">Nombre:</span>
                            <span class="info-value"><%= pedido.getNombreOdontologo() %></span>
                        </div>
                        <% if (pedido.getEmailOdontologo() != null) { %>
                        <div class="info-row">
                            <span class="info-label">Email:</span>
                            <span class="info-value">📧 <%= pedido.getEmailOdontologo() %></span>
                        </div>
                        <% } %>
                        <% if (pedido.getTelefonoOdontologo() != null) { %>
                        <div class="info-row">
                            <span class="info-label">Teléfono:</span>
                            <span class="info-value">📱 <%= pedido.getTelefonoOdontologo() %></span>
                        </div>
                        <% } %>
                    </div>
                </div>
                <% } %>

                <!-- Responsable Actual -->
                <% if (pedido.getNombreResponsable() != null) { %>
                <div class="info-card">
                    <div class="card-header">
                        <h2 class="card-title">
                            <span class="card-icon">👷</span>
                            Responsable Actual
                        </h2>
                    </div>
                    <div class="card-body">
                        <div class="info-row">
                            <span class="info-label">Técnico Asignado:</span>
                            <span class="info-value"><strong><%= pedido.getNombreResponsable() %></strong></span>
                        </div>
                    </div>
                </div>
                <% } %>

                <!-- Observaciones -->
                <% if (pedido.getObservaciones() != null && !pedido.getObservaciones().trim().isEmpty()) { %>
                <div class="info-card">
                    <div class="card-header">
                        <h2 class="card-title">
                            <span class="card-icon">📝</span>
                            Observaciones
                        </h2>
                    </div>
                    <div class="card-body">
                        <p class="observaciones-text"><%= pedido.getObservaciones() %></p>
                    </div>
                </div>
                <% } %>

            </div>

            <!-- Columna Derecha -->
            <div class="detail-column">
                
                <!-- Progreso del Pedido -->
                <div class="info-card">
                    <div class="card-header">
                        <h2 class="card-title">
                            <span class="card-icon">📊</span>
                            Progreso del Trabajo
                        </h2>
                    </div>
                    <div class="card-body">
                        <div class="progress-timeline">
                            <div class="timeline-item <%= "RECEPCION".equals(pedido.getEstadoActual()) || historial.stream().anyMatch(t -> "RECEPCION".equals(t.getEstadoNuevo())) ? "completed" : "" %> 
                                                        <%= "RECEPCION".equals(pedido.getEstadoActual()) ? "active" : "" %>">
                                <div class="timeline-marker">📥</div>
                                <div class="timeline-content">
                                    <div class="timeline-title">Recepción</div>
                                    <div class="timeline-description">Pedido recibido e ingresado</div>
                                </div>
                            </div>

                            <div class="timeline-item <%= "PARALELIZADO".equals(pedido.getEstadoActual()) || historial.stream().anyMatch(t -> "PARALELIZADO".equals(t.getEstadoNuevo())) ? "completed" : "" %> 
                                                        <%= "PARALELIZADO".equals(pedido.getEstadoActual()) ? "active" : "" %>">
                                <div class="timeline-marker">🔄</div>
                                <div class="timeline-content">
                                    <div class="timeline-title">Paralelizado</div>
                                    <div class="timeline-description">Preparación inicial</div>
                                </div>
                            </div>

                            <div class="timeline-item <%= "DISENO_CAD".equals(pedido.getEstadoActual()) || historial.stream().anyMatch(t -> "DISENO_CAD".equals(t.getEstadoNuevo())) ? "completed" : "" %> 
                                                        <%= "DISENO_CAD".equals(pedido.getEstadoActual()) ? "active" : "" %>">
                                <div class="timeline-marker">💻</div>
                                <div class="timeline-content">
                                    <div class="timeline-title">Diseño CAD</div>
                                    <div class="timeline-description">Diseño digital</div>
                                </div>
                            </div>

                            <div class="timeline-item <%= "PRODUCCION_CAM".equals(pedido.getEstadoActual()) || historial.stream().anyMatch(t -> "PRODUCCION_CAM".equals(t.getEstadoNuevo())) ? "completed" : "" %> 
                                                        <%= "PRODUCCION_CAM".equals(pedido.getEstadoActual()) ? "active" : "" %>">
                                <div class="timeline-marker">⚙️</div>
                                <div class="timeline-content">
                                    <div class="timeline-title">Producción CAM</div>
                                    <div class="timeline-description">Fresado y mecanizado</div>
                                </div>
                            </div>

                            <div class="timeline-item <%= "CERAMICA".equals(pedido.getEstadoActual()) || historial.stream().anyMatch(t -> "CERAMICA".equals(t.getEstadoNuevo())) ? "completed" : "" %> 
                                                        <%= "CERAMICA".equals(pedido.getEstadoActual()) ? "active" : "" %>">
                                <div class="timeline-marker">🎨</div>
                                <div class="timeline-content">
                                    <div class="timeline-title">Cerámica</div>
                                    <div class="timeline-description">Caracterización</div>
                                </div>
                            </div>

                            <div class="timeline-item <%= "CONTROL_CALIDAD".equals(pedido.getEstadoActual()) || historial.stream().anyMatch(t -> "CONTROL_CALIDAD".equals(t.getEstadoNuevo())) ? "completed" : "" %> 
                                                        <%= "CONTROL_CALIDAD".equals(pedido.getEstadoActual()) ? "active" : "" %>">
                                <div class="timeline-marker">✓</div>
                                <div class="timeline-content">
                                    <div class="timeline-title">Control de Calidad</div>
                                    <div class="timeline-description">Verificación final</div>
                                </div>
                            </div>

                            <div class="timeline-item <%= "LISTO_ENTREGA".equals(pedido.getEstadoActual()) || "ENTREGADO".equals(pedido.getEstadoActual()) ? "completed" : "" %> 
                                                        <%= "LISTO_ENTREGA".equals(pedido.getEstadoActual()) ? "active" : "" %>">
                                <div class="timeline-marker">📦</div>
                                <div class="timeline-content">
                                    <div class="timeline-title">Listo para Entrega</div>
                                    <div class="timeline-description">Disponible para recoger</div>
                                </div>
                            </div>

                            <% if ("ENTREGADO".equals(pedido.getEstadoActual())) { %>
                            <div class="timeline-item completed active">
                                <div class="timeline-marker">✅</div>
                                <div class="timeline-content">
                                    <div class="timeline-title">Entregado</div>
                                    <div class="timeline-description">Trabajo finalizado</div>
                                </div>
                            </div>
                            <% } %>
                        </div>
                    </div>
                </div>

                <!-- Historial de Transiciones -->
                <% if (historial != null && !historial.isEmpty()) { %>
                <div class="info-card">
                    <div class="card-header">
                        <h2 class="card-title">
                            <span class="card-icon">📜</span>
                            Historial de Movimientos
                        </h2>
                    </div>
                    <div class="card-body">
                        <div class="historial-list">
                            <% for (TransicionEstado t : historial) { %>
                            <div class="historial-item">
                                <div class="historial-header">
                                    <span class="historial-badge badge-<%= t.getEstadoNuevo().toLowerCase() %>">
                                        <%= t.getEstadoNuevoDescripcion() %>
                                    </span>
                                    <span class="historial-date">
                                        <%= sdf.format(t.getFechaTransicion()) %>
                                    </span>
                                </div>
                                <div class="historial-content">
                                    <div class="historial-user">
                                        <span class="user-icon">👤</span>
                                        <%= t.getNombreUsuario() %>
                                    </div>
                                    <% if (t.getObservaciones() != null && !t.getObservaciones().trim().isEmpty()) { %>
                                    <div class="historial-obs">
                                        💬 <%= t.getObservaciones() %>
                                    </div>
                                    <% } %>
                                    <% if (t.getTiempoEnEstado() != null && t.getTiempoEnEstado() > 0) { %>
                                    <div class="historial-time">
                                        ⏱️ Tiempo en estado anterior: <%= t.getTiempoEnEstado() %> minutos
                                    </div>
                                    <% } %>
                                </div>
                            </div>
                            <% } %>
                        </div>
                    </div>
                </div>
                <% } %>

                <!-- Evidencias/Archivos -->
                <% if (evidencias != null && !evidencias.isEmpty()) { %>
                <div class="info-card">
                    <div class="card-header">
                        <h2 class="card-title">
                            <span class="card-icon">📎</span>
                            Archivos y Evidencias
                        </h2>
                    </div>
                    <div class="card-body">
                        <div class="evidencias-grid">
                            <% for (Evidencia e : evidencias) { %>
                            <div class="evidencia-item">
                                <div class="evidencia-icon">
                                    <%= e.getIcono() %>
                                </div>
                                <div class="evidencia-info">
                                    <div class="evidencia-name">
                                        <%= e.getNombreArchivo() %>
                                    </div>
                                    <% if (e.getDescripcion() != null) { %>
                                    <div class="evidencia-desc">
                                        <%= e.getDescripcion() %>
                                    </div>
                                    <% } %>
                                    <div class="evidencia-meta">
                                        <span>📅 <%= sdf.format(e.getFechaSubida()) %></span>
                                        <span>👤 <%= e.getNombreUsuario() %></span>
                                    </div>
                                </div>
                                <% if (e.esImagen()) { %>
                                <a href="<%= request.getContextPath() %>/<%= e.getRutaArchivo() %>" 
                                   target="_blank" class="btn-evidencia btn-view">
                                    👁️
                                </a>
                                <% } %>
                                <a href="<%= request.getContextPath() %>/<%= e.getRutaArchivo() %>" 
                                   download class="btn-evidencia btn-download">
                                    ⬇️
                                </a>
                            </div>
                            <% } %>
                        </div>
                    </div>
                </div>
                <% } %>

            </div>
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
    console.log('✅ Detalle del pedido cargado: <%= pedido.getCodigoUnico() %>');
</script>

</body>
</html>