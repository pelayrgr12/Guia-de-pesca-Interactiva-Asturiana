package com.proyecto.guiadepescainteractivaasturiana.DTo;

import java.sql.Timestamp;
import java.util.List;

public class HistorialPuntoDTO {
    private Integer idHistorial;

    private int idPunto;
    private Timestamp fecha;
    private String descripcion;
    private List<ImagenHistorialDTo> imagenes;

    public HistorialPuntoDTO() {}


    public HistorialPuntoDTO(Integer idHistorial, int idPunto, Timestamp fecha, String descripcion, List<ImagenHistorialDTo> imagenes) {
        this.idHistorial = idHistorial;
        this.idPunto = idPunto;
        this.fecha = fecha;
        this.descripcion = descripcion;
        this.imagenes = imagenes;
    }

    public List<ImagenHistorialDTo> getImagenes() {
        return imagenes;
    }

    public void setImagenes(List<ImagenHistorialDTo> imagenes) {
        this.imagenes = imagenes;
    }

    public Timestamp getFecha() {
        return fecha;
    }

    public void setFecha(Timestamp fecha) {
        this.fecha = fecha;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getIdPunto() {
        return idPunto;
    }

    public void setIdPunto(int idPunto) {
        this.idPunto = idPunto;
    }



    public Integer getIdHistorial() {
        return idHistorial;
    }

    public void setIdHistorial(Integer idHistorial) {
        this.idHistorial = idHistorial;
    }

}