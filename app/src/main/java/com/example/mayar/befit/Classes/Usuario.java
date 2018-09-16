package com.example.mayar.befit.Classes;

public class Usuario {

    private String cod_usu;
    private String email_usu;
    private String senha_usu;
    private String nome_usu;
    private String idade_usu;
    private String sexo_usu;
    private static String tipo_usu;
    private String url_foto_usu;
    private String estado_usu;
    private String cidade_usu;
    private String celular_usu;

    public Usuario() {
    }

    public Usuario(String cod_usu, String email_usu, String senha_usu, String nome_usu, String idade_usu, String sexo_usu, String tipo_usu, String url_foto_usu, String estado_usu, String cidade_usu, String celular_usu) {
        setCod_usu(cod_usu);
        setEmail_usu(email_usu);
        setSenha_usu(senha_usu);
        setNome_usu(nome_usu);
        setIdade_usu(idade_usu);
        setSexo_usu(sexo_usu);
        setTipo_usu(tipo_usu);
        setUrl_foto_usu(url_foto_usu);
        setEstado_usu(estado_usu);
        setCidade_usu(cidade_usu);
        setCelular_usu(celular_usu);
    }

    public String getCod_usu() {
        return cod_usu;
    }

    public void setCod_usu(String cod_usu) {
        this.cod_usu = cod_usu;
    }

    public String getEmail_usu() {
        return email_usu;
    }

    public void setEmail_usu(String email_usu) {
        this.email_usu = email_usu;
    }

    public String getSenha_usu() {
        return senha_usu;
    }

    public void setSenha_usu(String senha_usu) {
        this.senha_usu = senha_usu;
    }

    public String getNome_usu() {
        return nome_usu;
    }

    public void setNome_usu(String nome_usu) {
        this.nome_usu = nome_usu;
    }

    public String getIdade_usu() {
        return idade_usu;
    }

    public void setIdade_usu(String idade_usu) {
        this.idade_usu = idade_usu;
    }

    public String getSexo_usu() {
        return sexo_usu;
    }

    public void setSexo_usu(String sexo_usu) {
        this.sexo_usu = sexo_usu;
    }

    public String getTipo_usu() {
        return tipo_usu;
    }

    public void setTipo_usu(String tipo_usu) {
        this.tipo_usu = tipo_usu;
    }

    public String getUrl_foto_usu() {
        return url_foto_usu;
    }

    public void setUrl_foto_usu(String url_foto_usu) {
        this.url_foto_usu = url_foto_usu;
    }

    public String getEstado_usu() {
        return estado_usu;
    }

    public void setEstado_usu(String estado_usu) {
        this.estado_usu = estado_usu;
    }

    public String getCidade_usu() {
        return cidade_usu;
    }

    public void setCidade_usu(String cidade_usu) {
        this.cidade_usu = cidade_usu;
    }

    public String getCelular_usu() {
        return celular_usu;
    }

    public void setCelular_usu(String celular_usu) {
        this.celular_usu = celular_usu;
    }
}

