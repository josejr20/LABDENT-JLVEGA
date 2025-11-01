package com.labdent.controller;

import com.labdent.dao.PedidoDAO;
import com.labdent.dao.UsuarioDAO;
import com.labdent.dao.TransicionEstadoDAO;
import com.labdent.model.Pedido;
import com.labdent.model.Usuario;
import com.labdent.model.TransicionEstado;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.usermodel.Font;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.Font.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/ExportarReporte")
public class ExportarReporteServlet extends HttpServlet {

    private PedidoDAO pedidoDAO;
    private UsuarioDAO usuarioDAO;
    private TransicionEstadoDAO transicionDAO;

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public void init() throws ServletException {
        super.init();
        pedidoDAO = new PedidoDAO();
        usuarioDAO = new UsuarioDAO();
        transicionDAO = new TransicionEstadoDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuario") == null) {
            response.sendRedirect("login");
            return;
        }

        Usuario usuario = (Usuario) session.getAttribute("usuario");

        // Obtener parámetros
        String tipo = request.getParameter("tipo");
        String formato = request.getParameter("formato");
        String fechaInicio = request.getParameter("fechaInicio");
        String fechaFin = request.getParameter("fechaFin");
        String estadoFiltro = request.getParameter("estado");
        String odontologoId = request.getParameter("odontologo");
        String responsableId = request.getParameter("responsable");
        String prioridad = request.getParameter("prioridad");

