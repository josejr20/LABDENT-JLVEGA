package com.labdent.dao;

import com.labdent.model.PedidoHistorial;
import com.labdent.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PedidoHistorialDAO {

    public List<PedidoHistorial> listarHistorialPorPedido(int pedidoId) {
        List<PedidoHistorial> lista = new ArrayList<>();
        String sql = "SELECT * FROM pedido_historial WHERE pedido_id = ? ORDER BY fecha DESC";

            try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {


            ps.setInt(1, pedidoId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                PedidoHistorial h = new PedidoHistorial();
                h.setId(rs.getInt("id"));
                h.setPedidoId(rs.getInt("pedido_id"));
                h.setEstado(rs.getString("estado"));
                h.setFechaHora(rs.getTimestamp("fecha"));
                h.setComentario(rs.getString("comentario"));
                lista.add(h);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
}