package com.devpass.pampasistemas.thiagopeixoto.devpass.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.devpass.pampasistemas.thiagopeixoto.devpass.R;
import com.devpass.pampasistemas.thiagopeixoto.devpass.activity.ConversasActivity;
import com.devpass.pampasistemas.thiagopeixoto.devpass.adapter.ContatosAdapter;
import com.devpass.pampasistemas.thiagopeixoto.devpass.helper.ConfiguracaoFirebase;
import com.devpass.pampasistemas.thiagopeixoto.devpass.helper.RecyclerItemClickListener;
import com.devpass.pampasistemas.thiagopeixoto.devpass.helper.UsuarioFirebase;
import com.devpass.pampasistemas.thiagopeixoto.devpass.model.Usuario;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContatosFragment extends Fragment {

    private RecyclerView recyclerViewListaContatos;
    private ContatosAdapter adapter;
    private ArrayList<Usuario> listaContatos = new ArrayList<>();

    private DatabaseReference usuariosRef;
    private ValueEventListener valueEventListenerContatos;

    private FirebaseUser usuarioAtual;


    public ContatosFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contatos, container, false);

        //configuraçoes iniciais
        recyclerViewListaContatos = view.findViewById(R.id.recyclerViewListaContatos);
        usuariosRef = ConfiguracaoFirebase.getFirebase().child("usuarios");
        usuarioAtual = UsuarioFirebase.getUsuarioAtual();

        //Conf adapter
        adapter = new ContatosAdapter( listaContatos, getActivity() );

        //Configurar adapter
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager( getActivity() );
        recyclerViewListaContatos.setLayoutManager( layoutManager);
        recyclerViewListaContatos.setHasFixedSize(true);
        recyclerViewListaContatos.setAdapter( adapter );

        //Configurar o recyclerView
        recyclerViewListaContatos.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getActivity(),
                        recyclerViewListaContatos,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {

                                Usuario usuarioSelecionado = listaContatos.get( position );
                                Intent i = new Intent(getActivity(), ConversasActivity.class);
                                i.putExtra("chatContato", usuarioSelecionado );
                                startActivity( i );


                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                            }

                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                            }
                        }
                )
        );


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        recuperarContatos();
        listaContatos.clear();
    }

    @Override
    public void onStop() {
        super.onStop();
        usuariosRef.removeEventListener( valueEventListenerContatos );
    }

    public void recuperarContatos(){

        valueEventListenerContatos = usuariosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for ( DataSnapshot dados: dataSnapshot.getChildren() ){

                    Usuario usuario = dados.getValue( Usuario.class );

                    String emailUsuarioAtual = usuarioAtual.getEmail();
                    if ( !emailUsuarioAtual.equals( usuario.getEmail())){
                        listaContatos.add( usuario );

                    }

                }

                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

}
