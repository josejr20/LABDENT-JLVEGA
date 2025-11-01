<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Sistema de Exportación - LABDENT</title>
    <link rel="stylesheet" href="css/sistemaExportacion.css"/>
</head>
<body>

<div class="export-container">
    <div class="export-header">
        <h1>📊 Centro de Exportación</h1>
        <p>Genera reportes y documentos personalizados</p>
    </div>
    
    <div class="export-body">
        <!-- Tabs de Categorías -->
        <div class="tabs">
            <button class="tab active" onclick="cambiarTab('produccion')">
                📈 Producción
            </button>
            <button class="tab" onclick="cambiarTab('financiero')">
                💰 Financiero
            </button>
            <button class="tab" onclick="cambiarTab('clientes')">
                👥 Clientes
            </button>
            <button class="tab" onclick="cambiarTab('rendimiento')">
                ⚡ Rendimiento
            </button>
            <button class="tab" onclick="cambiarTab('documentos')">
                📄 Documentos
            </button>
        </div>
        
        <!-- Tab: Producción -->
        <div id="produccion" class="tab-content active">
            <div class="report-grid">
                <div class="report-card" onclick="seleccionarReporte('estado_actual')">
                    <div class="report-icon">📋</div>
                    <div class="report-title">Estado Actual</div>
                    <div class="report-desc">Snapshot completo de todos los pedidos en producción</div>
                </div>
                
                <div class="report-card" onclick="seleccionarReporte('timeline')">
                    <div class="report-icon">⏱️</div>
                    <div class="report-title">Timeline de Pedidos</div>
                    <div class="report-desc">Tiempo promedio por etapa y cuellos de botella</div>
                </div>
                
                <div class="report-card" onclick="seleccionarReporte('atrasados')">
                    <div class="report-icon">⚠️</div>
                    <div class="report-title">Pedidos Atrasados</div>
                    <div class="report-desc">Listado de pedidos fuera de plazo</div>
                </div>
                
                <div class="report-card" onclick="seleccionarReporte('completados')">
                    <div class="report-icon">✅</div>
                    <div class="report-title">Pedidos Completados</div>
                    <div class="report-desc">Historial de entregas por período</div>
                </div>
            </div>
        </div>
        
        <!-- Tab: Financiero -->
        <div id="financiero" class="tab-content">
            <div class="report-grid">
                <div class="report-card" onclick="seleccionarReporte('facturacion')">
                    <div class="report-icon">💵</div>
                    <div class="report-title">Facturación</div>
                    <div class="report-desc">Ingresos por período y método de pago</div>
                </div>
                
                <div class="report-card" onclick="seleccionarReporte('por_odontologo')">
                    <div class="report-icon">👨‍⚕️</div>
                    <div class="report-title">Por Odontólogo</div>
                    <div class="report-desc">Pedidos y facturación por cliente</div>
                </div>
                
                <div class="report-card" onclick="seleccionarReporte('costos')">
                    <div class="report-icon">💎</div>
                    <div class="report-title">Análisis de Costos</div>
                    <div class="report-desc">Costos por material y tipo de prótesis</div>
                </div>
                
                <div class="report-card" onclick="seleccionarReporte('proyeccion')">
                    <div class="report-icon">📊</div>
                    <div class="report-title">Proyección</div>
                    <div class="report-desc">Estimación de ingresos futuros</div>
                </div>
            </div>
        </div>
        
        <!-- Tab: Clientes -->
        <div id="clientes" class="tab-content">
            <div class="report-grid">
                <div class="report-card" onclick="seleccionarReporte('historial_cliente')">
                    <div class="report-icon">📚</div>
                    <div class="report-title">Historial por Cliente</div>
                    <div class="report-desc">Todos los pedidos de un odontólogo específico</div>
                </div>
                
                <div class="report-card" onclick="seleccionarReporte('protesis_populares')">
                    <div class="report-icon">🦷</div>
                    <div class="report-title">Prótesis Populares</div>
                    <div class="report-desc">Tipos más solicitados y tendencias</div>
                </div>
                
                <div class="report-card" onclick="seleccionarReporte('satisfaccion')">
                    <div class="report-icon">⭐</div>
                    <div class="report-title">Satisfacción</div>
                    <div class="report-desc">Entregas a tiempo y calidad</div>
                </div>
                
                <div class="report-card" onclick="seleccionarReporte('nuevos_clientes')">
                    <div class="report-icon">🆕</div>
                    <div class="report-title">Nuevos Clientes</div>
                    <div class="report-desc">Registro de nuevos odontólogos</div>
                </div>
            </div>
        </div>
        
        <!-- Tab: Rendimiento -->
        <div id="rendimiento" class="tab-content">
            <div class="report-grid">
                <div class="report-card" onclick="seleccionarReporte('productividad')">
                    <div class="report-icon">🚀</div>
                    <div class="report-title">Productividad</div>
                    <div class="report-desc">Pedidos completados por técnico/ceramista</div>
                </div>
                
                <div class="report-card" onclick="seleccionarReporte('tiempos')">
                    <div class="report-icon">⏰</div>
                    <div class="report-title">Tiempos de Entrega</div>
                    <div class="report-desc">Análisis de cumplimiento de plazos</div>
                </div>
                
                <div class="report-card" onclick="seleccionarReporte('calidad')">
                    <div class="report-icon">🎯</div>
                    <div class="report-title">Control de Calidad</div>
                    <div class="report-desc">Rechazos y retrabajos</div>
                </div>
                
                <div class="report-card" onclick="seleccionarReporte('eficiencia')">
                    <div class="report-icon">📈</div>
                    <div class="report-title">Eficiencia</div>
                    <div class="report-desc">KPIs y métricas clave</div>
                </div>
            </div>
        </div>
        
        <!-- Tab: Documentos -->
        <div id="documentos" class="tab-content">
            <div class="report-grid">
                <div class="report-card" onclick="seleccionarReporte('certificado')">
                    <div class="report-icon">🏆</div>
                    <div class="report-title">Certificado de Garantía</div>
                    <div class="report-desc">Documento oficial de garantía del trabajo</div>
                </div>
                
                <div class="report-card" onclick="seleccionarReporte('orden_trabajo')">
                    <div class="report-icon">📝</div>
                    <div class="report-title">Orden de Trabajo</div>
                    <div class="report-desc">Especificaciones técnicas detalladas</div>
                </div>
                
                <div class="report-card" onclick="seleccionarReporte('etiquetas')">
                    <div class="report-icon">🏷️</div>
                    <div class="report-title">Etiquetas de Envío</div>
                    <div class="report-desc">Etiquetas con código de barras</div>
                </div>
                
                <div class="report-card" onclick="seleccionarReporte('factura')">
                    <div class="report-icon">🧾</div>
                    <div class="report-title">Factura/Boleta</div>
                    <div class="report-desc">Documento contable oficial</div>
                </div>
            </div>
        </div>
        
        <!-- Sección de Filtros -->
        <div class="filter-section">
            <div class="filter-title">
                <span>🎯</span>
                <span>Filtros y Opciones</span>
            </div>
            
            <div class="form-row">
                <div class="form-group">
                    <label class="form-label">Fecha Inicio:</label>
                    <input type="date" class="form-input" id="fechaInicio">
                </div>
                
                <div class="form-group">
                    <label class="form-label">Fecha Fin:</label>
                    <input type="date" class="form-input" id="fechaFin">
                </div>
                
                <div class="form-group">
                    <label class="form-label">Estado:</label>
                    <select class="form-select" id="estado">
                        <option value="">Todos</option>
                        <option value="RECEPCION">Recepción</option>
                        <option value="PARALELIZADO">Paralelizado</option>
                        <option value="DISENO_CAD">Diseño CAD</option>
                        <option value="PRODUCCION_CAM">Producción CAM</option>
                        <option value="CERAMICA">Cerámica</option>
                        <option value="CONTROL_CALIDAD">Control Calidad</option>
                        <option value="LISTO_ENTREGA">Listo Entrega</option>
                    </select>
                </div>
            </div>
            
            <div class="form-row">
                <div class="form-group">
                    <label class="form-label">Odontólogo:</label>
                    <select class="form-select" id="odontologo">
                        <option value="">Todos</option>
                        <!-- Cargar dinámicamente -->
                    </select>
                </div>
                
                <div class="form-group">
                    <label class="form-label">Responsable:</label>
                    <select class="form-select" id="responsable">
                        <option value="">Todos</option>
                        <!-- Cargar dinámicamente -->
                    </select>
                </div>
                
                <div class="form-group">
                    <label class="form-label">Prioridad:</label>
                    <select class="form-select" id="prioridad">
                        <option value="">Todas</option>
                        <option value="NORMAL">Normal</option>
                        <option value="URGENTE">Urgente</option>
                        <option value="EMERGENCIA">Emergencia</option>
                    </select>
                </div>
            </div>
        </div>
        
        <!-- Selector de Formato -->
        <div class="format-selector">
            <div class="filter-title">
                <span>📦</span>
                <span>Formato de Exportación</span>
            </div>
            
            <div class="format-options">
                <div class="format-option selected" data-format="excel">
                    <div class="format-icon">📊</div>
                    <div class="format-name">Excel</div>
                    <div class="format-size">.xlsx</div>
                </div>
                
                <div class="format-option" data-format="pdf">
                    <div class="format-icon">📄</div>
                    <div class="format-name">PDF</div>
                    <div class="format-size">.pdf</div>
                </div>
                
                <div class="format-option" data-format="csv">
                    <div class="format-icon">📋</div>
                    <div class="format-name">CSV</div>
                    <div class="format-size">.csv</div>
                </div>
                
                <div class="format-option" data-format="json">
                    <div class="format-icon">💾</div>
                    <div class="format-name">JSON</div>
                    <div class="format-size">.json</div>
                </div>
            </div>
        </div>
        
        <!-- Vista Previa -->
        <div class="preview-section" id="previewSection">
            <div class="filter-title">
                <span>👁️</span>
                <span>Vista Previa</span>
            </div>
            
            <table class="preview-table">
                <thead>
                    <tr>
                        <th>Código</th>
                        <th>Paciente</th>
                        <th>Estado</th>
                        <th>Fecha</th>
                        <th>Odontólogo</th>
                    </tr>
                </thead>
                <tbody id="previewBody">
                    <tr>
                        <td colspan="5" style="text-align: center; color: #999;">
                            Haz clic en "Vista Previa" para ver los datos
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>
        
        <div class="alert alert-info" id="infoAlert" style="display: none;">
            <span>ℹ️</span>
            <span id="infoMessage"></span>
        </div>
    </div>
    
    <!-- Botones de Acción -->
    <div class="action-buttons">
        <button class="btn btn-secondary" onclick="limpiarFiltros()">
            🔄 Limpiar
        </button>
        <button class="btn btn-success" onclick="vistaPrevia()">
            👁️ Vista Previa
        </button>
        <button class="btn btn-primary" onclick="exportar()">
            📥 Exportar
        </button>
    </div>
