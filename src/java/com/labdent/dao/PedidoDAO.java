package com.labdent.dao;

import com.labdent.model.Pedido;
import com.labdent.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PedidoDAO {

    public boolean registrar(Pedido pedido) {
        String sql = "INSERT INTO pedidos (codigo_unico, odontologo_id, nombre_paciente, piezas_dentales, "
           + "tipo_protesis, material, color_shade, fecha_compromiso, estado_actual, prioridad, "
           + "observaciones, archivo_adjunto, responsable_actual) "
           + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            if (pedido.getCodigoUnico() == null) {
                pedido.setCodigoUnico(generarCodigoUnico());
            }

            stmt.setString(1, pedido.getCodigoUnico());
            stmt.setInt(2, pedido.getOdontologoId());
            stmt.setString(3, pedido.getNombrePaciente());
            stmt.setString(4, pedido.getPiezasDentales());
            stmt.setString(5, pedido.getTipoProtesis());
            stmt.setString(6, pedido.getMaterial());
            stmt.setString(7, pedido.getColorShade());
            stmt.setDate(8, pedido.getFechaCompromiso());
            stmt.setString(9, pedido.getEstadoActual());
            stmt.setString(10, pedido.getPrioridad());
            stmt.setString(11, pedido.getObservaciones());
            stmt.setString(12, pedido.getArchivoAdjunto());

            if (pedido.getResponsableActual() != null) {
                stmt.setInt(13, pedido.getResponsableActual());
            } else {
                stmt.setNull(13, Types.INTEGER);
            }

            int filas = stmt.executeUpdate();
            if (filas > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    pedido.setId(rs.getInt(1));
                }
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Pedido obtenerPorId(int id) {
        String sql = "SELECT p.*, u.nombre_completo AS nombre_odontologo, "
                + "u.email AS email_odontologo, u.telefono AS telefono_odontologo, "
                + "r.nombre_completo AS nombre_responsable "
                + "FROM pedidos p "
                + "INNER JOIN usuarios u ON p.odontologo_id = u.id "
                + "LEFT JOIN usuarios r ON p.responsable_actual = r.id "
                + "WHERE p.id = ?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapearPedido(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Pedido obtenerPorCodigo(String codigoUnico) {
        String sql = "SELECT p.*, u.nombre_completo as nombre_odontologo, u.email as email_odontologo, "
                + "u.telefono as telefono_odontologo, r.nombre_completo as nombre_responsable "
                + "FROM pedidos p "
                + "INNER JOIN usuarios u ON p.odontologo_id = u.id "
                + "LEFT JOIN usuarios r ON p.responsable_actual = r.id "
                + "WHERE p.codigo_unico = ?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, codigoUnico);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapearPedido(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Pedido> listarTodos() {
        List<Pedido> pedido = new ArrayList<>();
        String sql = "SELECT p.*, u.nombre_completo as nombre_odontologo, u.email as email_odontologo, "
                + "u.telefono as telefono_odontologo, r.nombre_completo as nombre_responsable "
                + "FROM pedidos p "
                + "INNER JOIN usuarios u ON p.odontologo_id = u.id "
                + "LEFT JOIN usuarios r ON p.responsable_actual = r.id "
                + "ORDER BY p.fecha_ingreso ASC"; // FIFO

        try (Connection conn = DatabaseConnection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                pedido.add(mapearPedido(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pedido;
    }

    public List<Pedido> listarPorEstado(String estado) {
        List<Pedido> Pedido = new ArrayList<>();
        String sql = "SELECT p.*, u.nombre_completo as nombre_odontologo, u.email as email_odontologo, "
                + "u.telefono as telefono_odontologo, r.nombre_completo as nombre_responsable "
                + "FROM Pedidos p "
                + "INNER JOIN usuarios u ON p.odontologo_id = u.id "
                + "LEFT JOIN usuarios r ON p.responsable_actual = r.id "
                + "WHERE p.estado_actual = ? "
                + "ORDER BY p.fecha_ingreso ASC"; // FIFO

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, estado);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Pedido.add(mapearPedido(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Pedido;
    }

    public List<Pedido> listarPorOdontologo(int odontologoId) {
        List<Pedido> Pedido = new ArrayList<>();
        String sql = "SELECT p.*, u.nombre_completo as nombre_odontologo, u.email as email_odontologo, "
                + "u.telefono as telefono_odontologo, r.nombre_completo as nombre_responsable "
                + "FROM Pedidos p "
                + "INNER JOIN usuarios u ON p.odontologo_id = u.id "
                + "LEFT JOIN usuarios r ON p.responsable_actual = r.id "
                + "WHERE p.odontologo_id = ? "
                + "ORDER BY p.fecha_ingreso DESC";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, odontologoId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Pedido.add(mapearPedido(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Pedido;
    }

    public boolean actualizarEstado(int pedidoId, String nuevoEstado, int responsableId) {
        String sql = "UPDATE Pedido SET estado_actual = ?, responsable_actual = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nuevoEstado);
            stmt.setInt(2, responsableId);
            stmt.setInt(3, pedidoId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean marcarComoEntregado(int pedidoId) {
        String sql = "UPDATE Pedido SET estado_actual = 'ENTREGADO', fecha_entrega = CURRENT_TIMESTAMP WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, pedidoId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean actualizar(Pedido pedido) {
        String sql = "UPDATE Pedido SET nombre_paciente = ?, piezas_dentales = ?, tipo_protesis = ?, "
                + "material = ?, color_shade = ?, fecha_compromiso = ?, prioridad = ?, "
                + "observaciones = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, pedido.getNombrePaciente());
            stmt.setString(2, pedido.getPiezasDentales());
            stmt.setString(3, pedido.getTipoProtesis());
            stmt.setString(4, pedido.getMaterial());
            stmt.setString(5, pedido.getColorShade());
            stmt.setDate(6, pedido.getFechaCompromiso());
            stmt.setString(7, pedido.getPrioridad());
            stmt.setString(8, pedido.getObservaciones());
            stmt.setInt(9, pedido.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Pedido> listarAtrasados() {
        List<Pedido> Pedido = new ArrayList<>();
        String sql = "SELECT p.*, u.nombre_completo as nombre_odontologo, u.email as email_odontologo, "
                + "u.telefono as telefono_odontologo, r.nombre_completo as nombre_responsable "
                + "FROM Pedidos p "
                + "INNER JOIN usuarios u ON p.odontologo_id = u.id "
                + "LEFT JOIN usuarios r ON p.responsable_actual = r.id "
                + "WHERE p.fecha_compromiso < CURDATE() AND p.estado_actual != 'ENTREGADO' "
                + "ORDER BY p.fecha_compromiso ASC";

        try (Connection conn = DatabaseConnection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Pedido.add(mapearPedido(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Pedido;
    }

    private String generarCodigoUnico() {
        return "LAB-" + System.currentTimeMillis();
    }

    private Pedido mapearPedido(ResultSet rs) throws SQLException {
        Pedido pedido = new Pedido();

        pedido.setId(rs.getInt("id"));
        pedido.setCodigoUnico(rs.getString("codigo_unico"));
        pedido.setOdontologoId(rs.getInt("odontologo_id"));
        pedido.setNombrePaciente(rs.getString("nombre_paciente"));
        pedido.setPiezasDentales(rs.getString("piezas_dentales"));
        pedido.setTipoProtesis(rs.getString("tipo_protesis"));
        pedido.setMaterial(rs.getString("material"));
        pedido.setColorShade(rs.getString("color_shade"));
        pedido.setFechaIngreso(rs.getTimestamp("fecha_ingreso"));
        pedido.setFechaCompromiso(rs.getDate("fecha_compromiso"));
        pedido.setFechaEntrega(rs.getTimestamp("fecha_entrega"));
        pedido.setEstadoActual(rs.getString("estado_actual"));
        pedido.setPrioridad(rs.getString("prioridad"));
        pedido.setObservaciones(rs.getString("observaciones"));
        pedido.setArchivoAdjunto(rs.getString("archivo_adjunto"));
        pedido.setResponsableActual((Integer) rs.getObject("responsable_actual"));
        pedido.setNombreOdontologo(rs.getString("nombre_odontologo"));
        pedido.setEmailOdontologo(rs.getString("email_odontologo"));
        pedido.setTelefonoOdontologo(rs.getString("telefono_odontologo"));
        pedido.setNombreResponsable(rs.getString("nombre_responsable"));

        return pedido;
    }

    // en PedidoDAO.java
    public List<Pedido> obtenerPedidosPorCliente(Integer idCliente) {
        List<Pedido> lista = new ArrayList<>();
        String sql = "SELECT id, codigo_unico, nombre_paciente, tipo_protesis, material, estado_actual, fecha_compromiso, fecha_ingreso "
                + "FROM pedidos WHERE odontologo_id = ? ORDER BY fecha_ingreso DESC";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Pedido p = new Pedido();
                    p.setId(rs.getInt("id"));
                    p.setCodigoUnico(rs.getString("codigo_unico"));
                    p.setNombrePaciente(rs.getString("nombre_paciente"));
                    p.setTipoProtesis(rs.getString("tipo_protesis"));
                    p.setMaterial(rs.getString("material"));
                    p.setEstadoActual(rs.getString("estado_actual"));
                    // fecha_compromiso es DATE en la BD -> tu modelo usa java.sql.Date get/set
                    p.setFechaCompromiso(rs.getDate("fecha_compromiso"));
                    // También puedes setear fechaIngreso si la necesitas
                    p.setFechaIngreso(rs.getTimestamp("fecha_ingreso"));
                    lista.add(p);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public Pedido obtenerPedidoPorId(int idPedido) {
        Pedido pedido = null;
        String sql = "SELECT p.*, u.nombreCompleto AS nombreOdontologo, "
                + "u.email AS emailOdontologo, u.telefono AS telefonoOdontologo "
                + "FROM pedidos p "
                + "LEFT JOIN usuario u ON p.odontologo_id = u.id "
                + "WHERE p.id = ?";

        try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idPedido);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                pedido = new Pedido();
                pedido.setId(rs.getInt("id"));
                pedido.setCodigoUnico(rs.getString("codigo_unico"));
                pedido.setOdontologoId(rs.getInt("odontologo_id"));
                pedido.setNombrePaciente(rs.getString("nombre_paciente"));
                pedido.setPiezasDentales(rs.getString("piezas_dentales"));
                pedido.setTipoProtesis(rs.getString("tipo_protesis"));
                pedido.setMaterial(rs.getString("material"));
                pedido.setColorShade(rs.getString("color_shade"));
                pedido.setFechaIngreso(rs.getTimestamp("fecha_ingreso"));
                pedido.setFechaCompromiso(rs.getDate("fecha_compromiso"));
                pedido.setFechaEntrega(rs.getTimestamp("fecha_entrega"));
                pedido.setEstadoActual(rs.getString("estado_actual"));
                pedido.setPrioridad(rs.getString("prioridad"));
                pedido.setObservaciones(rs.getString("observaciones"));
                pedido.setArchivoAdjunto(rs.getString("archivo_adjunto"));
                pedido.setResponsableActual(rs.getInt("responsable_actual"));

                pedido.setNombreOdontologo(rs.getString("nombreOdontologo"));
                pedido.setEmailOdontologo(rs.getString("emailOdontologo"));
                pedido.setTelefonoOdontologo(rs.getString("telefonoOdontologo"));
            }

        } catch (Exception e) {
            System.out.println("Error obtenerPedidoPorId: " + e.getMessage());
        }

        return pedido;
    }

}
