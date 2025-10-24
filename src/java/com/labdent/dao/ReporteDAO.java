package com.labdent.dao;

import com.labdent.model.Reporte;
import com.labdent.util.DatabaseConnection;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * DAO encargado de generar reportes estadísticos del sistema de pedidos.
 * Mejora aplicada con Apache Commons Lang3 para:
 * - Evitar nulos con ObjectUtils
 * - Manejar excepciones de forma más limpia con ExceptionUtils
 */
public class ReporteDAO {

    public Reporte generarReporteGeneral() {
        Reporte reporte = new Reporte();

        try (Connection conn = DatabaseConnection.getConnection()) {

            // 🔹 Contadores generales
            reporte.setTotalPedidos(contarPedidos(conn, null));
            reporte.setPedidosEnProceso(contarPedidosEnProceso(conn));
            reporte.setPedidosEntregados(contarPedidos(conn, "ENTREGADO"));
            reporte.setPedidosAtrasados(contarPedidosAtrasados(conn));

            // 🔹 Pedidos por estado
            reporte.setPedidosRecepcion(contarPedidos(conn, "RECEPCION"));
            reporte.setPedidosParalelizado(contarPedidos(conn, "PARALELIZADO"));
            reporte.setPedidosDisenoCad(contarPedidos(conn, "DISENO_CAD"));
            reporte.setPedidosProduccionCam(contarPedidos(conn, "PRODUCCION_CAM"));
            reporte.setPedidosCeramica(contarPedidos(conn, "CERAMICA"));
            reporte.setPedidosControlCalidad(contarPedidos(conn, "CONTROL_CALIDAD"));
            reporte.setPedidosListoEntrega(contarPedidos(conn, "LISTO_ENTREGA"));

            // 🔹 Métricas adicionales
            calcularMetricas(conn, reporte);

        } catch (SQLException e) {
            System.err.println("❌ Error al generar reporte general: " +
                    ExceptionUtils.getRootCauseMessage(e));
        }

        return reporte;
    }

    // -------------------------------------------------------------------------
    // MÉTODOS PRIVADOS AUXILIARES
    // -------------------------------------------------------------------------

    private int contarPedidos(Connection conn, String estado) throws SQLException {
        String sql = (estado == null)
                ? "SELECT COUNT(*) AS total FROM pedidos"
                : "SELECT COUNT(*) AS total FROM pedidos WHERE estado_actual = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (estado != null) {
                stmt.setString(1, estado);
            }
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                // 🔸 Usa ObjectUtils para evitar NullPointerException
                return ObjectUtils.defaultIfNull(rs.getInt("total"), 0);
            }
        }
        return 0;
    }

    private int contarPedidosEnProceso(Connection conn) throws SQLException {
        String sql = "SELECT COUNT(*) AS total FROM pedidos WHERE estado_actual != 'ENTREGADO'";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            return rs.next() ? ObjectUtils.defaultIfNull(rs.getInt("total"), 0) : 0;
        }
    }

    private int contarPedidosAtrasados(Connection conn) throws SQLException {
        String sql = "SELECT COUNT(*) AS total FROM pedidos " +
                     "WHERE fecha_compromiso < CURDATE() AND estado_actual != 'ENTREGADO'";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            return rs.next() ? ObjectUtils.defaultIfNull(rs.getInt("total"), 0) : 0;
        }
    }

    private void calcularMetricas(Connection conn, Reporte reporte) throws SQLException {
        // 🔹 Tiempo promedio de entrega (en días)
        String sqlTiempo = "SELECT AVG(DATEDIFF(fecha_entrega, fecha_ingreso)) AS promedio " +
                           "FROM pedidos WHERE fecha_entrega IS NOT NULL";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sqlTiempo)) {

            if (rs.next()) {
                reporte.setTiempoPromedioEntrega(ObjectUtils.defaultIfNull(rs.getDouble("promedio"), 0.0));
            }
        }

        // 🔹 Tasa de cumplimiento (% de pedidos entregados a tiempo)
        String sqlCumplimiento = "SELECT " +
                "(SELECT COUNT(*) FROM pedidos WHERE fecha_entrega <= fecha_compromiso AND estado_actual = 'ENTREGADO') AS a_tiempo, " +
                "(SELECT COUNT(*) FROM pedidos WHERE estado_actual = 'ENTREGADO') AS total";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sqlCumplimiento)) {

            if (rs.next()) {
                int aTiempo = ObjectUtils.defaultIfNull(rs.getInt("a_tiempo"), 0);
                int total = ObjectUtils.defaultIfNull(rs.getInt("total"), 0);

                if (total > 0) {
                    double tasa = (aTiempo * 100.0) / total;
                    reporte.setTasaCumplimiento(tasa);
                } else {
                    reporte.setTasaCumplimiento(0.0);
                }
            }
        }
    }
}
