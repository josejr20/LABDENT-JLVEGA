package com.labdent.model;

import java.sql.Timestamp;
import com.google.common.base.MoreObjects; // ← Librería de Google Guava

public class PedidoHistorial {
    private int id;
    private int pedidoId;
    private String estado;
    private Timestamp fechaHora;
    private String comentario;

    // Getters y Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPedidoId() {
        return pedidoId;
    }

    public void setPedidoId(int pedidoId) {
        this.pedidoId = pedidoId;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Timestamp getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(Timestamp fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }
    

    // ==========================================
    // toString() con Google Guava
    // ==========================================
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("pedidoId", pedidoId)
                .add("estado", estado)
                .add("fechaHora", fechaHora)
                .add("comentario", comentario)
                .toString();
    }
}
