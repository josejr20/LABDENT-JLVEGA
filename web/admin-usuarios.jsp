<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.labdent.model.Usuario" %>
<%@ page import="com.labdent.dao.UsuarioDAO" %>
<%@ page import="java.util.List" %>
<%
    Usuario usuario = (Usuario) session.getAttribute("usuario");
    if (usuario == null || !usuario.isAdmin()) {
        response.sendRedirect("login");
        return;
    }
    
    UsuarioDAO usuarioDAO = new UsuarioDAO();
    List<Usuario> usuarios = usuarioDAO.listarTodos();
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Administrar Usuarios - LABDENT JLVEGA</title>
    <link rel="stylesheet" href="css/styles.css">
    <style>
        .user-actions {
            display: flex;
            gap: 5px;
        }
        .status-activo {
            color: var(--success-color);
            font-weight: bold;
        }
        .status-inactivo {
            color: var(--danger-color);
            font-weight: bold;
        }
        .modal {
            display: none;
            position: fixed;
            z-index: 1000;
            left: 0;
            top: 0;
            width: 100%;
            height: 100%;
            background-color: rgba(0,0,0,0.5);
        }
        .modal-content {
            background-color: white;
            margin: 5% auto;
            padding: 30px;
            border-radius: 12px;
            width: 90%;
            max-width: 600px;
            box-shadow: 0 8px 16px rgba(0,0,0,0.3);
        }
        .close {
            float: right;
            font-size: 28px;
            font-weight: bold;
            cursor: pointer;
            color: var(--gray-600);
        }
        .close:hover {
            color: var(--danger-color);
        }
    </style>
</head>
<body>
    <!-- Header -->
    <header class="header-dashboard">
        <div class="container">
            <div class="logo">
                <h1>LABDENT JLVEGA</h1>
            </div>
            <nav class="nav">
                <a href="dashboard">Dashboard</a>
                <a href="kanban">Kanban</a>
                <a href="reportes">Reportes</a>
                <a href="admin-usuarios.jsp" class="active">Usuarios</a>
                <div class="user-menu">
                    <span><%= usuario.getNombreCompleto() %></span>
                    <a href="logout" class="btn-logout">Cerrar Sesión</a>
                </div>
            </nav>
        </div>
    </header>

    <!-- Contenido Principal -->
    <main class="main-content">
        <div class="container">
            <h2>Administración de Usuarios</h2>

            <!-- Mensajes -->
            <% if (request.getAttribute("success") != null) { %>
                <div class="alert alert-success">
                    <%= request.getAttribute("success") %>
                </div>
            <% } %>

            <% if (request.getAttribute("error") != null) { %>
                <div class="alert alert-error">
                    <%= request.getAttribute("error") %>
                </div>
            <% } %>

            <!-- Botón para agregar usuario -->
            <div class="actions-bar" style="margin-bottom: 20px;">
                <button onclick="openModal()" class="btn btn-primary">+ Nuevo Usuario</button>
            </div>

            <!-- Tabla de Usuarios -->
            <div class="table-container">
                <table class="table-pedidos">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Nombre Completo</th>
                            <th>Email</th>
                            <th>Rol</th>
                            <th>Teléfono</th>
                            <th>Estado</th>
                            <th>Fecha Registro</th>
                            <th>Última Conexión</th>
                            <th>Acciones</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% if (usuarios != null && !usuarios.isEmpty()) { %>
                            <% for (Usuario u : usuarios) { %>
                            <tr>
                                <td><%= u.getId() %></td>
                                <td><%= u.getNombreCompleto() %></td>
                                <td><%= u.getEmail() %></td>
                                <td><span class="badge-estado" style="background-color: var(--info-color);"><%= u.getRol() %></span></td>
                                <td><%= u.getTelefono() != null ? u.getTelefono() : "N/A" %></td>
                                <td>
                                    <span class="<%= u.isActivo() ? "status-activo" : "status-inactivo" %>">
                                        <%= u.isActivo() ? "Activo" : "Inactivo" %>
                                    </span>
                                </td>
                                <td><%= u.getFechaRegistro() %></td>
                                <td><%= u.getUltimaConexion() != null ? u.getUltimaConexion() : "Nunca" %></td>
                                <td class="user-actions">
                                    <button onclick="editUser(<%= u.getId() %>)" class="btn-small" style="background: var(--warning-color); color: white;">Editar</button>
                                    <% if (u.getId() != usuario.getId()) { %>
                                        <form action="admin-usuarios" method="post" style="display: inline;">
                                            <input type="hidden" name="accion" value="toggle_estado">
                                            <input type="hidden" name="usuarioId" value="<%= u.getId() %>">
                                            <button type="submit" class="btn-small" style="background: <%= u.isActivo() ? "var(--danger-color)" : "var(--success-color)" %>; color: white;">
                                                <%= u.isActivo() ? "Desactivar" : "Activar" %>
                                            </button>
                                        </form>
                                    <% } %>
                                </td>
                            </tr>
                            <% } %>
                        <% } else { %>
                            <tr>
                                <td colspan="9" class="text-center">No hay usuarios registrados</td>
                            </tr>
                        <% } %>
                    </tbody>
                </table>
            </div>
        </div>
    </main>

    <!-- Modal para Nuevo Usuario -->
    <div id="userModal" class="modal">
        <div class="modal-content">
            <span class="close" onclick="closeModal()">&times;</span>
            <h3>Registrar Nuevo Usuario</h3>
            <form action="registro-usuario" method="post">
                <div class="form-group">
                    <label for="nombreCompleto">Nombre Completo: *</label>
                    <input type="text" id="nombreCompleto" name="nombreCompleto" required>
                </div>

                <div class="form-group">
                    <label for="email">Email: *</label>
                    <input type="email" id="email" name="email" required>
                </div>

                <div class="form-group">
                    <label for="password">Contraseña: *</label>
                    <input type="password" id="password" name="password" required minlength="6">
                </div>

                <div class="form-group">
                    <label for="rol">Rol: *</label>
                    <select id="rol" name="rol" required>
                        <option value="">Seleccione...</option>
                        <option value="ODONTOLOGO">Odontólogo</option>
                        <option value="TECNICO">Técnico</option>
                        <option value="CERAMISTA">Ceramista</option>
                        <option value="DELIVERISTA">Deliverista</option>
                        <option value="ADMIN">Administrador</option>
                    </select>
                </div>

                <div class="form-group">
                    <label for="telefono">Teléfono:</label>
                    <input type="text" id="telefono" name="telefono">
                </div>

                <div class="form-group">
                    <label for="direccion">Dirección:</label>
                    <textarea id="direccion" name="direccion" rows="2"></textarea>
                </div>

                <div class="form-actions">
                    <button type="submit" class="btn btn-primary">Registrar Usuario</button>
                    <button type="button" onclick="closeModal()" class="btn btn-secondary">Cancelar</button>
                </div>
            </form>
        </div>
    </div>

    <!-- Footer -->
    <footer class="footer">
        <div class="container">
            <p>&copy; 2025 LABDENT JLVEGA</p>
        </div>
    </footer>

    <script>
        function openModal() {
            document.getElementById('userModal').style.display = 'block';
        }

        function closeModal() {
            document.getElementById('userModal').style.display = 'none';
        }

        function editUser(userId) {
            alert('Funcionalidad de edición en desarrollo. ID: ' + userId);
        }

        // Cerrar modal al hacer clic fuera de él
        window.onclick = function(event) {
            let modal = document.getElementById('userModal');
            if (event.target == modal) {
                modal.style.display = 'none';
            }
        }
    </script>
    <script src="js/login.js"></script>
</body>
</html>