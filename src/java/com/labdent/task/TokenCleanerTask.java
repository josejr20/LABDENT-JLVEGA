package com.labdent.task;

import com.labdent.util.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.TimerTask;
import java.util.logging.Logger;

/**
 * Tarea programada para limpiar tokens JWT expirados
 * Ejecutar cada hora para mantener la base de datos limpia
 */
public class TokenCleanerTask extends TimerTask {
    
    private static final Logger LOGGER = Logger.getLogger(TokenCleanerTask.class.getName());
    
    @Override
    public void run() {
        limpiarTokensExpirados();
    }
    
    /**
     * Elimina tokens JWT expirados de la base de datos
     */
    private void limpiarTokensExpirados() {
        String sql = "UPDATE usuarios SET token_jwt = NULL, token_expiracion = NULL " +
                     "WHERE token_expiracion IS NOT NULL AND token_expiracion < NOW()";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            int filasAfectadas = stmt.executeUpdate();
            
            if (filasAfectadas > 0) {
                LOGGER.info("✅ Tokens expirados eliminados: " + filasAfectadas);
            }
            
        } catch (SQLException e) {
            LOGGER.severe("❌ Error limpiando tokens: " + e.getMessage());
        }
    }
}