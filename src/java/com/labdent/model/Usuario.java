package com.labdent.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class Usuario implements Serializable {
    private int id;
    private String nombreCompleto;
    private String email;
    private String password;
    private String rol;
    private String telefono;
    private String direccion;
    private boolean activo;
    private Timestamp fechaRegistro;
    private Timestamp ultimaConexion;
    private String tokenJwt;
    private Timestamp tokenExpiracion;

    // Constructores
    public Usuario() {}

    public Usuario(String nombreCompleto, String email, String password, String rol) {
        this.nombreCompleto = nombreCompleto;
        this.email = email;
        this.password = password;
        this.rol = rol;
        this.activo = true;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public Timestamp getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(Timestamp fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public Timestamp getUltimaConexion() { return ultimaConexion; }
    public void setUltimaConexion(Timestamp ultimaConexion) { this.ultimaConexion = ultimaConexion; }

    public String getTokenJwt() { return tokenJwt; }
    public void setTokenJwt(String tokenJwt) { this.tokenJwt = tokenJwt; }

    public Timestamp getTokenExpiracion() { return tokenExpiracion; }
    public void setTokenExpiracion(Timestamp tokenExpiracion) { this.tokenExpiracion = tokenExpiracion; }

    // Métodos auxiliares
    public boolean isOdontologo() { return "ODONTOLOGO".equals(rol); }
    public boolean isAdmin() { return "ADMIN".equals(rol); }
    public boolean isTecnico() { return "TECNICO".equals(rol); }
    public boolean isCeramista() { return "CERAMISTA".equals(rol); }
    public boolean isDeliverista() { return "DELIVERISTA".equals(rol); }
    public boolean isCliente() { return "CLIENTE".equals(rol); }
}