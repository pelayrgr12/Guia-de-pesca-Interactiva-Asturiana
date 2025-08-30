package com.proyecto.guiadepescainteractivaasturiana.DTo;

import java.util.Date;

public class UsuarioListDTO {

    private int id;
    private String nombre;
    private String correo;
    private Date fechaNacimiento;

    private boolean habilitado;

    public UsuarioListDTO(int id, String nombre, String correo, Date fechaNacimiento, boolean habilitado) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.fechaNacimiento = fechaNacimiento;
        this.habilitado = habilitado;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public Date getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(Date fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public boolean isHabilitado() {
        return habilitado;
    }

    public void setHabilitado(boolean habilitado) {
        this.habilitado = habilitado;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
