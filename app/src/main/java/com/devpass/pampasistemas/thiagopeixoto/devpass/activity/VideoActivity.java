package com.devpass.pampasistemas.thiagopeixoto.devpass.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.devpass.pampasistemas.thiagopeixoto.devpass.R;
import com.devpass.pampasistemas.thiagopeixoto.devpass.adapter.AdapterComentario;
import com.devpass.pampasistemas.thiagopeixoto.devpass.helper.ConfiguracaoFirebase;
import com.devpass.pampasistemas.thiagopeixoto.devpass.helper.UsuarioFirebase;
import com.devpass.pampasistemas.thiagopeixoto.devpass.model.Comentario;
import com.devpass.pampasistemas.thiagopeixoto.devpass.model.Usuario;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class VideoActivity extends YouTubeBaseActivity
        implements YouTubePlayer.OnInitializedListener {

    private EditText editComentario;
    private RecyclerView recyclerComentarios;

    private YouTubePlayerView youTubePlayerView;
    private String idVideo;
    private String idPostagem;
    private String desc, titulo;
    private static final String GOOGLE_API_KEY = "AIzaSyDqZvVzGhiydIoLm0r9a6J_naAzOGz-Vws";
    private TextView textDesc, textTitulo;

    private Usuario usuario;
    private DatabaseReference firebaserRef;
    private DatabaseReference comentariosRef;
    private ValueEventListener valueEventListenerComentarios;

    private List<Comentario> listaComentarios = new ArrayList<>();
    private AdapterComentario adapterComentario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);


        youTubePlayerView = findViewById(R.id.viewYoutubePlayer);
        youTubePlayerView.initialize(GOOGLE_API_KEY, this );

        //Configuracoes iniciais
        usuario = UsuarioFirebase.getDadosUsuarioLogado();
        firebaserRef = ConfiguracaoFirebase.getFirebase();

        iniciarcomponentes();

        //Configura recyclerview
        adapterComentario = new AdapterComentario(listaComentarios, getApplicationContext() );
        recyclerComentarios.setHasFixedSize( true );
        recyclerComentarios.setLayoutManager(new LinearLayoutManager(this));
        recyclerComentarios.setAdapter( adapterComentario );

        Bundle bundle = getIntent().getExtras();
        if( bundle != null ){
            //  video = (Video) bundle.getSerializable("videos") ;
            idVideo = bundle.getString("videos");
            desc = bundle.getString("desc");
            titulo = bundle.getString("titulo");
            idPostagem = bundle.getString("idPostagem");

        }

        iniciarcomponentes();

    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean foiRestaurado) {
        Toast.makeText(this,
                "Sucesso ao iniciar o Player!",
                Toast.LENGTH_SHORT).show();

        //youTubePlayer.loadVideo("qRXkEQOtQ98");
        Log.i("estado_player", "estado: " + foiRestaurado );

        if ( !foiRestaurado ){
            youTubePlayer.cueVideo(idVideo);
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        Toast.makeText(this,
                "Erro ao iniciar o Player!",
                Toast.LENGTH_SHORT).show();
    }

    private void iniciarcomponentes(){

        textDesc = findViewById(R.id.textDesc);
        textDesc.setText(desc);
        textTitulo = findViewById(R.id.textTituloVideo);
        textTitulo.setText(titulo);

        editComentario = findViewById(R.id.editComentario);
        recyclerComentarios = findViewById(R.id.recyclerComentariosVideos);



    }

    public  void salvarComentario(View view){

            Toast.makeText(this,
                    "ID POSTAGEM:  " + idPostagem,
                    Toast.LENGTH_SHORT).show();

        String textoComentario = editComentario.getText().toString();
        if( textoComentario != null && !textoComentario.equals("") ){

            Comentario comentario = new Comentario();
            comentario.setIdPostagem( idPostagem );
            comentario.setIdUsuario( usuario.getId() );
            comentario.setNomeUsuario( usuario.getNome() );
            comentario.setCaminhoFoto( usuario.getCaminhoFoto() );
            comentario.setComentario( textoComentario );
            if(comentario.salvar()){
                Toast.makeText(this,
                        "Comentário salvo com sucesso!",
                        Toast.LENGTH_SHORT).show();
            }

        }else {
            Toast.makeText(this,
                    "Insira o comentário antes de salvar!",
                    Toast.LENGTH_SHORT).show();
        }

        //Limpa comentário digitado
        editComentario.setText("");

    }

    private void recuperarComentarios(){

        comentariosRef = firebaserRef.child("comentarios")
                .child( idPostagem );
        valueEventListenerComentarios = comentariosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listaComentarios.clear();
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    listaComentarios.add( ds.getValue(Comentario.class) );
                }
                adapterComentario.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        recuperarComentarios();
    }

    @Override
    protected void onStop() {
        super.onStop();
        comentariosRef.removeEventListener( valueEventListenerComentarios );
    }

}
