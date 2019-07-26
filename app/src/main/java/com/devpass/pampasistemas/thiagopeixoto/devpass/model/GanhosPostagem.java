package com.devpass.pampasistemas.thiagopeixoto.devpass.model;

import com.devpass.pampasistemas.thiagopeixoto.devpass.helper.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;

/**
 * Created by Thiago Peixoto
 */

public class GanhosPostagem {

    public Feed feed;
    public Usuario usuario;
    public int qtdGanhos = 0;

    public GanhosPostagem() {
    }

    public void salvarGanho(){

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();

        //Objeto usuario
        HashMap<String, Object> dadosUsuario = new HashMap<>();
        dadosUsuario.put("nomeUsuario", usuario.getNome() );
        dadosUsuario.put("caminhoFoto", usuario.getCaminhoFoto() );

        DatabaseReference pGanhosRef = firebaseRef
                .child("ganhos-postagens")
                .child( feed.getId() )//id_postagem
                .child( usuario.getId() );//id_usuario_logado
        pGanhosRef.setValue( dadosUsuario );

        //atualizar quantidade de curtidas
        atualizarQtdGanhos(1);

    }

    public void atualizarQtdGanhos(int valor){

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();



        DatabaseReference pGanhosRef = firebaseRef
                .child("ganhos-postagens")
                .child( feed.getId() )//id_postagem
                .child("qtdGanhos");
        setQtdGanhos( getQtdGanhos() + valor );
        pGanhosRef.setValue( getQtdGanhos() );
    }

    public void remover(){

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();

        DatabaseReference pGanhosRef = firebaseRef
                .child("ganhos-postagens")
                .child( feed.getId() )//id_postagem
                .child( usuario.getId() );//id_usuario_logado
        pGanhosRef.removeValue();

        //atualizar quantidade de curtidas
        atualizarQtdGanhos(-1);

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



    public int getQtdGanhos() {
        return qtdGanhos;
    }

    public void setQtdGanhos(int qtdGanhos) {
        this.qtdGanhos = qtdGanhos;
    }
}
