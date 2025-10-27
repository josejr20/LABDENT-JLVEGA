package com.labdent.dao;

import com.labdent.model.Evidencia;
import com.labdent.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EvidenciaDAO {

    public List<Evidencia> listarPorPedido(int pedidoId) {
        List<Evidencia> evidencias = new ArrayList<>();
        String sql = "SELECT e.*, u.nombre_completo as nombre_usuario " +
                     "FROM evidencias e " +
                     "INNER JOIN usuarios u ON e.usuario_id = u.id " +
                     "WHERE e.pedido_id = ? " +
                     "ORDER BY e.fecha_subida DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, pedidoId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Evidencia evidencia = new Evidencia();
                evidencia.setId(rs.getInt("id"));
                evidencia.setPedidoId(rs.getInt("pedido_id"));
                evidencia.setTipoEvidencia(rs.getString("tipo_evidencia"));
                evidencia.setNombreArchivo(rs.getString("nombre_archivo"));
                evidencia.setRutaArchivo(rs.getString("ruta_archivo"));
                evidencia.setDescripcion(rs.getString("descripcion"));
                evidencia.setUsuarioId(rs.getInt("usuario_id"));
                evidencia.setFechaSubida(rs.getTimestamp("fecha_subida"));
                evidencia.setNombreUsuario(rs.getString("nombre_usuario"));
                
                evidencias.add(evidencia);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener evidencias: " + e.getMessage());
            e.printStackTrace();
        }

        return evidencias;
    }

    public boolean guardar(Evidencia evidencia) {
        String sql = "INSERT INTO evidencias " +
                     "(pedido_id, tipo_evidencia, nombre_archivo, ruta_archivo, " +
                     "descripcion, usuario_id) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, evidencia.getPedidoId());
            stmt.setString(2, evidencia.getTipoEvidencia());
            stmt.setString(3, evidencia.getNombreArchivo());
            stmt.setString(4, evidencia.getRutaArchivo());
            stmt.setString(5, evidencia.getDescripcion());
            stmt.setInt(6, evidencia.getUsuarioId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al guardar evidencia: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}