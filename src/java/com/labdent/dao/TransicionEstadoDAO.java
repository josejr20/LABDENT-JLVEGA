package com.labdent.dao;

import com.labdent.model.TransicionEstado;
import com.labdent.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DAO para gestión de transiciones de estado
 * Proporciona trazabilidad completa del flujo de trabajo
 */
public class TransicionEstadoDAO {
    
    /**
     * Registra una nueva transición de estado
     * @param transicion Objeto con los datos de la transición
     * @return true si se registró correctamente
     */
    public boolean registrarTransicion(TransicionEstado transicion) {
        String sql = "INSERT INTO transiciones_estado " +
                     "(pedido_id, estado_anterior, estado_nuevo, usuario_id, " +
                     "observaciones, checklist_completado, tiempo_en_estado) " +
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
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                // Obtener el ID generado
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        transicion.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
            
            return false;
            
        } catch (SQLException e) {
            System.err.println("❌ Error al registrar transición: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Obtiene el historial completo de transiciones de un pedido
     * @param pedidoId ID del pedido
     * @return Lista ordenada cronológicamente
     */
    public List<TransicionEstado> obtenerHistorialPorPedido(int pedidoId) {
        return obtenerPorPedido(pedidoId);
    }
    
    /**
     * Obtiene todas las transiciones de un pedido específico
     * @param pedidoId ID del pedido
     * @return Lista de transiciones ordenadas por fecha
     */
    public List<TransicionEstado> obtenerPorPedido(int pedidoId) {
        List<TransicionEstado> transiciones = new ArrayList<>();
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
                TransicionEstado t = mapearTransicion(rs);
                transiciones.add(t);
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener transiciones por pedido: " + e.getMessage());
            e.printStackTrace();
        }
        
        return transiciones;
    }
    
    /**
     * Obtiene todas las transiciones realizadas por un usuario
     * @param usuarioId ID del usuario
     * @return Lista de transiciones del usuario
     */
    public List<TransicionEstado> obtenerPorUsuario(int usuarioId) {
        List<TransicionEstado> transiciones = new ArrayList<>();
        String sql = "SELECT t.*, u.nombre_completo as nombre_usuario, p.codigo_unico " +
                     "FROM transiciones_estado t " +
                     "INNER JOIN usuarios u ON t.usuario_id = u.id " +
                     "INNER JOIN pedidos p ON t.pedido_id = p.id " +
                     "WHERE t.usuario_id = ? " +
                     "ORDER BY t.fecha_transicion DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, usuarioId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                TransicionEstado t = mapearTransicionCompleta(rs);
                transiciones.add(t);
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener transiciones por usuario: " + e.getMessage());
            e.printStackTrace();
        }
        
        return transiciones;
    }
    
