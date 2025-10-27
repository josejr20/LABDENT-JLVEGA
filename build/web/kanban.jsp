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
    <link rel="stylesheet" href="css/kanban.css"/>
</head>
<body>
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
            <a href="misPedidos">Mis Pedidos</a>
            <% } %>
            <% if (usuario.isAdmin() || usuario.isOdontologo()) { %>
            <a href="registro-pedido">Nuevo Pedido</a>
            <% }%>
            <div class="user-menu">
                <span>👤 <%= usuario.getNombreCompleto()%> (<%= usuario.getRol()%>)</span>
                <a href="logout" class="btn-logout">Cerrar Sesión</a>
            </div>
        </nav>
    </div>
</header>

<main class="main-content">
    <div class="container-fluid">
        <h2>Tablero Kanban - Gestión de Pedidos (FIFO)</h2>
        <p class="subtitle">Los pedidos se organizan por orden de llegada en cada etapa</p>

        <% if (request.getAttribute("success") != null) {%>
        <div class="alert alert-success">
            <%= request.getAttribute("success")%>
        </div>
        <% }%>

        <div class="kanban-board">

            <!-- RECEPCIÓN -->
            <div class="kanban-column" data-estado="RECEPCION">
                <div class="kanban-header" style="border-color: #3498db;">
                    <span class="kanban-title">📥 RECEPCIÓN</span>
                    <span class="kanban-count"><%= pedidosRecepcion != null ? pedidosRecepcion.size() : 0%></span>
                </div>
                <% if (pedidosRecepcion != null && !pedidosRecepcion.isEmpty()) { %>
                <% for (Pedido p : pedidosRecepcion) {%>
                <div class="kanban-card <%= p.isAtrasado() ? "card-atrasado" : ""%> <%= "URGENTE".equals(p.getPrioridad()) ? "card-urgente" : ""%>"
                     onclick="verDetalle(<%= p.getId()%>)">
                    <div class="card-code"><%= p.getCodigoUnico()%></div>
                    <div class="card-patient"><%= p.getNombrePaciente()%></div>
                    <div class="card-type"><%= p.getTipoProtesis()%> - <%= p.getMaterial()%></div>
                    <div class="card-date">
                        Compromiso: <%= p.getFechaCompromiso()%>
                        <% if (p.isAtrasado()) { %>
                        <br><strong style="color: #e74c3c;">⚠️ ATRASADO</strong>
                        <% } %>
                    </div>
                </div>
                <% } } else { %>
                <p class="text-muted">No hay pedidos</p>
                <% }%>
            </div>

            <!-- PARALELIZADO -->
            <div class="kanban-column" data-estado="PARALELIZADO">
                <div class="kanban-header" style="border-color: #9b59b6;">
                    <span class="kanban-title">🔄 PARALELIZADO</span>
                    <span class="kanban-count"><%= pedidosParalelizado != null ? pedidosParalelizado.size() : 0%></span>
                </div>
                <% if (pedidosParalelizado != null && !pedidosParalelizado.isEmpty()) { %>
                <% for (Pedido p : pedidosParalelizado) {%>
                <div class="kanban-card <%= p.isAtrasado() ? "card-atrasado" : ""%>"
                     onclick="verDetalle(<%= p.getId()%>)">
                    <div class="card-code"><%= p.getCodigoUnico()%></div>
                    <div class="card-patient"><%= p.getNombrePaciente()%></div>
                    <div class="card-type"><%= p.getTipoProtesis()%> - <%= p.getMaterial()%></div>
                    <div class="card-date">Compromiso: <%= p.getFechaCompromiso()%></div>
                </div>
                <% } } else { %>
                <p class="text-muted">No hay pedidos</p>
                <% }%>
            </div>

            <!-- DISEÑO CAD -->
            <div class="kanban-column" data-estado="DISENO_CAD">
                <div class="kanban-header" style="border-color: #1abc9c;">
                    <span class="kanban-title">💻 DISEÑO CAD</span>
                    <span class="kanban-count"><%= pedidosDisenoCad != null ? pedidosDisenoCad.size() : 0%></span>
                </div>
                <% if (pedidosDisenoCad != null && !pedidosDisenoCad.isEmpty()) { %>
                <% for (Pedido p : pedidosDisenoCad) {%>
                <div class="kanban-card <%= p.isAtrasado() ? "card-atrasado" : ""%>"
                     onclick="verDetalle(<%= p.getId()%>)">
                    <div class="card-code"><%= p.getCodigoUnico()%></div>
                    <div class="card-patient"><%= p.getNombrePaciente()%></div>
                    <div class="card-type"><%= p.getTipoProtesis()%> - <%= p.getMaterial()%></div>
                    <div class="card-date">Compromiso: <%= p.getFechaCompromiso()%></div>
                </div>
                <% } } else { %>
                <p class="text-muted">No hay pedidos</p>
                <% }%>
            </div>

            <!-- PRODUCCIÓN CAM -->
            <div class="kanban-column" data-estado="PRODUCCION_CAM">
                <div class="kanban-header" style="border-color: #f39c12;">
                    <span class="kanban-title">⚙️ PRODUCCIÓN CAM</span>
                    <span class="kanban-count"><%= pedidosProduccionCam != null ? pedidosProduccionCam.size() : 0%></span>
                </div>
                <% if (pedidosProduccionCam != null && !pedidosProduccionCam.isEmpty()) { %>
                <% for (Pedido p : pedidosProduccionCam) {%>
                <div class="kanban-card <%= p.isAtrasado() ? "card-atrasado" : ""%>"
                     onclick="verDetalle(<%= p.getId()%>)">
                    <div class="card-code"><%= p.getCodigoUnico()%></div>
                    <div class="card-patient"><%= p.getNombrePaciente()%></div>
                    <div class="card-type"><%= p.getTipoProtesis()%> - <%= p.getMaterial()%></div>
                    <div class="card-date">Compromiso: <%= p.getFechaCompromiso()%></div>
                </div>
                <% } } else { %>
                <p class="text-muted">No hay pedidos</p>
                <% }%>
            </div>

            <!-- CERÁMICA -->
            <div class="kanban-column" data-estado="CERAMICA">
                <div class="kanban-header" style="border-color: #e74c3c;">
                    <span class="kanban-title">🎨 CERÁMICA</span>
                    <span class="kanban-count"><%= pedidosCeramica != null ? pedidosCeramica.size() : 0%></span>
                </div>
                <% if (pedidosCeramica != null && !pedidosCeramica.isEmpty()) { %>
                <% for (Pedido p : pedidosCeramica) {%>
                <div class="kanban-card <%= p.isAtrasado() ? "card-atrasado" : ""%>"
                     onclick="verDetalle(<%= p.getId()%>)">
                    <div class="card-code"><%= p.getCodigoUnico()%></div>
                    <div class="card-patient"><%= p.getNombrePaciente()%></div>
                    <div class="card-type"><%= p.getTipoProtesis()%> - <%= p.getMaterial()%></div>
                    <div class="card-date">Compromiso: <%= p.getFechaCompromiso()%></div>
                </div>
                <% } } else { %>
                <p class="text-muted">No hay pedidos</p>
                <% }%>
            </div>

            <!-- CONTROL CALIDAD -->
            <div class="kanban-column" data-estado="CONTROL_CALIDAD">
                <div class="kanban-header" style="border-color: #16a085;">
                    <span class="kanban-title">✓ CONTROL CALIDAD</span>
                    <span class="kanban-count"><%= pedidosControlCalidad != null ? pedidosControlCalidad.size() : 0%></span>
                </div>
                <% if (pedidosControlCalidad != null && !pedidosControlCalidad.isEmpty()) { %>
                <% for (Pedido p : pedidosControlCalidad) {%>
                <div class="kanban-card <%= p.isAtrasado() ? "card-atrasado" : ""%>"
                     onclick="verDetalle(<%= p.getId()%>)">
                    <div class="card-code"><%= p.getCodigoUnico()%></div>
                    <div class="card-patient"><%= p.getNombrePaciente()%></div>
                    <div class="card-type"><%= p.getTipoProtesis()%> - <%= p.getMaterial()%></div>
                    <div class="card-date">Compromiso: <%= p.getFechaCompromiso()%></div>
                </div>
                <% } } else { %>
                <p class="text-muted">No hay pedidos</p>
                <% }%>
            </div>

            <!-- LISTO ENTREGA -->
            <div class="kanban-column" data-estado="LISTO_ENTREGA">
                <div class="kanban-header" style="border-color: #27ae60;">
                    <span class="kanban-title">📦 LISTO ENTREGA</span>
                    <span class="kanban-count"><%= pedidosListoEntrega != null ? pedidosListoEntrega.size() : 0%></span>
                </div>
                <% if (pedidosListoEntrega != null && !pedidosListoEntrega.isEmpty()) { %>
                <% for (Pedido p : pedidosListoEntrega) {%>
                <div class="kanban-card"
                     onclick="verDetalle(<%= p.getId()%>)">
                    <div class="card-code"><%= p.getCodigoUnico()%></div>
                    <div class="card-patient"><%= p.getNombrePaciente()%></div>
                    <div class="card-type"><%= p.getTipoProtesis()%> - <%= p.getMaterial()%></div>
                    <div class="card-date">Compromiso: <%= p.getFechaCompromiso()%></div>
                </div>
                <% } } else { %>
                <p class="text-muted">No hay pedidos</p>
                <% }%>
            </div>

        </div>
    </div>
</main>

<footer class="footer">
    <div class="container">
        <p>&copy; 2025 LABDENT JLVEGA - Tablero Kanban FIFO</p>
    </div>
</footer>

<script>
    console.log("✅ Sesión cargada →", "<%= usuario.getNombreCompleto()%>");
    
    const rolUsuario = "<%= usuario.getRol() %>";

    if (rolUsuario !== "CLIENTE") {
        habilitarDragAndDrop(); 
    }

    function verDetalle(idPedido) {
        window.location.href = "ver-pedido?id=" + idPedido;
    }
</script>

</body>
</html>