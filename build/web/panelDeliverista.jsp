<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.labdent.model.Usuario" %>
<%@ page import="com.labdent.model.Pedido" %>
<%@ page import="com.labdent.model.PedidoDelivery" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%
    Usuario usuario = (Usuario) session.getAttribute("usuario");
    if (usuario == null || !"DELIVERISTA".equals(usuario.getRol())) {
        response.sendRedirect("login");
        return;
    }
    
    List<Pedido> pedidosListos = (List<Pedido>) request.getAttribute("pedidosListos");
    List<PedidoDelivery> misDeliveries = (List<PedidoDelivery>) request.getAttribute("misDeliveries");
    
    int totalListos = pedidosListos != null ? pedidosListos.size() : 0;
    int misEnCurso = misDeliveries != null ? misDeliveries.size() : 0;
    
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    
    String success = (String) request.getAttribute("success");
    String error = (String) request.getAttribute("error");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Panel Deliverista - LABDENT</title>
    <link rel="stylesheet" href="css/panelDeliverista.css">
</head>
<body>

<header class="header">
    <div class="container">
        <div class="header-content">
            <div class="logo">
                <h1>🦷 LABDENT</h1>
            </div>
            <nav class="nav">
                <a href="PanelDeliverista" class="nav-link active">
                    <span class="icon">🏠</span>
                    <span>Panel Principal</span>
                </a>
                <a href="KanbanDeliverista" class="nav-link">
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
    <div class="container">
        
        <!-- Hero Section -->
        <section class="hero">
            <div class="hero-content">
                <h2 class="hero-title">¡Bienvenido, <%= usuario.getNombreCompleto()%>! 🚚</h2>
                <p class="hero-subtitle">Gestiona las entregas de pedidos dentales</p>
            </div>
            <div class="hero-actions">
                <button onclick="window.location.reload()" class="btn btn-primary">
                    <span>🔄</span> Actualizar
                </button>
                <a href="KanbanDeliverista" class="btn btn-secondary">
                    <span>📊</span> Ver Kanban
                </a>
            </div>
        </section>

        <!-- Alertas -->
        <% if (success != null) { %>
        <div class="alert alert-success">
            <span class="alert-icon">✅</span>
            <span><%= success %></span>
        </div>
        <% } %>
        
        <% if (error != null) { %>
        <div class="alert alert-error">
            <span class="alert-icon">❌</span>
            <span><%= error %></span>
        </div>
        <% } %>

        <!-- Stats Cards -->
        <div class="stats-grid">
            <div class="stat-card stat-card-green">
                <div class="stat-icon">📦</div>
                <div class="stat-info">
                    <h3 class="stat-title">Pedidos Listos</h3>
                    <p class="stat-number"><%= totalListos %></p>
                    <span class="stat-desc">Disponibles para tomar</span>
                </div>
            </div>
            
            <div class="stat-card stat-card-blue">
                <div class="stat-icon">🚚</div>
                <div class="stat-info">
                    <h3 class="stat-title">Mis Deliveries</h3>
                    <p class="stat-number"><%= misEnCurso %></p>
                    <span class="stat-desc">En proceso de entrega</span>
                </div>
            </div>
        </div>

        <!-- Mis Deliveries en Curso -->
        <section class="section-deliveries">
            <div class="section-header">
                <h2 class="section-title">
                    <span class="section-icon">🚚</span>
                    Mis Deliveries en Curso
                </h2>
            </div>

            <% if (misDeliveries != null && !misDeliveries.isEmpty()) { %>
            <div class="deliveries-grid">
                <% for (PedidoDelivery d : misDeliveries) { %>
                <div class="delivery-card">
                    <div class="delivery-header">
                        <div class="delivery-code">
                            <span class="code-icon">📦</span>
                            <strong><%= d.getCodigoPedido() %></strong>
                        </div>
                        <span class="delivery-badge badge-<%= d.getEstadoDelivery().toLowerCase() %>">
                            <%= d.getIconoEstado() %> <%= d.getEstadoDescripcion() %>
                        </span>
                    </div>
                    
                    <div class="delivery-body">
                        <div class="delivery-info">
                            <span class="info-icon">👤</span>
                            <span><strong>Paciente:</strong> <%= d.getNombrePaciente() %></span>
                        </div>
                        
                        <% if (d.getFechaSalida() != null) { %>
                        <div class="delivery-info">
                            <span class="info-icon">🚪</span>
                            <span><strong>Salida:</strong> <%= sdf.format(d.getFechaSalida()) %></span>
                        </div>
                        <% } %>
                        
                        <% if (d.getFechaLlegada() != null) { %>
                        <div class="delivery-info">
                            <span class="info-icon">📍</span>
                            <span><strong>Llegada:</strong> <%= sdf.format(d.getFechaLlegada()) %></span>
                        </div>
                        <% } %>
                        
                        <% if (d.getObservaciones() != null && !d.getObservaciones().isEmpty()) { %>
                        <div class="delivery-obs">
                            <strong>💬 Observaciones:</strong>
                            <p><%= d.getObservaciones() %></p>
                        </div>
                        <% } %>
                    </div>
                    
                    <div class="delivery-actions">
                        <button onclick="openUpdateModal(<%= d.getId() %>, '<%= d.getEstadoDelivery() %>', '<%= d.getCodigoPedido() %>')" 
                                class="btn btn-primary btn-block">
                            🔄 Actualizar Estado
                        </button>
                    </div>
                </div>
                <% } %>
            </div>
            <% } else { %>
            <div class="empty-state">
                <div class="empty-icon">🚚</div>
                <h3>No tienes deliveries en curso</h3>
                <p>Toma pedidos de la lista de "Pedidos Disponibles" para comenzar</p>
            </div>
            <% } %>
        </section>

        <!-- Pedidos Disponibles para Tomar -->
        <section class="section-available">
            <div class="section-header">
                <h2 class="section-title">
                    <span class="section-icon">📦</span>
                    Pedidos Disponibles para Delivery
                </h2>
            </div>

            <% if (pedidosListos != null && !pedidosListos.isEmpty()) { %>
            <div class="table-container">
                <table class="table-pedidos">
                    <thead>
                        <tr>
                            <th>Código</th>
                            <th>Paciente</th>
                            <th>Trabajo</th>
                            <th>Fecha Compromiso</th>
                            <th>Odontólogo</th>
                            <th>Acciones</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% for (Pedido p : pedidosListos) { %>
                        <tr>
                            <td>
                                <span class="pedido-code"><%= p.getCodigoUnico() %></span>
                            </td>
                            <td>
                                <div class="patient-info">
                                    <span>👤</span>
                                    <strong><%= p.getNombrePaciente() %></strong>
                                </div>
                            </td>
                            <td>
                                <div class="work-info">
                                    <strong>🦷 <%= p.getTipoProtesis() %></strong>
                                    <span>💎 <%= p.getMaterial() %></span>
                                </div>
                            </td>
                            <td>
                                <span class="date-info">
                                    📅 <%= p.getFechaCompromiso() %>
                                </span>
                            </td>
                            <td>
                                <% if (p.getNombreOdontologo() != null) { %>
                                    <%= p.getNombreOdontologo() %>
                                <% } else { %>
                                    <span class="text-muted">-</span>
                                <% } %>
                            </td>
                            <td>
                                <form action="TomarPedido" method="post" style="display: inline;">
                                    <input type="hidden" name="pedidoId" value="<%= p.getId() %>">
                                    <button type="submit" class="btn-action btn-take" 
                                            onclick="return confirm('¿Confirmas que vas a tomar este pedido para delivery?')">
                                        ✋ Tomar Pedido
                                    </button>
                                </form>
                            </td>
                        </tr>
                        <% } %>
                    </tbody>
                </table>
            </div>
            <% } else { %>
            <div class="empty-state">
                <div class="empty-icon">📭</div>
                <h3>No hay pedidos disponibles</h3>
                <p>Por el momento no hay pedidos listos para entregar</p>
            </div>
            <% } %>
        </section>

    </div>
