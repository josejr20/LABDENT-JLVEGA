<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Registrar Pedido - LABDENT JLVEGA</title>
</head>
<body>
<h2>Registrar Pedido Dental</h2>

<form action="RegistrarPedidoServlet" method="post" enctype="multipart/form-data">
    <label>Código Único:</label><br>
    <input type="text" name="codigo_unico" required><br><br>

    <label>ID del Odontólogo:</label><br>
    <input type="number" name="odontologo_id" required><br><br>

    <label>Nombre del Paciente:</label><br>
    <input type="text" name="nombre_paciente" required><br><br>

    <label>Piezas Dentales (ej: 11,12,13):</label><br>
    <input type="text" name="piezas_dentales" required><br><br>

    <label>Tipo de Prótesis:</label><br>
    <input type="text" name="tipo_protesis" required><br><br>

    <label>Material:</label><br>
    <select name="material" required>
        <option value="Zirconia">Zirconia</option>
        <option value="Metal-Porcelana">Metal-Porcelana</option>
        <option value="Disilicato de Litio">Disilicato de Litio</option>
        <option value="Cerámica">Cerámica</option>
    </select><br><br>

    <label>Color/Shade:</label><br>
    <input type="text" name="color_shade"><br><br>

    <label>Fecha Compromiso:</label><br>
    <input type="date" name="fecha_compromiso" required><br><br>

    <label>Prioridad:</label><br>
    <select name="prioridad">
        <option value="NORMAL">Normal</option>
        <option value="URGENTE">Urgente</option>
        <option value="EMERGENCIA">Emergencia</option>
    </select><br><br>

    <label>Observaciones:</label><br>
    <textarea name="observaciones" rows="4" cols="50"></textarea><br><br>

    <label>Archivo Adjunto (opcional):</label><br>
    <input type="file" name="archivo_adjunto"><br><br>

    <label>ID Responsable Actual:</label><br>
    <input type="number" name="responsable_actual" required><br><br>

    <input type="submit" value="Registrar Pedido">
</form>
</body>
</html>
