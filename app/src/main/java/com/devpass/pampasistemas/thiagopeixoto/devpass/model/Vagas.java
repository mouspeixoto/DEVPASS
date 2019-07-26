package com.devpass.pampasistemas.thiagopeixoto.devpass.model;

import com.devpass.pampasistemas.thiagopeixoto.devpass.helper.ConfiguracaoFirebase;
import com.devpass.pampasistemas.thiagopeixoto.devpass.helper.UsuarioFirebase;
import com.google.firebase.database.DatabaseReference;

public class Vagas {

    private String idVaga;
    private String tituloVaga;
    private String estadod;
    private String cidade;
    private String local;
    private String salario;
    private String tipoVaga;
    private String cargo;
    private String descricaoVaga;
    private String qtdCandidaturas;
    private Float avaliacaoEmpresa;

    private String nomeUsuario;
    private String fotoUsuario;


    public Vagas() {
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference vagasRef = firebaseRef.child("vagas");
        String idVagas = vagasRef.push().getKey();
        setIdVaga( idVagas );
    }

    public boolean salvar(){

        Usuario usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference vagasRef = firebaseRef.child("vagas")
             //   .child( usuarioLogado.getId())
                .child( getIdVaga() );
        vagasRef.setValue(this);

        DatabaseReference firebaseCandRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference candRef = firebaseCandRef.child("candidaturas")
                .child( getIdVaga() );
        candRef.setValue(this);

        return true;

    }


    public String getIdVaga() {
        return idVaga;
    }

    public void setIdVaga(String idVaga) {
        this.idVaga = idVaga;
    }

    public String getTituloVaga() {
        return tituloVaga;
    }

    public void setTituloVaga(String tituloVaga) {
        this.tituloVaga = tituloVaga;
    }

    public String getEstadod() {
        return estadod;
    }

    public void setEstadod(String estadod) {
        this.estadod = estadod;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public String getSalario() {
        return salario;
    }

    public void setSalario(String salario) {
        this.salario = salario;
    }

    public String getTipoVaga() {
        return tipoVaga;
    }

    public void setTipoVaga(String tipoVaga) {
        this.tipoVaga = tipoVaga;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getDescricaoVaga() {
        return descricaoVaga;
    }

    public void setDescricaoVaga(String descricaoVaga) {
        this.descricaoVaga = descricaoVaga;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    public String getFotoUsuario() {
        return fotoUsuario;
    }

    public void setFotoUsuario(String fotoUsuario) {
        this.fotoUsuario = fotoUsuario;
    }

    public Float getAvaliacaoEmpresa() {
        return avaliacaoEmpresa;
    }

    public void setAvaliacaoEmpresa(Float avaliacaoEmpresa) {
        this.avaliacaoEmpresa = avaliacaoEmpresa;
    }
}
