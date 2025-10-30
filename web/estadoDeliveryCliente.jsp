<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.labdent.model.Usuario" %>
<%@ page import="com.labdent.model.Pedido" %>
<%@ page import="com.labdent.model.PedidoDelivery" %>
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
    
    PedidoDelivery delivery = (PedidoDelivery) request.getAttribute("delivery");
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Seguimiento de Entrega - <%= pedido.getCodigoUnico() %></title>
    <link rel="stylesheet" href="css/estadoDeliveryCliente.css">
</head>
<body>

<header class="header">
    <div class="container">
        <div class="header-content">
            <div class="logo">
                <h1>🦷 LABDENT</h1>
            </div>
            <nav class="nav">
                <a href="PanelUsuario.jsp" class="nav-link">
                    <span class="icon">🏠</span>
                    <span>Inicio</span>
                </a>
                <a href="misPedidos-cliente" class="nav-link">
                    <span class="icon">📋</span>
                    <span>Mis Pedidos</span>
                </a>
                <a href="KanbanUsuarioServlet" class="nav-link">
                    <span class="icon">📊</span>
                    <span>Kanban</span>
                </a>
            </nav>
            <div class="user-menu">
                <div class="user-info">
                    <span class="user-avatar">👤</span>
                    <div class="user-details">
                        <span class="user-name"><%= usuario.getNombreCompleto()%></span>
                        <span class="user-role">Cliente</span>
                    </div>
                </div>
                <a href="logout" class="btn-logout">Cerrar Sesión</a>
            </div>
        </div>
    </div>
</header>

