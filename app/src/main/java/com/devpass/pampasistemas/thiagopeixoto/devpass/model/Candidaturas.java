package com.devpass.pampasistemas.thiagopeixoto.devpass.model;

import com.devpass.pampasistemas.thiagopeixoto.devpass.helper.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;

/**
 * Created by Thiago Peixoto
 */

public class Candidaturas {

   public Vagas vagas;
   public Usuario usuario;
   public int qtdCandidaturas = 0;
   public String Idvaga, nomeEmpresa, tituloVaga, cidadeEstado, salario, tipoVaga, descVaga, empresacFoto;
   public Float avaliacaoEmpresa;


    public Candidaturas() {
    }

    public void salvar(){

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        //Objeto usuario
        HashMap<String, Object> dadosUsuario = new HashMap<>();
        dadosUsuario.put( "cidade", getCidadeEstado());
        dadosUsuario.put("descricaoVaga", getDescVaga());
        dadosUsuario.put("salario", getSalario());
        dadosUsuario.put("tipoVaga", getTipoVaga());
        dadosUsuario.put("fotoUsuario", getEmpresacFoto() );
        dadosUsuario.put("tituloVaga", getTituloVaga());
        dadosUsuario.put("nomeUsuario", getNomeEmpresa());
        dadosUsuario.put("avaliacaoEmpresa", getAvaliacaoEmpresa());

        DatabaseReference pCurtidasRef = firebaseRef
                .child("candidaturas")
                .child(usuario.getId())
                .child( getIdvaga() ) ;//id_Vaga
        pCurtidasRef.setValue( dadosUsuario );

        //atualizar quantidade de curtidas
        atualizarQtd(1);

    }

    public void atualizarQtd(int valor){

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();

        DatabaseReference pCurtidasRef = firebaseRef
                .child("qtd-candidaturas")
                .child( getIdvaga() )//id_Vaga
                .child("qtdCandidaturas");
        setQtdCandidaturas( getQtdCandidaturas() + valor );
        pCurtidasRef.setValue( getQtdCandidaturas() );
    }

    public void remover(){

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();

        DatabaseReference pCurtidasRef = firebaseRef
                .child("candidaturas")
                .child( vagas.getIdVaga() )//id_vaga
                .child( usuario.getId() );//id_usuario_logado
        pCurtidasRef.removeValue();

        //atualizar quantidade de curtidas
        atualizarQtd(-1);

    }

    public int getQtdCandidaturas() {
        return qtdCandidaturas;
    }

    public void setQtdCandidaturas(int qtdCandidaturas) {
        this.qtdCandidaturas = qtdCandidaturas;
    }

    public Vagas getVagas() {
        return vagas;
    }

    public void setVagas(Vagas vagas) {
        this.vagas = vagas;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getIdvaga() {
        return Idvaga;
    }

    public void setIdvaga(String idvaga) {
        Idvaga = idvaga;
    }

    public String getNomeEmpresa() {
        return nomeEmpresa;
    }

    public void setNomeEmpresa(String nomeEmpresa) {
        this.nomeEmpresa = nomeEmpresa;
    }

    public String getTituloVaga() {
        return tituloVaga;
    }

    public void setTituloVaga(String tituloVaga) {
        this.tituloVaga = tituloVaga;
    }

    public String getCidadeEstado() {
        return cidadeEstado;
    }

    public void setCidadeEstado(String cidadeEstado) {
        this.cidadeEstado = cidadeEstado;
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

    public String getDescVaga() {
        return descVaga;
    }

    public void setDescVaga(String descVaga) {
        this.descVaga = descVaga;
    }

    public String getEmpresacFoto() {
        return empresacFoto;
    }

    public void setEmpresacFoto(String empresacFoto) {
        this.empresacFoto = empresacFoto;
    }

    public Float getAvaliacaoEmpresa() {
        return avaliacaoEmpresa;
    }

    public void setAvaliacaoEmpresa(Float avaliacaoEmpresa) {
        this.avaliacaoEmpresa = avaliacaoEmpresa;
    }
}

