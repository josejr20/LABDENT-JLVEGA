<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.labdent.model.Usuario" %>
<%@ page import="com.labdent.model.Reporte" %>
<%@ page import="java.text.DecimalFormat" %>
<%
    Usuario usuario = (Usuario) session.getAttribute("usuario");
    if (usuario == null || !usuario.isAdmin()) {
        response.sendRedirect("login");
        return;
    }
    
    Reporte reporte = (Reporte) request.getAttribute("reporte");
    if (reporte == null) {
        // Si no hay reporte, redireccionar al servlet que lo genera
        response.sendRedirect("GenerarReporte");
        return;
    }
    
    DecimalFormat df = new DecimalFormat("#.##");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Reportes - LABDENT JLVEGA</title>
    <link rel="stylesheet" href="css/dashboard.css">
    <script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.0/dist/chart.umd.min.js"></script>
    <style>
        .reportes-container {
            padding: 30px;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
        }
        
        .reportes-header {
            background: white;
            border-radius: 20px;
            padding: 30px;
            margin-bottom: 30px;
            box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
        }
        
        .reportes-header h1 {
            font-size: 32px;
            color: #333;
            margin-bottom: 10px;
        }
        
        .reportes-header p {
            color: #666;
            font-size: 16px;
        }
        
        .reportes-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 20px;
            margin-bottom: 30px;
        }
        
        .reporte-card {
            background: white;
            border-radius: 16px;
            padding: 25px;
            box-shadow: 0 5px 20px rgba(0, 0, 0, 0.08);
            transition: all 0.3s;
            position: relative;
            overflow: hidden;
        }
        
        .reporte-card::before {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            width: 100%;
            height: 4px;
            background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
        }
        
        .reporte-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 15px 40px rgba(0, 0, 0, 0.15);
        }
        
        .reporte-card.alert::before {
            background: linear-gradient(90deg, #e74c3c 0%, #c0392b 100%);
        }
        
        .reporte-card.success::before {
            background: linear-gradient(90deg, #27ae60 0%, #229954 100%);
        }
        
        .reporte-card.warning::before {
            background: linear-gradient(90deg, #f39c12 0%, #e67e22 100%);
        }
        
        .reporte-card h3 {
            font-size: 14px;
            color: #666;
            margin-bottom: 15px;
            text-transform: uppercase;
            letter-spacing: 1px;
        }
        
        .reporte-numero {
            font-size: 48px;
            font-weight: 700;
            color: #333;
            margin: 10px 0;
        }
        
        .reporte-desc {
            font-size: 13px;
            color: #999;
        }
        
        .kpi-section {
            background: white;
            border-radius: 20px;
            padding: 30px;
            margin-bottom: 30px;
            box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
        }
        
        .kpi-section h3 {
            font-size: 24px;
            color: #333;
            margin-bottom: 20px;
        }
        
        .kpi-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
            gap: 20px;
        }
        
        .kpi-card {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            border-radius: 16px;
            padding: 25px;
            transition: all 0.3s;
        }
        
        .kpi-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 15px 40px rgba(102, 126, 234, 0.4);
        }
        
        .kpi-card h4 {
            font-size: 16px;
            opacity: 0.9;
            margin-bottom: 15px;
        }
        
        .kpi-value {
            font-size: 42px;
            font-weight: 700;
            margin: 10px 0;
        }
        
        .kpi-desc {
            font-size: 13px;
            opacity: 0.8;
        }
        
        .chart-section {
            background: white;
            border-radius: 20px;
            padding: 30px;
            margin-bottom: 30px;
            box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
        }
        
        .chart-section h3 {
            font-size: 24px;
            color: #333;
            margin-bottom: 20px;
        }
        
        .charts-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
            gap: 30px;
        }
        
        .chart-container {
            position: relative;
            height: 300px;
        }
        
        .export-section {
            background: white;
            border-radius: 20px;
            padding: 30px;
            box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
            text-align: center;
        }
        
        .btn {
            padding: 12px 30px;
            border: none;
            border-radius: 8px;
            cursor: pointer;
            font-weight: 600;
            font-size: 14px;
            transition: all 0.3s;
            text-decoration: none;
            display: inline-block;
            margin: 0 10px;
        }
        
        .btn-primary {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
        }
        
        .btn-primary:hover {
            transform: translateY(-2px);
            box-shadow: 0 10px 25px rgba(102, 126, 234, 0.4);
        }
        
        .btn-secondary {
            background: #f8f9fa;
            color: #333;
            border: 2px solid #e9ecef;
        }
        
        .btn-secondary:hover {
            background: #e9ecef;
        }
        
        .btn-success {
            background: #27ae60;
            color: white;
        }
        
        .btn-success:hover {
            background: #229954;
            transform: translateY(-2px);
        }
        
        @media print {
            body {
                background: white;
            }
            .export-section {
                display: none;
            }
        }
    </style>
