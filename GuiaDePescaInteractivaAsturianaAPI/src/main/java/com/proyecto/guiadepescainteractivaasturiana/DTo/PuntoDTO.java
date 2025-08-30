package com.proyecto.guiadepescainteractivaasturiana.DTo;

import java.util.Objects;

public class PuntoDTO {

    private int idPunto;
    private double latitud;
    private double longitud;
    private String nombre;
    private String descripcion;


    public PuntoDTO(int idPunto, double latitud, double longitud, String nombre, String descripcion) {
        this.idPunto = idPunto;
        this.latitud = latitud;
        this.longitud = longitud;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PuntoDTO puntoDTO = (PuntoDTO) o;
        return nombre != null && nombre.equalsIgnoreCase(puntoDTO.nombre);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nombre);
    }

    public PuntoDTO() {

    }

    public int getIdPunto() {
        return idPunto;
    }

    public void setIdPunto(int idPunto) {
        this.idPunto = idPunto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }



}