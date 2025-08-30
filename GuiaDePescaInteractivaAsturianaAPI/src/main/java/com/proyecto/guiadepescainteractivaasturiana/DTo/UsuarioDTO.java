package com.proyecto.guiadepescainteractivaasturiana.DTo;

import java.util.Date;

public class UsuarioDTO {

    private String nombre;
    private String correo;
    private String contrasena;
    private Date fechaNacimiento;


    public UsuarioDTO(String nombre, String correo, String contrasena, Date fechaNacimiento) {
        this.nombre = nombre;
        this.correo = correo;
        this.contrasena = contrasena;
        this.fechaNacimiento = fechaNacimiento;
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

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }
}