</head>
<body>
    <div class="reportes-container">
        <div class="reportes-header">
            <h1>📊 Reportes de Productividad</h1>
            <p>Análisis completo del rendimiento del laboratorio dental</p>
        </div>
        
        <!-- Métricas Principales -->
        <div class="reportes-grid">
            <div class="reporte-card">
                <h3>Total de Pedidos</h3>
                <p class="reporte-numero"><%= reporte.getTotalPedidos() %></p>
                <p class="reporte-desc">Histórico completo</p>
            </div>
            
            <div class="reporte-card warning">
                <h3>En Proceso</h3>
                <p class="reporte-numero"><%= reporte.getPedidosEnProceso() %></p>
                <p class="reporte-desc">Actualmente en producción</p>
            </div>
            
            <div class="reporte-card success">
                <h3>Entregados</h3>
                <p class="reporte-numero"><%= reporte.getPedidosEntregados() %></p>
                <p class="reporte-desc">Completados exitosamente</p>
            </div>
            
            <div class="reporte-card alert">
                <h3>⚠️ Atrasados</h3>
                <p class="reporte-numero"><%= reporte.getPedidosAtrasados() %></p>
                <p class="reporte-desc">Requieren atención inmediata</p>
            </div>
        </div>
        
        <!-- Indicadores de Rendimiento -->
        <div class="kpi-section">
            <h3>🎯 Indicadores Clave de Rendimiento (KPIs)</h3>
            <div class="kpi-grid">
                <div class="kpi-card">
                    <h4>⏱️ Tiempo Promedio de Entrega</h4>
                    <p class="kpi-value"><%= df.format(reporte.getTiempoPromedioEntrega()) %> días</p>
                    <p class="kpi-desc">Desde ingreso hasta entrega final</p>
                </div>
                
                <div class="kpi-card">
                    <h4>✅ Tasa de Cumplimiento</h4>
                    <p class="kpi-value"><%= df.format(reporte.getTasaCumplimiento()) %>%</p>
                    <p class="kpi-desc">Pedidos entregados dentro del plazo</p>
                </div>
                
                <div class="kpi-card">
                    <h4>📈 Eficiencia de Producción</h4>
                    <p class="kpi-value">
                        <%= df.format((double)reporte.getPedidosEntregados() / reporte.getTotalPedidos() * 100) %>%
                    </p>
                    <p class="kpi-desc">Pedidos completados vs total</p>
                </div>
            </div>
        </div>
        
        <!-- Gráficos -->
        <div class="chart-section">
            <h3>📊 Análisis Visual</h3>
            <div class="charts-grid">
                <div>
                    <h4 style="text-align: center; margin-bottom: 15px;">Distribución por Estado</h4>
                    <div class="chart-container">
                        <canvas id="chartEstados"></canvas>
                    </div>
                </div>
                
                <div>
                    <h4 style="text-align: center; margin-bottom: 15px;">Estado de Pedidos</h4>
                    <div class="chart-container">
                        <canvas id="chartStatus"></canvas>
                    </div>
                </div>
            </div>
        </div>
        
        <!-- Desglose por Estado -->
        <div class="kpi-section">
            <h3>📋 Desglose Detallado por Estado</h3>
            <div class="reportes-grid">
                <div class="reporte-card">
                    <h3>📥 Recepción</h3>
                    <p class="reporte-numero"><%= reporte.getPedidosRecepcion() %></p>
                    <p class="reporte-desc">En registro inicial</p>
                </div>
                
                <div class="reporte-card">
                    <h3>🔄 Paralelizado</h3>
                    <p class="reporte-numero"><%= reporte.getPedidosParalelizado() %></p>
                    <p class="reporte-desc">En preparación</p>
                </div>
                
                <div class="reporte-card">
                    <h3>💻 Diseño CAD</h3>
                    <p class="reporte-numero"><%= reporte.getPedidosDisenoCad() %></p>
                    <p class="reporte-desc">En diseño digital</p>
                </div>
                
                <div class="reporte-card">
                    <h3>⚙️ Producción CAM</h3>
                    <p class="reporte-numero"><%= reporte.getPedidosProduccionCam() %></p>
                    <p class="reporte-desc">En fresado/fabricación</p>
                </div>
                
                <div class="reporte-card">
                    <h3>🎨 Cerámica</h3>
                    <p class="reporte-numero"><%= reporte.getPedidosCeramica() %></p>
                    <p class="reporte-desc">En aplicación cerámica</p>
                </div>
                
                <div class="reporte-card">
                    <h3>✓ Control Calidad</h3>
                    <p class="reporte-numero"><%= reporte.getPedidosControlCalidad() %></p>
                    <p class="reporte-desc">En revisión final</p>
                </div>
                
                <div class="reporte-card success">
                    <h3>📦 Listo Entrega</h3>
                    <p class="reporte-numero"><%= reporte.getPedidosListoEntrega() %></p>
                    <p class="reporte-desc">Preparado para despacho</p>
                </div>
            </div>
        </div>
        
        <!-- Botones de Exportación -->
        <div class="export-section">
            <h3 style="margin-bottom: 20px;">📤 Exportar Reportes</h3>
            <button onclick="window.print()" class="btn btn-secondary">
                🖨️ Imprimir
            </button>
            <a href="ExportarReporte?tipo=reporte_general&formato=excel" class="btn btn-success">
                📊 Exportar Excel
            </a>
            <a href="ExportarReporte?tipo=reporte_general&formato=pdf" class="btn btn-primary">
                📄 Exportar PDF
            </a>
            <button onclick="window.location.href='dashboard'" class="btn btn-secondary">
                ← Volver al Dashboard
            </button>
        </div>
    </div>
    
    <script>
        // Configuración de colores
        const colors = {
            primary: '#667eea',
            success: '#27ae60',
            warning: '#f39c12',
            danger: '#e74c3c',
            info: '#3498db',
            purple: '#9b59b6',
            teal: '#1abc9c',
            orange: '#e67e22'
        };
        
        // Gráfico de Distribución por Estado
        const ctxEstados = document.getElementById('chartEstados').getContext('2d');
        new Chart(ctxEstados, {
            type: 'doughnut',
            data: {
                labels: ['Recepción', 'Paralelizado', 'Diseño CAD', 'Producción CAM', 
                         'Cerámica', 'Control Calidad', 'Listo Entrega'],
                datasets: [{
                    data: [
                        <%= reporte.getPedidosRecepcion() %>,
                        <%= reporte.getPedidosParalelizado() %>,
                        <%= reporte.getPedidosDisenoCad() %>,
                        <%= reporte.getPedidosProduccionCam() %>,
                        <%= reporte.getPedidosCeramica() %>,
                        <%= reporte.getPedidosControlCalidad() %>,
                        <%= reporte.getPedidosListoEntrega() %>
                    ],
                    backgroundColor: [
                        colors.info,
                        colors.purple,
                        colors.teal,
                        colors.orange,
                        colors.danger,
                        colors.primary,
                        colors.success
                    ],
                    borderWidth: 0
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        position: 'bottom',
                        labels: {
                            padding: 15,
                            font: {
                                size: 12
                            }
                        }
                    }
                }
            }
        });
        
        // Gráfico de Estado General
        const ctxStatus = document.getElementById('chartStatus').getContext('2d');
        new Chart(ctxStatus, {
            type: 'bar',
            data: {
                labels: ['En Proceso', 'Entregados', 'Atrasados'],
                datasets: [{
                    label: 'Cantidad de Pedidos',
                    data: [
                        <%= reporte.getPedidosEnProceso() %>,
                        <%= reporte.getPedidosEntregados() %>,
                        <%= reporte.getPedidosAtrasados() %>
                    ],
                    backgroundColor: [
                        colors.warning,
                        colors.success,
                        colors.danger
                    ],
                    borderWidth: 0,
                    borderRadius: 8
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        display: false
                    }
                },
                scales: {
                    y: {
                        beginAtZero: true,
                        grid: {
                            display: true,
                            color: 'rgba(0, 0, 0, 0.05)'
                        }
                    },
                    x: {
                        grid: {
                            display: false
                        }
                    }
                }
            }
        });
        
        console.log('✅ Reportes cargados correctamente');
    </script>
</body>
</html>