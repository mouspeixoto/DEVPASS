package com.devpass.pampasistemas.thiagopeixoto.devpass.activity;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.devpass.pampasistemas.thiagopeixoto.devpass.fragment.GruposUsuarioFragment;
import com.devpass.pampasistemas.thiagopeixoto.devpass.fragment.PostUsuarioFragment;
import com.devpass.pampasistemas.thiagopeixoto.devpass.fragment.SobreUsuarioFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.devpass.pampasistemas.thiagopeixoto.devpass.R;
import com.devpass.pampasistemas.thiagopeixoto.devpass.adapter.AdapterGrid;
import com.devpass.pampasistemas.thiagopeixoto.devpass.helper.ConfiguracaoFirebase;
import com.devpass.pampasistemas.thiagopeixoto.devpass.helper.UsuarioFirebase;
import com.devpass.pampasistemas.thiagopeixoto.devpass.model.Postagem;
import com.devpass.pampasistemas.thiagopeixoto.devpass.model.Usuario;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PerfilAmigoActivity extends AppCompatActivity {

    private Usuario usuarioSelecionado;
    private Usuario usuarioLogado;
    private Button buttonAcaoPerfil;
    private CircleImageView imagePerfil;
    private TextView textPublicacoes, textSeguidores, textSeguindo, textNome,textAvaliacao;
    private GridView gridViewPerfil;
    private AdapterGrid adapterGrid;
    private RatingBar avaliacao;

    private DatabaseReference firebaseRef;
    private DatabaseReference usuariosRef;
    private DatabaseReference usuarioAmigoRef;
    private DatabaseReference usuarioLogadoRef;
    private DatabaseReference seguidoresRef;
    private DatabaseReference postagensUsuarioRef;
    private ValueEventListener valueEventListenerPerfilAmigo;

    private String idUsuarioLogado;
    private List<Postagem> postagens;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_amigo);

        //Configurações iniciais
        firebaseRef = ConfiguracaoFirebase.getFirebase();
        usuariosRef = firebaseRef.child("usuarios");
        seguidoresRef = firebaseRef.child("seguidores");
        idUsuarioLogado = UsuarioFirebase.getIdentificadorUsuario();

        //Inicializar componentes
        inicializarComponentes();
        //Configurar bottom navigation view Superior
        configuraBottomNavigationView_perfil();
        //Configurar bottom navigation view Inferior
        configuraBottomNavigationView();

        //Recuperar usuario selecionado
        Bundle bundle = getIntent().getExtras();
        if( bundle != null ){
            usuarioSelecionado = (Usuario) bundle.getSerializable("usuarioSelecionado");

            //Configurar referencia postagens usuario
            postagensUsuarioRef = ConfiguracaoFirebase.getFirebase()
                    .child("postagens")
                    .child( usuarioSelecionado.getId() );



            //Recuperar foto do usuário
            String caminhoFoto = usuarioSelecionado.getCaminhoFoto();
            if( caminhoFoto != null ){
                Uri url = Uri.parse( caminhoFoto );
                Glide.with(PerfilAmigoActivity.this)
                        .load( url )
                        .into( imagePerfil );
            }

        }

        //Inicializar image loader
        inicializarImageLoader();

        //Carrega as fotos das postagens de um usuário
        carregarFotosPostagem();

        //Abre a foto clicada
        gridViewPerfil.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Postagem postagem = postagens.get( position );
                Intent i = new Intent(getApplicationContext(), VisualizarPostagemActivity.class );

                i.putExtra("postagem", postagem );
                i.putExtra("usuario", usuarioSelecionado );

                startActivity( i );

            }
        });

    }

    /**
     * Instancia a UniversalImageLoader
     */
    public void inicializarImageLoader() {

        ImageLoaderConfiguration config = new ImageLoaderConfiguration
                .Builder(this)
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024)
                .diskCacheSize(50 * 1024 * 1024)
                .diskCacheFileCount(100)
                .diskCacheFileNameGenerator(new HashCodeFileNameGenerator())
                .build();
        ImageLoader.getInstance().init( config );

    }

    private void configuraBottomNavigationView(){
        BottomNavigationViewEx bottomNavigationViewExInferior = findViewById(R.id.bottomNavigationPerfil);

        //faz configuraçoes iniciais do bottom Navigation
        bottomNavigationViewExInferior.setTextVisibility(true);
        bottomNavigationViewExInferior.enableItemShiftingMode(true);
        bottomNavigationViewExInferior.enableAnimation(false);


        //Habilitar navegação
        habilitarNavegacao(bottomNavigationViewExInferior);
    }

    private void habilitarNavegacao(BottomNavigationViewEx viewEx) {

        viewEx.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                switch (item.getItemId()) {

                    case R.id.ic_home_perfil:
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                        return true;
                }

                return false;
            }
        });
    }

    public void carregarFotosPostagem(){

        //Recupera as fotos postadas pelo usuario
        postagens = new ArrayList<>();
        postagensUsuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //Configurar o tamanho do grid
                int tamanhoGrid = getResources().getDisplayMetrics().widthPixels;
                int tamanhoImagem = tamanhoGrid / 3;
                gridViewPerfil.setColumnWidth( tamanhoImagem );

                List<String> urlFotos = new ArrayList<>();
                for( DataSnapshot ds: dataSnapshot.getChildren() ){
                    Postagem postagem = ds.getValue( Postagem.class );
                    postagens.add( postagem );
                    urlFotos.add( postagem.getCaminhoFoto() );
                    //Log.i("postagem", "url:" + postagem.getCaminhoFoto() );
                }

                //Configurar adapter
                adapterGrid = new AdapterGrid(getApplicationContext(), R.layout.grid_postagem, urlFotos );
                gridViewPerfil.setAdapter( adapterGrid );

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void recuperarDadosUsuarioLogado(){

        usuarioLogadoRef = usuariosRef.child( idUsuarioLogado );
        usuarioLogadoRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        //Recupera dados de usuário logado
                        usuarioLogado = dataSnapshot.getValue( Usuario.class );

                        /* Verifica se usuário já está seguindo
                           amigo selecionado
                         */
                        verificaSegueUsuarioAmigo();

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }
        );

    }

    private void verificaSegueUsuarioAmigo(){

        DatabaseReference seguidorRef = seguidoresRef
                .child( usuarioSelecionado.getId() )
                .child( idUsuarioLogado );

        seguidorRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if( dataSnapshot.exists() ){
                            //Já está seguindo
                            Log.i("dadosUsuario", ": Seguindo" );
                            habilitarBotaoSeguir( true );
                        }else {
                            //Ainda não está seguindo
                            Log.i("dadosUsuario", ": seguir" );
                            habilitarBotaoSeguir( false );
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }
        );

    }

    private void habilitarBotaoSeguir( boolean segueUsuario ){

        if ( segueUsuario ){
            buttonAcaoPerfil.setText("Deixar de seguir");
            buttonAcaoPerfil.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //apagar seguidor
                    apagarseguidor(usuarioLogado,usuarioSelecionado );
                }
            });
        }else {

            buttonAcaoPerfil.setText("Seguir");
            //Adiciona evento para seguir usuário
            buttonAcaoPerfil.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //Salvar seguidor
                    salvarSeguidor(usuarioLogado, usuarioSelecionado);
                }
            });

        }

    }
    private void habilitarBotaoSeguir2( boolean segueUsuario){

        if ( segueUsuario ){
            buttonAcaoPerfil.setText("Deixar de seguir");
            buttonAcaoPerfil.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //apagar seguidor
                    Toast.makeText(PerfilAmigoActivity.this, "Desculpa, mas isso so pode ser feito daqui a 2 minutos!", Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            buttonAcaoPerfil.setText("Seguir");
            buttonAcaoPerfil.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //salvar seguidor
                    Toast.makeText(PerfilAmigoActivity.this, "Desculpa, mas isso so pode ser feito daqui a 2 minutos!", Toast.LENGTH_SHORT).show();

                }
            });
        }
    }

    private void salvarSeguidor(Usuario uLogado, Usuario uAmigo){

        /*
        * seguidores
        * id_usuario_selecionado (id amigo)
        *   id_usuario_logado (id usuario logado)
        *       dados logado
        * */
        HashMap<String, Object> dadosUsuarioLogado = new HashMap<>();
        dadosUsuarioLogado.put("nome", uLogado.getNome() );
        dadosUsuarioLogado.put("caminhoFoto", uLogado.getCaminhoFoto() );
        DatabaseReference seguidorRef = seguidoresRef
                .child( uAmigo.getId() )
                .child( uLogado.getId() );
        seguidorRef.setValue( dadosUsuarioLogado );

        //Alterar botao acao para seguindo
        buttonAcaoPerfil.setText("Seguindo");
        buttonAcaoPerfil.setOnClickListener(null);

        //Incrementar seguindo do usuário logado
        int seguindo = uLogado.getSeguindo() + 1;
        HashMap<String, Object> dadosSeguindo = new HashMap<>();
        dadosSeguindo.put("seguindo", seguindo );
        DatabaseReference usuarioSeguindo = usuariosRef
                .child( uLogado.getId() );
        usuarioSeguindo.updateChildren( dadosSeguindo );

        //Incrementar seguidores do amigo
        int seguidores = uAmigo.getSeguidores() + 1;
        HashMap<String, Object> dadosSeguidores = new HashMap<>();
        dadosSeguidores.put("seguidores", seguidores );
        DatabaseReference usuarioSeguidores = usuariosRef
                .child( uAmigo.getId() );
        usuarioSeguidores.updateChildren( dadosSeguidores );

        habilitarBotaoSeguir2(true);


    }

    private void apagarseguidor(final Usuario uLogado, final Usuario uAmigo){

        seguidoresRef.limitToFirst(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String ulogado = dataSnapshot.getKey();
                seguidoresRef.child(uAmigo.getId())
                        .child(usuarioLogado.getId()).removeValue();


                //Incrementar seguindo do usuario Logado
                int seguindo = uLogado.getSeguindo() - 1;
                //usuarioLogado.setSeguindo( seguindo);
                HashMap<String, Object> dadosSeguindo = new HashMap<>();
                dadosSeguindo.put("seguindo", seguindo);
                DatabaseReference usuarioSeguindo = usuariosRef
                        .child(uLogado.getId());
                usuarioSeguindo.updateChildren(dadosSeguindo);

                //Incrementar seguidores do amigo
                int seguidores = uAmigo.getSeguidores() -1;
                //uAmigo.setSeguidores( seguidores);
                HashMap<String, Object> dadosSeguidores = new HashMap<>();
                dadosSeguidores.put("seguidores", seguidores);
                DatabaseReference usuarioSeguidores = usuariosRef
                        .child( uAmigo.getId() );
                usuarioSeguidores.updateChildren( dadosSeguidores );

                habilitarBotaoSeguir2(false);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();

        //Recuperar dados do amigo selecionado
        recuperarDadosPerfilAmigo();

        //Recuperar dados usuario logado
        recuperarDadosUsuarioLogado();

    }

    @Override
    protected void onStop() {
        super.onStop();
        usuarioAmigoRef.removeEventListener( valueEventListenerPerfilAmigo );
    }

    private void recuperarDadosPerfilAmigo(){

        usuarioAmigoRef = usuariosRef.child( usuarioSelecionado.getId() );
        valueEventListenerPerfilAmigo = usuarioAmigoRef.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        Usuario usuario = dataSnapshot.getValue(Usuario.class);

                        String postagens = String.valueOf(usuario.getPostagens());
                        String seguindo = String.valueOf(usuario.getSeguindo());
                        String seguidores = String.valueOf(usuario.getSeguidores());

                        //Configura valores recuperados
                        textPublicacoes.setText(postagens);
                        textSeguidores.setText(seguidores);
                        textSeguindo.setText(seguindo);
                        textNome.setText(usuario.getNome());

                        final String empresa = "nao";
                        textAvaliacao.setVisibility(View.VISIBLE);

                        if (usuario.getEmpresa().equals(empresa)) {
                            avaliacaoNula();
                        }
                        else {
                            if (usuario.getAvaliation() != 0){
                                // textAvaliacao.setVisibility(View.GONE);
                                avaliacao.setRating(usuario.getAvaliation());
                            }else {
                                textAvaliacao.setText("Avaliação em andamento");
                                avaliacao.setRating( 0 );
                            }

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }
        );

    }

    private void configuraBottomNavigationView_perfil(){
        BottomNavigationViewEx bottomNavigationViewExPerfil = findViewById(R.id.perfil_bottomNavigation);

        //faz configuraçoes iniciais do bottom Navigation
        bottomNavigationViewExPerfil.setTextVisibility(true);
        bottomNavigationViewExPerfil.enableItemShiftingMode(true);
        bottomNavigationViewExPerfil.enableAnimation(false);


        //Habilitar navegação
        habilitarNavegacao_perfil(bottomNavigationViewExPerfil);
    }

    private void habilitarNavegacao_perfil(BottomNavigationViewEx viewEx) {

        viewEx.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                switch (item.getItemId()) {

                    case R.id.ic_perfil_post:
                        fragmentTransaction.replace(R.id.viewPagerPerfil, new PostUsuarioFragment()).commit();
                        gridViewPerfil.setVisibility(View.VISIBLE);
                        return true;
                    case R.id.ic_perfil_grupos:
                        fragmentTransaction.replace(R.id.viewPagerPerfil, new GruposUsuarioFragment()).commit();
                        gridViewPerfil.setVisibility(View.GONE);
                        return true;
                    case R.id.ic_perfil_sobre:
                        fragmentTransaction.replace(R.id.viewPagerPerfil, new SobreUsuarioFragment()).commit();
                        gridViewPerfil.setVisibility(View.GONE);
                        return true;


                }

                return false;
            }
        });
    }

    private void inicializarComponentes(){
        imagePerfil = findViewById(R.id.imagePerfil);
        gridViewPerfil = findViewById(R.id.gridtest);
        buttonAcaoPerfil = findViewById(R.id.buttonAcaoPerfil);
        textPublicacoes = findViewById(R.id.textPublicacoes);
        textSeguidores = findViewById(R.id.textSeguidores);
        textSeguindo = findViewById(R.id.textSeguindo);
        textNome = findViewById(R.id.perfilNomeUsario);
        buttonAcaoPerfil.setText("Carregando");
        avaliacao = findViewById(R.id.ratingBarPerfil);
        textAvaliacao = findViewById(R.id.textAvaliacaoPerfil);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }

    private void avaliacaoNula(){

        textAvaliacao.setVisibility(View.GONE);
        avaliacao.setVisibility(View.GONE);

    }

}
