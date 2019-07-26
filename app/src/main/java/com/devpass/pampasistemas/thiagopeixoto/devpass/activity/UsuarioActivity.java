package com.devpass.pampasistemas.thiagopeixoto.devpass.activity;

import android.content.Intent;
import android.media.Rating;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.devpass.pampasistemas.thiagopeixoto.devpass.R;
import com.devpass.pampasistemas.thiagopeixoto.devpass.adapter.AdapterGrid;
import com.devpass.pampasistemas.thiagopeixoto.devpass.fragment.GruposUsuarioFragment;
import com.devpass.pampasistemas.thiagopeixoto.devpass.fragment.PostUsuarioFragment;
import com.devpass.pampasistemas.thiagopeixoto.devpass.fragment.SobreUsuarioFragment;
import com.devpass.pampasistemas.thiagopeixoto.devpass.helper.ConfiguracaoFirebase;
import com.devpass.pampasistemas.thiagopeixoto.devpass.helper.UsuarioFirebase;
import com.devpass.pampasistemas.thiagopeixoto.devpass.model.Postagem;
import com.devpass.pampasistemas.thiagopeixoto.devpass.model.Usuario;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsuarioActivity extends AppCompatActivity {

    private CircleImageView imagePerfil;
    private TextView textPublicacoes, textSeguidores, textSeguindo, textNome, textAvaliacao;
    private Button buttonAcaoPerfil, buttonMod;
    private Usuario usuarioLogado;
    private GridView gridViewPerfil;
    private AdapterGrid adapterGrid;
    private RatingBar avaliacaoUm, avaliacaoDois, avaliacaoTres, avaliacaoQuatro, avaliacaoCinco;
    private float estrelas;
    private LinearLayout pageAvaliacao;

    private DatabaseReference firebaseRef;
    private DatabaseReference usuariosRef;
    private DatabaseReference usuarioLogadoRef;
    private ValueEventListener valueEventListenerPerfil;
    private ValueEventListener valueEventListenerEmpresa;
    private DatabaseReference postagensUsuarioRef;

    private List<Postagem> postagens;
    private String RENATADAMERI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario);


        gridViewPerfil = findViewById(R.id.gridtest);
        //Configuraçoes Iniciais
        usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();
        firebaseRef = ConfiguracaoFirebase.getFirebase();
        usuariosRef = firebaseRef.child("usuarios");



        //Configura referencias postagens usuario
        postagensUsuarioRef = ConfiguracaoFirebase.getFirebase()
                .child("postagens")
                .child(usuarioLogado.getId());

        //configuraçoes iniciais de chamar os metodos
        inicializarcomponentes();
        //Configurar bottom navigation view Inferior
        configuraBottomNavigationView();
        //Configurar bottom navigation view Superior
        configuraBottomNavigationView_perfil();

        habilitarBotaoMod();


        buttonAcaoPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UsuarioActivity.this, EditarPerfilActivity.class);
                startActivity(i);
            }
        });

        buttonMod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UsuarioActivity.this, ModeradorActivity.class);
                startActivity(i);
            }
        });


        //Recuperar dados do ussuario
        FirebaseUser usuarioPerfil = UsuarioFirebase.getUsuarioAtual();
        textNome.setText( usuarioPerfil.getDisplayName().toUpperCase() );

        //Recuperar foto do usuario
        String caminhoFoto = usuarioLogado.getCaminhoFoto();
        if( caminhoFoto != null){
            Uri url = Uri.parse(caminhoFoto);
            Glide.with(UsuarioActivity.this)
                    .load(url)
                    .into(imagePerfil)
            ;
        }

        //Inicializa image loader
        inicializarImageLoader();
        //Carrega as fotos das postagens de um usuario
        carregarFotosPostagem();

        //Abre a foto clicada
        gridViewPerfil.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Postagem  postagem = postagens.get( position);

            }
        });

        //Abre a foto clicada
        gridViewPerfil.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Postagem postagem = postagens.get( position );
                Intent i = new Intent(getApplicationContext(), VisualizarPostagemActivity.class );

                i.putExtra("postagem", postagem );
                i.putExtra("usuario", usuarioLogado );

                startActivity( i );

            }
        });


    }

    public void carregarFotosPostagem(){

        //Recuperar posts do ussuario
        postagens = new ArrayList<>();
        postagensUsuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //Configura o tamanha do grid
                int tamanhoGrid = getResources().getDisplayMetrics().widthPixels;
                int tamanhoImagem = tamanhoGrid/2;
                gridViewPerfil.setColumnWidth( tamanhoImagem );

                List<String> urlFotos = new ArrayList<>();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    Postagem postagem = ds.getValue( Postagem.class );
                    postagens.add(postagem);
                    urlFotos.add( postagem.getCaminhoFoto());
                    //Log.i("postagem", "url:" + postagem.getCaminhoFoto());

                }

                int quantPostagens = urlFotos.size();
                textPublicacoes.setText( String.valueOf(quantPostagens) );

                //Configura Adapter
                adapterGrid = new AdapterGrid(getApplicationContext(), R.layout.grid_postagem,urlFotos );
                gridViewPerfil.setAdapter( adapterGrid );

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void empresa() {


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
                        startActivity(new Intent(getApplicationContext(), MenuPrincipalActivity.class));
                        finish();
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

    public void inicializarImageLoader() {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration
                .Builder(this)
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024)
                .diskCacheSize(50 * 1024 * 1024)
                .diskCacheFileCount(100)
                .diskCacheFileNameGenerator(new HashCodeFileNameGenerator())
                .build();
        ImageLoader.getInstance().init(config);
    }

    private void recuperaDadosUsuarioLagodo(){

        usuarioLogadoRef = usuariosRef.child(  usuarioLogado.getId() );
        valueEventListenerPerfil = usuarioLogadoRef.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        final Usuario usuario = dataSnapshot.getValue(Usuario.class);

                        String postagens = String.valueOf(usuario.getPostagens());
                        String seguindo  = String.valueOf(usuario.getSeguindo());
                        String seguidores = String.valueOf(usuario.getSeguidores());

                        //Configura valores recuperado
                        textPublicacoes.setText(postagens);
                        textSeguindo.setText(seguindo);
                        textSeguidores.setText(seguidores);
                        textNome.setText(usuario.getNome());

                            String mod = "sim";
                        if (usuario.getModerador().equals(mod)  ){
                            buttonMod.setVisibility(View.VISIBLE);
                        }

                        if ( usuario.getAvaliation().equals("nulo")){

                            avaliacaoNula();
                            textAvaliacao.setText("Avaliação em Andamento");
                        }



                        final String empresa = "nao";
                        textAvaliacao.setVisibility(View.VISIBLE);

                        if (usuario.getEmpresa().equals(empresa)) {
                            avaliacaoNula();
                        } else {

                            if (usuario.getAvaliation().equals("um")) {

                                avaliacaoUm.setVisibility(View.VISIBLE);
                                avaliacaoDois.setVisibility(View.GONE);
                                avaliacaoTres.setVisibility(View.GONE);
                                avaliacaoQuatro.setVisibility(View.GONE);
                                avaliacaoCinco.setVisibility(View.GONE);
                            } else {
                                if (usuario.getAvaliation().equals("dois")) {
                                    avaliacaoUm.setVisibility(View.GONE);
                                    avaliacaoDois.setVisibility(View.VISIBLE);
                                    avaliacaoTres.setVisibility(View.GONE);
                                    avaliacaoQuatro.setVisibility(View.GONE);
                                    avaliacaoCinco.setVisibility(View.GONE);
                                } else {
                                    if (usuario.getAvaliation().equals("tres")) {
                                        avaliacaoUm.setVisibility(View.GONE);
                                        avaliacaoDois.setVisibility(View.GONE);
                                        avaliacaoTres.setVisibility(View.VISIBLE);
                                        avaliacaoQuatro.setVisibility(View.GONE);
                                        avaliacaoCinco.setVisibility(View.GONE);
                                    } else {
                                        if (usuario.getAvaliation().equals("quatro")) {
                                            avaliacaoUm.setVisibility(View.GONE);
                                            avaliacaoDois.setVisibility(View.GONE);
                                            avaliacaoTres.setVisibility(View.GONE);
                                            avaliacaoQuatro.setVisibility(View.VISIBLE);
                                            avaliacaoCinco.setVisibility(View.GONE);
                                        } else {
                                            if (usuario.getAvaliation().equals("cinco")) {
                                                avaliacaoUm.setVisibility(View.GONE);
                                                avaliacaoDois.setVisibility(View.GONE);
                                                avaliacaoTres.setVisibility(View.GONE);
                                                avaliacaoQuatro.setVisibility(View.GONE);
                                                avaliacaoCinco.setVisibility(View.VISIBLE);
                                            }
                                        }
                                    }
                                }
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );


    }

    private void habilitarBotaoMod(){





    }

    @Override
    protected void onStart() {
        //Recupera dados do usuario
        recuperaDadosUsuarioLagodo();
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        usuarioLogadoRef.removeEventListener(valueEventListenerPerfil);
    }


    public void inicializarcomponentes(){

        imagePerfil = findViewById(R.id.imagePerfil);
        textNome = findViewById(R.id.perfilNomeUsario);
        textAvaliacao = findViewById(R.id.textAvaliacaoPerfil);
        gridViewPerfil = findViewById(R.id.gridtest);
        textPublicacoes = findViewById(R.id.textPublicacoes);
        textSeguidores = findViewById(R.id.textSeguidores);
        textSeguindo = findViewById(R.id.textSeguindo);
        buttonAcaoPerfil = findViewById(R.id.buttonAcaoPerfil);
        buttonAcaoPerfil.setText("Editar perfil");
        buttonMod = findViewById(R.id.buttonMod);
        avaliacaoUm = findViewById(R.id.ratingBarPerfil);
        pageAvaliacao = findViewById(R.id.linearLayout2);


    }

    private void avaliacaoNula(){

        textAvaliacao.setVisibility(View.GONE);
        avaliacaoUm.setVisibility(View.GONE);
        avaliacaoDois.setVisibility(View.GONE);
        avaliacaoTres.setVisibility(View.GONE);
        avaliacaoQuatro.setVisibility(View.GONE);
        avaliacaoCinco.setVisibility(View.GONE);

    }

}