    /**
     * Obtiene la última transición de un pedido
     * @param pedidoId ID del pedido
     * @return Última transición o null si no existe
     */
    public TransicionEstado obtenerUltimaTransicion(int pedidoId) {
        String sql = "SELECT t.*, u.nombre_completo as nombre_usuario " +
                     "FROM transiciones_estado t " +
                     "INNER JOIN usuarios u ON t.usuario_id = u.id " +
                     "WHERE t.pedido_id = ? " +
                     "ORDER BY t.fecha_transicion DESC " +
                     "LIMIT 1";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, pedidoId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapearTransicion(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener última transición: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Obtiene transiciones en un rango de fechas
     * @param fechaInicio Fecha de inicio
     * @param fechaFin Fecha de fin
     * @return Lista de transiciones en el rango
     */
    public List<TransicionEstado> obtenerPorRangoFechas(java.util.Date fechaInicio, java.util.Date fechaFin) {
        List<TransicionEstado> transiciones = new ArrayList<>();
        String sql = "SELECT t.*, u.nombre_completo as nombre_usuario, p.codigo_unico " +
                     "FROM transiciones_estado t " +
                     "INNER JOIN usuarios u ON t.usuario_id = u.id " +
                     "INNER JOIN pedidos p ON t.pedido_id = p.id " +
                     "WHERE DATE(t.fecha_transicion) BETWEEN ? AND ? " +
                     "ORDER BY t.fecha_transicion DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, new java.sql.Date(fechaInicio.getTime()));
            stmt.setDate(2, new java.sql.Date(fechaFin.getTime()));
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                TransicionEstado t = mapearTransicionCompleta(rs);
                transiciones.add(t);
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener transiciones por rango de fechas: " + e.getMessage());
            e.printStackTrace();
        }
        
        return transiciones;
    }
    
    /**
     * Calcula el tiempo promedio por estado
     * @return Mapa con estado y tiempo promedio en horas
     */
    public Map<String, Double> obtenerTiempoPromedioEstados() {
        Map<String, Double> tiempos = new HashMap<>();
        String sql = "SELECT estado_nuevo, AVG(tiempo_en_estado) as promedio " +
                     "FROM transiciones_estado " +
                     "WHERE tiempo_en_estado IS NOT NULL AND tiempo_en_estado > 0 " +
                     "GROUP BY estado_nuevo";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                String estado = rs.getString("estado_nuevo");
                double promedioMinutos = rs.getDouble("promedio");
                double promedioHoras = promedioMinutos / 60.0;
                tiempos.put(estado, Math.round(promedioHoras * 100.0) / 100.0); // 2 decimales
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener tiempo promedio por estados: " + e.getMessage());
            e.printStackTrace();
        }
        
        return tiempos;
    }
    
    /**
     * Cuenta transiciones por estado
     * @return Mapa con estado y cantidad de transiciones
     */
    public Map<String, Integer> contarTransicionesPorEstado() {
        Map<String, Integer> conteos = new HashMap<>();
        String sql = "SELECT estado_nuevo, COUNT(*) as total " +
                     "FROM transiciones_estado " +
                     "GROUP BY estado_nuevo " +
                     "ORDER BY total DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                conteos.put(rs.getString("estado_nuevo"), rs.getInt("total"));
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error al contar transiciones por estado: " + e.getMessage());
            e.printStackTrace();
        }
        
