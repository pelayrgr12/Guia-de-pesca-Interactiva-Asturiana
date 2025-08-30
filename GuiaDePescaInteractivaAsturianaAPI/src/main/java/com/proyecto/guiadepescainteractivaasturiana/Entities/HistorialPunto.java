package com.proyecto.guiadepescainteractivaasturiana.Entities;

import jakarta.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "historial_puntos")
public class HistorialPunto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idHistorial;

    private Timestamp fecha;
    private String descripcion;

    @OneToMany(mappedBy = "historial", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ImagenHistorial> imagenes;

    @ManyToOne
    @JoinColumn(name = "id_punto", nullable = false)
    private Punto punto;

    // Getters y Setters

    public int getIdHistorial() {
        return idHistorial;
    }

    public void setIdHistorial(int idHistorial) {
        this.idHistorial = idHistorial;
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

    public List<ImagenHistorial> getImagenes() {
        return imagenes;
    }

    public void setImagenes(List<ImagenHistorial> imagenes) {
        this.imagenes = imagenes;
    }

    public Punto getPunto() {
        return punto;
    }

    public void setPunto(Punto punto) {
        this.punto = punto;
    }
}
