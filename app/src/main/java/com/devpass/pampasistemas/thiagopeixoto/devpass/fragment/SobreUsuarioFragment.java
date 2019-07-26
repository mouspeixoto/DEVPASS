package com.devpass.pampasistemas.thiagopeixoto.devpass.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.devpass.pampasistemas.thiagopeixoto.devpass.R;
import com.devpass.pampasistemas.thiagopeixoto.devpass.helper.ConfiguracaoFirebase;
import com.devpass.pampasistemas.thiagopeixoto.devpass.helper.UsuarioFirebase;
import com.devpass.pampasistemas.thiagopeixoto.devpass.model.Usuario;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class SobreUsuarioFragment extends Fragment {


    private TextView biografia , nomeUsuario;
    private Usuario usuarioLogado;
    private DatabaseReference usuarioLogadoRef;
    private DatabaseReference usuariosRef;
    private ValueEventListener valueEventListenerPerfil;
    private DatabaseReference firebaseRef;

    public SobreUsuarioFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sobre_usuario, container, false);


        //Configuraçoes Iniciais
        usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();
        firebaseRef = ConfiguracaoFirebase.getFirebase();
        usuariosRef = firebaseRef.child("usuarios");

        biografia = view.findViewById(R.id.textBiografiaPerfil);
        nomeUsuario = view.findViewById(R.id.textNomeSobre);




        return view;
    }
    @Override
    public void onStart() {
        //Recupera dados do usuario
        recuperaDadosUsuarioLagodo();
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        usuarioLogadoRef.removeEventListener(valueEventListenerPerfil);
    }


    private void recuperaDadosUsuarioLagodo(){

        usuarioLogadoRef = usuariosRef.child(  usuarioLogado.getId() );
        valueEventListenerPerfil = usuarioLogadoRef.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        Usuario usuario = dataSnapshot.getValue(Usuario.class);

                        biografia.setText(usuario.getBiografia());
                        nomeUsuario.setText(usuario.getNome());


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );


    }



}
