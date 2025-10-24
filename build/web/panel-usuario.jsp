<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page session="true" %>

<%
    String nombreUsuario = (String) session.getAttribute("usuarioNombre");
    String rol = (String) session.getAttribute("usuarioRol");
    Object idObj = session.getAttribute("usuarioId");
    Integer idCliente = (idObj instanceof Integer) ? (Integer) idObj : null;

    if (nombreUsuario == null || idCliente == null || !"CLIENTE".equals(rol)) {
        response.sendRedirect("login.jsp");
        return;
    }

    com.labdent.dao.PedidoDAO pedidoDAO = new com.labdent.dao.PedidoDAO();
    java.util.List<com.labdent.model.Pedido> pedidos = pedidoDAO.obtenerPedidosPorCliente(idCliente);

    long total = pedidos.size();
    long enProceso = pedidos.stream().filter(p -> !"ENTREGADO".equals(p.getEstadoActual())).count();
    long finalizados = pedidos.stream().filter(p -> "ENTREGADO".equals(p.getEstadoActual())).count();
%>

<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <title>Panel del Usuario</title>
        <link rel="stylesheet" href="css/panelUsuario.css">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
    </head>
    <body>

        <header class="header">
            <div class="header-content">
                <h2>Hola, <%= nombreUsuario%></h2>
                <nav class="nav">
                    <ul>
                        <li><a href="mis-pedidos.jsp">Mis pedidos</a></li>
                        <li><a href="registro-pedido.jsp">Registrar Pedido</a></li>
                        <li><a href="logout">Cerrar Sesión</a></li>
                    </ul>
                </nav>
            </div>
        </header>

        <main class="main">

            <section class="resumen">
                <div class="card">
                    <h3>Total de pedidos</h3>
                    <p class="numero"><%= total%></p>
                </div>
                <div class="card">
                    <h3>En proceso</h3>
                    <p class="numero"><%= enProceso%></p>
                </div>
                <div class="card">
                    <h3>Finalizados</h3>
                    <p class="numero"><%= finalizados%></p>
                </div>
            </section>

            <section class="info-panel">
                <h3>Seguimiento a tu ritmo, con garantía</h3>
                <p>Controla en tiempo real el estado de tus pedidos.  
                    Transparencia y calidad en cada proceso.</p>
            </section>

            <section class="carrusel">
                <div class="slider">
                    <div class="slide active">Seguimiento en tiempo real</div>
                    <div class="slide">Materiales garantizados</div>
                    <div class="slide">Atención personalizada</div>
                </div>
            </section>

            <section class="seguimiento">
                <h3>Pedidos recientes</h3>

                <% if (pedidos == null || pedidos.isEmpty()) { %>
                <p>Aún no tienes pedidos registrados.</p>
                <% } else {
                    String[] etapas = {"RECEPCION", "PARALELIZADO", "DISENO_CAD", "PRODUCCION_CAM", "CERAMICA", "CONTROL_CALIDAD", "LISTO_ENTREGA", "ENTREGADO"};
                    for (com.labdent.model.Pedido p : pedidos) {
                %>

                <div class="pedido-item">
                    <h4><%= p.getCodigoUnico()%></h4>
                    <p><strong>Paciente:</strong> <%= p.getNombrePaciente()%></p>
                    <p><strong>Tipo:</strong> <%= p.getTipoProtesis()%> | 
                        <strong>Material:</strong> <%= p.getMaterial()%></p>
                    <p><strong>Fecha compromiso:</strong> <%= p.getFechaCompromiso()%></p>

                    <div class="progress-bar">
                        <% for (String etapa : etapas) {
                                boolean activo = etapa.equals(p.getEstadoActual());
                        %>
                        <div class="step <%= activo ? "active" : ""%>"><%= etapa%></div>
                        <% }%>
                    </div>

                    <!-- ✅ Redirección correcta con pedidoId -->
                    <a class="btn-detalle" href="detalle-pedido-cliente.jsp?pedidoId=<%= p.getId()%>">
                        Ver Detalles
                    </a>
                </div>

                <% }
            }%>
            </section>

        </main>

        <footer class="footer">
            <p>LABDENT © 2025 | Gestión de Clientes</p>
        </footer>

        <script>
            let index = 0;
            const slides = document.querySelectorAll(".slide");
            setInterval(() => {
                slides[index].classList.remove("active");
                index = (index + 1) % slides.length;
                slides[index].classList.add("active");
            }, 3500);
        </script>

    </body>
</html>
