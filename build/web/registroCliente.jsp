<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Registro de Cliente - LABDENT JLVEGA</title>
    <link rel="stylesheet" href="css/styles.css">
</head>
<body>

    <div class="container" style="max-width: 450px; margin-top: 50px;">
        <h2>Crear Cuenta</h2>

        <% if (request.getAttribute("error") != null) { %>
            <div class="alert alert-error"><%= request.getAttribute("error") %></div>
        <% } %>

        <% if (request.getAttribute("success") != null) { %>
            <div class="alert alert-success"><%= request.getAttribute("success") %></div>
        <% } %>

        <form action="registro-cliente" method="post">
            <div class="form-group">
                <label>Nombre Completo *</label>
                <input type="text" name="nombreCompleto" required>
            </div>

            <div class="form-group">
                <label>Email *</label>
                <input type="email" name="email" required>
            </div>

            <div class="form-group">
                <label>Contraseña *</label>
                <input type="password" name="password" minlength="6" required>
            </div>

            <button type="submit" class="btn btn-primary" style="width:100%;">Registrarme</button>

        </form>

        <p style="text-align:center; margin-top:10px;">
            ¿Ya tienes cuenta? <a href="login">Iniciar Sesión</a>
        </p>
    </div>

</body>
</html>