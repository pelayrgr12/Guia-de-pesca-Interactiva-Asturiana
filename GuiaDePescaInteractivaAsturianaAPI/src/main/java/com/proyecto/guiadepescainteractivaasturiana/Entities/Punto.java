package com.proyecto.guiadepescainteractivaasturiana.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "puntos")
public class Punto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idPunto;
    private double latitud;
    private double longitud;
    private String nombre;
    private String descripcion;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    @JsonIgnore
    private Usuario usuario;

    @OneToMany(mappedBy = "punto", cascade = CascadeType.ALL)
    private List<HistorialPunto> historialPuntos;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public int getIdPunto() {
        return idPunto;
    }

    public void setIdPunto(int idPunto) {
        this.idPunto = idPunto;
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

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public List<HistorialPunto> getHistorialPuntos() {
        return historialPuntos;
    }

    public void setHistorialPuntos(List<HistorialPunto> historialPuntos) {
        this.historialPuntos = historialPuntos;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
