package com.labdent.dao;

import com.labdent.model.PedidoDelivery;
import com.labdent.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PedidoDeliveryDAO {

    public boolean registrar(PedidoDelivery delivery) {
        String sql = "INSERT INTO pedido_delivery " +
                     "(pedido_id, deliverista_id, estado_delivery, observaciones) " +
                     "VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, delivery.getPedidoId());
            stmt.setInt(2, delivery.getDeliveristaId());
            stmt.setString(3, delivery.getEstadoDelivery());
            stmt.setString(4, delivery.getObservaciones());

            int filas = stmt.executeUpdate();
            if (filas > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    delivery.setId(rs.getInt(1));
                }
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Error al registrar delivery: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public PedidoDelivery obtenerPorId(int id) {
        String sql = "SELECT pd.*, p.codigo_unico, p.nombre_paciente, u.nombre_completo as nombre_deliverista " +
                     "FROM pedido_delivery pd " +
                     "INNER JOIN pedidos p ON pd.pedido_id = p.id " +
                     "INNER JOIN usuarios u ON pd.deliverista_id = u.id " +
                     "WHERE pd.id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapearDelivery(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener delivery: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public PedidoDelivery obtenerPorPedidoId(int pedidoId) {
        String sql = "SELECT pd.*, p.codigo_unico, p.nombre_paciente, u.nombre_completo as nombre_deliverista " +
                     "FROM pedido_delivery pd " +
                     "INNER JOIN pedidos p ON pd.pedido_id = p.id " +
                     "INNER JOIN usuarios u ON pd.deliverista_id = u.id " +
                     "WHERE pd.pedido_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, pedidoId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapearDelivery(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener delivery por pedido: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public List<PedidoDelivery> listarPorDeliverista(int deliveristaId) {
        List<PedidoDelivery> deliveries = new ArrayList<>();
        String sql = "SELECT pd.*, p.codigo_unico, p.nombre_paciente, u.nombre_completo as nombre_deliverista " +
                     "FROM pedido_delivery pd " +
                     "INNER JOIN pedidos p ON pd.pedido_id = p.id " +
                     "INNER JOIN usuarios u ON pd.deliverista_id = u.id " +
                     "WHERE pd.deliverista_id = ? AND pd.estado_delivery != 'PEDIDO_ENTREGADO' " +
                     "ORDER BY pd.created_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, deliveristaId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                deliveries.add(mapearDelivery(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al listar deliveries: " + e.getMessage());
            e.printStackTrace();
        }
        return deliveries;
    }

    public boolean actualizarEstado(int deliveryId, String nuevoEstado, String observaciones) {
        StringBuilder sql = new StringBuilder("UPDATE pedido_delivery SET estado_delivery = ?, observaciones = ?");

        // Actualizar timestamps según el estado
        switch (nuevoEstado) {
            case "SALIO_EMPRESA":
                sql.append(", fecha_salida = CURRENT_TIMESTAMP");
                break;
            case "LLEGO_DESTINO":
                sql.append(", fecha_llegada = CURRENT_TIMESTAMP");
                break;
            case "PEDIDO_ENTREGADO":
                sql.append(", fecha_entrega = CURRENT_TIMESTAMP");
                break;
        }

        sql.append(" WHERE id = ?");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            stmt.setString(1, nuevoEstado);
            stmt.setString(2, observaciones);
            stmt.setInt(3, deliveryId);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar estado delivery: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private PedidoDelivery mapearDelivery(ResultSet rs) throws SQLException {
        PedidoDelivery delivery = new PedidoDelivery();
        delivery.setId(rs.getInt("id"));
        delivery.setPedidoId(rs.getInt("pedido_id"));
        delivery.setDeliveristaId(rs.getInt("deliverista_id"));
        delivery.setEstadoDelivery(rs.getString("estado_delivery"));
        delivery.setFechaSalida(rs.getTimestamp("fecha_salida"));
        delivery.setFechaLlegada(rs.getTimestamp("fecha_llegada"));
        delivery.setFechaEntrega(rs.getTimestamp("fecha_entrega"));
        delivery.setObservaciones(rs.getString("observaciones"));
        delivery.setCreatedAt(rs.getTimestamp("created_at"));
        delivery.setUpdatedAt(rs.getTimestamp("updated_at"));
        delivery.setCodigoPedido(rs.getString("codigo_unico"));
        delivery.setNombrePaciente(rs.getString("nombre_paciente"));
        delivery.setNombreDeliverista(rs.getString("nombre_deliverista"));
        return delivery;
    }
}