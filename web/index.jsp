<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>LABDENT JLVEGA - Laboratorio Dental Digital</title>
    <link rel="stylesheet" href="css/styles.css">
    <style>
        .hero {
            background: linear-gradient(135deg, var(--secondary-color) 0%, var(--primary-color) 100%);
            color: white;
            padding: 80px 20px;
            text-align: center;
            margin-bottom: 40px;
        }
        .hero h2 {
            font-size: 42px;
            margin-bottom: 20px;
        }
        .hero p {
            font-size: 18px;
            margin-bottom: 30px;
            opacity: 0.95;
        }
        .hero-buttons {
            display: flex;
            gap: 15px;
            justify-content: center;
            flex-wrap: wrap;
        }
        .about {
            padding: 40px 0;
            background: white;
        }
        .about p {
            line-height: 1.8;
            color: var(--gray-700);
            max-width: 900px;
            margin: 20px auto;
        }
        .services-preview {
            padding: 40px 0;
        }
        .services-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 25px;
            margin-bottom: 30px;
        }
        .service-card {
            background: white;
            padding: 30px;
            border-radius: 12px;
            box-shadow: 0 4px 6px rgba(0,0,0,0.1);
            text-align: center;
            transition: transform 0.3s;
        }
        .service-card:hover {
            transform: translateY(-10px);
            box-shadow: 0 8px 12px rgba(0,0,0,0.15);
        }
        .service-icon {
            font-size: 48px;
            margin-bottom: 15px;
        }
        .service-card h3 {
            color: var(--primary-color);
            margin-bottom: 10px;
        }
        .quick-search {
            background: white;
            padding: 60px 20px;
            text-align: center;
        }
        .search-form {
            max-width: 600px;
            margin: 30px auto 0;
            display: flex;
            gap: 10px;
        }
        .search-form input {
            flex: 1;
            padding: 15px;
            border: 2px solid var(--gray-300);
            border-radius: 8px;
            font-size: 16px;
        }
        .tiempos {
            padding: 40px 0;
            background: var(--gray-100);
        }
        .tabla-tiempos {
            max-width: 800px;
            margin: 30px auto;
            background: white;
            border-radius: 8px;
            overflow: hidden;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
        .tabla-tiempos th,
        .tabla-tiempos td {
            padding: 15px;
            text-align: left;
        }
        .tabla-tiempos thead {
            background: var(--primary-color);
            color: white;
        }
        .tabla-tiempos tbody tr:nth-child(even) {
            background: var(--gray-100);
        }
        .nota {
            text-align: center;
            margin-top: 20px;
            font-size: 14px;
            color: var(--gray-600);
            font-style: italic;
        }
        .contacto {
            padding: 40px 0;
            background: white;
        }
        .contacto-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 30px;
            margin-top: 30px;
        }
        .contacto-item {
            text-align: center;
            padding: 20px;
        }
        .contacto-item h4 {
            color: var(--primary-color);
            margin-bottom: 10px;
            font-size: 18px;
        }
        .info-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
            gap: 25px;
            margin-top: 30px;
        }
    </style>
