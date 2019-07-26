package com.devpass.pampasistemas.thiagopeixoto.devpass.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.devpass.pampasistemas.thiagopeixoto.devpass.R;
import com.devpass.pampasistemas.thiagopeixoto.devpass.fragment.CriarNoticiaFragment;
import com.devpass.pampasistemas.thiagopeixoto.devpass.fragment.FeedFragment;
import com.devpass.pampasistemas.thiagopeixoto.devpass.fragment.OutrasFragment;
import com.devpass.pampasistemas.thiagopeixoto.devpass.fragment.PesquisaFragment;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

public class ModeradorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moderador);


        configuraBottomNavigationView();

        //Configura toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Bem vindo moderador");
        setSupportActionBar( toolbar );

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_voltar_mod_24dp);

    }

    private void configuraBottomNavigationView(){

        BottomNavigationViewEx bottomNavigationViewEx = findViewById(R.id.mod_bottomNavigation);

        //faz configurações iniciais do Bottom Navigation
        bottomNavigationViewEx.enableAnimation(true);
        bottomNavigationViewEx.enableItemShiftingMode(true);
        bottomNavigationViewEx.enableShiftingMode(false);
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
                    case R.id.ic_mod_noticias :
                        Toast.makeText(ModeradorActivity.this, "Noticia", Toast.LENGTH_SHORT).show();
                        fragmentTransaction.replace(R.id.viewMod, new CriarNoticiaFragment()).commit();
                        return true;
                    case R.id.ic_mod_outras :
                        Toast.makeText(ModeradorActivity.this, "Outras", Toast.LENGTH_SHORT).show();
                        fragmentTransaction.replace(R.id.viewMod, new OutrasFragment()).commit();
                        return true;
                }

                return false;
            }
        });

    }


}
