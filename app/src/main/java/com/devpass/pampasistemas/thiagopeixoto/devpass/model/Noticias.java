package com.devpass.pampasistemas.thiagopeixoto.devpass.model;

import com.devpass.pampasistemas.thiagopeixoto.devpass.helper.ConfiguracaoFirebase;
import com.devpass.pampasistemas.thiagopeixoto.devpass.helper.UsuarioFirebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.Map;

public class Noticias {

    /*
     * Modelo de noticia
     * noticias
     *      <id_noticia>
     *          titulo
     *          descricao
     *          caminhoFoto
     *          idUsuario
     *
     * */

    private String idNoticia;
    private String fotoNoticia;
    private String descricao;
    private String tituloNoticia;
    private String idUsuario;

    public Noticias() {

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference noticiaRef = firebaseRef.child("noticia");
        String idNoticia = noticiaRef.push().getKey();
        setIdNoticia( idNoticia );

    }

    public boolean salvar(){

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference noticiasRef = firebaseRef.child("noticias")
                .child( getIdNoticia() );
        noticiasRef.setValue(this);
        return true;

    }







    public String getIdNoticia() {
        return idNoticia;
    }

    public void setIdNoticia(String idNoticia) {
        this.idNoticia = idNoticia;
    }

    public String getFotoNoticia() {
        return fotoNoticia;
    }

    public void setFotoNoticia(String fotoNoticia) {
        this.fotoNoticia = fotoNoticia;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getTituloNoticia() {
        return tituloNoticia;
    }

    public void setTituloNoticia(String tituloNoticia) {
        this.tituloNoticia = tituloNoticia;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }
}