</main>

<footer class="footer">
    <div class="container">
        <p>&copy; 2025 LABDENT JLVEGA | Sistema de Gestión Dental</p>
    </div>
</footer>

<!-- Modal para Actualizar Estado -->
<div id="updateModal" class="modal">
    <div class="modal-content">
        <div class="modal-header">
            <h3 class="modal-title">🔄 Actualizar Estado de Delivery</h3>
            <button class="modal-close" onclick="closeUpdateModal()">&times;</button>
        </div>
        
        <form action="ActualizarDelivery" method="post" id="updateForm">
            <input type="hidden" name="deliveryId" id="deliveryId">
            
            <div class="modal-body">
                <div class="form-group">
                    <label class="form-label">Pedido:</label>
                    <div class="form-value" id="modalCodigoPedido"></div>
                </div>
                
                <div class="form-group">
                    <label class="form-label">Estado Actual:</label>
                    <div class="form-value" id="modalEstadoActual"></div>
                </div>
                
                <div class="form-group">
                    <label class="form-label">Nuevo Estado: *</label>
                    <select name="estado" id="nuevoEstado" class="form-select" required>
                        <option value="">Seleccionar estado...</option>
                        <option value="SALIO_EMPRESA">🚪 Salió de la Empresa</option>
                        <option value="EN_CURSO">🚚 En Curso</option>
                        <option value="LLEGO_DESTINO">📍 Llegó a Destino</option>
                        <option value="PEDIDO_ENTREGADO">✅ Pedido Entregado</option>
                    </select>
                </div>
                
                <div class="form-group">
                    <label class="form-label">Observaciones:</label>
                    <textarea name="observaciones" class="form-textarea" rows="4" 
                              placeholder="Agregar observaciones sobre el estado..."></textarea>
                </div>
            </div>
            
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" onclick="closeUpdateModal()">
                    Cancelar
                </button>
                <button type="submit" class="btn btn-primary">
                    💾 Guardar Cambios
                </button>
            </div>
        </form>
    </div>
