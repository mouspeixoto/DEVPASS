package com.devpass.pampasistemas.thiagopeixoto.devpass.activity;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import com.devpass.pampasistemas.thiagopeixoto.devpass.R;
import com.devpass.pampasistemas.thiagopeixoto.devpass.helper.ConfiguracaoFirebase;
import com.devpass.pampasistemas.thiagopeixoto.devpass.helper.UsuarioFirebase;
import com.devpass.pampasistemas.thiagopeixoto.devpass.model.Noticias;
import com.devpass.pampasistemas.thiagopeixoto.devpass.model.Postagem;
import com.devpass.pampasistemas.thiagopeixoto.devpass.model.PostagemVideo;
import com.devpass.pampasistemas.thiagopeixoto.devpass.model.Usuario;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;


public class PostarVideoActivity extends AppCompatActivity {

    private TextInputEditText textDescricaoVideo;
    private EditText campoIdVideo, campoTitulo;
    private ImageView imageMiniaturaEscolhida;
    private Bitmap imagem;
    private String video = "sim";

    private Button botaoPublicar;
    private String idUsuarioLogado;
    private Usuario usuarioLogado;
    private AlertDialog dialog;

    private DatabaseReference usuariosRef;
    private DatabaseReference usuarioLogadoRef;
    private DatabaseReference firebaseRef;
    private DataSnapshot seguidoresSnapshot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postar_video);

        //Configurações iniciais
        firebaseRef = ConfiguracaoFirebase.getFirebase();
        idUsuarioLogado = UsuarioFirebase.getIdentificadorUsuario();
        usuariosRef = ConfiguracaoFirebase.getFirebase().child("usuarios");

        //Inicializa Componentes
        inicializarComponentes();
        recuperarDadosVideo();

        //Configura toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Publique seu Video");
        setSupportActionBar( toolbar );

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_voltar_24dp);

        //Recupera a imagem escolhida pelo usuário
        Bundle bundle = getIntent().getExtras();
        if( bundle != null ){
            byte[] dadosImagem = bundle.getByteArray("fotoEscolhida");
            imagem = BitmapFactory.decodeByteArray(dadosImagem, 0, dadosImagem.length );
            imageMiniaturaEscolhida.setImageBitmap( imagem );
        }

    }

    private void abrirDialogCarregamento(String titulo){

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle( titulo );
        alert.setCancelable(false);
        alert.setView(R.layout.carregamento);

        dialog = alert.create();
        dialog.show();

    }

    private void recuperarDadosVideo(){

        abrirDialogCarregamento("Carregando dados, aguarde!");
        usuarioLogadoRef = usuariosRef.child( idUsuarioLogado );
        usuarioLogadoRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        //Recupera dados de usuário logado
                        usuarioLogado = dataSnapshot.getValue( Usuario.class );

                        /*
                         * Recuperar seguidores */
                        DatabaseReference seguidoresRef = firebaseRef
                                .child("seguidores")
                                .child( idUsuarioLogado );
                        seguidoresRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                seguidoresSnapshot = dataSnapshot;
                                dialog.cancel();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }
        );

    }

    private void publicarVideo(){

        abrirDialogCarregamento("Salvando postagem");
        final PostagemVideo postagemVideo = new PostagemVideo();
        postagemVideo.setIdUsuario( idUsuarioLogado );
        postagemVideo.setDescricao( textDescricaoVideo.getText().toString() );
        postagemVideo.setTituloVideo(campoTitulo.getText().toString());
        postagemVideo.setVideoID(campoIdVideo.getText().toString());
        postagemVideo.setIndentificadorVideo(video);
        postagemVideo.setUserStatus(usuarioLogado.getUserStatus());

        //Recuperar dados da imagem para o firebase
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        byte[] dadosImagem = baos.toByteArray();

        //Salvar imagem no firebase storage
        StorageReference storageRef = ConfiguracaoFirebase.getFirebaseStorage();
        StorageReference imagemRef = storageRef
                .child("imagens")
                .child("postagens")
                .child( postagemVideo.getId() + ".jpeg");

        UploadTask uploadTask = imagemRef.putBytes( dadosImagem );
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(PostarVideoActivity.this,
                        "Erro ao salvar a imagem, tente novamente!",
                        Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                //Recuperar local da foto
                Uri url = taskSnapshot.getDownloadUrl();
                postagemVideo.setCaminhoFoto( url.toString() );

                //Atualizar qtde de postagens
                int qtdPostagem = usuarioLogado.getPostagens() + 1;
                usuarioLogado.setPostagens( qtdPostagem );
                usuarioLogado.atualizarQtdPostagem();

                //Salvar postagem
                if( postagemVideo.salvar( seguidoresSnapshot ) ){

                    Toast.makeText(PostarVideoActivity.this,
                            "Sucesso ao salvar postagem!",
                            Toast.LENGTH_SHORT).show();
                    dialog.cancel();
                    finish();
                }

            }
        });


    }

    public void inicializarComponentes(){

        campoIdVideo      = findViewById(R.id.editIdVideo);
        campoTitulo      = findViewById(R.id.editTituloVideo);
        botaoPublicar  = findViewById(R.id.buttonPublicarVideo);
        textDescricaoVideo = findViewById(R.id.textDescricaoVideo);
        imageMiniaturaEscolhida = findViewById(R.id.imageMiniaturaNoticia);



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_filtro, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch ( item.getItemId() ){
            case R.id.ic_salvar_postagem :
                publicarVideo();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }


}
