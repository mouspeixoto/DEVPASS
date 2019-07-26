package com.devpass.pampasistemas.thiagopeixoto.devpass.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.devpass.pampasistemas.thiagopeixoto.devpass.R;
import com.devpass.pampasistemas.thiagopeixoto.devpass.activity.PerfilAmigoActivity;
import com.devpass.pampasistemas.thiagopeixoto.devpass.activity.VideoActivity;
import com.devpass.pampasistemas.thiagopeixoto.devpass.activity_menu_principal.VagaActivity;
import com.devpass.pampasistemas.thiagopeixoto.devpass.adapter.AdapterNoticias;
import com.devpass.pampasistemas.thiagopeixoto.devpass.adapter.AdapterVagas;
import com.devpass.pampasistemas.thiagopeixoto.devpass.adapter.AdapterVideo;
import com.devpass.pampasistemas.thiagopeixoto.devpass.helper.ConfiguracaoFirebase;
import com.devpass.pampasistemas.thiagopeixoto.devpass.helper.RecyclerItemClickListener;
import com.devpass.pampasistemas.thiagopeixoto.devpass.helper.UsuarioFirebase;
import com.devpass.pampasistemas.thiagopeixoto.devpass.model.Noticias;
import com.devpass.pampasistemas.thiagopeixoto.devpass.model.Usuario;
import com.devpass.pampasistemas.thiagopeixoto.devpass.model.Vagas;
import com.devpass.pampasistemas.thiagopeixoto.devpass.model.Video;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class VagasFragment extends Fragment {

    private SearchView searchViewPesquisaVagas;
    private RecyclerView recyclerPesquisaVagas;

    private List<Vagas> listaVagas = new ArrayList<>();
    private DatabaseReference vagasRef;
    private AdapterVagas adapterVagas;
    private String idUsuarioLogado;

    private ValueEventListener valueEventListenerVagas;


    public VagasFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_vagas, container, false);

        //Configurações iniciais
        idUsuarioLogado = UsuarioFirebase.getIdentificadorUsuario();
        vagasRef = ConfiguracaoFirebase.getFirebase()
                .child("vagas");

        //Inicia componentes
        searchViewPesquisaVagas =  view.findViewById(R.id.searchViewVagas);
        recyclerPesquisaVagas   =  view.findViewById(R.id.recyclerVagas);

        return view;
    }

    private void listarNoticias(){

        valueEventListenerVagas = vagasRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for ( DataSnapshot ds: dataSnapshot.getChildren() ){
                    listaVagas.add( ds.getValue(Vagas.class) );
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

        //Configura recyclerview
        adapterVagas = new AdapterVagas(listaVagas, getActivity() );
        recyclerPesquisaVagas.setHasFixedSize(true);
        recyclerPesquisaVagas.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerPesquisaVagas.setAdapter( adapterVagas );


        //Configura evento de clique

        recyclerPesquisaVagas.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getActivity(),
                        recyclerPesquisaVagas,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {

                                Vagas vagas = listaVagas.get(position);
                                String idVaga = vagas.getIdVaga();

                              Intent a = new Intent(getActivity(), VagaActivity.class);
                                a.putExtra("idVaga", idVaga);
                                a.putExtra("desc", vagas.getDescricaoVaga());
                                a.putExtra("titulo", vagas.getTituloVaga());
                                a.putExtra("tipo",  vagas.getTipoVaga());
                                a.putExtra("cargo",  vagas.getCargo());
                                a.putExtra("cidade",  vagas.getCidade() + "  " + vagas.getEstadod());
                               // a.putExtra("estado",  vagas.getEstadod());
                                a.putExtra("salario",  vagas.getSalario());
                                a.putExtra("local",  vagas.getLocal());

                                a.putExtra("fotoUsuario",  vagas.getFotoUsuario());
                                a.putExtra("nomeUsuario",  vagas.getNomeUsuario());
                                a.putExtra("avaliation", vagas.getAvaliacaoEmpresa());
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

    }

    @Override
    public void onStart() {
        super.onStart();
        listaVagas.clear();
        listarNoticias();
    }

    @Override
    public void onStop() {
        super.onStop();
        vagasRef.removeEventListener( valueEventListenerVagas );
    }

}
