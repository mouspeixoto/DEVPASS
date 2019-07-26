package com.devpass.pampasistemas.thiagopeixoto.devpass.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.devpass.pampasistemas.thiagopeixoto.devpass.R;
import com.devpass.pampasistemas.thiagopeixoto.devpass.fragment.ContatosFragment;
import com.devpass.pampasistemas.thiagopeixoto.devpass.fragment.ConversasFragment;
import com.devpass.pampasistemas.thiagopeixoto.devpass.fragment.FeedFragment;
import com.devpass.pampasistemas.thiagopeixoto.devpass.fragment.PesquisaFragment;
import com.devpass.pampasistemas.thiagopeixoto.devpass.fragment.PostagemFragment;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

/**
 * Created by Thiago Peixoto
 */

public class ChatActivity extends AppCompatActivity {

    private BottomNavigationViewEx botaofeed;
    private MaterialSearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        inicializarComponentes();

        Toolbar toolbar = findViewById(R.id.toolbarChat);
        toolbar.setTitle("Conversas");
        setSupportActionBar( toolbar );

        //Configurar abas
        final FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(),
                FragmentPagerItems.with(this)
                        .add("Conversas", ConversasFragment.class)
                        .add("Contatos", ContatosFragment.class)
                        .create()
        );
        ViewPager viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter( adapter );

        SmartTabLayout viewPagerTab = findViewById(R.id.viewPagerTab);
        viewPagerTab.setViewPager( viewPager );

        configuraBottomNavigationView();

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        //Configuração do search view
        searchView = findViewById(R.id.materialSearchPrincipal);
        //Listener para o search view
        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {

            }

            @Override
            public void onSearchViewClosed() {

                ConversasFragment fragment = (ConversasFragment) adapter.getPage(0);
                fragment.recarregarConversas();

            }
        });

        //Listener para caixa de texto
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //Log.d("evento", "onQueryTextSubmit");
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Log.d("evento", "onQueryTextChange");

                ConversasFragment fragment = (ConversasFragment) adapter.getPage(0);
                if( newText != null && !newText.isEmpty() ){
                    fragment.pesquisarConversas( newText.toLowerCase() );
                }

                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_chat, menu);

        //Configurar botao de pesquisa
        MenuItem item = menu.findItem(R.id.pesquisaChat);
        searchView.setMenuItem(item);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch ( item.getItemId() ){
            case R.id.menuConfiguracoes :
                startActivity(new Intent(getApplicationContext(), EditarPerfilActivity.class));
                finish();
            break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void configuraBottomNavigationView(){

        BottomNavigationViewEx bottomNavigationViewEx = findViewById(R.id.bottomNavigationChat);

        //faz configurações iniciais do Bottom Navigation
        bottomNavigationViewEx.enableAnimation(true);
        bottomNavigationViewEx.enableItemShiftingMode(true);
        bottomNavigationViewEx.enableShiftingMode(false);
        bottomNavigationViewEx.setTextVisibility(true);


        //Habilitar navegação
        habilitarNavegacao( bottomNavigationViewEx );

        //configura item selecionado inicialmente
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(1);
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

                    case R.id.ic_perfil_chat:
                        startActivity(new Intent(getApplicationContext(), UsuarioActivity.class));
                        finish();
                        return true;

                    case R.id.ic_home_chat:
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                        return true;
                }

                return false;
            }
        });

    }

    public void inicializarComponentes() {
        botaofeed = findViewById(R.id.bottomNavigationChat);
    }


}
