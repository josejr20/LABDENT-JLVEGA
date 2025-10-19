package com.labdent.model;

import java.io.Serializable;

public class Reporte implements Serializable {
    private int totalPedidos;
    private int pedidosEnProceso;
    private int pedidosEntregados;
    private int pedidosAtrasados;
    private double tiempoPromedioEntrega;
    private double tasaCumplimiento;
    
    // Por estado
    private int pedidosRecepcion;
    private int pedidosParalelizado;
    private int pedidosDisenoCad;
    private int pedidosProduccionCam;
    private int pedidosCeramica;
    private int pedidosControlCalidad;
    private int pedidosListoEntrega;

    // Constructor
    public Reporte() {}

    // Getters y Setters
    public int getTotalPedidos() { return totalPedidos; }
    public void setTotalPedidos(int totalPedidos) { this.totalPedidos = totalPedidos; }

    public int getPedidosEnProceso() { return pedidosEnProceso; }
    public void setPedidosEnProceso(int pedidosEnProceso) { this.pedidosEnProceso = pedidosEnProceso; }

    public int getPedidosEntregados() { return pedidosEntregados; }
    public void setPedidosEntregados(int pedidosEntregados) { this.pedidosEntregados = pedidosEntregados; }

    public int getPedidosAtrasados() { return pedidosAtrasados; }
    public void setPedidosAtrasados(int pedidosAtrasados) { this.pedidosAtrasados = pedidosAtrasados; }

    public double getTiempoPromedioEntrega() { return tiempoPromedioEntrega; }
    public void setTiempoPromedioEntrega(double tiempoPromedioEntrega) { this.tiempoPromedioEntrega = tiempoPromedioEntrega; }

    public double getTasaCumplimiento() { return tasaCumplimiento; }
    public void setTasaCumplimiento(double tasaCumplimiento) { this.tasaCumplimiento = tasaCumplimiento; }

    public int getPedidosRecepcion() { return pedidosRecepcion; }
    public void setPedidosRecepcion(int pedidosRecepcion) { this.pedidosRecepcion = pedidosRecepcion; }

    public int getPedidosParalelizado() { return pedidosParalelizado; }
    public void setPedidosParalelizado(int pedidosParalelizado) { this.pedidosParalelizado = pedidosParalelizado; }

    public int getPedidosDisenoCad() { return pedidosDisenoCad; }
    public void setPedidosDisenoCad(int pedidosDisenoCad) { this.pedidosDisenoCad = pedidosDisenoCad; }

    public int getPedidosProduccionCam() { return pedidosProduccionCam; }
    public void setPedidosProduccionCam(int pedidosProduccionCam) { this.pedidosProduccionCam = pedidosProduccionCam; }

    public int getPedidosCeramica() { return pedidosCeramica; }
    public void setPedidosCeramica(int pedidosCeramica) { this.pedidosCeramica = pedidosCeramica; }

    public int getPedidosControlCalidad() { return pedidosControlCalidad; }
    public void setPedidosControlCalidad(int pedidosControlCalidad) { this.pedidosControlCalidad = pedidosControlCalidad; }

    public int getPedidosListoEntrega() { return pedidosListoEntrega; }
    public void setPedidosListoEntrega(int pedidosListoEntrega) { this.pedidosListoEntrega = pedidosListoEntrega; }
}