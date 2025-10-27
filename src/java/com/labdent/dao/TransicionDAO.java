package com.labdent.dao;

import com.labdent.model.TransicionEstado;
import com.labdent.util.DatabaseConnection;
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
            stmt.setString(2, transicion.getEstadoAnterior());
            stmt.setString(3, transicion.getEstadoNuevo());
            stmt.setInt(4, transicion.getUsuarioId());
            stmt.setString(5, transicion.getObservaciones());
            stmt.setBoolean(6, transicion.isChecklistCompletado());
            
            if (transicion.getTiempoEnEstado() != null) {
                stmt.setInt(7, transicion.getTiempoEnEstado());
            } else {
                stmt.setNull(7, Types.INTEGER);
            }
            
            int filasAfectadas = stmt.executeUpdate();
            
            if (filasAfectadas > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    transicion.setId(generatedKeys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<TransicionEstado> listarPorPedido(int pedidoId) {
        List<TransicionEstado> transiciones = new ArrayList<>();
        String sql = "SELECT t.*, u.nombre_completo as nombre_usuario, p.codigo_unico as codigo_pedido " +
                     "FROM transiciones_estado t " +
                     "INNER JOIN usuarios u ON t.usuario_id = u.id " +
                     "INNER JOIN pedidos p ON t.pedido_id = p.id " +
                     "WHERE t.pedido_id = ? " +
                     "ORDER BY t.fecha_transicion ASC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, pedidoId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                transiciones.add(mapearTransicion(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transiciones;
    }

    public int calcularTiempoEnEstado(int pedidoId, String estadoActual) {
        String sql = "SELECT TIMESTAMPDIFF(MINUTE, MAX(fecha_transicion), NOW()) as minutos " +
                     "FROM transiciones_estado " +
                     "WHERE pedido_id = ? AND estado_nuevo = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, pedidoId);
            stmt.setString(2, estadoActual);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("minutos");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private TransicionEstado mapearTransicion(ResultSet rs) throws SQLException {
        TransicionEstado transicion = new TransicionEstado();
        transicion.setId(rs.getInt("id"));
        transicion.setPedidoId(rs.getInt("pedido_id"));
        transicion.setEstadoAnterior(rs.getString("estado_anterior"));
        transicion.setEstadoNuevo(rs.getString("estado_nuevo"));
        transicion.setUsuarioId(rs.getInt("usuario_id"));
        transicion.setObservaciones(rs.getString("observaciones"));
        transicion.setChecklistCompletado(rs.getBoolean("checklist_completado"));
        
        Integer tiempoEnEstado = (Integer) rs.getObject("tiempo_en_estado");
        transicion.setTiempoEnEstado(tiempoEnEstado);
        
        transicion.setFechaTransicion(rs.getTimestamp("fecha_transicion"));
        transicion.setNombreUsuario(rs.getString("nombre_usuario"));
        transicion.setCodigoPedido(rs.getString("codigo_pedido"));
        
        return transicion;
    }
}