package com.devpass.pampasistemas.thiagopeixoto.devpass.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Toast;

import com.devpass.pampasistemas.thiagopeixoto.devpass.R;
import com.devpass.pampasistemas.thiagopeixoto.devpass.adapter.AdapterMiniaturas;
import com.devpass.pampasistemas.thiagopeixoto.devpass.helper.ConfiguracaoFirebase;
import com.devpass.pampasistemas.thiagopeixoto.devpass.helper.RecyclerItemClickListener;
import com.devpass.pampasistemas.thiagopeixoto.devpass.helper.UsuarioFirebase;
import com.devpass.pampasistemas.thiagopeixoto.devpass.model.Noticias;
import com.devpass.pampasistemas.thiagopeixoto.devpass.model.Usuario;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.utils.ThumbnailItem;

import java.io.ByteArrayOutputStream;

public class CriarPostagemActivity extends AppCompatActivity {

    private TextInputEditText textDescricaoNoticia , textTitulo;
    private ImageView imageFotoEscolhida;
    private Bitmap imagem;

    private AlertDialog dialog;

    private String idUsuarioLogado;
    private Usuario usuarioLogado;

    private DatabaseReference usuariosRef;
    private DatabaseReference usuarioLogadoRef;
    private DatabaseReference firebaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criar_postagem);

        //Configurações iniciais
        firebaseRef = ConfiguracaoFirebase.getFirebase();
        idUsuarioLogado = UsuarioFirebase.getIdentificadorUsuario();
        usuariosRef = ConfiguracaoFirebase.getFirebase().child("usuarios");

        //Inicializar componentes
        imageFotoEscolhida = findViewById(R.id.imageFotoNoticia);
        textTitulo = findViewById(R.id.textTitulo);
        textDescricaoNoticia = findViewById(R.id.textDescricaoPostagem);

        recuperarDadosNoticia();


        //Configura toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Anuncie a noticia");
        setSupportActionBar( toolbar );

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_voltar_24dp);


        //Recupera a imagem escolhida pelo usuário
        Bundle bundle = getIntent().getExtras();
        if( bundle != null ){
            byte[] dadosImagem = bundle.getByteArray("fotoPostagemEscolhida");
            imagem = BitmapFactory.decodeByteArray(dadosImagem, 0, dadosImagem.length );
            imageFotoEscolhida.setImageBitmap( imagem );
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

    private void recuperarDadosNoticia(){

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


    private void publicarNoticia(){

        abrirDialogCarregamento("Salvando postagem");
        final Noticias noticias = new Noticias();
        noticias.setIdUsuario( idUsuarioLogado );
        noticias.setDescricao( textDescricaoNoticia.getText().toString() );
        noticias.setTituloNoticia( textTitulo.getText().toString() );

        //Recuperar dados da imagem para o firebase
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        byte[] dadosImagem = baos.toByteArray();

        //Salvar imagem no firebase storage
        StorageReference storageRef = ConfiguracaoFirebase.getFirebaseStorage();
        StorageReference imagemRef = storageRef
                .child("imagens")
                .child("noticias")
                .child( noticias.getIdNoticia() + ".jpeg");

        UploadTask uploadTask = imagemRef.putBytes( dadosImagem );
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CriarPostagemActivity.this,
                        "Erro ao salvar a imagem, tente novamente!",
                        Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                //Recuperar local da foto
                Uri url = taskSnapshot.getDownloadUrl();
                noticias.setFotoNoticia( url.toString() );

                //Salvar postagem
                if( noticias.salvar() ){

                    //Atualizar qtde de postagens
                    int qtdPostagem = usuarioLogado.getPostagens() + 1;
                    usuarioLogado.setPostagens( qtdPostagem );
                    usuarioLogado.atualizarQtdPostagem();

                    Toast.makeText(CriarPostagemActivity.this,
                            "Sucesso ao salvar postagem!",
                            Toast.LENGTH_SHORT).show();
                    dialog.cancel();
                    finish();
                }

            }
        });


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
                publicarNoticia();
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
