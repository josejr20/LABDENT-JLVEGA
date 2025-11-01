package com.labdent.dao;

import com.labdent.model.Notificacion;
import com.labdent.util.DatabaseConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para gestionar notificaciones
 */
public class NotificacionDAO {
    
    private static final Logger logger = LoggerFactory.getLogger(NotificacionDAO.class);

    /**
     * Registra una nueva notificación
     */
    public boolean registrar(Notificacion notificacion) {
        String sql = "INSERT INTO notificaciones " +
                     "(usuario_id, pedido_id, tipo, titulo, mensaje, leida) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, notificacion.getUsuarioId());
            
            if (notificacion.getPedidoId() != null) {
                stmt.setInt(2, notificacion.getPedidoId());
            } else {
                stmt.setNull(2, Types.INTEGER);
            }
            
            stmt.setString(3, notificacion.getTipo());
            stmt.setString(4, notificacion.getTitulo());
            stmt.setString(5, notificacion.getMensaje());
            stmt.setBoolean(6, notificacion.isLeida());

            int filas = stmt.executeUpdate();
            
            if (filas > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    notificacion.setId(rs.getInt(1));
                }
                logger.debug("Notificación registrada con ID: {}", notificacion.getId());
                return true;
            }

        } catch (SQLException e) {
            logger.error("Error al registrar notificación", e);
        }
        
        return false;
    }

    /**
     * Obtiene una notificación por ID
     */
    public Notificacion obtenerPorId(int id) {
        String sql = "SELECT * FROM notificaciones WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapearNotificacion(rs);
            }

        } catch (SQLException e) {
            logger.error("Error al obtener notificación por ID", e);
        }
        
        return null;
    }

    /**
     * Lista notificaciones por usuario
     */
    public List<Notificacion> listarPorUsuario(int usuarioId) {
        return listarPorUsuario(usuarioId, false);
    }

    /**
     * Lista notificaciones por usuario con filtro de leídas
     */
    public List<Notificacion> listarPorUsuario(int usuarioId, boolean soloNoLeidas) {
        List<Notificacion> notificaciones = new ArrayList<>();
        
        String sql = "SELECT * FROM notificaciones WHERE usuario_id = ?";
        if (soloNoLeidas) {
            sql += " AND leida = FALSE";
        }
        sql += " ORDER BY fecha_creacion DESC LIMIT 50";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, usuarioId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                notificaciones.add(mapearNotificacion(rs));
            }

        } catch (SQLException e) {
            logger.error("Error al listar notificaciones por usuario", e);
        }
        
        return notificaciones;
    }

    /**
     * Marca una notificación como leída
     */
    public boolean marcarComoLeida(int notificacionId) {
        String sql = "UPDATE notificaciones SET leida = TRUE WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, notificacionId);
            int filas = stmt.executeUpdate();
            
            if (filas > 0) {
                logger.debug("Notificación {} marcada como leída", notificacionId);
                return true;
            }

        } catch (SQLException e) {
            logger.error("Error al marcar notificación como leída", e);
        }
        
        return false;
    }

    /**
     * Marca todas las notificaciones de un usuario como leídas
     */
    public boolean marcarTodasComoLeidas(int usuarioId) {
        String sql = "UPDATE notificaciones SET leida = TRUE WHERE usuario_id = ? AND leida = FALSE";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, usuarioId);
            int filas = stmt.executeUpdate();
            
            logger.debug("{} notificaciones marcadas como leídas para usuario {}", filas, usuarioId);
            return filas > 0;

        } catch (SQLException e) {
            logger.error("Error al marcar todas las notificaciones como leídas", e);
        }
        
        return false;
    }

    /**
     * Cuenta notificaciones no leídas de un usuario
     */
    public int contarNoLeidas(int usuarioId) {
        String sql = "SELECT COUNT(*) FROM notificaciones WHERE usuario_id = ? AND leida = FALSE";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, usuarioId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            logger.error("Error al contar notificaciones no leídas", e);
        }
        
        return 0;
    }

    /**
     * Elimina una notificación
     */
    public boolean eliminar(int notificacionId) {
        String sql = "DELETE FROM notificaciones WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, notificacionId);
            int filas = stmt.executeUpdate();
            
            if (filas > 0) {
                logger.debug("Notificación {} eliminada", notificacionId);
                return true;
            }

        } catch (SQLException e) {
            logger.error("Error al eliminar notificación", e);
        }
        
        return false;
    }

    /**
     * Elimina notificaciones antiguas (más de 30 días)
     */
    public int eliminarAntiguas() {
        String sql = "DELETE FROM notificaciones WHERE fecha_creacion < DATE_SUB(NOW(), INTERVAL 30 DAY)";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            int filas = stmt.executeUpdate(sql);
            logger.info("{} notificaciones antiguas eliminadas", filas);
            return filas;

        } catch (SQLException e) {
            logger.error("Error al eliminar notificaciones antiguas", e);
        }
        
        return 0;
    }

    /**
     * Mapea un ResultSet a un objeto Notificacion
     */
    private Notificacion mapearNotificacion(ResultSet rs) throws SQLException {
        Notificacion notificacion = new Notificacion();
        
        notificacion.setId(rs.getInt("id"));
        notificacion.setUsuarioId(rs.getInt("usuario_id"));
        
        Integer pedidoId = (Integer) rs.getObject("pedido_id");
        notificacion.setPedidoId(pedidoId);
        
        notificacion.setTipo(rs.getString("tipo"));
        notificacion.setTitulo(rs.getString("titulo"));
        notificacion.setMensaje(rs.getString("mensaje"));
        notificacion.setLeida(rs.getBoolean("leida"));
        notificacion.setFechaCreacion(rs.getTimestamp("fecha_creacion"));
        
        return notificacion;
    }
}