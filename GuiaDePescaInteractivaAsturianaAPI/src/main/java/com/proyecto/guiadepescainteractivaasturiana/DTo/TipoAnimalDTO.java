package com.proyecto.guiadepescainteractivaasturiana.DTo;

public class TipoAnimalDTO {

    private int idtipo;
    private String nombre;

    public TipoAnimalDTO(int idtipo, String nombre) {
        this.idtipo = idtipo;
        this.nombre = nombre;
    }

    public TipoAnimalDTO() {

    }

    public int getIdtipo() {
        return idtipo;
    }

    public void setIdtipo(int id_tipo) {
        this.idtipo = id_tipo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
