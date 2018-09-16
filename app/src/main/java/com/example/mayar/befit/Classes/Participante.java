package com.example.mayar.befit.Classes;

/**
 * Created by lylly on 03/08/2018.
 */

public class Participante {
    private String cod_parti;
    private String cod_usu_parti;
    private String cod_atv_parti;
    private int id_parti;
    private int status_parti;
    private int tipo_parti;
    private float nota_parti;

    public String getCod_parti() {

        return cod_parti;
    }

    public void setCod_parti(String cod_parti) {
        this.cod_parti = cod_parti;
    }

    public String getCod_usu_parti() {
        return cod_usu_parti;
    }

    public void setCod_usu_parti(String cod_usu_parti) {
        this.cod_usu_parti = cod_usu_parti;
    }

    public String getCod_atv_parti() {
        return cod_atv_parti;
    }

    public void setCod_atv_parti(String cod_atv_parti) {
        this.cod_atv_parti = cod_atv_parti;
    }

    public int getId_parti() {
        return id_parti;
    }

    public void setId_parti(int id_parti) {
        this.id_parti = id_parti;
    }

    public int getStatus_parti() {
        return status_parti;
    }

    public void setStatus_parti(int status_parti) {
        this.status_parti = status_parti;
    }

    public int getTipo_parti() {
        return tipo_parti;
    }

    public void setTipo_parti(int tipo_parti) {
        this.tipo_parti = tipo_parti;
    }

    public float getNota_parti() {
        return nota_parti;
    }

    public void setNota_parti(float nota_parti) {
        this.nota_parti = nota_parti;
    }
}
