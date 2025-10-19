<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.labdent.model.Usuario" %>
<%@ page import="com.labdent.model.Pedido" %>
<%@ page import="com.labdent.model.TransicionEstado" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%
    Usuario usuario = (Usuario) session.getAttribute("usuario");
    if (usuario == null) {
        response.sendRedirect("login");
        return;
    }
    
    Pedido pedido = (Pedido) request.getAttribute("pedido");
    List<TransicionEstado> transiciones = (List<TransicionEstado>) request.getAttribute("transiciones");
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Detalle del Pedido - LABDENT JLVEGA</title>
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
                <a href="javascript:history.back()">← Volver</a>
                <% if (usuario.isAdmin()) { %>
                    <a href="dashboard">Dashboard</a>
                <% } %>
                <a href="kanban">Kanban</a>
                <div class="user-menu">
                    <span><%= usuario.getNombreCompleto() %></span>
                    <a href="logout" class="btn-logout">Cerrar Sesión</a>
                </div>
            </nav>
        </div>
    </header>

    <!-- Contenido Principal -->
    <main class="main-content">
        <div class="container-medium">
            <% if (pedido != null) { %>
                <div class="detalle-header">
                    <h2>Detalle del Pedido: <%= pedido.getCodigoUnico() %></h2>
                    <span class="badge-estado-grande" style="background-color: <%= pedido.getColorEstado() %>;">
                        <%= pedido.getEstadoDescripcion() %>
                    </span>
                </div>

                <!-- Información General -->
                <div class="info-grid">
                    <div class="info-card">
                        <h3>Información del Paciente</h3>
                        <table class="info-table">
                            <tr>
                                <th>Paciente:</th>
                                <td><%= pedido.getNombrePaciente() %></td>
                            </tr>
                            <tr>
                                <th>Piezas Dentales:</th>
                                <td><%= pedido.getPiezasDentales() %></td>
                            </tr>
                            <tr>
                                <th>Odontólogo:</th>
                                <td><%= pedido.getNombreOdontologo() %></td>
                            </tr>
                            <tr>
                                <th>Teléfono:</th>
                                <td><%= pedido.getTelefonoOdontologo() %></td>
                            </tr>
                        </table>
                    </div>

                    <div class="info-card">
                        <h3>Especificaciones Técnicas</h3>
                        <table class="info-table">
                            <tr>
                                <th>Tipo de Prótesis:</th>
                                <td><%= pedido.getTipoProtesis() %></td>
                            </tr>
                            <tr>
                                <th>Material:</th>
                                <td><%= pedido.getMaterial() %></td>
                            </tr>
                            <tr>
                                <th>Color/Shade:</th>
                                <td><%= pedido.getColorShade() != null ? pedido.getColorShade() : "N/A" %></td>
                            </tr>
                            <tr>
                                <th>Prioridad:</th>
                                <td><strong><%= pedido.getPrioridad() %></strong></td>
                            </tr>
                        </table>
                    </div>

                    <div class="info-card">
                        <h3>Fechas y Estado</h3>
                        <table class="info-table">
                            <tr>
                                <th>Fecha Ingreso:</th>
                                <td><%= sdf.format(pedido.getFechaIngreso()) %></td>
                            </tr>
                            <tr>
                                <th>Fecha Compromiso:</th>
                                <td><%= pedido.getFechaCompromiso() %></td>
                            </tr>
                            <tr>
                                <th>Días Restantes:</th>
                                <td>
                                    <% if (pedido.isAtrasado()) { %>
                                        <strong style="color: #e74c3c;">
                                            <%= Math.abs(pedido.getDiasRestantes()) %> días ATRASADO
                                        </strong>
                                    <% } else { %>
                                        <%= pedido.getDiasRestantes() %> días
                                    <% } %>
                                </td>
                            </tr>
                            <tr>
                                <th>Responsable Actual:</th>
                                <td><%= pedido.getNombreResponsable() != null ? pedido.getNombreResponsable() : "Sin asignar" %></td>
                            </tr>
                        </table>
                    </div>
                </div>

                <!-- Observaciones -->
                <% if (pedido.getObservaciones() != null && !pedido.getObservaciones().isEmpty()) { %>
                <div class="info-card">
                    <h3>Observaciones</h3>
                    <p><%= pedido.getObservaciones() %></p>
                </div>
                <% } %>

                <!-- Historial de Transiciones -->
                <div class="info-card">
                    <h3>Historial de Transiciones (Trazabilidad FIFO)</h3>
                    <% if (transiciones != null && !transiciones.isEmpty()) { %>
                        <div class="timeline">
                            <% for (TransicionEstado t : transiciones) { %>
                                <div class="timeline-item">
                                    <div class="timeline-marker"></div>
                                    <div class="timeline-content">
                                        <div class="timeline-header">
                                            <strong>
                                                <% if (t.getEstadoAnterior() != null) { %>
                                                    <%= t.getEstadoAnterior() %> → 
                                                <% } %>
                                                <%= t.getEstadoNuevo() %>
                                            </strong>
                                            <span class="timeline-date"><%= sdf.format(t.getFechaTransicion()) %></span>
                                        </div>
                                        <p class="timeline-user">Por: <%= t.getNombreUsuario() %></p>
                                        <% if (t.getObservaciones() != null && !t.getObservaciones().isEmpty()) { %>
                                            <p class="timeline-obs"><%= t.getObservaciones() %></p>
                                        <% } %>
                                        <% if (t.getTiempoEnEstado() != null && t.getTiempoEnEstado() > 0) { %>
                                            <p class="timeline-time">
                                                Tiempo en estado anterior: <%= t.getTiempoEnEstado() %> minutos
                                            </p>
                                        <% } %>
                                        <% if (t.isChecklistCompletado()) { %>
                                            <span class="badge-check">✓ Checklist Completo</span>
                                        <% } %>
                                    </div>
                                </div>
                            <% } %>
                        </div>
                    <% } else { %>
                        <p class="text-muted">No hay historial de transiciones</p>
                    <% } %>
                </div>

                <!-- Acciones (solo para personal interno) -->
                <% if (!usuario.isOdontologo()) { %>
                <div class="info-card">
                    <h3>Acciones</h3>
                    <form action="kanban" method="post" class="form-inline">
                        <input type="hidden" name="accion" value="actualizar_estado">
                        <input type="hidden" name="pedidoId" value="<%= pedido.getId() %>">
                        
                        <div class="form-group">
                            <label for="nuevoEstado">Cambiar Estado:</label>
                            <select id="nuevoEstado" name="nuevoEstado" required>
                                <option value="">Seleccione...</option>
                                <option value="RECEPCION" <%= "RECEPCION".equals(pedido.getEstadoActual()) ? "selected" : "" %>>Recepción</option>
                                <option value="PARALELIZADO" <%= "PARALELIZADO".equals(pedido.getEstadoActual()) ? "selected" : "" %>>Paralelizado</option>
                                <option value="DISENO_CAD" <%= "DISENO_CAD".equals(pedido.getEstadoActual()) ? "selected" : "" %>>Diseño CAD</option>
                                <option value="PRODUCCION_CAM" <%= "PRODUCCION_CAM".equals(pedido.getEstadoActual()) ? "selected" : "" %>>Producción CAM</option>
                                <option value="CERAMICA" <%= "CERAMICA".equals(pedido.getEstadoActual()) ? "selected" : "" %>>Cerámica</option>
                                <option value="CONTROL_CALIDAD" <%= "CONTROL_CALIDAD".equals(pedido.getEstadoActual()) ? "selected" : "" %>>Control Calidad</option>
                                <option value="LISTO_ENTREGA" <%= "LISTO_ENTREGA".equals(pedido.getEstadoActual()) ? "selected" : "" %>>Listo Entrega</option>
                                <option value="ENTREGADO">Entregado</option>
                            </select>
                        </div>

                        <div class="form-group">
                            <label for="observaciones">Observaciones:</label>
                            <textarea id="observaciones" name="observaciones" rows="2"></textarea>
                        </div>

                        <div class="form-group checkbox-group">
                            <label>
                                <input type="checkbox" name="checklistCompletado"> Checklist Completado
                            </label>
                        </div>

                        <button type="submit" class="btn btn-primary">Actualizar Estado</button>
                    </form>
                </div>
                <% } %>

            <% } else { %>
                <div class="alert alert-error">
                    <p>No se encontró el pedido solicitado.</p>
                    <a href="kanban" class="btn btn-secondary">Volver al Kanban</a>
                </div>
            <% } %>
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