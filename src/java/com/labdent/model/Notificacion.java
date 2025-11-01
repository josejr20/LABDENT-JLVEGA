package com.labdent.model;

import java.sql.Timestamp;

/**
 * Modelo para notificaciones del sistema
 */
public class Notificacion {
    private int id;
    private int usuarioId;
    private Integer pedidoId;
    private String tipo; // INFO, ALERTA, URGENTE
    private String titulo;
    private String mensaje;
    private boolean leida;
    private Timestamp fechaCreacion;

    // Constructores
    public Notificacion() {}

    public Notificacion(int usuarioId, String tipo, String titulo, String mensaje) {
        this.usuarioId = usuarioId;
        this.tipo = tipo;
        this.titulo = titulo;
        this.mensaje = mensaje;
        this.leida = false;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Integer getPedidoId() {
        return pedidoId;
    }

    public void setPedidoId(Integer pedidoId) {
        this.pedidoId = pedidoId;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public boolean isLeida() {
        return leida;
    }

    public void setLeida(boolean leida) {
        this.leida = leida;
    }

    public Timestamp getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Timestamp fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    // Métodos helper
    public String getIconoTipo() {
        switch (tipo) {
            case "INFO":
                return "ℹ️";
            case "ALERTA":
                return "⚠️";
            case "URGENTE":
                return "🔴";
            default:
                return "📢";
        }
    }

    public String getClaseCss() {
        switch (tipo) {
            case "INFO":
                return "notif-info";
            case "ALERTA":
                return "notif-alerta";
            case "URGENTE":
                return "notif-urgente";
            default:
                return "notif-default";
        }
    }

    @Override
    public String toString() {
        return "Notificacion{" +
                "id=" + id +
                ", usuarioId=" + usuarioId +
                ", pedidoId=" + pedidoId +
                ", tipo='" + tipo + '\'' +
                ", titulo='" + titulo + '\'' +
                ", leida=" + leida +
                ", fechaCreacion=" + fechaCreacion +
                '}';
    }
}