<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Iniciar Sesión - LABDENT JLVEGA</title>
        <link rel="stylesheet" href="css/styles.css">
    </head>
    <body class="login-page">
        <div class="login-container">
            <div class="login-card">
                <div class="login-header">
                    <h1>LABDENT JLVEGA</h1>
                    <p>Sistema de Gestión de Pedidos</p>
                </div>

                <!-- Mensajes de error -->
                <% if (request.getAttribute("error") != null) {%>
                <div class="alert alert-error">
                    <%= request.getAttribute("error")%>
                </div>
                <% }%>

                <!-- Formulario de Login -->
                <form action="login" method="post" class="login-form">
                    <div class="form-group">
                        <label for="email">Correo Electrónico:</label>
                        <input type="email" 
                               id="email" 
                               name="email" 
                               value="<%= request.getAttribute("email") != null ? request.getAttribute("email") : ""%>"
                               placeholder="ejemplo@correo.com" 
                               required
                               autofocus>
                    </div>

                    <div class="form-group">
                        <label for="password">Contraseña:</label>
                        <input type="password" 
                               id="password" 
                               name="password" 
                               placeholder="Ingrese su contraseña" 
                               required>
                    </div>

                    <div class="form-group checkbox-group">
                        <label>
                            <input type="checkbox" name="recordar"> Recordar mi sesión
                        </label>
                    </div>

                    <button type="submit" class="btn btn-primary btn-block">
                        Iniciar Sesión
                    </button>
                    <input type="hidden" id="usuario-id" value="<%= session.getAttribute("usuarioId") != null ? session.getAttribute("usuarioId") : ""%>" />
                    <input type="hidden" id="usuario-nombre" value="<%= session.getAttribute("usuarioNombre") != null ? session.getAttribute("usuarioNombre") : ""%>" />

                </form>

                <div class="login-footer">
                    <p><a href="index.jsp">← Volver al inicio</a></p>
                    <p><a href="registroCliente.jsp">Nuevo Usuario</a></p>
                    <p class="help-text">¿Problemas para acceder? Contacte al administrador</p>
                </div>

                <!-- Usuarios de prueba (solo en desarrollo)
                <div class="test-users">
                    <h4>Usuarios de Prueba:</h4>
                    <ul>
                        <li><strong>Admin:</strong> admin@labdent.com / admin123</li>
                        <li><strong>Odontólogo:</strong> cmendoza@gmail.com / pass123</li>
                        <li><strong>Técnico:</strong> jtecnico@labdent.com / pass123</li>
                        <li><strong>Ceramista:</strong> mceramista@labdent.com / pass123</li>
                    </ul>
                </div> -->
            </div>
        </div>
        <script src="js/login.js"></script>

    </body>
</html>