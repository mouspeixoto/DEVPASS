package com.devpass.pampasistemas.thiagopeixoto.devpass.fragment;


import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.devpass.pampasistemas.thiagopeixoto.devpass.R;
import com.devpass.pampasistemas.thiagopeixoto.devpass.activity.FiltroActivity;
import com.devpass.pampasistemas.thiagopeixoto.devpass.activity.PostarVideoActivity;
import com.devpass.pampasistemas.thiagopeixoto.devpass.helper.Permissao;

import java.io.ByteArrayOutputStream;

/**
 * A simple {@link Fragment} subclass.
 */
public class PostagemFragment extends Fragment {

    private Button buttonAbrirGaleria, buttonAbrirCamera, buttonAbrirVideo;
    private static final int SELECAO_CAMERA  = 100;
    private static final int SELECAO_GALERIA = 200;
    private static final int SELECAO_GALERIA_VIDEO = 300;

    private String[] permissoesNecessarias = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    public PostagemFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_postagem, container, false);

        //Validar permissões
        Permissao.validarPermissoes(permissoesNecessarias, getActivity(), 1 );

        //Inicializar componentes
        buttonAbrirCamera = view.findViewById(R.id.buttonAbrirCamera);
        buttonAbrirGaleria = view.findViewById(R.id.buttonAbrirGaleria);
        buttonAbrirVideo = view.findViewById(R.id.buttonYoutubeVideo);


        //Adiciona evento de clique no botão da camera

        buttonAbrirVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent( Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI );
                if( i.resolveActivity( getActivity().getPackageManager() ) != null ){
                    startActivityForResult(i, SELECAO_GALERIA_VIDEO );
                }
            }
        });

        buttonAbrirCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if( i.resolveActivity( getActivity().getPackageManager() ) != null ){
                    startActivityForResult(i, SELECAO_CAMERA );
                }
            }
        });



        //Adiciona evento de clique no botão da galeria
        buttonAbrirGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                 Intent i = new Intent( Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI );
                if( i.resolveActivity( getActivity().getPackageManager() ) != null ){
                    startActivityForResult(i, SELECAO_GALERIA );
                }
            }
        });


        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if( resultCode == getActivity().RESULT_OK ){

            Bitmap imagem = null;
            Bitmap miniatura = null;


            try {

                //Valida tipo de seleção da imagem
                switch ( requestCode ){
                    case SELECAO_CAMERA :
                        imagem = (Bitmap) data.getExtras().get("data");
                        break;
                    case SELECAO_GALERIA :
                        Uri localImagemSelecionada = data.getData();
                        imagem = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), localImagemSelecionada);
                        break;
                    case SELECAO_GALERIA_VIDEO :
                        Uri localImagemMiniaturaSelecionada = data.getData();
                        miniatura = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), localImagemMiniaturaSelecionada);
                        break;

                }

                //Valida imagem selecionada
                if( imagem != null ){

                    //Converte imagem em byte array
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] dadosImagem = baos.toByteArray();

                    //Envia imagem escolhida para aplicação de filtro
                    Intent i = new Intent(getActivity(), FiltroActivity.class);
                    i.putExtra("fotoEscolhida", dadosImagem );
                    startActivity( i );

                }

                if( miniatura != null ){

                    //Converte imagem em byte array
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    miniatura.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] dadosImagem = baos.toByteArray();

                    //Envia imagem escolhida para aplicação de filtro
                    Intent i = new Intent(getActivity(), PostarVideoActivity.class);
                    i.putExtra("fotoEscolhida", dadosImagem );
                    startActivity( i );

                }

            }catch (Exception e){
                e.printStackTrace();
            }

        }

    }
}