        try {
            // Obtener datos según el tipo de reporte
            List<Pedido> pedidos = obtenerDatosReporte(tipo, fechaInicio, fechaFin,
                    estadoFiltro, odontologoId, responsableId, prioridad);

            // Generar reporte según el formato
            switch (formato.toLowerCase()) {
                case "excel":
                    generarExcel(response, tipo, pedidos, usuario);
                    break;
                case "pdf":
                    generarPDF(response, tipo, pedidos, usuario);
                    break;
                case "csv":
                    generarCSV(response, tipo, pedidos);
                    break;
                case "json":
                    generarJSON(response, tipo, pedidos);
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                            "Formato no soportado");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Error al generar reporte: " + e.getMessage());
        }
    }

    // ==================== OBTENCIÓN DE DATOS ====================
    private List<Pedido> obtenerDatosReporte(String tipo, String fechaInicio,
            String fechaFin, String estado, String odontologoId,
            String responsableId, String prioridad) {

        List<Pedido> pedidos = new ArrayList<>();

        try {
            switch (tipo) {
                case "estado_actual":
                    pedidos = pedidoDAO.listarTodos();
                    break;

                case "atrasados":
                    pedidos = pedidoDAO.listarTodos().stream()
                            .filter(Pedido::isAtrasado)
                            .collect(Collectors.toList());
                    break;

                case "completados":
                    pedidos = pedidoDAO.listarPorEstado("LISTO_ENTREGA");
                    pedidos.addAll(pedidoDAO.listarPorEstado("ENTREGADO"));
                    break;

                case "timeline":
                case "facturacion":
                case "por_odontologo":
                case "productividad":
                case "tiempos":
                    pedidos = pedidoDAO.listarTodos();
                    break;

                default:
                    pedidos = pedidoDAO.listarTodos();
            }

            // Aplicar filtros
            if (estado != null && !estado.isEmpty()) {
                pedidos = pedidos.stream()
                        .filter(p -> p.getEstadoActual().equals(estado))
                        .collect(Collectors.toList());
            }

            if (prioridad != null && !prioridad.isEmpty()) {
                pedidos = pedidos.stream()
                        .filter(p -> p.getPrioridad().equals(prioridad))
                        .collect(Collectors.toList());
            }

            if (odontologoId != null && !odontologoId.isEmpty()) {
                int odontoId = Integer.parseInt(odontologoId);
                pedidos = pedidos.stream()
                        .filter(p -> p.getOdontologoId() == odontoId)
                        .collect(Collectors.toList());
            }

            if (responsableId != null && !responsableId.isEmpty()) {
                int respId = Integer.parseInt(responsableId);
                pedidos = pedidos.stream()
                        .filter(p -> p.getResponsableActual() != null
                        && p.getResponsableActual() == respId)
                        .collect(Collectors.toList());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return pedidos;
    }

    // ==================== GENERACIÓN EXCEL ====================
    private void generarExcel(HttpServletResponse response, String tipo,
            List<Pedido> pedidos, Usuario usuario) throws IOException {

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet(obtenerNombreReporte(tipo));

        // Estilos
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);

        CellStyle dateStyle = workbook.createCellStyle();
        CreationHelper createHelper = workbook.getCreationHelper();
        dateStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd/MM/yyyy"));

        CellStyle urgentStyle = workbook.createCellStyle();
        urgentStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
        urgentStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // Crear encabezado del documento
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("LABDENT JLVEGA - " + obtenerNombreReporte(tipo));

        CellStyle titleStyle = workbook.createCellStyle();
        Font titleFont = workbook.createFont();
        titleFont.setBold(true);
        titleFont.setFontHeightInPoints((short) 16);
        titleStyle.setFont(titleFont);
        titleCell.setCellStyle(titleStyle);

        Row infoRow = sheet.createRow(1);
        infoRow.createCell(0).setCellValue("Generado por: " + usuario.getNombreCompleto());

        Row dateRow = sheet.createRow(2);
        dateRow.createCell(0).setCellValue("Fecha: "
                + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()));

        // Fila vacía
        int rowNum = 4;

        // Encabezados de columnas según el tipo de reporte
        Row headerRow = sheet.createRow(rowNum++);

        switch (tipo) {
            case "estado_actual":
            case "atrasados":
            case "completados":
                crearEncabezadosEstadoActual(headerRow, headerStyle);
                rowNum = llenarDatosEstadoActual(sheet, pedidos, rowNum, dateStyle, urgentStyle);
                break;

            case "timeline":
                crearEncabezadosTimeline(headerRow, headerStyle);
                rowNum = llenarDatosTimeline(sheet, pedidos, rowNum, dateStyle);
                break;

            case "facturacion":
                crearEncabezadosFacturacion(headerRow, headerStyle);
                rowNum = llenarDatosFacturacion(sheet, pedidos, rowNum);
                break;

            case "por_odontologo":
                crearEncabezadosPorOdontologo(headerRow, headerStyle);
                rowNum = llenarDatosPorOdontologo(sheet, pedidos, rowNum);
                break;

            case "productividad":
                crearEncabezadosProductividad(headerRow, headerStyle);
                rowNum = llenarDatosProductividad(sheet, rowNum);
                break;

            default:
                crearEncabezadosEstadoActual(headerRow, headerStyle);
                rowNum = llenarDatosEstadoActual(sheet, pedidos, rowNum, dateStyle, urgentStyle);
        }

        // Ajustar ancho de columnas
        for (int i = 0; i < headerRow.getLastCellNum(); i++) {
            sheet.autoSizeColumn(i);
        }

        // Agregar totales
        agregarTotales(sheet, rowNum, tipo, pedidos);

        // Configurar respuesta HTTP
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition",
                "attachment; filename=\"reporte_" + tipo + "_"
                + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".xlsx\"");

        // Escribir y cerrar
        OutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
    }

    private void crearEncabezadosEstadoActual(Row headerRow, CellStyle style) {
        String[] headers = {"Código", "Paciente", "Piezas", "Tipo Prótesis",
            "Material", "Estado", "Prioridad", "Fecha Compromiso",
            "Odontólogo", "Responsable", "Observaciones"};

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(style);
        }
    }

    private int llenarDatosEstadoActual(Sheet sheet, List<Pedido> pedidos,
            int rowNum, CellStyle dateStyle, CellStyle urgentStyle) {

        for (Pedido pedido : pedidos) {
            Row row = sheet.createRow(rowNum++);

            row.createCell(0).setCellValue(pedido.getCodigoUnico());
            row.createCell(1).setCellValue(pedido.getNombrePaciente());
            row.createCell(2).setCellValue(pedido.getPiezasDentales());
            row.createCell(3).setCellValue(pedido.getTipoProtesis());
            row.createCell(4).setCellValue(pedido.getMaterial());
            row.createCell(5).setCellValue(pedido.getEstadoActual());

            Cell prioridadCell = row.createCell(6);
            prioridadCell.setCellValue(pedido.getPrioridad());
            if ("URGENTE".equals(pedido.getPrioridad())
                    || "EMERGENCIA".equals(pedido.getPrioridad())) {
                prioridadCell.setCellStyle(urgentStyle);
            }

            Cell dateCell = row.createCell(7);
            dateCell.setCellValue(pedido.getFechaCompromiso());
            dateCell.setCellStyle(dateStyle);

            row.createCell(8).setCellValue(pedido.getNombreOdontologo() != null
                    ? pedido.getNombreOdontologo() : "N/A");
            row.createCell(9).setCellValue(pedido.getNombreResponsable() != null
                    ? pedido.getNombreResponsable() : "Sin asignar");
            row.createCell(10).setCellValue(pedido.getObservaciones() != null
                    ? pedido.getObservaciones() : "");
        }

        return rowNum;
    }

    private void crearEncabezadosTimeline(Row headerRow, CellStyle style) {
        String[] headers = {"Código", "Paciente", "Recepción", "Paralelizado",
            "Diseño CAD", "Producción CAM", "Cerámica", "Control Calidad",
            "Listo Entrega", "Tiempo Total (días)"};

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(style);
        }
    }

    private int llenarDatosTimeline(Sheet sheet, List<Pedido> pedidos,
            int rowNum, CellStyle dateStyle) {

        for (Pedido pedido : pedidos) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(pedido.getCodigoUnico());
            row.createCell(1).setCellValue(pedido.getNombrePaciente());

            // Aquí deberías obtener las transiciones del pedido
            List<TransicionEstado> transiciones
                    = transicionDAO.obtenerPorPedido(pedido.getId());

            // Llenar fechas de cada estado
            int col = 2;
            for (TransicionEstado t : transiciones) {
                Cell cell = row.createCell(col++);
                cell.setCellValue(new SimpleDateFormat("dd/MM/yyyy HH:mm")
                        .format(t.getFechaTransicion()));
            }

            // Calcular tiempo total
            if (!transiciones.isEmpty()) {
                long dias = calcularDiasTranscurridos(
                        transiciones.get(0).getFechaTransicion(), new Date());
                row.createCell(col).setCellValue(dias);
            }
        }

        return rowNum;
    }

    private void crearEncabezadosFacturacion(Row headerRow, CellStyle style) {
        String[] headers = {"Período", "Código", "Paciente", "Tipo Prótesis",
            "Material", "Odontólogo", "Monto", "Estado Pago", "Fecha"};

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(style);
        }
    }

    private int llenarDatosFacturacion(Sheet sheet, List<Pedido> pedidos, int rowNum) {
        double totalFacturado = 0.0;

        for (Pedido pedido : pedidos) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(new SimpleDateFormat("MM/yyyy")
                    .format(pedido.getFechaIngreso()));
            row.createCell(1).setCellValue(pedido.getCodigoUnico());
            row.createCell(2).setCellValue(pedido.getNombrePaciente());
            row.createCell(3).setCellValue(pedido.getTipoProtesis());
            row.createCell(4).setCellValue(pedido.getMaterial());
            row.createCell(5).setCellValue(pedido.getNombreOdontologo());

            // Calcular monto según tipo de prótesis (esto debería venir de BD)
            double monto = calcularMontoPedido(pedido);
            row.createCell(6).setCellValue(monto);
            totalFacturado += monto;

            row.createCell(7).setCellValue("LISTO_ENTREGA".equals(pedido.getEstadoActual())
                    || "ENTREGADO".equals(pedido.getEstadoActual()) ? "PAGADO" : "PENDIENTE");
            row.createCell(8).setCellValue(new SimpleDateFormat("dd/MM/yyyy")
                    .format(pedido.getFechaIngreso()));
        }

        // Fila de total
        Row totalRow = sheet.createRow(rowNum++);
        totalRow.createCell(5).setCellValue("TOTAL FACTURADO:");
        Cell totalCell = totalRow.createCell(6);
        totalCell.setCellValue(totalFacturado);

        CellStyle totalStyle = sheet.getWorkbook().createCellStyle();
        Font boldFont = sheet.getWorkbook().createFont();
        boldFont.setBold(true);
        totalStyle.setFont(boldFont);
        totalRow.getCell(5).setCellStyle(totalStyle);
        totalCell.setCellStyle(totalStyle);

        return rowNum;
    }

    private void crearEncabezadosPorOdontologo(Row headerRow, CellStyle style) {
        String[] headers = {"Odontólogo", "Total Pedidos", "En Proceso",
            "Completados", "Atrasados", "Total Facturado"};

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(style);
        }
    }

    private int llenarDatosPorOdontologo(Sheet sheet, List<Pedido> pedidos, int rowNum) {
        Map<String, List<Pedido>> pedidosPorOdontologo = pedidos.stream()
                .collect(Collectors.groupingBy(Pedido::getNombreOdontologo));

        for (Map.Entry<String, List<Pedido>> entry : pedidosPorOdontologo.entrySet()) {
            Row row = sheet.createRow(rowNum++);
            String odontologo = entry.getKey();
            List<Pedido> pedidosOdon = entry.getValue();

            row.createCell(0).setCellValue(odontologo);
            row.createCell(1).setCellValue(pedidosOdon.size());

            long enProceso = pedidosOdon.stream()
                    .filter(p -> !"LISTO_ENTREGA".equals(p.getEstadoActual())
                    && !"ENTREGADO".equals(p.getEstadoActual()))
                    .count();
            row.createCell(2).setCellValue(enProceso);

            long completados = pedidosOdon.stream()
                    .filter(p -> "LISTO_ENTREGA".equals(p.getEstadoActual())
                    || "ENTREGADO".equals(p.getEstadoActual()))
                    .count();
            row.createCell(3).setCellValue(completados);

            long atrasados = pedidosOdon.stream()
                    .filter(Pedido::isAtrasado)
                    .count();
            row.createCell(4).setCellValue(atrasados);

            double total = pedidosOdon.stream()
                    .mapToDouble(this::calcularMontoPedido)
                    .sum();
            row.createCell(5).setCellValue(total);
        }

        return rowNum;
    }

    private void crearEncabezadosProductividad(Row headerRow, CellStyle style) {
        String[] headers = {"Técnico/Ceramista", "Rol", "Pedidos Asignados",
            "Pedidos Completados", "Tiempo Promedio (horas)", "Eficiencia %"};

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(style);
        }
    }

    private int llenarDatosProductividad(Sheet sheet, int rowNum) {
        try {
            List<Usuario> tecnicos = usuarioDAO.listarPorRol("TECNICO");
            tecnicos.addAll(usuarioDAO.listarPorRol("CERAMISTA"));

            for (Usuario tecnico : tecnicos) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(tecnico.getNombreCompleto());
                row.createCell(1).setCellValue(tecnico.getRol());

                // Obtener estadísticas del técnico
                Map<String, Object> stats = obtenerEstadisticasTecnico(tecnico.getId());

                row.createCell(2).setCellValue((Integer) stats.getOrDefault("asignados", 0));
                row.createCell(3).setCellValue((Integer) stats.getOrDefault("completados", 0));
                row.createCell(4).setCellValue((Double) stats.getOrDefault("tiempoPromedio", 0.0));
                row.createCell(5).setCellValue((Double) stats.getOrDefault("eficiencia", 0.0));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return rowNum;
    }

    private void agregarTotales(Sheet sheet, int rowNum, String tipo, List<Pedido> pedidos) {
        rowNum += 2; // Dejar espacio
        Row totalRow = sheet.createRow(rowNum);

        CellStyle boldStyle = sheet.getWorkbook().createCellStyle();
        Font boldFont = sheet.getWorkbook().createFont();
        boldFont.setBold(true);
        boldStyle.setFont(boldFont);

        Cell labelCell = totalRow.createCell(0);
        labelCell.setCellValue("RESUMEN:");
        labelCell.setCellStyle(boldStyle);

        Row countRow = sheet.createRow(rowNum + 1);
        countRow.createCell(0).setCellValue("Total de registros:");
        countRow.createCell(1).setCellValue(pedidos.size());

        if (tipo.equals("atrasados")) {
            Row atrasadosRow = sheet.createRow(rowNum + 2);
            atrasadosRow.createCell(0).setCellValue("Pedidos atrasados:");
            atrasadosRow.createCell(1).setCellValue(pedidos.stream()
                    .filter(Pedido::isAtrasado).count());
        }
    }

    // ==================== GENERACIÓN PDF ====================
    private void generarPDF(HttpServletResponse response, String tipo,
            List<Pedido> pedidos, Usuario usuario) throws IOException {

        // Configurar la respuesta HTTP para descarga de PDF
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition",
                "attachment; filename=\"reporte_" + tipo + "_"
                + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".pdf\"");

        try {
            // Crear documento PDF en tamaño A4 horizontal
            Document document = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(document, response.getOutputStream());
            document.open();

            // ----------------- TÍTULO -----------------
            com.itextpdf.text.Font titleFont = FontFactory.getFont(
                    FontFactory.HELVETICA_BOLD, 18, BaseColor.DARK_GRAY);
            Paragraph title = new Paragraph("LABDENT JLVEGA", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            // ----------------- SUBTÍTULO -----------------
            com.itextpdf.text.Font subtitleFont = FontFactory.getFont(
                    FontFactory.HELVETICA, 14, BaseColor.BLUE);
            Paragraph subtitle = new Paragraph(obtenerNombreReporte(tipo), subtitleFont);
            subtitle.setAlignment(Element.ALIGN_CENTER);
            subtitle.setSpacingAfter(10);
            document.add(subtitle);

            // ----------------- INFORMACIÓN DEL REPORTE -----------------
            com.itextpdf.text.Font infoFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
            document.add(new Paragraph("Generado por: " + usuario.getNombreCompleto(), infoFont));
            document.add(new Paragraph("Fecha: "
                    + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()), infoFont));
            document.add(new Paragraph(" "));

            // ----------------- TABLA DE DATOS -----------------
            PdfPTable table = crearTablaPDF(tipo, pedidos);
            document.add(table);

            // ----------------- RESUMEN -----------------
            document.add(new Paragraph(" "));
            com.itextpdf.text.Font summaryFont = FontFactory.getFont(
                    FontFactory.HELVETICA_BOLD, 12);
            document.add(new Paragraph("RESUMEN", summaryFont));
            document.add(new Paragraph("Total de registros: " + pedidos.size(), infoFont));

            if ("atrasados".equals(tipo)) {
                long atrasados = pedidos.stream().filter(Pedido::isAtrasado).count();
                document.add(new Paragraph("Pedidos atrasados: " + atrasados, infoFont));
            }

            document.close();

        } catch (DocumentException e) {
            e.printStackTrace();
            throw new IOException("Error al generar PDF", e);
        }
    }

    private PdfPTable crearTablaPDF(String tipo, List<Pedido> pedidos) throws DocumentException {
        PdfPTable table;

        switch (tipo) {
            case "estado_actual":
            case "atrasados":
            case "completados":
                table = new PdfPTable(8);
                table.setWidthPercentage(100);
                table.setWidths(new float[]{2, 3, 2, 2, 2, 2, 2, 3});

                // Encabezados
                agregarCeldaEncabezado(table, "Código");
                agregarCeldaEncabezado(table, "Paciente");
                agregarCeldaEncabezado(table, "Tipo");
                agregarCeldaEncabezado(table, "Material");
                agregarCeldaEncabezado(table, "Estado");
                agregarCeldaEncabezado(table, "Prioridad");
                agregarCeldaEncabezado(table, "F. Compromiso");
                agregarCeldaEncabezado(table, "Odontólogo");

                // Datos
                for (Pedido p : pedidos) {
                    table.addCell(p.getCodigoUnico());
                    table.addCell(p.getNombrePaciente());
                    table.addCell(p.getTipoProtesis());
                    table.addCell(p.getMaterial());
                    table.addCell(p.getEstadoActual());
                    table.addCell(p.getPrioridad());
                    table.addCell(p.getFechaCompromiso() != null
                            ? new SimpleDateFormat("dd/MMM/yyyy").format(p.getFechaCompromiso())
                            : "");
                    table.addCell(p.getNombreOdontologo() != null
                            ? p.getNombreOdontologo() : "N/A");
                }
                break;

            default:
                table = new PdfPTable(6);
                table.setWidthPercentage(100);

                agregarCeldaEncabezado(table, "Código");
                agregarCeldaEncabezado(table, "Paciente");
                agregarCeldaEncabezado(table, "Tipo");
                agregarCeldaEncabezado(table, "Estado");
                agregarCeldaEncabezado(table, "Fecha");
                agregarCeldaEncabezado(table, "Odontólogo");

                for (Pedido p : pedidos) {
                    table.addCell(p.getCodigoUnico());
                    table.addCell(p.getNombrePaciente());
                    table.addCell(p.getTipoProtesis());
                    table.addCell(p.getEstadoActual());
                    table.addCell(sdf.format(p.getFechaCompromiso()));
                    table.addCell(p.getNombreOdontologo() != null
                            ? p.getNombreOdontologo() : "N/A");
                }
        }

        return table;
    }

    private void agregarCeldaEncabezado(PdfPTable table, String texto) {
        com.itextpdf.text.Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.WHITE);
        PdfPCell cell = new PdfPCell(new Phrase(texto, font));
        cell.setBackgroundColor(BaseColor.DARK_GRAY);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(5);
        table.addCell(cell);
    }

    // ==================== GENERACIÓN CSV ====================
    private void generarCSV(HttpServletResponse response, String tipo,
            List<Pedido> pedidos) throws IOException {

        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader("Content-Disposition",
                "attachment; filename=\"reporte_" + tipo + "_"
                + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".csv\"");

        PrintWriter writer = response.getWriter();

        // BOM para UTF-8 (para Excel)
        writer.write('\ufeff');

        // Encabezados
        writer.println("Código,Paciente,Piezas Dentales,Tipo Prótesis,Material,Color,"
                + "Estado,Prioridad,Fecha Compromiso,Fecha Ingreso,Odontólogo,Responsable,Observaciones");

        // Datos
        for (Pedido pedido : pedidos) {
            writer.println(String.format("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"",
                    escaparCSV(pedido.getCodigoUnico()),
                    escaparCSV(pedido.getNombrePaciente()),
                    escaparCSV(pedido.getPiezasDentales()),
                    escaparCSV(pedido.getTipoProtesis()),
                    escaparCSV(pedido.getMaterial()),
                    escaparCSV(pedido.getColorShade()),
                    escaparCSV(pedido.getEstadoActual()),
                    escaparCSV(pedido.getPrioridad()),
                    escaparCSV(sdf.format(pedido.getFechaCompromiso())),
                    escaparCSV(new SimpleDateFormat("dd/MM/yyyy").format(pedido.getFechaIngreso())),
                    escaparCSV(pedido.getNombreOdontologo() != null ? pedido.getNombreOdontologo() : "N/A"),
                    escaparCSV(pedido.getNombreResponsable() != null ? pedido.getNombreResponsable() : "Sin asignar"),
                    escaparCSV(pedido.getObservaciones() != null ? pedido.getObservaciones() : "")
            ));
        }

        writer.flush();
        writer.close();
    }

    private String escaparCSV(String valor) {
        if (valor == null) {
            return "";
        }
        return valor.replace("\"", "\"\"");
    }

    // ==================== GENERACIÓN JSON ====================
    private void generarJSON(HttpServletResponse response, String tipo,
            List<Pedido> pedidos) throws IOException {

        response.setContentType("application/json; charset=UTF-8");
        response.setHeader("Content-Disposition",
                "attachment; filename=\"reporte_" + tipo + "_"
                + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".json\"");

        PrintWriter writer = response.getWriter();

        // Construir JSON manualmente (o usar una librería como Gson/Jackson)
        StringBuilder json = new StringBuilder();
        json.append("{\n");
        json.append("  \"reporte\": \"").append(obtenerNombreReporte(tipo)).append("\",\n");
        json.append("  \"fechaGeneracion\": \"").append(
                new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(new Date())).append("\",\n");
        json.append("  \"totalRegistros\": ").append(pedidos.size()).append(",\n");
        json.append("  \"datos\": [\n");

        for (int i = 0; i < pedidos.size(); i++) {
            Pedido p = pedidos.get(i);
            json.append("    {\n");
            json.append("      \"codigo\": \"").append(escaparJSON(p.getCodigoUnico())).append("\",\n");
            json.append("      \"paciente\": \"").append(escaparJSON(p.getNombrePaciente())).append("\",\n");
            json.append("      \"piezasDentales\": \"").append(escaparJSON(p.getPiezasDentales())).append("\",\n");
            json.append("      \"tipoProtesis\": \"").append(escaparJSON(p.getTipoProtesis())).append("\",\n");
            json.append("      \"material\": \"").append(escaparJSON(p.getMaterial())).append("\",\n");
            json.append("      \"colorShade\": \"").append(escaparJSON(p.getColorShade())).append("\",\n");
            json.append("      \"estado\": \"").append(escaparJSON(p.getEstadoActual())).append("\",\n");
            json.append("      \"prioridad\": \"").append(escaparJSON(p.getPrioridad())).append("\",\n");
            json.append("      \"fechaCompromiso\": \"").append(escaparJSON(sdf.format(p.getFechaCompromiso()))).append("\",\n");
            json.append("      \"fechaIngreso\": \"").append(
                    new SimpleDateFormat("yyyy-MM-dd").format(p.getFechaIngreso())).append("\",\n");
            json.append("      \"odontologo\": \"").append(
                    escaparJSON(p.getNombreOdontologo() != null ? p.getNombreOdontologo() : "N/A")).append("\",\n");
            json.append("      \"responsable\": \"").append(
                    escaparJSON(p.getNombreResponsable() != null ? p.getNombreResponsable() : "Sin asignar")).append("\",\n");
            json.append("      \"atrasado\": ").append(p.isAtrasado()).append(",\n");
            json.append("      \"observaciones\": \"").append(
                    escaparJSON(p.getObservaciones() != null ? p.getObservaciones() : "")).append("\"\n");
            json.append("    }");

            if (i < pedidos.size() - 1) {
                json.append(",");
            }
            json.append("\n");
        }

        json.append("  ]\n");
        json.append("}\n");

        writer.write(json.toString());
        writer.flush();
        writer.close();
    }

    private String escaparJSON(String valor) {
        if (valor == null) {
            return "";
        }
        return valor.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    // ==================== MÉTODOS AUXILIARES ====================
    private String obtenerNombreReporte(String tipo) {
        switch (tipo) {
            case "estado_actual":
                return "Estado Actual de Producción";
            case "timeline":
                return "Timeline de Pedidos";
            case "atrasados":
                return "Pedidos Atrasados";
            case "completados":
                return "Pedidos Completados";
            case "facturacion":
                return "Reporte de Facturación";
            case "por_odontologo":
                return "Análisis por Odontólogo";
            case "costos":
                return "Análisis de Costos";
            case "proyeccion":
                return "Proyección de Ingresos";
            case "historial_cliente":
                return "Historial de Cliente";
            case "protesis_populares":
                return "Prótesis Más Populares";
            case "satisfaccion":
                return "Satisfacción del Cliente";
            case "nuevos_clientes":
                return "Nuevos Clientes";
            case "productividad":
                return "Productividad del Personal";
            case "tiempos":
                return "Análisis de Tiempos de Entrega";
            case "calidad":
                return "Control de Calidad";
            case "eficiencia":
                return "Eficiencia y KPIs";
            case "certificado":
                return "Certificado de Garantía";
            case "orden_trabajo":
                return "Orden de Trabajo";
            case "etiquetas":
                return "Etiquetas de Envío";
            case "factura":
                return "Factura/Boleta";
            default:
                return "Reporte General";
        }
    }

    private double calcularMontoPedido(Pedido pedido) {
        // Tabla de precios base (esto debería estar en BD)
        Map<String, Double> precios = new HashMap<>();
        precios.put("Corona", 350.0);
        precios.put("Puente", 800.0);
        precios.put("Carilla", 250.0);
        precios.put("Prótesis Removible", 600.0);
        precios.put("Prótesis Total", 1200.0);
        precios.put("Incrustación", 200.0);

        // Multiplicadores por material
        Map<String, Double> multiplicadores = new HashMap<>();
        multiplicadores.put("Zirconia", 1.5);
        multiplicadores.put("Disilicato de Litio", 1.3);
        multiplicadores.put("Metal-Porcelana", 1.0);
        multiplicadores.put("PMMA", 0.8);
        multiplicadores.put("Acrílico", 0.7);

        double precioBase = precios.getOrDefault(pedido.getTipoProtesis(), 300.0);
        double multiplicador = multiplicadores.getOrDefault(pedido.getMaterial(), 1.0);

        // Número de piezas
        int numPiezas = (pedido.getPiezasDentales() != null && !pedido.getPiezasDentales().isEmpty())
                ? pedido.getPiezasDentales().split(",").length
                : 1;

        return precioBase * multiplicador * numPiezas;
    }

    private long calcularDiasTranscurridos(Date inicio, Date fin) {
        long diff = fin.getTime() - inicio.getTime();
        return diff / (1000 * 60 * 60 * 24);
    }

    private Map<String, Object> obtenerEstadisticasTecnico(int tecnicoId) {
        Map<String, Object> stats = new HashMap<>();

        try {
            // Obtener pedidos asignados
            List<Pedido> asignados = pedidoDAO.listarTodos().stream()
                    .filter(p -> p.getResponsableActual() != null
                    && p.getResponsableActual() == tecnicoId)
                    .collect(Collectors.toList());

            stats.put("asignados", asignados.size());

            // Obtener completados
            long completados = asignados.stream()
                    .filter(p -> "LISTO_ENTREGA".equals(p.getEstadoActual())
                    || "ENTREGADO".equals(p.getEstadoActual()))
                    .count();

            stats.put("completados", (int) completados);

            // Obtener transiciones del técnico
            List<TransicionEstado> transiciones = transicionDAO.obtenerPorUsuario(tecnicoId);

            // Calcular tiempo promedio
            if (!transiciones.isEmpty()) {
                double promedioMinutos = transiciones.stream()
                        .filter(t -> t.getTiempoEnEstado() != null)
                        .mapToInt(TransicionEstado::getTiempoEnEstado)
                        .average()
                        .orElse(0.0);

                stats.put("tiempoPromedio", promedioMinutos / 60.0); // Convertir a horas
            } else {
                stats.put("tiempoPromedio", 0.0);
            }

            // Calcular eficiencia
            if (asignados.size() > 0) {
                double eficiencia = (completados * 100.0) / asignados.size();
                stats.put("eficiencia", eficiencia);
            } else {
                stats.put("eficiencia", 0.0);
            }

        } catch (Exception e) {
            e.printStackTrace();
            stats.put("asignados", 0);
            stats.put("completados", 0);
            stats.put("tiempoPromedio", 0.0);
            stats.put("eficiencia", 0.0);
        }

        return stats;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
