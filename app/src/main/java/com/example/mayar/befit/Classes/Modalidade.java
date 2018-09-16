package com.example.mayar.befit.Classes;

/**
 * Created by lylly on 10/05/2018.
 */

public class Modalidade {
    private String cod_mod;
    private String nome_mod;
    private boolean status_mod;
    private String descricao_mod;
    private String url_icone_mod;

    public Modalidade(){
    }

    public Modalidade(String cod_mod, String nome_mod, boolean status_mod, String descricao_mod, String url_icone_mod) {
        setCod_mod(cod_mod);
        setNome_mod(nome_mod);
        setStatus_mod(status_mod);
        setDescricao_mod(descricao_mod);
        setUrl_icone_mod(url_icone_mod);
    }

    public String getCod_mod() {
        return cod_mod;
    }

    public void setCod_mod(String cod_mod) {
        this.cod_mod = cod_mod;
    }

    public String getNome_mod() {
        return nome_mod;
    }

    public void setNome_mod(String nome_mod) {
        this.nome_mod = nome_mod;
    }

    public boolean isStatus_mod() {
        return status_mod;
    }

    public void setStatus_mod(boolean status_mod) {
        this.status_mod = status_mod;
    }

    public String getDescricao_mod() {
        return descricao_mod;
    }

    public void setDescricao_mod(String descricao_mod) {
        this.descricao_mod = descricao_mod;
    }

    public String getUrl_icone_mod() {
        return url_icone_mod;
    }

    public void setUrl_icone_mod(String url_icone_mod) {
        this.url_icone_mod = url_icone_mod;
    }
}
