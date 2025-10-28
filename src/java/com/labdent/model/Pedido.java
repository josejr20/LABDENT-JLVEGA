package com.labdent.model;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;

public class Pedido implements Serializable {

    private int id;
    private String codigoUnico;
    private int odontologoId;
    private int UsuarioId;
    private String nombrePaciente;
    private String piezasDentales;
    private String tipoProtesis;
    private String material;
    private String colorShade;
    private Timestamp fechaIngreso;
    private Date fechaCompromiso;
    private Timestamp fechaEntrega;
    private String estadoActual;
    private String prioridad;
    private String observaciones;
    private String archivoAdjunto;
    private Integer responsableActual;

    // Datos relacionados
    private String nombreOdontologo;
    private String emailOdontologo;
    private String telefonoOdontologo;
    private String nombreResponsable;

    // Constructor
    public Pedido() {
        this.fechaIngreso = new Timestamp(System.currentTimeMillis());
        this.estadoActual = "RECEPCION";
        this.prioridad = "NORMAL";
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCodigoUnico() {
        return codigoUnico;
    }

    public void setCodigoUnico(String codigoUnico) {
        this.codigoUnico = codigoUnico;
    }

    public int getOdontologoId() {
        return odontologoId;
    }

    public void setOdontologoId(int odontologoId) {
        this.odontologoId = odontologoId;
    }

    public int getUsuarioId() {
        return UsuarioId;
    }

    public void setUsuarioId(int UsuarioId) {
        this.UsuarioId = UsuarioId;
    }

    public String getNombrePaciente() {
        return nombrePaciente;
    }

    public void setNombrePaciente(String nombrePaciente) {
        this.nombrePaciente = nombrePaciente;
    }

    public String getPiezasDentales() {
        return piezasDentales;
    }

    public void setPiezasDentales(String piezasDentales) {
        this.piezasDentales = piezasDentales;
    }

    public String getTipoProtesis() {
        return tipoProtesis;
    }

    public void setTipoProtesis(String tipoProtesis) {
        this.tipoProtesis = tipoProtesis;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getColorShade() {
        return colorShade;
    }

    public void setColorShade(String colorShade) {
        this.colorShade = colorShade;
    }

    public Timestamp getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(Timestamp fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public Date getFechaCompromiso() {
        return fechaCompromiso;
    }

    public void setFechaCompromiso(Date fechaCompromiso) {
        this.fechaCompromiso = fechaCompromiso;
    }

    public Timestamp getFechaEntrega() {
        return fechaEntrega;
    }

    public void setFechaEntrega(Timestamp fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
    }

    public String getEstadoActual() {
        return estadoActual;
    }

    public void setEstadoActual(String estadoActual) {
        this.estadoActual = estadoActual;
    }

    public String getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(String prioridad) {
        this.prioridad = prioridad;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getArchivoAdjunto() {
        return archivoAdjunto;
    }

    public void setArchivoAdjunto(String archivoAdjunto) {
        this.archivoAdjunto = archivoAdjunto;
    }

    public Integer getResponsableActual() {
        return responsableActual;
    }

    public void setResponsableActual(Integer responsableActual) {
        this.responsableActual = responsableActual;
    }

    public String getNombreOdontologo() {
        return nombreOdontologo;
    }

    public void setNombreOdontologo(String nombreOdontologo) {
        this.nombreOdontologo = nombreOdontologo;
    }

    public String getEmailOdontologo() {
        return emailOdontologo;
    }

    public void setEmailOdontologo(String emailOdontologo) {
        this.emailOdontologo = emailOdontologo;
    }

    public String getTelefonoOdontologo() {
        return telefonoOdontologo;
    }

    public void setTelefonoOdontologo(String telefonoOdontologo) {
        this.telefonoOdontologo = telefonoOdontologo;
    }

    public String getNombreResponsable() {
        return nombreResponsable;
    }

    public void setNombreResponsable(String nombreResponsable) {
        this.nombreResponsable = nombreResponsable;
    }

    // Métodos auxiliares
    public String getEstadoDescripcion() {
        switch (estadoActual) {
            case "RECEPCION":
                return "Recepción y Validación";
            case "PARALELIZADO":
                return "Paralelizado";
            case "DISENO_CAD":
                return "Diseño CAD/CAM";
            case "PRODUCCION_CAM":
                return "Producción CAM";
            case "CERAMICA":
                return "Aplicación de Cerámica";
            case "CONTROL_CALIDAD":
                return "Control de Calidad";
            case "LISTO_ENTREGA":
                return "Listo para Entrega";
            case "ENTREGADO":
                return "Entregado";
            default:
                return estadoActual;
        }
    }

    public String getColorEstado() {
        switch (estadoActual) {
            case "RECEPCION":
                return "#3498db";
            case "PARALELIZADO":
                return "#9b59b6";
            case "DISENO_CAD":
                return "#1abc9c";
            case "PRODUCCION_CAM":
                return "#f39c12";
            case "CERAMICA":
                return "#e74c3c";
            case "CONTROL_CALIDAD":
                return "#16a085";
            case "LISTO_ENTREGA":
                return "#27ae60";
            case "ENTREGADO":
                return "#2ecc71";
            default:
                return "#95a5a6";
        }
    }

    public boolean isAtrasado() {
        if (fechaCompromiso == null || "ENTREGADO".equals(estadoActual)) {
            return false;
        }
        return new java.util.Date().after(fechaCompromiso);
    }

    public int getDiasRestantes() {
        if (fechaCompromiso == null || "ENTREGADO".equals(estadoActual)) {
            return 0;
        }
        java.util.Date hoy = new java.util.Date();
        long diferencia = fechaCompromiso.getTime() - hoy.getTime();
        return (int) (diferencia / (1000 * 60 * 60 * 24));
    }
}
