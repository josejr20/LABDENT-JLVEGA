package com.labdent.model;

import java.sql.Timestamp;
import com.google.common.base.MoreObjects; // ← Import de Guava

public class PedidoArchivo {
    private int id;
    private int pedidoId;
    private String nombreArchivo;
    private String rutaArchivo;
    private String tipo;
    private Timestamp fechaSubida;

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getPedidoId() { return pedidoId; }
    public void setPedidoId(int pedidoId) { this.pedidoId = pedidoId; }

    public String getNombreArchivo() { return nombreArchivo; }
    public void setNombreArchivo(String nombreArchivo) { this.nombreArchivo = nombreArchivo; }

    public String getRutaArchivo() { return rutaArchivo; }
    public void setRutaArchivo(String rutaArchivo) { this.rutaArchivo = rutaArchivo; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public Timestamp getFechaSubida() { return fechaSubida; }
    public void setFechaSubida(Timestamp fechaSubida) { this.fechaSubida = fechaSubida; }

    // ==========================================
    // toString() usando Google Guava
    // ==========================================
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("pedidoId", pedidoId)
                .add("nombreArchivo", nombreArchivo)
                .add("rutaArchivo", rutaArchivo)
                .add("tipo", tipo)
                .add("fechaSubida", fechaSubida)
                .toString();
    }
}
