package com.labdent.model;

import java.sql.Timestamp;

public class Evidencia {
    private int id;
    private int pedidoId;
    private String tipoEvidencia;
    private String nombreArchivo;
    private String rutaArchivo;
    private String descripcion;
    private int usuarioId;
    private Timestamp fechaSubida;
    private String nombreUsuario;

    // Constructores
    public Evidencia() {}

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getPedidoId() { return pedidoId; }
    public void setPedidoId(int pedidoId) { this.pedidoId = pedidoId; }

    public String getTipoEvidencia() { return tipoEvidencia; }
    public void setTipoEvidencia(String tipoEvidencia) { this.tipoEvidencia = tipoEvidencia; }

    public String getNombreArchivo() { return nombreArchivo; }
    public void setNombreArchivo(String nombreArchivo) { this.nombreArchivo = nombreArchivo; }

    public String getRutaArchivo() { return rutaArchivo; }
    public void setRutaArchivo(String rutaArchivo) { this.rutaArchivo = rutaArchivo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }

    public Timestamp getFechaSubida() { return fechaSubida; }
    public void setFechaSubida(Timestamp fechaSubida) { this.fechaSubida = fechaSubida; }

    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }

    // Método helper para obtener icono según tipo
    public String getIcono() {
        if (tipoEvidencia == null) return "📄";
        
        switch (tipoEvidencia) {
            case "FOTO_INICIAL": return "📷";
            case "FOTO_PROCESO": return "📸";
            case "FOTO_FINAL": return "🖼️";
            case "DOCUMENTO": return "📄";
            case "CAD_FILE": return "💾";
            default: return "📎";
        }
    }

    // Método para verificar si es imagen
    public boolean esImagen() {
        return tipoEvidencia != null && 
               (tipoEvidencia.contains("FOTO") || nombreArchivo.matches(".*\\.(jpg|jpeg|png|gif)$"));
    }
}