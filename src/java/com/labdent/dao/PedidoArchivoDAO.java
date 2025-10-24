package com.labdent.dao;

import com.labdent.model.PedidoArchivo;
import com.labdent.util.DatabaseConnection;
import org.apache.commons.lang3.StringUtils; // 👈 Importamos Apache Commons Lang

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PedidoArchivoDAO {

    public List<PedidoArchivo> listarArchivosPorPedido(int pedidoId) {
        List<PedidoArchivo> lista = new ArrayList<>();
        String sql = "SELECT * FROM pedido_archivo WHERE pedido_id = ? ORDER BY fecha_subida DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, pedidoId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                PedidoArchivo a = new PedidoArchivo();
                a.setId(rs.getInt("id"));
                a.setPedidoId(rs.getInt("pedido_id"));

                // 👇 Evita nulls o cadenas vacías
                String nombreArchivo = rs.getString("nombre_archivo");
                a.setNombreArchivo(StringUtils.defaultIfBlank(nombreArchivo, "Sin nombre"));

                a.setRutaArchivo(rs.getString("ruta_archivo"));
                a.setFechaSubida(rs.getTimestamp("fecha_subida"));
                lista.add(a);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
}
