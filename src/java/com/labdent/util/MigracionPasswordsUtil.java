package com.labdent.util;

import com.labdent.dao.UsuarioDAO;
import com.labdent.model.Usuario;
import java.util.List;

/**
 * Utilidad para migrar contraseñas en texto plano a BCrypt
 * EJECUTAR SOLO UNA VEZ antes de implementar el nuevo sistema
 */
public class MigracionPasswordsUtil {
    
    public static void main(String[] args) {
        System.out.println("=== INICIANDO MIGRACIÓN DE CONTRASEÑAS ===");
        
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        List<Usuario> usuarios = usuarioDAO.listarTodos();
        
        int migrados = 0;
        int errores = 0;
        
        for (Usuario usuario : usuarios) {
            try {
                String passwordActual = usuario.getPassword();
                
                // Verificar si ya está encriptada (BCrypt empieza con $2a$)
                if (passwordActual.startsWith("$2a$") || passwordActual.startsWith("$2b$")) {
                    System.out.println("Usuario " + usuario.getEmail() + " ya tiene password encriptada");
                    continue;
                }
                
                // Encriptar la contraseña actual
                String passwordEncriptada = PasswordUtil.encriptar(passwordActual);
                
                // Actualizar directamente en la base de datos
                if (usuarioDAO.cambiarPassword(usuario.getId(), passwordActual)) {
                    migrados++;
                    System.out.println("✅ Migrado: " + usuario.getEmail());
                } else {
                    errores++;
                    System.err.println("❌ Error al migrar: " + usuario.getEmail());
                }
                
            } catch (Exception e) {
                errores++;
                System.err.println("❌ Error en usuario " + usuario.getEmail() + ": " + e.getMessage());
            }
        }
        
        System.out.println("\n=== MIGRACIÓN COMPLETADA ===");
        System.out.println("Total usuarios: " + usuarios.size());
        System.out.println("Migrados: " + migrados);
        System.out.println("Errores: " + errores);
        System.out.println("Ya encriptados: " + (usuarios.size() - migrados - errores));
    }
}