</div>

<script>
    let reporteSeleccionado = null;
    let formatoSeleccionado = 'excel';
    
    // Establecer fechas por defecto (último mes)
    const hoy = new Date();
    const haceUnMes = new Date();
    haceUnMes.setMonth(haceUnMes.getMonth() - 1);
    
    document.getElementById('fechaInicio').valueAsDate = haceUnMes;
    document.getElementById('fechaFin').valueAsDate = hoy;
    
    function cambiarTab(tab) {
        // Remover active de todos los tabs
        document.querySelectorAll('.tab').forEach(t => t.classList.remove('active'));
        document.querySelectorAll('.tab-content').forEach(c => c.classList.remove('active'));
        
        // Activar tab seleccionado
        event.target.classList.add('active');
        document.getElementById(tab).classList.add('active');
        
        // Limpiar selección de reporte
        document.querySelectorAll('.report-card').forEach(c => c.classList.remove('selected'));
        reporteSeleccionado = null;
        ocultarVistaPrevia();
    }
    
    function seleccionarReporte(tipo) {
        // Remover selección previa
        document.querySelectorAll('.report-card').forEach(c => c.classList.remove('selected'));
        
        // Seleccionar nuevo
        event.currentTarget.classList.add('selected');
        reporteSeleccionado = tipo;
        
        mostrarInfo(`Reporte seleccionado: ${event.currentTarget.querySelector('.report-title').textContent}`);
        ocultarVistaPrevia();
    }
    
    // Selector de formato
    document.querySelectorAll('.format-option').forEach(option => {
        option.addEventListener('click', function() {
            document.querySelectorAll('.format-option').forEach(o => o.classList.remove('selected'));
            this.classList.add('selected');
            formatoSeleccionado = this.getAttribute('data-format');
            mostrarInfo(`Formato seleccionado: ${formatoSeleccionado.toUpperCase()}`);
        });
    });
    
    function limpiarFiltros() {
        document.getElementById('estado').value = '';
        document.getElementById('odontologo').value = '';
        document.getElementById('responsable').value = '';
        document.getElementById('prioridad').value = '';
        
        const hoy = new Date();
        const haceUnMes = new Date();
        haceUnMes.setMonth(haceUnMes.getMonth() - 1);
        
        document.getElementById('fechaInicio').valueAsDate = haceUnMes;
        document.getElementById('fechaFin').valueAsDate = hoy;
        
        ocultarVistaPrevia();
        mostrarInfo('Filtros limpiados');
    }
    
    function vistaPrevia() {
        if (!reporteSeleccionado) {
            alert('⚠️ Selecciona un tipo de reporte primero');
            return;
        }
        
        // Simulación de datos
        const datosSimulados = [
            { codigo: 'LD-2025-001', paciente: 'Juan Pérez', estado: 'PRODUCCION_CAM', fecha: '2025-01-15', odontologo: 'Dr. García' },
            { codigo: 'LD-2025-002', paciente: 'María López', estado: 'CERAMICA', fecha: '2025-01-16', odontologo: 'Dr. Martínez' },
            { codigo: 'LD-2025-003', paciente: 'Carlos Ruiz', estado: 'CONTROL_CALIDAD', fecha: '2025-01-17', odontologo: 'Dra. Sánchez' },
            { codigo: 'LD-2025-004', paciente: 'Ana Torres', estado: 'DISENO_CAD', fecha: '2025-01-18', odontologo: 'Dr. García' },
            { codigo: 'LD-2025-005', paciente: 'Pedro Gómez', estado: 'LISTO_ENTREGA', fecha: '2025-01-19', odontologo: 'Dr. Rodríguez' }
        ];
        
        const tbody = document.getElementById('previewBody');
        tbody.innerHTML = '';
        
        datosSimulados.forEach(dato => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td><strong>${dato.codigo}</strong></td>
                <td>${dato.paciente}</td>
                <td>${dato.estado}</td>
                <td>${dato.fecha}</td>
                <td>${dato.odontologo}</td>
            `;
            tbody.appendChild(tr);
        });
        
        document.getElementById('previewSection').classList.add('active');
        mostrarInfo(`Vista previa generada: ${datosSimulados.length} registros encontrados`);
    }
    
    function ocultarVistaPrevia() {
        document.getElementById('previewSection').classList.remove('active');
    }
    
    function exportar() {
        if (!reporteSeleccionado) {
            alert('⚠️ Selecciona un tipo de reporte primero');
            return;
        }
        
        const fechaInicio = document.getElementById('fechaInicio').value;
        const fechaFin = document.getElementById('fechaFin').value;
        const estado = document.getElementById('estado').value;
        const odontologo = document.getElementById('odontologo').value;
        const responsable = document.getElementById('responsable').value;
        const prioridad = document.getElementById('prioridad').value;
        
        // Construcción de URL con parámetros
        const params = new URLSearchParams({
            tipo: reporteSeleccionado,
            formato: formatoSeleccionado,
            fechaInicio: fechaInicio,
            fechaFin: fechaFin,
            estado: estado || '',
            odontologo: odontologo || '',
            responsable: responsable || '',
            prioridad: prioridad || ''
        });
        
        // Simulación de exportación
        mostrarInfo(`🚀 Generando reporte ${reporteSeleccionado} en formato ${formatoSeleccionado.toUpperCase()}...`);
        
        setTimeout(() => {
            // En producción, esto sería una llamada real al backend
            // window.location.href = `ExportarReporte?${params.toString()}`;
            
            const infoAlert = document.getElementById('infoAlert');
            infoAlert.className = 'alert alert-success';
            infoAlert.querySelector('#infoMessage').textContent = 
                `✅ Reporte generado exitosamente. La descarga comenzará en breve...`;
            
            // Simular descarga
            console.log('Exportando con parámetros:', {
                tipo: reporteSeleccionado,
                formato: formatoSeleccionado,
                fechaInicio,
                fechaFin,
                estado,
                odontologo,
                responsable,
                prioridad
            });
            
            // Aquí iría la llamada real al backend
            descargarReporte(params);
        }, 1500);
    }
    
    function descargarReporte(params) {
        // Simulación de descarga
        // En producción esto redirige al servlet de exportación
        
        const tipoReporte = params.get('tipo');
        const formato = params.get('formato');
        
        // Ejemplo de implementación real:
        
        const url = `ExportarReporte?${params.toString()}`;
        const a = document.createElement('a');
        a.href = url;
        a.download = "reporte_" + tipoReporte + "_" + timestamp + "." + formato ;
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        
        
        console.log(`📥 Descargando: reporte_${tipoReporte}.${formato}`);
        
        // Información adicional según el tipo de reporte
        const infoReporte = {
            'estado_actual': '📋 Estado actual de todos los pedidos en producción',
            'timeline': '⏱️ Análisis temporal de pedidos con gráficos',
            'atrasados': '⚠️ Listado detallado de pedidos fuera de plazo',
            'completados': '✅ Historial de entregas exitosas',
            'facturacion': '💵 Reporte financiero con totales y gráficos',
            'por_odontologo': '👨‍⚕️ Desglose por cliente con estadísticas',
            'costos': '💎 Análisis de costos por material y tipo',
            'proyeccion': '📊 Proyección de ingresos futuros',
            'historial_cliente': '📚 Historial completo de un cliente',
            'protesis_populares': '🦷 Análisis de tendencias de productos',
            'satisfaccion': '⭐ Métricas de satisfacción del cliente',
            'nuevos_clientes': '🆕 Listado de nuevos registros',
            'productividad': '🚀 Rendimiento individual por técnico',
            'tiempos': '⏰ Análisis de tiempos de entrega',
            'calidad': '🎯 Métricas de control de calidad',
            'eficiencia': '📈 KPIs y métricas clave',
            'certificado': '🏆 Certificado de garantía oficial',
            'orden_trabajo': '📝 Orden de trabajo detallada',
            'etiquetas': '🏷️ Etiquetas con código de barras',
            'factura': '🧾 Factura o boleta de venta'
        };
        
        setTimeout(() => {
            alert(`✅ Exportación completada\n\n${infoReporte[tipoReporte] || 'Reporte generado'}\n\nFormato: ${formato.toUpperCase()}`);
        }, 2000);
    }
    
    function mostrarInfo(mensaje) {
        const infoAlert = document.getElementById('infoAlert');
        infoAlert.className = 'alert alert-info';
        infoAlert.style.display = 'flex';
        infoAlert.querySelector('#infoMessage').textContent = mensaje;
        
        setTimeout(() => {
            infoAlert.style.display = 'none';
        }, 4000);
    }
    
    // Cargar datos iniciales (simulado)
    window.addEventListener('load', function() {
        // Cargar odontólogos
        const selectOdontologo = document.getElementById('odontologo');
        const odontologos = [
            'Dr. García López',
            'Dra. Martínez Silva',
            'Dr. Rodríguez Pérez',
            'Dra. Sánchez Torres'
        ];
        
        odontologos.forEach(odon => {
            const option = document.createElement('option');
            option.value = odon;
            option.textContent = odon;
            selectOdontologo.appendChild(option);
        });
        
        // Cargar responsables
        const selectResponsable = document.getElementById('responsable');
        const responsables = [
            'Juan Pérez - Técnico CAD',
            'María García - Técnico CAM',
            'Carlos López - Ceramista',
            'Ana Martínez - Control Calidad'
        ];
        
        responsables.forEach(resp => {
            const option = document.createElement('option');
            option.value = resp;
            option.textContent = resp;
            selectResponsable.appendChild(option);
        });
        
        console.log('✅ Sistema de exportación cargado');
    });
</script>

</body>
</html>