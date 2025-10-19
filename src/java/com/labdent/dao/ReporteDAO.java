package com.labdent.dao;

import com.labdent.model.Reporte;
import com.labdent.util.DatabaseConnection;
import java.sql.*;

public class ReporteDAO {

    public Reporte generarReporteGeneral() {
        Reporte reporte = new Reporte();
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Total de pedidos
            reporte.setTotalPedidos(contarPedidos(conn, null));
            
            // Pedidos en proceso (no entregados)
            reporte.setPedidosEnProceso(contarPedidosEnProceso(conn));
            
            // Pedidos entregados
            reporte.setPedidosEntregados(contarPedidos(conn, "ENTREGADO"));
            
            // Pedidos atrasados
            reporte.setPedidosAtrasados(contarPedidosAtrasados(conn));
            
            // Por estado
            reporte.setPedidosRecepcion(contarPedidos(conn, "RECEPCION"));
            reporte.setPedidosParalelizado(contarPedidos(conn, "PARALELIZADO"));
            reporte.setPedidosDisenoCad(contarPedidos(conn, "DISENO_CAD"));
            reporte.setPedidosProduccionCam(contarPedidos(conn, "PRODUCCION_CAM"));
            reporte.setPedidosCeramica(contarPedidos(conn, "CERAMICA"));
            reporte.setPedidosControlCalidad(contarPedidos(conn, "CONTROL_CALIDAD"));
            reporte.setPedidosListoEntrega(contarPedidos(conn, "LISTO_ENTREGA"));
            
            // Tiempo promedio y tasa de cumplimiento
            calcularMetricas(conn, reporte);
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return reporte;
    }

    private int contarPedidos(Connection conn, String estado) throws SQLException {
        String sql = estado == null ? 
            "SELECT COUNT(*) as total FROM pedidos" :
            "SELECT COUNT(*) as total FROM pedidos WHERE estado_actual = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (estado != null) {
                stmt.setString(1, estado);
            }
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            }
        }
        return 0;
    }

    private int contarPedidosEnProceso(Connection conn) throws SQLException {
        String sql = "SELECT COUNT(*) as total FROM pedidos WHERE estado_actual != 'ENTREGADO'";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("total");
            }
        }
        return 0;
    }

    private int contarPedidosAtrasados(Connection conn) throws SQLException {
        String sql = "SELECT COUNT(*) as total FROM pedidos " +
                     "WHERE fecha_compromiso < CURDATE() AND estado_actual != 'ENTREGADO'";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("total");
            }
        }
        return 0;
    }

    private void calcularMetricas(Connection conn, Reporte reporte) throws SQLException {
        // Tiempo promedio de entrega (en días)
        String sqlTiempo = "SELECT AVG(DATEDIFF(fecha_entrega, fecha_ingreso)) as promedio " +
                          "FROM pedidos WHERE fecha_entrega IS NOT NULL";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sqlTiempo)) {
            if (rs.next()) {
                reporte.setTiempoPromedioEntrega(rs.getDouble("promedio"));
            }
        }
        
        // Tasa de cumplimiento (pedidos entregados a tiempo / total entregados * 100)
        String sqlCumplimiento = "SELECT " +
                                "(SELECT COUNT(*) FROM pedidos WHERE fecha_entrega <= fecha_compromiso AND estado_actual = 'ENTREGADO') as a_tiempo, " +
                                "(SELECT COUNT(*) FROM pedidos WHERE estado_actual = 'ENTREGADO') as total";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sqlCumplimiento)) {
            if (rs.next()) {
                int aTiempo = rs.getInt("a_tiempo");
                int total = rs.getInt("total");
                if (total > 0) {
                    reporte.setTasaCumplimiento((aTiempo * 100.0) / total);
                }
            }
        }
    }
}