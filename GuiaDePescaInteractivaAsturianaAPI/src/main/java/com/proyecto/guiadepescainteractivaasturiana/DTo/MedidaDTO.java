package com.proyecto.guiadepescainteractivaasturiana.DTo;

public class MedidaDTO {
    private int idMedida;
    private String nombreComun;
    private String nombreCientifico;
    private String tallaMinima;
    private String imagen;
    private int idTipo;

    public MedidaDTO() {

    }

    public MedidaDTO(int idMedida, String nombreComun, String nombreCientifico,
                     String tallaMinima, String imagen, int idTipo) {
        this.idMedida = idMedida;
        this.nombreComun = nombreComun;
        this.nombreCientifico = nombreCientifico;
        this.tallaMinima = tallaMinima;
        this.imagen = imagen;
        this.idTipo = idTipo;
    }

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

    public int getIdTipo() {
        return idTipo;
    }

    public void setIdTipo(int idTipo) {
        this.idTipo = idTipo;
    }
}
