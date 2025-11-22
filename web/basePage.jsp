<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.labdent.model.Usuario" %>
<%@ page import="com.labdent.util.SessionDataUtil" %>
<%
    // ✅ Validar sesión
    Usuario usuario = SessionDataUtil.obtenerUsuarioDesdeSesion(request);
    if (usuario == null) {
        response.sendRedirect("login?error=session_expired");
        return;
    }
    
    // ✅ Validar sesión con token
    if (!SessionDataUtil.tieneSesionValida(request)) {
        response.sendRedirect("login?error=session_expired");
        return;
    }
    
    // ✅ Generar CSRF token
    String csrfToken = SessionDataUtil.generarCSRFToken(session);
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="X-Content-Type-Options" content="nosniff">
    <meta http-equiv="X-Frame-Options" content="DENY">
    <meta http-equiv="X-XSS-Protection" content="1; mode=block">
    <title>LABDENT JLVEGA - Panel</title>
    <link rel="stylesheet" href="css/styles.css">
</head>
<body>
    <!-- ✅ Campos ocultos con datos del usuario (accesibles desde JavaScript) -->
    <input type="hidden" id="usuario-id" value="<%= usuario.getId() %>">
    <input type="hidden" id="usuario-nombre" value="<%= usuario.getNombreCompleto() %>">
    <input type="hidden" id="usuario-rol" value="<%= usuario.getRol() %>">
    <input type="hidden" id="usuario-email" value="<%= usuario.getEmail() %>">
    <input type="hidden" id="csrf-token" value="<%= csrfToken %>">
    
    <!-- Header -->
    <header class="header-dashboard">
        <div class="container">
            <div class="logo">
                <h1>🦷 LABDENT JLVEGA</h1>
            </div>
            <nav class="nav">
                <% if (usuario.isAdmin()) { %>
                    <a href="dashboard">Dashboard</a>
                    <a href="kanban">Kanban</a>
                    <a href="reportes">Reportes</a>
                    <a href="admin-usuarios.jsp">Usuarios</a>
                <% } else if (usuario.isOdontologo()) { %>
                    <a href="misPedidos">Mis Pedidos</a>
                    <a href="nuevoPedido.jsp">Nuevo Pedido</a>
                <% } %>
                
                <div class="user-menu">
                    <span>👤 <%= usuario.getNombreCompleto() %></span>
                    <span class="badge"><%= usuario.getRol() %></span>
                    <a href="logout" class="btn-logout" onclick="return confirmarLogout()">🔒 Cerrar Sesión</a>
                </div>
            </nav>
        </div>
    </header>

    <!-- Contenido Principal -->
    <main class="main-content">
        <!-- Aquí va el contenido específico de cada página -->
    </main>

    <!-- Footer -->
    <footer class="footer">
        <div class="container">
            <p>&copy; 2025 LABDENT JLVEGA | Conexión Segura 🔒</p>
        </div>
    </footer>

    <!-- Scripts -->
    <script src="js/cookieHandler.js"></script>
    <script>
        // ✅ Confirmar logout
        function confirmarLogout() {
            return confirm('¿Está seguro que desea cerrar sesión?');
        }
        
        // ✅ Verificar sesión cada 5 minutos
        setInterval(function() {
            fetch('api/verificar-sesion', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                credentials: 'same-origin'
            })
            .then(response => {
                if (!response.ok) {
                    alert('Su sesión ha expirado. Será redirigido al login.');
                    window.location.href = 'login?error=session_expired';
                }
            })
            .catch(error => {
                console.error('Error verificando sesión:', error);
            });
        }, 5 * 60 * 1000); // 5 minutos
        
        // ✅ Obtener CSRF token para formularios
        function obtenerCSRFToken() {
            const tokenElement = document.getElementById('csrf-token');
            return tokenElement ? tokenElement.value : '';
        }
        
        // ✅ Agregar CSRF token a todos los formularios
        document.addEventListener('DOMContentLoaded', function() {
            const forms = document.querySelectorAll('form');
            const csrfToken = obtenerCSRFToken();
            
            forms.forEach(form => {
                if (form.method.toLowerCase() === 'post' && csrfToken) {
                    const input = document.createElement('input');
                    input.type = 'hidden';
                    input.name = 'csrf_token';
                    input.value = csrfToken;
                    form.appendChild(input);
                }
            });
        });
    </script>
</body>
</html>