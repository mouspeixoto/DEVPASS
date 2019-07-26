package com.devpass.pampasistemas.thiagopeixoto.devpass.model;

/**
 * Created by Thiago Peixoto
 */

public class Feed {

    private String id;
    private String fotoPostagem;
    private String descricao;
    private String nomeUsuario;
    private String fotoUsuario;
    private String userStatus;
    private int ganhosPublicacao;

    public Feed() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFotoPostagem() {
        return fotoPostagem;
    }

    public void setFotoPostagem(String fotoPostagem) {
        this.fotoPostagem = fotoPostagem;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
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

    public int getGanhosPublicacao() {
        return ganhosPublicacao;
    }

    public void setGanhosPublicacao(int ganhosPublicacao) {
        this.ganhosPublicacao = ganhosPublicacao;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }
}