</div>

<script>
    function openUpdateModal(deliveryId, estadoActual, codigoPedido) {
        document.getElementById('deliveryId').value = deliveryId;
        document.getElementById('modalCodigoPedido').textContent = codigoPedido;
        document.getElementById('modalEstadoActual').textContent = getEstadoDescripcion(estadoActual);
        
        // Filtrar opciones según el estado actual
        filterEstadoOptions(estadoActual);
        
        document.getElementById('updateModal').style.display = 'flex';
    }
    
    function closeUpdateModal() {
        document.getElementById('updateModal').style.display = 'none';
        document.getElementById('updateForm').reset();
    }
    
    function getEstadoDescripcion(estado) {
        const estados = {
            'SALIO_EMPRESA': '🚪 Salió de la Empresa',
            'EN_CURSO': '🚚 En Curso',
            'LLEGO_DESTINO': '📍 Llegó a Destino',
            'PEDIDO_ENTREGADO': '✅ Pedido Entregado'
        };
        return estados[estado] || estado;
    }
    
    function filterEstadoOptions(estadoActual) {
        const select = document.getElementById('nuevoEstado');
        const options = select.querySelectorAll('option');
        
        // Definir el orden de estados
        const orden = ['SALIO_EMPRESA', 'EN_CURSO', 'LLEGO_DESTINO', 'PEDIDO_ENTREGADO'];
        const indexActual = orden.indexOf(estadoActual);
        
        // Deshabilitar estados anteriores o igual al actual
        options.forEach(option => {
            if (option.value) {
                const indexOption = orden.indexOf(option.value);
                if (indexOption <= indexActual) {
                    option.disabled = true;
                    option.style.color = '#ccc';
                } else {
                    option.disabled = false;
                    option.style.color = '';
                }
            }
        });
    }
    
    // Cerrar modal al hacer clic fuera
    window.onclick = function(event) {
        const modal = document.getElementById('updateModal');
        if (event.target === modal) {
            closeUpdateModal();
        }
    }
    
    console.log('✅ Panel Deliverista cargado - <%= totalListos %> pedidos disponibles');
</script>

</body>
</html>