</head>
<body>
    <!-- Header -->
    <header class="header">
        <div class="container">
            <div class="logo">
                <h1>LABDENT JLVEGA</h1>
                <p class="tagline">Tecnología CAD/CAM de Vanguardia</p>
            </div>
            <nav class="nav">
                <a href="index.jsp" class="active">Inicio</a>
                <a href="#servicios">Servicios</a>
                <a href=".registrarPedido">Registrar pedido</a>
                <a href="#consulta">Consultar Pedido</a>
                <a href="#contacto">Contacto</a>
                <a href="login">Iniciar Sesión</a>
            </nav>
        </div>
    </header>

    <!-- Hero Section -->
    <section class="hero">
        <div class="hero-content">
            <h2>Prótesis Dentales de Alta Calidad</h2>
            <p>Especialistas en diseño y fabricación con tecnología digital CAD/CAM</p>
            <div class="hero-buttons">
                <a href="#consulta" class="btn btn-primary">Consultar mi Pedido</a>
                <a href="login" class="btn btn-outline" style="color: white; border-color: white;">Sistema Interno</a>
            </div>
        </div>
    </section>

    <!-- Sección: Sobre Nosotros -->
    <section class="about">
        <div class="container">
            <h2 style="text-align: center; color: var(--primary-color);">¿Quiénes Somos?</h2>
            <p>
                <strong>LABDENT JLVEGA</strong> es un laboratorio dental especializado en el diseño, 
                fabricación y acabado de prótesis dentales fijas mediante tecnología digital. 
                Ubicados en <strong>Tarapoto, San Martín</strong>, contamos con un equipo de técnicos altamente 
                calificados que trabajan con software CAD/CAM, impresoras 3D de alta precisión 
                y máquinas de fresado <strong>CAMDENT CDM6G de 5 ejes</strong>.
            </p>
            <p>
                Nos distinguimos por brindar un servicio personalizado, eficaz y eficiente, 
                con una filosofía centrada en la calidad, la innovación y la colaboración 
                con el profesional odontólogo, buscando siempre la excelencia en cada 
                rehabilitación oral.
            </p>
        </div>
    </section>

    <!-- Sección: Servicios Destacados -->
    <section id="servicios" class="services-preview">
        <div class="container">
            <h2 style="text-align: center; color: var(--primary-color);">Nuestros Servicios</h2>
            <div class="services-grid">
                <div class="service-card">
                    <div class="service-icon">🦷</div>
                    <h3>Coronas de Zirconia</h3>
                    <p>Restauraciones estéticas de máxima resistencia y biocompatibilidad</p>
                </div>
                <div class="service-card">
                    <div class="service-icon">✨</div>
                    <h3>Metal-Porcelana</h3>
                    <p>Prótesis tradicionales con excelente durabilidad y ajuste</p>
                </div>
                <div class="service-card">
                    <div class="service-icon">💎</div>
                    <h3>Disilicato de Litio</h3>
                    <p>Máxima estética y translucidez natural para sector anterior</p>
                </div>
                <div class="service-card">
                    <div class="service-icon">🖨️</div>
                    <h3>Impresión 3D</h3>
                    <p>Tecnología de última generación para resultados precisos</p>
                </div>
            </div>
        </div>
    </section>

    <!-- Sección: Consulta Rápida -->
    <section id="consulta" class="quick-search">
        <div class="container">
            <h2 style="color: var(--primary-color);">¿Ya realizaste un pedido?</h2>
            <p>Consulta el estado de tu trabajo en tiempo real ingresando tu código de seguimiento</p>
            <form action="consulta-publica" method="get" class="search-form">
                <input type="text" 
                       name="codigo" 
                       placeholder="Ingresa tu código de seguimiento (ej: LAB-1727890123456)" 
                       required>
                <button type="submit" class="btn btn-primary">Consultar</button>
            </form>
            <p style="margin-top: 15px; font-size: 14px; color: var(--gray-600);">
                Si aún no tienes cuenta, <a href="login" style="color: var(--secondary-color);">inicia sesión aquí</a>
            </p>
        </div>
    </section>

    <!-- Sección: Tiempos Estimados -->
    <section class="tiempos">
        <div class="container">
            <h2 style="text-align: center; color: var(--primary-color);">Tiempos de Entrega Estimados</h2>
            <table class="tabla-tiempos">
                <thead>
                    <tr>
                        <th>Tipo de Trabajo</th>
                        <th>Material</th>
                        <th>Tiempo Estimado</th>
                    </tr>
                </thead>
                <tbody>
                    <tr>
                        <td>Corona Individual</td>
                        <td>Zirconia</td>
                        <td>3-5 días hábiles</td>
                    </tr>
                    <tr>
                        <td>Corona Individual</td>
                        <td>Metal-Porcelana</td>
                        <td>4-6 días hábiles</td>
                    </tr>
                    <tr>
                        <td>Puente (3 piezas)</td>
                        <td>Zirconia</td>
                        <td>5-7 días hábiles</td>
                    </tr>
                    <tr>
                        <td>Carillas</td>
                        <td>Disilicato de Litio</td>
                        <td>4-5 días hábiles</td>
                    </tr>
                    <tr>
                        <td>Incrustación</td>
                        <td>Cerámica</td>
                        <td>3-4 días hábiles</td>
                    </tr>
                </tbody>
            </table>
            <p class="nota">* Los tiempos pueden variar según la complejidad del caso y la carga de trabajo</p>
        </div>
    </section>

    <!-- Sección: Visión y Misión -->
    <section class="about">
        <div class="container">
            <div class="info-grid">
                <div class="info-card">
                    <h3>Visión</h3>
                    <p>
                        Ser una empresa líder en el sector de la industria dental a nivel regional, 
                        con capacidad de competir exitosamente en el mercado, generando productos 
                        y servicios innovadores que satisfagan las necesidades de los odontólogos 
                        y pacientes, con altos estándares de calidad.
                    </p>
                </div>
                <div class="info-card">
                    <h3>Misión</h3>
                    <p>
                        Satisfacer las necesidades de nuestros clientes en el área de la prótesis 
                        y rehabilitación dental, proporcionándoles diseños biocompatibles y 
                        biomecánicos, utilizando tecnología de punta, materiales contemporáneos 
                        y procesos de fabricación innovadores.
                    </p>
                </div>
            </div>
        </div>
    </section>

    <!-- Sección: Contacto -->
    <section id="contacto" class="contacto">
        <div class="container">
            <h2 style="text-align: center; color: var(--primary-color);">Contacto</h2>
            <div class="contacto-grid">
                <div class="contacto-item">
                    <h4>📍 Ubicación</h4>
                    <p>Tarapoto, San Martín, Perú</p>
                </div>
                <div class="contacto-item">
                    <h4>📞 Teléfono</h4>
                    <p>(042) 123-456</p>
                    <p>WhatsApp: 942 000 000</p>
                </div>
                <div class="contacto-item">
                    <h4>📧 Email</h4>
                    <p>contacto@labdentjlvega.com</p>
                </div>
                <div class="contacto-item">
                    <h4>🕐 Horario</h4>
                    <p>Lun - Vie: 8:00 AM - 6:00 PM</p>
                    <p>Sáb: 9:00 AM - 1:00 PM</p>
                </div>
            </div>
        </div>
    </section>

    <!-- Footer -->
    <footer class="footer">
        <div class="container">
            <p>&copy; 2025 LABDENT JLVEGA. Todos los derechos reservados.</p>
            <p>Sistema de Gestión de Pedidos v2.0 - Desarrollado con Java Servlets y JSP</p>
            <p style="margin-top: 10px;">
                <a href="login" style="color: white; text-decoration: underline;">Acceso Personal Interno</a>
            </p>
        </div>
    </footer>
</body>
</html>