<main class="main-content">
    <div class="container">
        
        <!-- Breadcrumb -->
        <div class="breadcrumb">
            <a href="PanelUsuario.jsp" class="breadcrumb-link">
                <span>🏠</span> Panel
            </a>
            <span class="breadcrumb-separator">›</span>
            <a href="KanbanUsuarioServlet" class="breadcrumb-link">
                <span>📊</span> Kanban
            </a>
            <span class="breadcrumb-separator">›</span>
            <span class="breadcrumb-current">Seguimiento de Entrega</span>
        </div>

        <!-- Header del Pedido -->
        <div class="delivery-header">
            <div class="delivery-header-left">
                <h1 class="delivery-title">
                    <span class="delivery-icon">🚚</span>
                    Seguimiento de Entrega
                </h1>
                <div class="delivery-subtitle">
                    Pedido: <strong><%= pedido.getCodigoUnico() %></strong>
                </div>
            </div>
            <div class="delivery-header-right">
                <a href="KanbanUsuarioServlet" class="btn btn-secondary">
                    ← Volver al Kanban
                </a>
            </div>
        </div>

        <!-- Información del Pedido -->
        <div class="info-card-compact">
            <div class="info-row-inline">
                <div class="info-item">
                    <span class="info-icon">👤</span>
                    <div>
                        <span class="info-label">Paciente</span>
                        <span class="info-value"><%= pedido.getNombrePaciente() %></span>
                    </div>
                </div>
                <div class="info-item">
                    <span class="info-icon">🦷</span>
                    <div>
                        <span class="info-label">Trabajo</span>
                        <span class="info-value"><%= pedido.getTipoProtesis() %></span>
                    </div>
                </div>
                <div class="info-item">
                    <span class="info-icon">💎</span>
                    <div>
                        <span class="info-label">Material</span>
                        <span class="info-value"><%= pedido.getMaterial() %></span>
                    </div>
                </div>
            </div>
        </div>

        <% if (delivery != null) { %>
        <!-- Estado Actual de Delivery -->
        <div class="status-banner status-<%= delivery.getEstadoDelivery().toLowerCase() %>">
            <div class="status-icon-large">
                <%= delivery.getIconoEstado() %>
            </div>
            <div class="status-content">
                <h2 class="status-title"><%= delivery.getEstadoDescripcion() %></h2>
                <p class="status-description">
                    <% if ("SALIO_EMPRESA".equals(delivery.getEstadoDelivery())) { %>
                        Tu pedido ha salido de nuestras instalaciones y está en camino
                    <% } else if ("EN_CURSO".equals(delivery.getEstadoDelivery())) { %>
                        El repartidor está en camino con tu pedido
                    <% } else if ("LLEGO_DESTINO".equals(delivery.getEstadoDelivery())) { %>
                        El repartidor ha llegado a la dirección de entrega
                    <% } else if ("PEDIDO_ENTREGADO".equals(delivery.getEstadoDelivery())) { %>
                        Tu pedido ha sido entregado exitosamente
                    <% } %>
                </p>
            </div>
        </div>

        <!-- Timeline de Delivery -->
        <div class="delivery-timeline-container">
            <h2 class="section-title">
                <span class="section-icon">📍</span>
                Historial de Seguimiento
            </h2>
            
            <div class="delivery-timeline">
                <!-- Salió de Empresa -->
                <div class="timeline-step <%= delivery.getFechaSalida() != null ? "completed" : "" %> 
                                          <%= "SALIO_EMPRESA".equals(delivery.getEstadoDelivery()) ? "active" : "" %>">
                    <div class="timeline-marker">
                        <div class="timeline-icon">🚪</div>
                    </div>
                    <div class="timeline-content">
                        <div class="timeline-header">
                            <h3 class="timeline-title">Salió de la Empresa</h3>
                            <% if (delivery.getFechaSalida() != null) { %>
                            <span class="timeline-date"><%= sdf.format(delivery.getFechaSalida()) %></span>
                            <% } %>
                        </div>
                        <p class="timeline-description">El pedido ha sido despachado y está listo para ser entregado</p>
                        <% if (delivery.getNombreDeliverista() != null) { %>
                        <div class="timeline-deliverista">
                            <span class="deliverista-icon">👤</span>
                            <span>Repartidor: <strong><%= delivery.getNombreDeliverista() %></strong></span>
                        </div>
                        <% } %>
                    </div>
                </div>

                <!-- En Curso -->
                <div class="timeline-step <%= "EN_CURSO".equals(delivery.getEstadoDelivery()) || "LLEGO_DESTINO".equals(delivery.getEstadoDelivery()) || "PEDIDO_ENTREGADO".equals(delivery.getEstadoDelivery()) ? "completed" : "" %> 
                                          <%= "EN_CURSO".equals(delivery.getEstadoDelivery()) ? "active" : "" %>">
                    <div class="timeline-marker">
                        <div class="timeline-icon">🚚</div>
                    </div>
                    <div class="timeline-content">
                        <div class="timeline-header">
                            <h3 class="timeline-title">En Curso</h3>
                        </div>
                        <p class="timeline-description">El repartidor está en camino hacia la dirección de entrega</p>
                    </div>
                </div>

                <!-- Llegó a Destino -->
                <div class="timeline-step <%= delivery.getFechaLlegada() != null ? "completed" : "" %> 
                                          <%= "LLEGO_DESTINO".equals(delivery.getEstadoDelivery()) ? "active" : "" %>">
                    <div class="timeline-marker">
                        <div class="timeline-icon">📍</div>
                    </div>
                    <div class="timeline-content">
                        <div class="timeline-header">
                            <h3 class="timeline-title">Llegó a Destino</h3>
                            <% if (delivery.getFechaLlegada() != null) { %>
                            <span class="timeline-date"><%= sdf.format(delivery.getFechaLlegada()) %></span>
                            <% } %>
                        </div>
                        <p class="timeline-description">El repartidor ha llegado a la ubicación de entrega</p>
                    </div>
                </div>

                <!-- Pedido Entregado -->
                <div class="timeline-step <%= delivery.getFechaEntrega() != null ? "completed" : "" %> 
                                          <%= "PEDIDO_ENTREGADO".equals(delivery.getEstadoDelivery()) ? "active" : "" %>">
                    <div class="timeline-marker">
                        <div class="timeline-icon">✅</div>
                    </div>
                    <div class="timeline-content">
                        <div class="timeline-header">
                            <h3 class="timeline-title">Pedido Entregado</h3>
                            <% if (delivery.getFechaEntrega() != null) { %>
                            <span class="timeline-date"><%= sdf.format(delivery.getFechaEntrega()) %></span>
                            <% } %>
                        </div>
                        <p class="timeline-description">Tu pedido ha sido entregado satisfactoriamente</p>
                    </div>
                </div>
            </div>
        </div>

        <!-- Observaciones del Repartidor -->
        <% if (delivery.getObservaciones() != null && !delivery.getObservaciones().trim().isEmpty()) { %>
        <div class="observations-card">
            <h3 class="observations-title">
                <span class="observations-icon">💬</span>
                Observaciones del Repartidor
            </h3>
            <p class="observations-text"><%= delivery.getObservaciones() %></p>
        </div>
        <% } %>

        <% } else { %>
        <!-- Sin Información de Delivery -->
        <div class="no-delivery-card">
            <div class="no-delivery-icon">📦</div>
            <h2 class="no-delivery-title">Pedido Listo para Entrega</h2>
            <p class="no-delivery-text">
                Tu pedido está listo y pronto será asignado a un repartidor para su entrega.
                Te notificaremos cuando el proceso de entrega comience.
            </p>
            <div class="no-delivery-status">
                <div class="status-item">
                    <span class="status-item-icon">✅</span>
                    <span>Producción Completada</span>
                </div>
                <div class="status-item">
                    <span class="status-item-icon">✅</span>
                    <span>Control de Calidad Aprobado</span>
                </div>
                <div class="status-item">
                    <span class="status-item-icon">⏳</span>
                    <span>Esperando Asignación de Repartidor</span>
                </div>
            </div>
        </div>
        <% } %>

        <!-- Información de Contacto -->
        <div class="contact-card">
            <h3 class="contact-title">
                <span class="contact-icon">📞</span>
                ¿Necesitas Ayuda?
            </h3>
            <p class="contact-text">
                Si tienes alguna pregunta sobre tu pedido o necesitas más información, 
                no dudes en contactarnos:
            </p>
            <div class="contact-info">
                <div class="contact-item">
                    <span class="contact-item-icon">📧</span>
                    <span>contacto@labdent.com</span>
                </div>
                <div class="contact-item">
                    <span class="contact-item-icon">📱</span>
                    <span>+51 942 000 000</span>
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
    console.log('✅ Seguimiento de Delivery cargado - Pedido: <%= pedido.getCodigoUnico() %>');
    
    // Auto-refresh cada 30 segundos si el pedido no está entregado
    <% if (delivery != null && !"PEDIDO_ENTREGADO".equals(delivery.getEstadoDelivery())) { %>
    setTimeout(function() {
        console.log('🔄 Actualizando estado de delivery...');
        window.location.reload();
    }, 30000);
    <% } %>
</script>

</body>
</html>