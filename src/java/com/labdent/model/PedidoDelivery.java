package com.labdent.model;

import java.sql.Timestamp;

public class PedidoDelivery {
    private int id;
    private int pedidoId;
    private int deliveristaId;
    private String estadoDelivery;
    private Timestamp fechaSalida;
    private Timestamp fechaLlegada;
    private Timestamp fechaEntrega;
    private String observaciones;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    
    // Datos relacionados
    private String codigoPedido;
    private String nombrePaciente;
    private String nombreDeliverista;

    // Constructor
    public PedidoDelivery() {}

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getPedidoId() { return pedidoId; }
    public void setPedidoId(int pedidoId) { this.pedidoId = pedidoId; }

    public int getDeliveristaId() { return deliveristaId; }
    public void setDeliveristaId(int deliveristaId) { this.deliveristaId = deliveristaId; }

    public String getEstadoDelivery() { return estadoDelivery; }
    public void setEstadoDelivery(String estadoDelivery) { this.estadoDelivery = estadoDelivery; }

    public Timestamp getFechaSalida() { return fechaSalida; }
    public void setFechaSalida(Timestamp fechaSalida) { this.fechaSalida = fechaSalida; }

    public Timestamp getFechaLlegada() { return fechaLlegada; }
    public void setFechaLlegada(Timestamp fechaLlegada) { this.fechaLlegada = fechaLlegada; }

    public Timestamp getFechaEntrega() { return fechaEntrega; }
    public void setFechaEntrega(Timestamp fechaEntrega) { this.fechaEntrega = fechaEntrega; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    public String getCodigoPedido() { return codigoPedido; }
    public void setCodigoPedido(String codigoPedido) { this.codigoPedido = codigoPedido; }

    public String getNombrePaciente() { return nombrePaciente; }
    public void setNombrePaciente(String nombrePaciente) { this.nombrePaciente = nombrePaciente; }

    public String getNombreDeliverista() { return nombreDeliverista; }
    public void setNombreDeliverista(String nombreDeliverista) { this.nombreDeliverista = nombreDeliverista; }

    // Método helper para obtener descripción del estado
    public String getEstadoDescripcion() {
        if (estadoDelivery == null) return "";
        
        switch (estadoDelivery) {
            case "SALIO_EMPRESA": return "Salió de la Empresa";
            case "EN_CURSO": return "En Curso";
            case "LLEGO_DESTINO": return "Llegó a Destino";
            case "PEDIDO_ENTREGADO": return "Pedido Entregado";
            default: return estadoDelivery;
        }
    }

    // Método para obtener icono según estado
    public String getIconoEstado() {
        if (estadoDelivery == null) return "📦";
        
        switch (estadoDelivery) {
            case "SALIO_EMPRESA": return "🚪";
            case "EN_CURSO": return "🚚";
            case "LLEGO_DESTINO": return "📍";
            case "PEDIDO_ENTREGADO": return "✅";
            default: return "📦";
        }
    }
}