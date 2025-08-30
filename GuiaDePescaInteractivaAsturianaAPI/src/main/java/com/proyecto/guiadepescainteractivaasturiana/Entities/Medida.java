package com.proyecto.guiadepescainteractivaasturiana.Entities;

import jakarta.persistence.*;

@Entity
@Table(name = "medidas")
public class Medida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idMedida;

    @Column(name = "nombre_comun")
    private String nombreComun;

    @Column(name = "nombre_cientifico")
    private String nombreCientifico;

    @Column(name = "talla_minima")
    private String tallaMinima;

    private String imagen;

    @ManyToOne
    @JoinColumn(name = "id_tipo", nullable = false)
    private TipoAnimal tipoAnimal;

    public int getIdMedida() {
        return idMedida;
    }

    public void setIdMedida(int idMedida) {
        this.idMedida = idMedida;
    }

    public String getNombreComun() {
        return nombreComun;
    }

    public void setNombreComun(String nombreComun) {
        this.nombreComun = nombreComun;
    }

    public String getNombreCientifico() {
        return nombreCientifico;
    }

    public void setNombreCientifico(String nombreCientifico) {
        this.nombreCientifico = nombreCientifico;
    }

    public String getTallaMinima() {
        return tallaMinima;
    }

    public void setTallaMinima(String tallaMinima) {
        this.tallaMinima = tallaMinima;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public TipoAnimal getTipoAnimal() {
        return tipoAnimal;
    }

    public void setTipoAnimal(TipoAnimal tipoAnimal) {
        this.tipoAnimal = tipoAnimal;
    }
}
