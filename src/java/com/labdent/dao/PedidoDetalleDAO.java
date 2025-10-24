package com.labdent.dao;

import com.labdent.model.PedidoHistorial;
import com.labdent.model.PedidoArchivo;
import com.labdent.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PedidoDetalleDAO {

    public List<PedidoHistorial> obtenerHistorial(int idPedido) {
        List<PedidoHistorial> lista = new ArrayList<>();
        String sql = "SELECT * FROM pedido_historial WHERE id_pedido = ? ORDER BY fecha_hora ASC";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPedido);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                PedidoHistorial ph = new PedidoHistorial();
                ph.setId(rs.getInt("id"));
                ph.setPedidoId(rs.getInt("id_pedido"));// ✅ Nombre correcto 
                ph.setEstado(rs.getString("etapa")); // ✅ Coincide con tu modelo 
                ph.setFechaHora(rs.getTimestamp("fecha_hora")); // ✅ usa setFecha() 
                ph.setComentario(rs.getString("comentario"));
                lista.add(ph);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public List<PedidoArchivo> obtenerArchivos(int idPedido) {
        List<PedidoArchivo> lista = new ArrayList<>();
        String sql = "SELECT * FROM pedido_archivos WHERE id_pedido = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPedido);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                PedidoArchivo pa = new PedidoArchivo();
                pa.setId(rs.getInt("id"));
                pa.setPedidoId(rs.getInt("id_pedido")); // ✅ Nombre correcto 
                pa.setNombreArchivo(rs.getString("nombre_archivo"));
                pa.setRutaArchivo(rs.getString("ruta")); // ✅ usa setRutaArchivo() 
                pa.setTipo(rs.getString("tipo"));
                pa.setFechaSubida(rs.getTimestamp("fecha_subida"));
                lista.add(pa);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
}
