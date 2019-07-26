package com.devpass.pampasistemas.thiagopeixoto.devpass.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.devpass.pampasistemas.thiagopeixoto.devpass.R;
import com.devpass.pampasistemas.thiagopeixoto.devpass.activity_menu_principal.VagaActivity;
import com.devpass.pampasistemas.thiagopeixoto.devpass.adapter.AdapterVagas;
import com.devpass.pampasistemas.thiagopeixoto.devpass.helper.ConfiguracaoFirebase;
import com.devpass.pampasistemas.thiagopeixoto.devpass.helper.RecyclerItemClickListener;
import com.devpass.pampasistemas.thiagopeixoto.devpass.helper.UsuarioFirebase;
import com.devpass.pampasistemas.thiagopeixoto.devpass.model.Candidaturas;
import com.devpass.pampasistemas.thiagopeixoto.devpass.model.PostagemCurtida;
import com.devpass.pampasistemas.thiagopeixoto.devpass.model.Usuario;
import com.devpass.pampasistemas.thiagopeixoto.devpass.model.Vagas;
import com.devpass.pampasistemas.thiagopeixoto.devpass.model.Video;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.like.LikeButton;
import com.like.OnLikeListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.support.constraint.Constraints.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class CandidaturasFragment extends Fragment {

    private RecyclerView recyclerCandidaturas;

    private List<Vagas> listaVagas = new ArrayList<>();
    private DatabaseReference candidaturasRef, vagasRef;
    private AdapterVagas adapterVagas;
    private String idUsuarioLogado;
    private Vagas vagas;
    private String idVagas;
    private String TAG = "Teste";

    private ValueEventListener valueEventListenerCandidaturas, valueEventListenerConfirir;


    public CandidaturasFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_candidaturas, container, false);

        //Configurações iniciais
        idUsuarioLogado = UsuarioFirebase.getIdentificadorUsuario();
        candidaturasRef = ConfiguracaoFirebase.getFirebase()
                .child("candidaturas")
                .child(idUsuarioLogado);




        //Inicia componentes
        recyclerCandidaturas   =  view.findViewById(R.id.recyclerCandidaturas);

        return view;
    }

    private void listarCandidaturas(){


        valueEventListenerCandidaturas = candidaturasRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for ( DataSnapshot ds: dataSnapshot.getChildren() ){
                    listaVagas.add( ds.getValue(Vagas.class) );


                   if (dataSnapshot.hasChild(idUsuarioLogado)){
                       Toast.makeText(getContext(),"SUCESSO" , Toast.LENGTH_SHORT).show();
                   }

                    //Configura recyclerview
                    adapterVagas = new AdapterVagas(listaVagas, getActivity() );
                    recyclerCandidaturas.setHasFixedSize(true);
                    recyclerCandidaturas.setLayoutManager(new LinearLayoutManager(getActivity()));
                    recyclerCandidaturas.setAdapter( adapterVagas );

                    int position = 0;
                    Vagas video = listaVagas.get(position);

                    recuperarVaga();
                }

                Collections.reverse( listaVagas );
                adapterVagas.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void recuperarVaga(){



     /*
        */


        //Configura evento de clique

        recyclerCandidaturas.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getActivity(),
                        recyclerCandidaturas,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {

                                Vagas video = listaVagas.get(position);
                                String idVideo = video.getIdVaga();

                                Toast.makeText(getContext(),"SUCESSO" + idVideo, Toast.LENGTH_SHORT).show();
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

    }


    @Override
    public void onStart() {
        super.onStart();
        listaVagas.clear();
        listarCandidaturas();
    }

    @Override
    public void onStop() {
        super.onStop();
        candidaturasRef.removeEventListener( valueEventListenerCandidaturas );
    }

}
