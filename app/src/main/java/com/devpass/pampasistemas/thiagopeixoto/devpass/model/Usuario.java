package com.devpass.pampasistemas.thiagopeixoto.devpass.model;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.devpass.pampasistemas.thiagopeixoto.devpass.helper.ConfiguracaoFirebase;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Thiago Peixoto
 */

public class Usuario implements Serializable {

    private String id;
    private String nome;
    private String moderador;
    private String email;
    private String senha;
    private String caminhoFoto;
    private String biografia;
    private Float avaliation;
    private int seguidores = 0;
    private int seguindo = 0;
    private int postagens = 0;
    private int boostFire = 0;
    private String empresa;
    private String userStatus;


    public Usuario() {
    }

    public void salvar(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference usuariosRef = firebaseRef.child("usuarios").child( getId() );
        usuariosRef.setValue( this );
    }


    public void atualizarQtdBoostFire( int valor){

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference usuariosRef = firebaseRef
                .child("usuarios")
                .child( getId() );

        HashMap<String, Object> dados = new HashMap<>();
        dados.put("boostFire:", getBoostFire() - valor );

        usuariosRef.updateChildren( dados );

    }

    public void salvarBoostFire(){

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();



        DatabaseReference pGBoostRef = firebaseRef
                .child("usuario")
                .child( getId() )//id_postagem
                .child("boostFire");

        //atualizar quantidade de curtidas
        atualizarQtdBoostFire(1);

    }

    public void atualizarQtdPostagem(){

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference usuariosRef = firebaseRef
                .child("usuarios")
                .child( getId() );

        HashMap<String, Object> dados = new HashMap<>();
        dados.put("postagens", getPostagens() );

        usuariosRef.updateChildren( dados );

    }

    public void atualizar(){

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();

        Map objeto = new HashMap();
        objeto.put("/usuarios/" + getId() + "/nome", getNome() );
        objeto.put("/usuarios/" + getId() + "/biografia", getBiografia() );
        objeto.put("/usuarios/" + getId() + "/caminhoFoto", getCaminhoFoto() );

        firebaseRef.updateChildren( objeto );

    }

    public void atualizarBoostFire(){

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        Map objeto = new HashMap();
        objeto.put("/usuarios/" + getId() + "/boostFire", getBoostFire() - 1 );


        firebaseRef.updateChildren( objeto );

    }

    public Map<String, Object> converterParaMap(){

        HashMap<String, Object> usuarioMap = new HashMap<>();
        usuarioMap.put("email", getEmail() );
        usuarioMap.put("nome", getNome() );
        usuarioMap.put("id", getId() );
        usuarioMap.put("caminhoFoto", getCaminhoFoto() );
        usuarioMap.put("seguidores", getSeguidores() );
        usuarioMap.put("seguindo", getSeguindo() );
        usuarioMap.put("postagens", getPostagens() );
        usuarioMap.put("biografia", getBiografia());
        usuarioMap.put("boostFire", getBoostFire());
        usuarioMap.put("userStatus", getUserStatus());

        return usuarioMap;

    }

    public int getBoostFire() {
        return boostFire;
    }

    public void setBoostFire(int boostFire) {
        this.boostFire = boostFire;
    }

    public String getBiografia() {
        return biografia;
    }

    public void setBiografia(String biografia) {
        this.biografia = biografia;
    }

    public int getSeguidores() {
        return seguidores;
    }

    public void setSeguidores(int seguidores) {
        this.seguidores = seguidores;
    }

    public int getSeguindo() {
        return seguindo;
    }

    public void setSeguindo(int seguindo) {
        this.seguindo = seguindo;
    }

    public int getPostagens() {
        return postagens;
    }

    public void setPostagens(int postagens) {
        this.postagens = postagens;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome.toUpperCase();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Exclude
    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getCaminhoFoto() {
        return caminhoFoto;
    }

    public void setCaminhoFoto(String caminhoFoto) {
        this.caminhoFoto = caminhoFoto;
    }

    public String getModerador() {
        return moderador;
    }

    public void setModerador(String moderador) {
        this.moderador = moderador;
    }

    public String getEmpresa() {
        return empresa;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }

    public Float getAvaliation() {
        return avaliation;
    }

    public void setAvaliation(Float avaliation) {
        this.avaliation = avaliation;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }
}