        return conteos;
    }
    
    /**
     * Obtiene estadísticas de transiciones por usuario
     * @param usuarioId ID del usuario
     * @return Mapa con estadísticas
     */
    public Map<String, Object> obtenerEstadisticasUsuario(int usuarioId) {
        Map<String, Object> stats = new HashMap<>();
        
        // Total de transiciones
        String sqlTotal = "SELECT COUNT(*) as total FROM transiciones_estado WHERE usuario_id = ?";
        
        // Transiciones por estado
        String sqlPorEstado = "SELECT estado_nuevo, COUNT(*) as cantidad " +
                              "FROM transiciones_estado " +
                              "WHERE usuario_id = ? " +
                              "GROUP BY estado_nuevo";
        
        // Tiempo promedio
        String sqlTiempo = "SELECT AVG(tiempo_en_estado) as promedio " +
                           "FROM transiciones_estado " +
                           "WHERE usuario_id = ? AND tiempo_en_estado IS NOT NULL";
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            
            // Total
            try (PreparedStatement stmt = conn.prepareStatement(sqlTotal)) {
                stmt.setInt(1, usuarioId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    stats.put("totalTransiciones", rs.getInt("total"));
                }
            }
            
            // Por estado
            Map<String, Integer> porEstado = new HashMap<>();
            try (PreparedStatement stmt = conn.prepareStatement(sqlPorEstado)) {
                stmt.setInt(1, usuarioId);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    porEstado.put(rs.getString("estado_nuevo"), rs.getInt("cantidad"));
                }
            }
            stats.put("porEstado", porEstado);
            
            // Tiempo promedio
            try (PreparedStatement stmt = conn.prepareStatement(sqlTiempo)) {
                stmt.setInt(1, usuarioId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    double promedioHoras = rs.getDouble("promedio") / 60.0;
                    stats.put("tiempoPromedioHoras", Math.round(promedioHoras * 100.0) / 100.0);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener estadísticas de usuario: " + e.getMessage());
            e.printStackTrace();
        }
        
        return stats;
    }
    
    /**
     * Obtiene transiciones de un período específico
     * @param dias Número de días hacia atrás
     * @return Lista de transiciones recientes
     */
    public List<TransicionEstado> obtenerTransicionesRecientes(int dias) {
        List<TransicionEstado> transiciones = new ArrayList<>();
        String sql = "SELECT t.*, u.nombre_completo as nombre_usuario, p.codigo_unico " +
                     "FROM transiciones_estado t " +
                     "INNER JOIN usuarios u ON t.usuario_id = u.id " +
                     "INNER JOIN pedidos p ON t.pedido_id = p.id " +
                     "WHERE t.fecha_transicion >= DATE_SUB(NOW(), INTERVAL ? DAY) " +
                     "ORDER BY t.fecha_transicion DESC " +
                     "LIMIT 100";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, dias);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                TransicionEstado t = mapearTransicionCompleta(rs);
                transiciones.add(t);
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener transiciones recientes: " + e.getMessage());
            e.printStackTrace();
        }
        
        return transiciones;
    }
    
    /**
     * Calcula el tiempo total de un pedido desde inicio hasta estado actual
     * @param pedidoId ID del pedido
     * @return Tiempo total en minutos
     */
    public int calcularTiempoTotalPedido(int pedidoId) {
        String sql = "SELECT SUM(tiempo_en_estado) as total " +
                     "FROM transiciones_estado " +
                     "WHERE pedido_id = ? AND tiempo_en_estado IS NOT NULL";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, pedidoId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("total");
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error al calcular tiempo total del pedido: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }
    
    /**
     * Elimina todas las transiciones de un pedido (cascada)
     * @param pedidoId ID del pedido
     * @return true si se eliminaron correctamente
     */
    public boolean eliminarPorPedido(int pedidoId) {
        String sql = "DELETE FROM transiciones_estado WHERE pedido_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, pedidoId);
            stmt.executeUpdate();
            return true;
            
        } catch (SQLException e) {
            System.err.println("❌ Error al eliminar transiciones: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // ==================== MÉTODOS PRIVADOS DE MAPEO ====================
    
    /**
     * Mapea un ResultSet a un objeto TransicionEstado
     */
    private TransicionEstado mapearTransicion(ResultSet rs) throws SQLException {
        TransicionEstado t = new TransicionEstado();
        t.setId(rs.getInt("id"));
        t.setPedidoId(rs.getInt("pedido_id"));
        t.setEstadoAnterior(rs.getString("estado_anterior"));
        t.setEstadoNuevo(rs.getString("estado_nuevo"));
        t.setUsuarioId(rs.getInt("usuario_id"));
        t.setNombreUsuario(rs.getString("nombre_usuario"));
        t.setObservaciones(rs.getString("observaciones"));
        t.setChecklistCompletado(rs.getBoolean("checklist_completado"));
        
        int tiempoEnEstado = rs.getInt("tiempo_en_estado");
        if (!rs.wasNull()) {
            t.setTiempoEnEstado(tiempoEnEstado);
        }
        
        t.setFechaTransicion(rs.getTimestamp("fecha_transicion"));
        
        return t;
    }
    
    /**
     * Mapea un ResultSet con información completa (incluye código de pedido)
     */
    private TransicionEstado mapearTransicionCompleta(ResultSet rs) throws SQLException {
        TransicionEstado t = mapearTransicion(rs);
        t.setCodigoPedido(rs.getString("codigo_unico"));
        return t;
    }
    
    // ==================== MÉTODOS DE UTILIDAD ====================
    
    /**
     * Verifica si un pedido tiene transiciones registradas
     */
    public boolean tieneTransiciones(int pedidoId) {
        String sql = "SELECT COUNT(*) as total FROM transiciones_estado WHERE pedido_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, pedidoId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("total") > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error al verificar transiciones: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Obtiene el conteo total de transiciones
     */
    public int contarTotalTransiciones() {
        String sql = "SELECT COUNT(*) as total FROM transiciones_estado";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt("total");
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error al contar transiciones totales: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }
}