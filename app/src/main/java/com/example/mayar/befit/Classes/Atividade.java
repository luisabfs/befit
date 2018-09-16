package com.example.mayar.befit.Classes;

/**
 * Created by lylly on 10/05/2018.
 */

public class Atividade {
    private String cod_atv;
    private int tipo_atv;
    private String data_atv;
    private String local_atv;
    private String horario_inicio_atv;
    private String horario_fim_atv;
    private String descricao_atv;
    private String titulo_atv;
    private String endereco_atv;
    private double latitude_atv;
    private double longitude_atv;
    private int qparticipante_atv;
    private double avaliacao_atv;
    private int status_atv;
    private String cod_usu_atv;
    private String url_modalidade_atv;

    public Atividade(){
    }

    public Atividade(String cod_atv, int tipo_atv, String data_atv, String local_atv, String horario_inicio_atv, String horario_fim_atv, String descricao_atv, String titulo_atv, String endereco_atv, int qparticipante_atv, double avaliacao_atv, int status_atv, String cod_usu_atv, String url_modalidade_atv) {
        setCod_atv(cod_atv);
        setTipo_atv(tipo_atv);
        setData_atv(data_atv);
        setLocal_atv(local_atv);
        setHorario_inicio_atv(horario_inicio_atv);
        setHorario_fim_atv(horario_fim_atv);
        setDescricao_atv(descricao_atv);
        setTitulo_atv(titulo_atv);
        setEndereco_atv(endereco_atv);
        setQparticipante_atv(qparticipante_atv);
        setAvaliacao_atv(avaliacao_atv);
        setStatus_atv(status_atv);
        setCod_usu_atv(cod_usu_atv);
        setUrl_modalidade_atv(url_modalidade_atv);
    }

    public String getCod_atv() {
        return cod_atv;
    }

    public void setCod_atv(String cod_atv) {
        this.cod_atv = cod_atv;
    }

    public int getTipo_atv() {
        return tipo_atv;
    }

    public void setTipo_atv(int tipo_atv) {
        this.tipo_atv = tipo_atv;
    }

    public String getData_atv() {
        return data_atv;
    }

    public void setData_atv(String data_atv) {
        this.data_atv = data_atv;
    }

    public String getLocal_atv() {
        return local_atv;
    }

    public void setLocal_atv(String local_atv) {
        this.local_atv = local_atv;
    }

    public void setHorario_fim_atv(String horario_fim_atv) {
        this.horario_fim_atv = horario_fim_atv;
    }

    public String getHorario_fim_atv() {
        return horario_fim_atv;
    }

    public void setHorario_inicio_atv(String horario_inicio_atv) {
        this.horario_inicio_atv = horario_inicio_atv;
    }

    public String getHorario_inicio_atv() {
        return horario_inicio_atv;
    }

    public String getDescricao_atv() {
        return descricao_atv;
    }

    public void setDescricao_atv(String descricao_atv) {
        this.descricao_atv = descricao_atv;
    }

    public String getTitulo_atv() {
        return titulo_atv;
    }

    public void setTitulo_atv(String titulo_atv) {
        this.titulo_atv = titulo_atv;
    }

    public String getEndereco_atv() {
        return endereco_atv;
    }

    public void setEndereco_atv(String endereco_atv) {
        this.endereco_atv = endereco_atv;
    }

    public double getLatitude_atv() {
        return latitude_atv;
    }

    public void setLatitude_atv(double latitude_atv) {
        this.latitude_atv = latitude_atv;
    }

    public double getLongitude_atv() {
        return longitude_atv;
    }

    public void setLongitude_atv(double longitude_atv) {
        this.longitude_atv = longitude_atv;
    }

    public int getQparticipante_atv() {
        return qparticipante_atv;
    }

    public void setQparticipante_atv(int qparticipante_atv) {
        this.qparticipante_atv = qparticipante_atv;
    }

    public double getAvaliacao_atv() {
        return avaliacao_atv;
    }

    public void setAvaliacao_atv(double avaliacao_atv) {
        this.avaliacao_atv = avaliacao_atv;
    }

    public int getStatus_atv() {
        return status_atv;
    }

    public void setStatus_atv(int status_atv) {
        this.status_atv = status_atv;
    }

    public String getCod_usu_atv() {
        return cod_usu_atv;
    }

    public void setCod_usu_atv(String cod_usu_atv) {
        this.cod_usu_atv = cod_usu_atv;
    }

    public String getUrl_modalidade_atv() {
        return url_modalidade_atv;
    }

    public void setUrl_modalidade_atv(String url_modalidade_atv) {
        this.url_modalidade_atv = url_modalidade_atv;
    }
}
