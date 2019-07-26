package com.devpass.pampasistemas.thiagopeixoto.devpass.model;

import java.io.Serializable;

/**
 * Created by Thiago Peixoto
 */

public class Empresas implements Serializable {

    private String id;
    private String nomeEmpresa;
    private String moderador;
    private String email;
    private String senha;
    private String caminhoFoto;
    private String biografia;
    private int seguidores = 0;
    private int seguindo = 0;
    private int postagens = 0;
    private int avaliation = 0;


    public Empresas() {
    }



}
