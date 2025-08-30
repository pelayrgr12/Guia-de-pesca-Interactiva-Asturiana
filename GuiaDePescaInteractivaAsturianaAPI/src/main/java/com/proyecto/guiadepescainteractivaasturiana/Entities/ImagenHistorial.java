package com.proyecto.guiadepescainteractivaasturiana.Entities;

import jakarta.persistence.*;

@Entity
@Table(name = "imagenes_historial")
public class ImagenHistorial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String nombre;

    @ManyToOne
    @JoinColumn(name = "id_historial", nullable = false)
    private HistorialPunto historial;

    // Getters y setters
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

    public HistorialPunto getHistorial() {
        return historial;
    }

    public void setHistorial(HistorialPunto historial) {
        this.historial = historial;
    }
}
