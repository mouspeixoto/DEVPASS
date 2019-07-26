package com.devpass.pampasistemas.thiagopeixoto.devpass.model;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.devpass.pampasistemas.thiagopeixoto.devpass.helper.ConfiguracaoFirebase;
import com.devpass.pampasistemas.thiagopeixoto.devpass.helper.UsuarioFirebase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Thiago Peixoto
 */

public class Postagem implements Serializable {

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
    private String idUsuario;
    private String descricao;
    private String caminhoFoto;
    private String statusUser;
    private String TAG = "UserStatus: ";


    public Postagem() {

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference postagemRef = firebaseRef.child("postagens");
        String idPostagem = postagemRef.push().getKey();
        setId( idPostagem );

    }

    public boolean salvar(DataSnapshot seguidoresSnapshot){

        Map objeto = new HashMap();

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        Usuario usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();

        //Referência para postagem
        for( DataSnapshot seguidores: seguidoresSnapshot.getChildren() ){



            /*
            + feed
              + id_seguidor<jose renato>
                + id_postagem <01>
                    postagem< por Thiaguinho>
           */
            String idSeguidor = seguidores.getKey();

            //Monta objeto para salvar
            HashMap<String, Object> dadosSeguidor = new HashMap<>();
            dadosSeguidor.put("fotoPostagem", getCaminhoFoto() );
            dadosSeguidor.put("descricao", getDescricao() );
            dadosSeguidor.put("id", getId() );
            dadosSeguidor.put("nomeUsuario", usuarioLogado.getNome() );
            dadosSeguidor.put("fotoUsuario", usuarioLogado.getCaminhoFoto() );
            dadosSeguidor.put("userStatus", getStatusUser());

            String idsAtualizacao = "/" + idSeguidor + "/" + getId();
            objeto.put("/feed" + idsAtualizacao, dadosSeguidor );

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

    public void setStatusUser(String statusUser) {
        this.statusUser = statusUser;
    }

    public String getStatusUser() {
        return statusUser;
    }
}
