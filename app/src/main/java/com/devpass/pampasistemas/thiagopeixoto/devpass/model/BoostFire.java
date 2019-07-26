package com.devpass.pampasistemas.thiagopeixoto.devpass.model;

import com.devpass.pampasistemas.thiagopeixoto.devpass.helper.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;

/**
 * Created by Thiago Peixoto
 */

public class BoostFire {

    public Feed feed;
    public Usuario usuario;
    public int boostFire = 0;

    public BoostFire() {
    }

    public void atualizarQtdBoostFire( int valor){

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference usuariosRef = firebaseRef
                .child("usuarios")
                .child( usuario.getId() );

        HashMap<String, Object> dados = new HashMap<>();
        dados.put("boostFire:", getBoostFire() - valor );

        usuariosRef.updateChildren( dados );

    }

    public void salvarBoostFire(){

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();



        DatabaseReference pGBoostRef = firebaseRef
                .child("usuario")
                .child( usuario.getId() )//id_postagem
                .child("boostFire");

        //atualizar quantidade de curtidas
        atualizarQtdBoostFire(1);

    }

    public void atualizarQtdBf(int valor){

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();



        DatabaseReference pGBoostRef = firebaseRef
                .child("usuario")
                .child( usuario.getId() )//id_postagem
                .child("boostFire");
        setBoostFire( getBoostFire() - valor );
    }

    public void remover(){

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();

        DatabaseReference pGanhosRef = firebaseRef
                .child("ganhos-postagens")
                .child( feed.getId() )//id_postagem
                .child( usuario.getId() );//id_usuario_logado
        pGanhosRef.removeValue();

        //atualizar quantidade de curtidas
        atualizarQtdBf(-1);

    }

    public Feed getFeed() {
        return feed;
    }

    public void setFeed(Feed feed) {
        this.feed = feed;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public int getBoostFire() {
        return boostFire;
    }

    public void setBoostFire(int boostFire) {
        this.boostFire = boostFire;
    }
}
