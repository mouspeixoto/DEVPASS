package com.devpass.pampasistemas.thiagopeixoto.devpass.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.devpass.pampasistemas.thiagopeixoto.devpass.R;
import com.devpass.pampasistemas.thiagopeixoto.devpass.activity_menu_principal.CriarVagasActivity;
import com.devpass.pampasistemas.thiagopeixoto.devpass.activity_menu_principal.EmpregosActivity;
import com.devpass.pampasistemas.thiagopeixoto.devpass.activity_menu_principal.VisualizarCurriculoActivity;
import com.devpass.pampasistemas.thiagopeixoto.devpass.adapter.AdapterGrid;
import com.devpass.pampasistemas.thiagopeixoto.devpass.fragment.PostUsuarioFragment;
import com.devpass.pampasistemas.thiagopeixoto.devpass.fragment.SobreUsuarioFragment;
import com.devpass.pampasistemas.thiagopeixoto.devpass.helper.ConfiguracaoFirebase;
import com.devpass.pampasistemas.thiagopeixoto.devpass.helper.UsuarioFirebase;
import com.devpass.pampasistemas.thiagopeixoto.devpass.model.GanhosPostagem;
import com.devpass.pampasistemas.thiagopeixoto.devpass.model.Postagem;
import com.devpass.pampasistemas.thiagopeixoto.devpass.model.Usuario;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MenuPrincipalActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private CircleImageView imagePerfil, imagePerfilMenu;
    private TextView textPublicacoes, textSeguidores, textSeguindo, textNome, textAvaliacao, textEmailMenu, textNomeMenu;
    private Button buttonAcaoPerfil, buttonMod;
    private ImageView imgNavHeaderBg, imgProfile;
    private Usuario usuarioLogado;
    private GridView gridViewPerfil;
    private AdapterGrid adapterGrid;
    private RatingBar avaliacao;
    private float estrelas;
    private LinearLayout pageAvaliacao;

    private DatabaseReference firebaseRef;
    private DatabaseReference usuariosRef;
    private DatabaseReference usuarioLogadoRef;
    private ValueEventListener valueEventListenerPerfil;
    private ValueEventListener valueEventListenerEmpresa;
    private DatabaseReference postagensUsuarioRef;
    private StorageReference excPostagem;

    private List<Postagem> postagens;
    private String TAG = "Caminho foto";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);

        gridViewPerfil = findViewById(R.id.gridtest);

        //Configuraçoes Iniciais
        excPostagem = ConfiguracaoFirebase.getFirebaseStorage();
        usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();
        firebaseRef = ConfiguracaoFirebase.getFirebase();
        usuariosRef = firebaseRef.child("usuarios");

        //Configura referencias postagens usuario
        postagensUsuarioRef = ConfiguracaoFirebase.getFirebase()
                .child("postagens")
                .child(usuarioLogado.getId());

        Toolbar toolbar = findViewById(R.id.toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);



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
                Intent i = new Intent(MenuPrincipalActivity.this, EditarPerfilActivity.class);
                startActivity(i);
            }
        });

        buttonMod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MenuPrincipalActivity.this, ModeradorActivity.class);
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
            Glide.with(MenuPrincipalActivity.this)
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

        gridViewPerfil.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                final Postagem postagem = postagens.get( position );
                Toast.makeText(MenuPrincipalActivity.this, "Caminho da foto" + postagem.getCaminhoFoto(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Valor " + postagem.getId());

                StorageReference excluirImagem = excPostagem
                        .child("imagens")
                        .child("postagens")
                        .child( postagem.getId() + ".jpeg");
                excluirImagem.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        DatabaseReference excluirPostagem = postagensUsuarioRef
                                .child(postagem.getId());
                        excluirPostagem.removeValue();

                        Toast.makeText(MenuPrincipalActivity.this,
                                "Sucesso em excluir!",
                                Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MenuPrincipalActivity.this,
                                "Algo deu errado, tente novamente!",
                                Toast.LENGTH_SHORT).show();
                    }
                });

                return false;
            }
        });



    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_principal, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();



        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {
            startActivity(new Intent(getApplicationContext(), EmpregosActivity.class));
            return true;

        } else if (id == R.id.nav_slideshow) {
            startActivity(new Intent(getApplicationContext(), CriarVagasActivity.class));
            return true;

        } else if (id == R.id.nav_fireboots) {
            startActivity(new Intent(getApplicationContext(), MeusFirebootsActivity.class));
            return true;
        } else if (id == R.id.nav_share) {
            startActivity(new Intent(getApplicationContext(), VisualizarCurriculoActivity.class));
            return true;

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public void carregarFotosPostagem(){

        //Recuperar posts do ussuario
        postagens = new ArrayList<>();
        postagensUsuarioRef.addValueEventListener(new ValueEventListener() {
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

                        return true;
                    case R.id.ic_perfil_sobre:
                        fragmentTransaction.replace(R.id.viewPagerPerfil, new SobreUsuarioFragment()).commit();
                        gridViewPerfil.setVisibility(View.GONE);
                        String TAG = "MainActivity";
                        Log.e(TAG, "valor " + usuarioLogado.getNome());
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

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        textNomeMenu = headerView.findViewById(R.id.textNomeMenu);
        textNomeMenu.setText("BEM VINDO " + usuarioLogado.getNome() );
        textEmailMenu = headerView.findViewById(R.id.textEmailMenu);
        textEmailMenu.setText(usuarioLogado.getEmail());
        imagePerfilMenu = headerView.findViewById(R.id.imagePerfilMenu);
        String caminhoFoto = usuarioLogado.getCaminhoFoto();
        if( caminhoFoto != null){
            Uri url = Uri.parse(caminhoFoto);
            Glide.with(MenuPrincipalActivity.this)
                    .load(url)
                    .into(imagePerfilMenu)
            ;
        }


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
        avaliacao = findViewById(R.id.ratingBarPerfil);
        pageAvaliacao = findViewById(R.id.linearLayout2);


    }

    private void avaliacaoNula(){

        avaliacao.setVisibility(View.GONE);
        textAvaliacao.setVisibility(View.GONE);
    }
}
