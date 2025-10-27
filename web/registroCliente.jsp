<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <title>Registro de Cliente - LABDENT JLVEGA</title>
        <link rel="stylesheet" href="css/styles.css">
    </head>

    <body class="login-page">

        <div class="login-container">
            <div class="login-card">
                <div class="login-header">
                    <h1>LABDENT JLVEGA</h1>
                    <p>Crear cuenta para pedidos online</p>
                </div>

                <% if (request.getAttribute("error") != null) {%>
                <div class="alert alert-error"><%= request.getAttribute("error")%></div>
                <% } %>

                <form action="registroCliente" method="post" class="login-form">

                    <div class="form-group">
                        <label for="nombreCompleto">Nombre Completo *</label>
                        <input type="text" id="nombreCompleto" name="nombreCompleto"
                               required maxlength="80">
                    </div>

                    <div class="form-group">
                        <label for="telefono">Teléfono *</label>
                        <input type="text" id="telefono" name="telefono"
                               required maxlength="9" pattern="[0-9]{9}">
                    </div>

                    <div class="form-group">
                        <label for="direccion">Dirección *</label>
                        <input type="text" id="direccion" name="direccion"
                               required maxlength="120">
                    </div>

                    <div class="form-group">
                        <label for="email">Correo Electrónico *</label>
                        <input type="email" id="email" name="email"
                               required maxlength="100">
                    </div>

                    <div class="form-group">
                        <label for="password">Contraseña *</label>
                        <input type="password" id="password" name="password"
                               minlength="6" required>
                    </div>

                    <button type="submit" class="btn btn-primary btn-block">Crear Cuenta</button>

                </form>

                <p style="text-align:center; margin-top:10px;">
                    ¿Ya tienes cuenta? <a href="login">Iniciar Sesión</a>
                </p>

            </div>
        </div>

        <% if (request.getAttribute("success") != null) {%>
        <script>
    alert("✅ <%= request.getAttribute("success")%>");
    window.location.href = "login";
        </script>
        <% }%>

    </body>
</html>