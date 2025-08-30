package com.proyecto.guiadepescainteractivaasturiana.DTo;

public class ImagenHistorialDTo {
    private int id;
    private String nombre;

    public ImagenHistorialDTo() {}

    public ImagenHistorialDTo(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
