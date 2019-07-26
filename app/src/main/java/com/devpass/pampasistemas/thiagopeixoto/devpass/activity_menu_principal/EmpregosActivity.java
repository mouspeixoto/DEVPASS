package com.devpass.pampasistemas.thiagopeixoto.devpass.activity_menu_principal;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.MenuItem;

import com.devpass.pampasistemas.thiagopeixoto.devpass.R;
import com.devpass.pampasistemas.thiagopeixoto.devpass.adapter.AdapterVagas;
import com.devpass.pampasistemas.thiagopeixoto.devpass.fragment.CandidaturasFragment;
import com.devpass.pampasistemas.thiagopeixoto.devpass.fragment.FeedFragment;
import com.devpass.pampasistemas.thiagopeixoto.devpass.fragment.MundiaFeedFragment;
import com.devpass.pampasistemas.thiagopeixoto.devpass.fragment.NoticiasFragment;
import com.devpass.pampasistemas.thiagopeixoto.devpass.fragment.VagasFragment;
import com.devpass.pampasistemas.thiagopeixoto.devpass.model.Vagas;
import com.google.firebase.database.DatabaseReference;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.List;

public class EmpregosActivity extends AppCompatActivity {

    private SearchView searchViewPesquisaVagas;
    private RecyclerView recyclerPesquisaVagas;

    private List<Vagas> listaVagas;
    private DatabaseReference usuariosRef;
    private AdapterVagas adapterVagas;
    private String idUsuarioLogado;

    private BottomNavigationViewEx botaoVagas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empregos);


        botaoVagas = findViewById(R.id.empregos_bottomNavigation);

        configuraBottomNavigationViewMenu();

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.viewPagerVagas, new VagasFragment()).commit();



    }

    private void configuraBottomNavigationViewMenu(){


        BottomNavigationViewEx bottomNavigationViewEx = findViewById(R.id.empregos_bottomNavigation);

        //faz configuraçoes iniciais do bottom Navigation
        bottomNavigationViewEx.enableAnimation(true);
        bottomNavigationViewEx.enableItemShiftingMode(true);
        bottomNavigationViewEx.enableShiftingMode(true);
        bottomNavigationViewEx.setTextVisibility(true);

        //Habilitar navegação
        habilitarNavegacaoMenu(bottomNavigationViewEx);
    }

    private void habilitarNavegacaoMenu(BottomNavigationViewEx viewEx) {

        viewEx.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                switch (item.getItemId()) {

                    case R.id.ic_emprego_seguindo:
                        fragmentTransaction.replace(R.id.viewPagerVagas, new VagasFragment()).commit();
                        return true;
                    case R.id.ic_emprego_PQ:
                        fragmentTransaction.replace(R.id.viewPagerVagas, new CandidaturasFragment()).commit();
                        return true;
                }

                return false;
            }
        });
    }

}
