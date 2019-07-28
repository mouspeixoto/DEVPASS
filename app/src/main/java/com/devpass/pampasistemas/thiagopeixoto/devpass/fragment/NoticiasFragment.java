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
import android.widget.Toast;

import com.devpass.pampasistemas.thiagopeixoto.devpass.R;
import com.devpass.pampasistemas.thiagopeixoto.devpass.activity.VideoActivity;
import com.devpass.pampasistemas.thiagopeixoto.devpass.activity.ViewNoticiasActivity;
import com.devpass.pampasistemas.thiagopeixoto.devpass.activity.VisualizarPostagemActivity;
import com.devpass.pampasistemas.thiagopeixoto.devpass.adapter.AdapterFeed;
import com.devpass.pampasistemas.thiagopeixoto.devpass.adapter.AdapterNoticias;
import com.devpass.pampasistemas.thiagopeixoto.devpass.helper.ConfiguracaoFirebase;
import com.devpass.pampasistemas.thiagopeixoto.devpass.helper.RecyclerItemClickListener;
import com.devpass.pampasistemas.thiagopeixoto.devpass.helper.UsuarioFirebase;
import com.devpass.pampasistemas.thiagopeixoto.devpass.model.Feed;
import com.devpass.pampasistemas.thiagopeixoto.devpass.model.Noticias;
import com.devpass.pampasistemas.thiagopeixoto.devpass.model.Postagem;
import com.devpass.pampasistemas.thiagopeixoto.devpass.model.Video;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class NoticiasFragment extends Fragment {

    private String idUsuarioLogado;
    private DatabaseReference NoticiasRef;
    private RecyclerView recyclerNoticias;
    private AdapterNoticias adapterNoticias;
    private List<Noticias> listaNoticias = new ArrayList<>();
    private ValueEventListener valueEventListenerNoticias;
    private Noticias noticias;


    public NoticiasFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_noticias, container, false);

        //Configurações iniciais
        idUsuarioLogado = UsuarioFirebase.getIdentificadorUsuario();
        NoticiasRef = ConfiguracaoFirebase.getFirebase()
                .child("noticias");


        //Inicializar componentes
        recyclerNoticias = view.findViewById(R.id.recyclerNoticias);

        //Configura recyclerview
        adapterNoticias = new AdapterNoticias(listaNoticias, getActivity() );
        recyclerNoticias.setHasFixedSize(true);
        recyclerNoticias.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerNoticias.setAdapter( adapterNoticias );


        //Configura evento de clique
        recyclerNoticias.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getActivity(),
                        recyclerNoticias,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {

                                Noticias noticias = listaNoticias.get(position);

                                Intent a = new Intent(getActivity(), ViewNoticiasActivity.class);
                                a.putExtra("url", noticias.getUrlNoticia());
                                startActivity(a);
                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                            }

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            }
                        }
                )
        );
        



        return view;
    }

    private void listarNoticias(){

        valueEventListenerNoticias = NoticiasRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for ( DataSnapshot ds: dataSnapshot.getChildren() ){
                    listaNoticias.add( ds.getValue(Noticias.class) );
                }
                Collections.reverse( listaNoticias );
                adapterNoticias.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    @Override
    public void onStart() {
        super.onStart();
        listaNoticias.clear();
        listarNoticias();
    }

    @Override
    public void onStop() {
        super.onStop();
        NoticiasRef.removeEventListener( valueEventListenerNoticias );
    }


}