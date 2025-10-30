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
        <title>Panel del Cliente - LABDENT</title>
        <link rel="stylesheet" href="css/panelUsuario.css">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
    </head>
    <body>

        <header class="header">
            <div class="container">
                <div class="header-content">
                    <div class="logo">
                        <h1>🦷 LABDENT</h1>
                    </div>
                    <nav class="nav">
                        <a href="panelUsuario.jsp" class="nav-link active">
                            <span class="icon">🏠</span>
                            <span>Inicio</span>
                        </a>
                        <a href="misPedidos.jsp" class="nav-link">
                            <span class="icon">📋</span>
                            <span>Mis Pedidos</span>
                        </a>
                        <a href="registro-pedido.jsp" class="nav-link">
                            <span class="icon">➕</span>
                            <span>Nuevo Pedido</span>
                        </a>
                        <a href="KanbanUsuarioServlet" class="nav-link">
                            <span class="icon">📊</span>
                            <span>Kanban</span>
                        </a>
                    </nav>
                    <div class="user-menu">
                        <div class="user-info">
                            <span class="user-avatar">👤</span>
                            <div class="user-details">
                                <span class="user-name"><%= nombreUsuario%></span>
                                <span class="user-role">Cliente</span>
                            </div>
                        </div>
                        <a href="logout" class="btn-logout">Cerrar Sesión</a>
                    </div>
                </div>
            </div>
        </header>

        <main class="main">
            <div class="container">
                
                <!-- Hero Section -->
                <section class="hero">
                    <div class="hero-content">
                        <h2 class="hero-title">¡Bienvenido, <%= nombreUsuario%>! 👋</h2>
                        <p class="hero-subtitle">Gestiona tus pedidos dentales de forma simple y eficiente</p>
                    </div>
                    <div class="hero-actions">
                        <a href="registro-pedido.jsp" class="btn btn-primary">
                            <span>➕</span> Nuevo Pedido
                        </a>
                        <a href="KanbanUsuarioServlet" class="btn btn-secondary">
                            <span>📊</span> Ver Kanban
                        </a>
                    </div>
                </section>

                <!-- Cards de Resumen -->
                <section class="stats-grid">
                    <div class="stat-card stat-card-blue">
                        <div class="stat-icon">📦</div>
                        <div class="stat-info">
                            <h3 class="stat-title">Pedidos Activos</h3>
                            <p class="stat-number" id="pedidos-activos">-</p>
                        </div>
                    </div>
                    
                    <div class="stat-card stat-card-green">
                        <div class="stat-icon">✅</div>
                        <div class="stat-info">
                            <h3 class="stat-title">Completados</h3>
                            <p class="stat-number" id="pedidos-completados">-</p>
                        </div>
                    </div>
                    
                    <div class="stat-card stat-card-orange">
                        <div class="stat-icon">⏱️</div>
                        <div class="stat-info">
                            <h3 class="stat-title">En Proceso</h3>
                            <p class="stat-number" id="pedidos-proceso">-</p>
                        </div>
                    </div>
                    
                    <div class="stat-card stat-card-purple">
                        <div class="stat-icon">🎯</div>
                        <div class="stat-info">
                            <h3 class="stat-title">Listos</h3>
                            <p class="stat-number" id="pedidos-listos">-</p>
                        </div>
                    </div>
                </section>

                <!-- Info Panel -->
                <section class="info-panel">
                    <div class="info-card">
                        <div class="info-icon">🔍</div>
                        <div class="info-content">
                            <h3>Seguimiento en Tiempo Real</h3>
                            <p>Monitorea el estado de tus pedidos en cada etapa del proceso productivo</p>
                        </div>
                    </div>
                    
                    <div class="info-card">
                        <div class="info-icon">✨</div>
                        <div class="info-content">
                            <h3>Calidad Garantizada</h3>
                            <p>Materiales certificados y control de calidad en cada trabajo</p>
                        </div>
                    </div>
                    
                    <div class="info-card">
                        <div class="info-icon">🎯</div>
                        <div class="info-content">
                            <h3>Entrega a Tiempo</h3>
                            <p>Cumplimos con las fechas comprometidas para tus pacientes</p>
                        </div>
                    </div>
                </section>

                <!-- Carrusel -->
                <section class="carousel-section">
                    <div class="carousel">
                        <div class="carousel-item active">
                            <h3>🚀 Tecnología de Punta</h3>
                            <p>Diseño CAD/CAM y fresado de última generación</p>
                        </div>
                        <div class="carousel-item">
                            <h3>🏆 Experiencia Comprobada</h3>
                            <p>Más de 15 años en el mercado dental</p>
                        </div>
                        <div class="carousel-item">
                            <h3>💎 Materiales Premium</h3>
                            <p>Zirconia, disilicato de litio y más</p>
                        </div>
                    </div>
                </section>

            </div>
        </main>

        <footer class="footer">
            <div class="container">
                <p>&copy; 2025 LABDENT JLVEGA | Laboratorio Dental de Excelencia</p>
            </div>
        </footer>

        <script>
            // Carrusel
            let currentSlide = 0;
            const slides = document.querySelectorAll(".carousel-item");
            
            function showSlide(index) {
                slides.forEach((slide, i) => {
                    slide.classList.toggle("active", i === index);
                });
            }
            
            setInterval(() => {
                currentSlide = (currentSlide + 1) % slides.length;
                showSlide(currentSlide);
            }, 4000);

            // Cargar estadísticas
            const usuarioId = <%= idCliente %>;
            
            fetch(`pedido/listarPorUsuario?id=${usuarioId}`)
                .then(res => res.json())
                .then(data => {
                    if (data.status === 'success' && data.data) {
                        const pedidos = data.data;
                        const activos = pedidos.filter(p => p.estadoActual !== 'ENTREGADO').length;
                        const completados = pedidos.filter(p => p.estadoActual === 'ENTREGADO').length;
                        const proceso = pedidos.filter(p => 
                            ['PARALELIZADO', 'DISENO_CAD', 'PRODUCCION_CAM', 'CERAMICA'].includes(p.estadoActual)
                        ).length;
                        const listos = pedidos.filter(p => 
                            ['CONTROL_CALIDAD', 'LISTO_ENTREGA'].includes(p.estadoActual)
                        ).length;
                        
                        document.getElementById('pedidos-activos').textContent = activos;
                        document.getElementById('pedidos-completados').textContent = completados;
                        document.getElementById('pedidos-proceso').textContent = proceso;
                        document.getElementById('pedidos-listos').textContent = listos;
                    }
                })
                .catch(err => console.error('Error al cargar estadísticas:', err));
        </script>

    </body>
</html>