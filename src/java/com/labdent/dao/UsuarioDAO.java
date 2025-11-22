package com.labdent.dao;

import com.labdent.model.Usuario;
import com.labdent.util.DatabaseConnection;
import com.labdent.util.JWTUtil;
import com.labdent.util.PasswordUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    /**
     * Autentica un usuario verificando email y contraseña encriptada
     * ✅ Protegido contra SQL Injection con PreparedStatement
     */
    public Usuario autenticar(String email, String password) {
        Usuario usuario = null;
        // ✅ Usar PreparedStatement previene SQL Injection
        String sql = "SELECT * FROM usuarios WHERE email = ? AND activo = 1";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            // ✅ Sanitizar entrada
            stmt.setString(1, email.trim().toLowerCase());
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                String hashedPassword = rs.getString("password");
                
                // ✅ Verificar la contraseña usando BCrypt
                if (PasswordUtil.verificar(password, hashedPassword)) {
                    usuario = mapearUsuario(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en autenticación: " + e.getMessage());
        }
        return usuario;
    }
    
    /**
     * Verifica si un email ya está registrado
     */
    public boolean existeEmail(String email) {
        String sql = "SELECT COUNT(*) FROM usuarios WHERE email = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email.trim().toLowerCase());
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error verificando email: " + e.getMessage());
        }
        return false;
    }

    /**
     * Registra un nuevo usuario con contraseña encriptada
     */
    public boolean registrar(Usuario usuario) {
        String sql = "INSERT INTO usuarios (nombre_completo, email, password, rol, telefono, direccion, activo) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            // ✅ Encriptar la contraseña antes de guardarla
            String passwordEncriptada = PasswordUtil.encriptar(usuario.getPassword());
            
            stmt.setString(1, usuario.getNombreCompleto().trim());
            stmt.setString(2, usuario.getEmail().trim().toLowerCase());
            stmt.setString(3, passwordEncriptada);
            stmt.setString(4, usuario.getRol().toUpperCase());
            stmt.setString(5, usuario.getTelefono() != null ? usuario.getTelefono().trim() : null);
            stmt.setString(6, usuario.getDireccion() != null ? usuario.getDireccion().trim() : null);
            stmt.setBoolean(7, usuario.isActivo());
            
            int filasAfectadas = stmt.executeUpdate();
            
            if (filasAfectadas > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    usuario.setId(generatedKeys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error al registrar usuario: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Guarda el token JWT en la base de datos
     */
    public boolean guardarToken(int usuarioId, String token) {
        String sql = "UPDATE usuarios SET token_jwt = ?, token_expiracion = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            java.util.Date expiracion = JWTUtil.obtenerExpiracion(token);
            
            stmt.setString(1, token);
            stmt.setTimestamp(2, new Timestamp(expiracion.getTime()));
            stmt.setInt(3, usuarioId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error guardando token: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Elimina el token JWT de la base de datos (logout)
     */
    public boolean eliminarToken(int usuarioId) {
        String sql = "UPDATE usuarios SET token_jwt = NULL, token_expiracion = NULL WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, usuarioId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error eliminando token: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Verifica si un token es válido en la base de datos
     */
    public boolean validarTokenEnBD(int usuarioId, String token) {
        String sql = "SELECT token_jwt, token_expiracion FROM usuarios WHERE id = ? AND activo = 1";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, usuarioId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                String tokenDB = rs.getString("token_jwt");
                Timestamp expiracion = rs.getTimestamp("token_expiracion");
                
                if (tokenDB != null && tokenDB.equals(token)) {
                    // Verificar que no haya expirado
                    return expiracion != null && expiracion.after(new Timestamp(System.currentTimeMillis()));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error validando token: " + e.getMessage());
        }
        return false;
    }

    public Usuario obtenerPorId(int id) {
        String sql = "SELECT * FROM usuarios WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapearUsuario(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error obteniendo usuario: " + e.getMessage());
        }
        return null;
    }

    public List<Usuario> listarTodos() {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM usuarios ORDER BY nombre_completo";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                usuarios.add(mapearUsuario(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error listando usuarios: " + e.getMessage());
        }
        return usuarios;
    }

    public List<Usuario> listarPorRol(String rol) {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM usuarios WHERE rol = ? AND activo = 1 ORDER BY nombre_completo";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, rol.toUpperCase());
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                usuarios.add(mapearUsuario(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error listando por rol: " + e.getMessage());
        }
        return usuarios;
    }

    public boolean actualizar(Usuario usuario) {
        String sql = "UPDATE usuarios SET nombre_completo = ?, email = ?, telefono = ?, " +
                     "direccion = ?, activo = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, usuario.getNombreCompleto().trim());
            stmt.setString(2, usuario.getEmail().trim().toLowerCase());
            stmt.setString(3, usuario.getTelefono() != null ? usuario.getTelefono().trim() : null);
            stmt.setString(4, usuario.getDireccion() != null ? usuario.getDireccion().trim() : null);
            stmt.setBoolean(5, usuario.isActivo());
            stmt.setInt(6, usuario.getId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error actualizando usuario: " + e.getMessage());
            return false;
        }
    }

    public boolean cambiarPassword(int usuarioId, String nuevoPassword) {
        String sql = "UPDATE usuarios SET password = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String passwordEncriptada = PasswordUtil.encriptar(nuevoPassword);
            
            stmt.setString(1, passwordEncriptada);
            stmt.setInt(2, usuarioId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error cambiando password: " + e.getMessage());
            return false;
        }
    }

    private void actualizarUltimaConexion(int usuarioId) {
        String sql = "UPDATE usuarios SET ultima_conexion = CURRENT_TIMESTAMP WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, usuarioId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error actualizando última conexión: " + e.getMessage());
        }
    }

    private Usuario mapearUsuario(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setId(rs.getInt("id"));
        usuario.setNombreCompleto(rs.getString("nombre_completo"));
        usuario.setEmail(rs.getString("email"));
        usuario.setPassword(rs.getString("password"));
        usuario.setRol(rs.getString("rol"));
        usuario.setTelefono(rs.getString("telefono"));
        usuario.setDireccion(rs.getString("direccion"));
        usuario.setActivo(rs.getBoolean("activo"));
        usuario.setFechaRegistro(rs.getTimestamp("fecha_registro"));
        usuario.setUltimaConexion(rs.getTimestamp("ultima_conexion"));
        usuario.setTokenJwt(rs.getString("token_jwt"));
        usuario.setTokenExpiracion(rs.getTimestamp("token_expiracion"));
        return usuario;
    }
}