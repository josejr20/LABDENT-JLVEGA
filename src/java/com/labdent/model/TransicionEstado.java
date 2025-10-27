package com.labdent.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class TransicionEstado implements Serializable {
    private int id;
    private int pedidoId;
    private String estadoAnterior;
    private String estadoNuevo;
    private int usuarioId;
    private String observaciones;
    private boolean checklistCompletado;
    private Integer tiempoEnEstado;
    private Timestamp fechaTransicion;
    
    // Datos relacionados
    private String nombreUsuario;
    private String codigoPedido;

    // Constructor
    public TransicionEstado() {}

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getPedidoId() { return pedidoId; }
    public void setPedidoId(int pedidoId) { this.pedidoId = pedidoId; }

    public String getEstadoAnterior() { return estadoAnterior; }
    public void setEstadoAnterior(String estadoAnterior) { this.estadoAnterior = estadoAnterior; }

    public String getEstadoNuevo() { return estadoNuevo; }
    public void setEstadoNuevo(String estadoNuevo) { this.estadoNuevo = estadoNuevo; }

    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public boolean isChecklistCompletado() { return checklistCompletado; }
    public void setChecklistCompletado(boolean checklistCompletado) { this.checklistCompletado = checklistCompletado; }

    public Integer getTiempoEnEstado() { return tiempoEnEstado; }
    public void setTiempoEnEstado(Integer tiempoEnEstado) { this.tiempoEnEstado = tiempoEnEstado; }

    public Timestamp getFechaTransicion() { return fechaTransicion; }
    public void setFechaTransicion(Timestamp fechaTransicion) { this.fechaTransicion = fechaTransicion; }

    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }

    public String getCodigoPedido() { return codigoPedido; }
    public void setCodigoPedido(String codigoPedido) { this.codigoPedido = codigoPedido; }
    
    // Método helper para obtener descripción del estado
    public String getEstadoNuevoDescripcion() {
        if (estadoNuevo == null) return "";
        
        switch (estadoNuevo) {
            case "RECEPCION": return "Recepción";
            case "PARALELIZADO": return "Paralelizado";
            case "DISENO_CAD": return "Diseño CAD";
            case "PRODUCCION_CAM": return "Producción CAM";
            case "CERAMICA": return "Cerámica";
            case "CONTROL_CALIDAD": return "Control de Calidad";
            case "LISTO_ENTREGA": return "Listo para Entrega";
            case "ENTREGADO": return "Entregado";
            default: return estadoNuevo;
        }
    }
}