package com.labdent.dao;

import com.labdent.model.TransicionEstado;
import com.labdent.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransicionEstadoDAO {

    public List<TransicionEstado> obtenerHistorialPorPedido(int pedidoId) {
        List<TransicionEstado> historial = new ArrayList<>();
        String sql = "SELECT t.*, u.nombre_completo as nombre_usuario " +
                     "FROM transiciones_estado t " +
                     "INNER JOIN usuarios u ON t.usuario_id = u.id " +
                     "WHERE t.pedido_id = ? " +
                     "ORDER BY t.fecha_transicion ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, pedidoId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                TransicionEstado transicion = new TransicionEstado();
                transicion.setId(rs.getInt("id"));
                transicion.setPedidoId(rs.getInt("pedido_id"));
                transicion.setEstadoAnterior(rs.getString("estado_anterior"));
                transicion.setEstadoNuevo(rs.getString("estado_nuevo"));
                transicion.setUsuarioId(rs.getInt("usuario_id"));
                transicion.setObservaciones(rs.getString("observaciones"));
                transicion.setChecklistCompletado(rs.getBoolean("checklist_completado"));
                transicion.setTiempoEnEstado(rs.getInt("tiempo_en_estado"));
                transicion.setFechaTransicion(rs.getTimestamp("fecha_transicion"));
                transicion.setNombreUsuario(rs.getString("nombre_usuario"));
                
                historial.add(transicion);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener historial: " + e.getMessage());
            e.printStackTrace();
        }

        return historial;
    }

    public boolean registrarTransicion(TransicionEstado transicion) {
        String sql = "INSERT INTO transiciones_estado " +
                     "(pedido_id, estado_anterior, estado_nuevo, usuario_id, " +
                     "observaciones, checklist_completado, tiempo_en_estado) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, transicion.getPedidoId());
            stmt.setString(2, transicion.getEstadoAnterior());
            stmt.setString(3, transicion.getEstadoNuevo());
            stmt.setInt(4, transicion.getUsuarioId());
            stmt.setString(5, transicion.getObservaciones());
            stmt.setBoolean(6, transicion.isChecklistCompletado());
            stmt.setObject(7, transicion.getTiempoEnEstado(), Types.INTEGER);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al registrar transición: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}