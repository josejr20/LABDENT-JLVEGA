<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.labdent.model.Usuario" %>
<%
    Usuario usuario = (Usuario) session.getAttribute("usuario");
    if (usuario == null) {
        response.sendRedirect("login");
        return;
    }
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Registrar Pedido - LABDENT JLVEGA</title>
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
                <% if (usuario.isAdmin()) { %>
                    <a href="dashboard">Dashboard</a>
                    <a href="kanban">Kanban</a>
                <% } %>
                <% if (usuario.isOdontologo()) { %>
                    <a href="misPedidos">Mis Pedidos</a>
                <% } %>
                <a href="registro-pedido" class="active">Nuevo Pedido</a>
                <div class="user-menu">
                    <span>👤 <%= usuario.getNombreCompleto() %></span>
                    <a href="logout" class="btn-logout">Cerrar Sesión</a>
                </div>
            </nav>
        </div>
    </header>

    <!-- Contenido Principal -->
    <main class="main-content">
        <div class="container-medium">
            <h2>Registrar Nuevo Pedido</h2>

            <!-- Mensajes -->
            <% if (request.getAttribute("success") != null) { %>
                <div class="alert alert-success">
                    <strong>✅ <%= request.getAttribute("success") %></strong><br>
                    <%= request.getAttribute("mensaje") %>
                    <% if (request.getAttribute("codigoPedido") != null) { %>
                        <div class="codigo-destacado">
                            Código: <strong><%= request.getAttribute("codigoPedido") %></strong>
                        </div>
                    <% } %>
                </div>
            <% } %>

            <% if (request.getAttribute("error") != null) { %>
                <div class="alert alert-error">
                    <%= request.getAttribute("error") %>
                </div>
            <% } %>

            <!-- Formulario de Registro -->
            <form action="registro-pedido" method="post" enctype="multipart/form-data" class="form-registro">
                <div class="form-section">
                    <h3>Información del Paciente</h3>
                    <div class="form-row">
                        <div class="form-group">
                            <label for="nombrePaciente">Nombre del Paciente: *</label>
                            <input type="text" id="nombrePaciente" name="nombrePaciente" required>
                        </div>

                        <div class="form-group">
                            <label for="piezasDentales">Piezas Dentales: *</label>
                            <input type="text" id="piezasDentales" name="piezasDentales" 
                                   placeholder="Ej: 1.6, 1.7" required>
                            <small>Separar con comas</small>
                        </div>
                    </div>
                </div>

                <div class="form-section">
                    <h3>Especificaciones del Trabajo</h3>
                    <div class="form-row">
                        <div class="form-group">
                            <label for="tipoProtesis">Tipo de Prótesis: *</label>
                            <select id="tipoProtesis" name="tipoProtesis" required>
                                <option value="">Seleccione...</option>
                                <option value="Corona Individual">Corona Individual</option>
                                <option value="Puente">Puente</option>
                                <option value="Carilla">Carilla</option>
                                <option value="Incrustación">Incrustación</option>
                                <option value="Prótesis sobre Implante">Prótesis sobre Implante</option>
                            </select>
                        </div>

                        <div class="form-group">
                            <label for="material">Material: *</label>
                            <select id="material" name="material" required>
                                <option value="">Seleccione...</option>
                                <option value="Zirconia">Zirconia</option>
                                <option value="Metal-Porcelana">Metal-Porcelana</option>
                                <option value="Disilicato de Litio">Disilicato de Litio</option>
                                <option value="Resina">Resina</option>
                                <option value="PMMA">PMMA</option>
                            </select>
                        </div>

                        <div class="form-group">
                            <label for="colorShade">Color/Shade:</label>
                            <input type="text" id="colorShade" name="colorShade" 
                                   placeholder="Ej: A2, B1">
                        </div>
                    </div>
                </div>

                <div class="form-section">
                    <h3>Fechas y Prioridad</h3>
                    <div class="form-row">
                        <div class="form-group">
                            <label for="fechaCompromiso">Fecha de Compromiso: *</label>
                            <input type="date" id="fechaCompromiso" name="fechaCompromiso" required>
                        </div>

                        <div class="form-group">
                            <label for="prioridad">Prioridad:</label>
                            <select id="prioridad" name="prioridad">
                                <option value="NORMAL">Normal</option>
                                <option value="URGENTE">Urgente</option>
                                <option value="EMERGENCIA">Emergencia</option>
                            </select>
                        </div>
                    </div>
                </div>

                <div class="form-section">
                    <h3>Información Adicional</h3>
                    <div class="form-group">
                        <label for="observaciones">Observaciones:</label>
                        <textarea id="observaciones" name="observaciones" rows="4" 
                                  placeholder="Indicaciones especiales, detalles técnicos, etc."></textarea>
                    </div>

                    <div class="form-group">
                        <label for="archivoAdjunto">Archivo Adjunto (Fotos, Escaneos):</label>
                        <input type="file" id="archivoAdjunto" name="archivoAdjunto" 
                               accept="image/*,.pdf,.stl,.zip">
                        <small>Formatos: JPG, PNG, PDF, STL, ZIP (Max: 10MB)</small>
                    </div>
                </div>

                <div class="form-actions">
                    <button type="submit" class="btn btn-primary">
                        Registrar Pedido
                    </button>
                    <a href="<%= usuario.isOdontologo() ? "misPedidos" : "dashboard" %>" class="btn btn-secondary">
                        Cancelar
                    </a>
                </div>
            </form>
        </div>
    </main>

    <!-- Footer -->
    <footer class="footer">
        <div class="container">
            <p>&copy; 2025 LABDENT JLVEGA</p>
        </div>
    </footer>

    <script>
        // Establecer fecha mínima como hoy
        document.getElementById('fechaCompromiso').min = new Date().toISOString().split('T')[0];
    </script>
</body>
</html>