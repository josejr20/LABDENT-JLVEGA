package com.labdent.dao;

import com.labdent.model.TransicionEstado;
import com.labdent.util.DatabaseConnection;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransicionDAO {

    public boolean registrar(TransicionEstado transicion) {
        String sql = "INSERT INTO transiciones_estado (pedido_id, estado_anterior, estado_nuevo, " +
                     "usuario_id, observaciones, checklist_completado, tiempo_en_estado) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, transicion.getPedidoId());
            stmt.setString(2, StringUtils.defaultIfBlank(transicion.getEstadoAnterior(), "SIN_ESTADO"));
            stmt.setString(3, StringUtils.defaultIfBlank(transicion.getEstadoNuevo(), "SIN_ESTADO"));
            stmt.setInt(4, transicion.getUsuarioId());
            stmt.setString(5, StringUtils.defaultIfBlank(transicion.getObservaciones(), "N/A"));
            stmt.setBoolean(6, transicion.isChecklistCompletado());

            Integer tiempo = ObjectUtils.defaultIfNull(transicion.getTiempoEnEstado(), 0);
            stmt.setInt(7, tiempo);

            int filas = stmt.executeUpdate();
            if (filas > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) transicion.setId(rs.getInt(1));
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error al registrar transición: " + ExceptionUtils.getRootCauseMessage(e));
        }
        return false;
    }

    public List<TransicionEstado> listarPorPedido(int pedidoId) {
        List<TransicionEstado> transiciones = new ArrayList<>();
        String sql = "SELECT t.*, u.nombre_completo AS nombre_usuario, p.codigo_unico AS codigo_pedido " +
                     "FROM transiciones_estado t " +
                     "INNER JOIN usuarios u ON t.usuario_id = u.id " +
                     "INNER JOIN pedidos p ON t.pedido_id = p.id " +
                     "WHERE t.pedido_id = ? ORDER BY t.fecha_transicion ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, pedidoId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                transiciones.add(mapearTransicion(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar transiciones: " + ExceptionUtils.getRootCauseMessage(e));
        }
        return transiciones;
    }

    private TransicionEstado mapearTransicion(ResultSet rs) throws SQLException {
        TransicionEstado t = new TransicionEstado();
        t.setId(rs.getInt("id"));
        t.setPedidoId(rs.getInt("pedido_id"));
        t.setEstadoAnterior(StringUtils.defaultIfBlank(rs.getString("estado_anterior"), "N/A"));
        t.setEstadoNuevo(StringUtils.defaultIfBlank(rs.getString("estado_nuevo"), "N/A"));
        t.setUsuarioId(rs.getInt("usuario_id"));
        t.setObservaciones(StringUtils.defaultIfBlank(rs.getString("observaciones"), ""));
        t.setChecklistCompletado(rs.getBoolean("checklist_completado"));
        t.setTiempoEnEstado(ObjectUtils.defaultIfNull((Integer) rs.getObject("tiempo_en_estado"), 0));
        t.setFechaTransicion(rs.getTimestamp("fecha_transicion"));
        t.setNombreUsuario(StringUtils.defaultIfBlank(rs.getString("nombre_usuario"), "Sin usuario"));
        t.setCodigoPedido(StringUtils.defaultIfBlank(rs.getString("codigo_pedido"), "Sin código"));
        return t;
    }
    // ✅ Método auxiliar para calcular el tiempo que un pedido estuvo en un estado anterior

    public int calcularTiempoEnEstado(int pedidoId, String estadoAnterior) {
        String sql = "SELECT TIMESTAMPDIFF(HOUR, fecha_inicio, IFNULL(fecha_fin, NOW())) AS horas "
                + "FROM transicion_estado "
                + "WHERE pedido_id = ? AND estado_anterior = ? "
                + "ORDER BY fecha_inicio DESC LIMIT 1";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, pedidoId);
            ps.setString(2, estadoAnterior);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("horas");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0; // Si no se encuentra registro, devolver 0
    }
}
