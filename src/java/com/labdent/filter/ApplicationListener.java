package com.labdent.listener;

import com.labdent.task.TokenCleanerTask;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import java.util.Timer;
import java.util.logging.Logger;

/**
 * Listener que se ejecuta al iniciar/detener la aplicación
 * Inicia la tarea de limpieza de tokens expirados
 */
@WebListener
public class ApplicationListener implements ServletContextListener {
    
    private static final Logger LOGGER = Logger.getLogger(ApplicationListener.class.getName());
    private Timer tokenCleanerTimer;
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        LOGGER.info("🚀 Iniciando aplicación LABDENT JLVEGA");
        
        // Iniciar tarea de limpieza de tokens cada hora
        tokenCleanerTimer = new Timer("TokenCleaner", true);
        long oneHourInMillis = 60 * 60 * 1000;
        
        tokenCleanerTimer.scheduleAtFixedRate(
            new TokenCleanerTask(),
            0, // Ejecutar inmediatamente
            oneHourInMillis // Repetir cada hora
        );
        
        LOGGER.info("✅ Tarea de limpieza de tokens JWT iniciada (cada hora)");
    }
    
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        LOGGER.info("🛑 Deteniendo aplicación LABDENT JLVEGA");
        
        if (tokenCleanerTimer != null) {
            tokenCleanerTimer.cancel();
            LOGGER.info("✅ Tarea de limpieza de tokens detenida");
        }
    }
}