package com.proyecto.guiadepescainteractivaasturiana.Entities;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "tipo_animal")
public class TipoAnimal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idTipo;

    private String nombre;

    @OneToMany(mappedBy = "tipoAnimal")
    private List<Medida> medidas;

    public int getIdTipo() {
        return idTipo;
    }

    public void setIdTipo(int idTipo) {
        this.idTipo = idTipo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public List<Medida> getMedidas() {
        return medidas;
    }

    public void setMedidas(List<Medida> medidas) {
        this.medidas = medidas;
    }
}
