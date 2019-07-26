package com.devpass.pampasistemas.thiagopeixoto.devpass.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.devpass.pampasistemas.thiagopeixoto.devpass.fragment.MundiaFeedFragment;
import com.devpass.pampasistemas.thiagopeixoto.devpass.fragment.NoticiasFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.devpass.pampasistemas.thiagopeixoto.devpass.R;
import com.devpass.pampasistemas.thiagopeixoto.devpass.fragment.FeedFragment;
import com.devpass.pampasistemas.thiagopeixoto.devpass.fragment.PesquisaFragment;
import com.devpass.pampasistemas.thiagopeixoto.devpass.fragment.PostagemFragment;
import com.devpass.pampasistemas.thiagopeixoto.devpass.helper.ConfiguracaoFirebase;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;
    private BottomNavigationViewEx botaofeed;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inicializarComponentes();

        //configuracoes de objetos
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        botaofeed.setVisibility(View.VISIBLE);

         configuraBottomNavigationViewMenuFeed();
        //Configurar bottom navigation view
        configuraBottomNavigationView();

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.viewPager, new FeedFragment()).commit();

    }

    /**
     * Método responsável por criar a BottomNavigation
     */
    private void configuraBottomNavigationView(){

        BottomNavigationViewEx bottomNavigationViewEx = findViewById(R.id.bottomNavigation);

        //faz configurações iniciais do Bottom Navigation
        bottomNavigationViewEx.enableAnimation(true);
        bottomNavigationViewEx.enableItemShiftingMode(true);
        bottomNavigationViewEx.enableShiftingMode(true);
        bottomNavigationViewEx.setTextVisibility(true);


        //Habilitar navegação
        habilitarNavegacao( bottomNavigationViewEx );

        //configura item selecionado inicialmente
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);

    }

    /**
     * Método responsável por tratar eventos de click na BottomNavigation
     * @param viewEx
     */
    private void habilitarNavegacao(BottomNavigationViewEx viewEx){

        viewEx.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                switch (item.getItemId()){
                    case R.id.ic_home :
                        fragmentTransaction.replace(R.id.viewPager, new FeedFragment()).commit();
                        botaofeed.setVisibility(View.VISIBLE);
                        return true;
                    case R.id.ic_pesquisa :
                        fragmentTransaction.replace(R.id.viewPager, new PesquisaFragment()).commit();
                        botaofeed.setVisibility(View.GONE);
                        return true;
                    case R.id.ic_postagem :
                        fragmentTransaction.replace(R.id.viewPager, new PostagemFragment()).commit();
                        botaofeed.setVisibility(View.GONE);
                        return true;
                    case R.id.ic_perfil:
                        startActivity(new Intent(getApplicationContext(), MenuPrincipalActivity.class));
                        return true;

                    case R.id.ic_chat:
                        startActivity(new Intent(getApplicationContext(), ChatActivity.class));
                        finish();
                        return true;
                }

                return false;
            }
        });

    }

    private void configuraBottomNavigationViewMenuFeed(){
        BottomNavigationViewEx bottomNavigationViewEx = findViewById(R.id.feed_bottomNavigation);

        //faz configuraçoes iniciais do bottom Navigation
        bottomNavigationViewEx.setTextVisibility(false);
        bottomNavigationViewEx.enableItemShiftingMode(true);
        bottomNavigationViewEx.enableAnimation(false);
        bottomNavigationViewEx.enableShiftingMode(false);

        //Habilitar navegação
       habilitarNavegacaoMenuFeed(bottomNavigationViewEx);
    }

    private void habilitarNavegacaoMenuFeed(BottomNavigationViewEx viewEx) {

        viewEx.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                switch (item.getItemId()) {

                    case R.id.ic_feed_seguindo:
                        fragmentTransaction.replace(R.id.viewPager, new NoticiasFragment()).commit();
                        return true;
                    case R.id.ic_feed_PQ:
                        fragmentTransaction.replace(R.id.viewPager, new MundiaFeedFragment()).commit();
                        return true;
                }

                return false;
            }
        });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.menu_sair :
                deslogarUsuario();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void deslogarUsuario(){
        try{
            autenticacao.signOut();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void inicializarComponentes() {
        botaofeed = findViewById(R.id.feed_bottomNavigation);
    }

}
