package com.devpass.pampasistemas.thiagopeixoto.devpass.model;

import com.devpass.pampasistemas.thiagopeixoto.devpass.helper.ConfiguracaoFirebase;
import com.devpass.pampasistemas.thiagopeixoto.devpass.helper.UsuarioFirebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Thiago Peixoto
 */

public class PostagemVideo implements Serializable {

    /*
    * Modelo de postagem
    * postagens
    *  <id_usuario>
    *      <id_postagem_firebase>
    *          descricao
    *          caminhoFoto
    *          idUsuario
    *
    * */

    private String id;
    private String indentificadorVideo;
    private String videoID;
    private String idUsuario;
    private String descricao;
    private String caminhoFoto;
    private String tituloVideo;
    private String userStatus;

    public PostagemVideo() {

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference postagemRef = firebaseRef.child("postagens");
        String idPostagem = postagemRef.push().getKey();
        setId( idPostagem );

    }

    public boolean salvar(DataSnapshot seguidoresSnapshot){

        Map objeto = new HashMap();
        Usuario usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();

        //Referência para postagem
        String combinacaoId = "/" + getIdUsuario() + "/" + getId();
        objeto.put("/postagens" + combinacaoId, this );

        //Referência para postagem
        for( DataSnapshot seguidores: seguidoresSnapshot.getChildren() ){

            /*
            + feed
              + id_seguidor<jose renato>
                + id_postagem <01>
                    postagem< por thiago>
           */
            String idSeguidor = seguidores.getKey();

            //Monta objeto para salvar
            HashMap<String, Object> dadosSeguidor = new HashMap<>();
            dadosSeguidor.put("fotoPostagem", getCaminhoFoto() );
            dadosSeguidor.put("descricao", getDescricao() );
            dadosSeguidor.put("titulo", getTituloVideo() );
            dadosSeguidor.put("id", getId() );
            dadosSeguidor.put("videoID", getVideoID() );
            dadosSeguidor.put("nomeUsuario", usuarioLogado.getNome() );
            dadosSeguidor.put("fotoUsuario", usuarioLogado.getCaminhoFoto() );
            dadosSeguidor.put("userStatus", getUserStatus());

            String idsAtualizacao = "/" + idSeguidor + "/" + getId();
            objeto.put("/feed-video" + idsAtualizacao, dadosSeguidor );

        }

        firebaseRef.updateChildren( objeto );
        return true;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getCaminhoFoto() {
        return caminhoFoto;
    }

    public void setCaminhoFoto(String caminhoFoto) {
        this.caminhoFoto = caminhoFoto;
    }

    public String getTituloVideo() {
        return tituloVideo;
    }

    public void setTituloVideo(String tituloVideo) {
        this.tituloVideo = tituloVideo;
    }

    public String getVideoID() {
        return videoID;
    }

    public void setVideoID(String videoID) {
        this.videoID = videoID;
    }

    public String getIndentificadorVideo() {
        return indentificadorVideo;
    }

    public void setIndentificadorVideo(String indentificadorVideo) {
        this.indentificadorVideo = indentificadorVideo;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }
}
