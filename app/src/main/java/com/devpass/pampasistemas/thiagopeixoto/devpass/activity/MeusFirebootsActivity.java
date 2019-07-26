package com.devpass.pampasistemas.thiagopeixoto.devpass.activity;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.devpass.pampasistemas.thiagopeixoto.devpass.R;
import com.devpass.pampasistemas.thiagopeixoto.devpass.helper.ConfiguracaoFirebase;
import com.devpass.pampasistemas.thiagopeixoto.devpass.helper.UsuarioFirebase;
import com.devpass.pampasistemas.thiagopeixoto.devpass.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class MeusFirebootsActivity extends AppCompatActivity {

    private Usuario usuarioLogado;
    private DatabaseReference firebaseRef;
    private DatabaseReference usuariosRef;
    private DatabaseReference usuarioLogadoRef;
    private ValueEventListener valueEventListenerPerfil;

    private Button buttonAbrirComprar;

    private TextView textQtdBoots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meus_fireboots);

        inicializarcomponentes();

        //Configuraçoes Iniciais
        usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();
        firebaseRef = ConfiguracaoFirebase.getFirebase();
        usuariosRef = firebaseRef.child("usuarios");

        //Configura toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Voltar");
        setSupportActionBar( toolbar );

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_voltar_24dp);

        buttonAbrirComprar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MeusFirebootsActivity.this,
                        "Seção esta em desenvolvimento",
                        Toast.LENGTH_SHORT).show();
                //startActivity(new Intent(getApplicationContext(), ComprarActivity.class));
                //finish();
            }
        });


    }

    @Override
    public boolean onSupportNavigateUp() {
        startActivity(new Intent(getApplicationContext(), MenuPrincipalActivity.class));
        finish();
        return true;
    }

    private void recuperaDadosUsuarioLagodo(){

        usuarioLogadoRef = usuariosRef.child(  usuarioLogado.getId() );
        valueEventListenerPerfil = usuarioLogadoRef.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        final Usuario usuario = dataSnapshot.getValue(Usuario.class);

                        String qtdBoots = String.valueOf(usuario.getBoostFire());

                        //Configura valores recuperado
                        textQtdBoots.setText(qtdBoots);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );


    }

    public void inicializarcomponentes(){

        textQtdBoots = findViewById(R.id.qtdBoots);
        buttonAbrirComprar = findViewById(R.id.buttonAbrirComprar);
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

}
