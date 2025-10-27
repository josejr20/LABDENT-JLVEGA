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
                        <li><a href="misPedidos.jsp">Mis pedidos</a></li>
                        <li><a href="registro-pedido.jsp">Registrar Pedidore</a></li>
                        <li><a href="kanbanUsuario.jsp">Kanban</a></li>
                        <a id="logout-btn" href="logout">Cerrar sesión</a>
                    </ul>
                </nav>
            </div>
        </header>

        <main class="main">



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

        <input type="hidden" id="usuario-id" 
               value="<%= session.getAttribute("usuarioId")%>" />

        <input type="hidden" id="usuario-nombre" 
               value="<%= session.getAttribute("usuarioNombre")%>" />

        <script>
            const usuario = localStorage.getItem("usuarioData");
            if (!usuario) {
                console.warn("⚠ Usuario no encontrado en LocalStorage. Restaurando desde sesión.");
                const usuarioRestaurado = {
                    id: '<%= session.getAttribute("usuarioId")%>',
                    nombre: '<%= session.getAttribute("usuarioNombre")%>',
                    rol: '<%= session.getAttribute("usuarioRol")%>'
                };
                localStorage.setItem("usuarioData", JSON.stringify(usuarioRestaurado));
            }
        </script>

        <script src="js/login.js"></script>

    </body>
</html>
