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
import com.devpass.pampasistemas.thiagopeixoto.devpass.activity.MainActivity;
import com.devpass.pampasistemas.thiagopeixoto.devpass.activity.PerfilAmigoActivity;
import com.devpass.pampasistemas.thiagopeixoto.devpass.activity.PerfilUsuarioActivity;
import com.devpass.pampasistemas.thiagopeixoto.devpass.activity.PlayerActivity;
import com.devpass.pampasistemas.thiagopeixoto.devpass.activity.VideoActivity;
import com.devpass.pampasistemas.thiagopeixoto.devpass.adapter.AdapterFeed;
import com.devpass.pampasistemas.thiagopeixoto.devpass.adapter.AdapterVideo;
import com.devpass.pampasistemas.thiagopeixoto.devpass.helper.ConfiguracaoFirebase;
import com.devpass.pampasistemas.thiagopeixoto.devpass.helper.RecyclerItemClickListener;
import com.devpass.pampasistemas.thiagopeixoto.devpass.helper.RecyclerTouchListener;
import com.devpass.pampasistemas.thiagopeixoto.devpass.helper.UsuarioFirebase;
import com.devpass.pampasistemas.thiagopeixoto.devpass.model.Feed;
import com.devpass.pampasistemas.thiagopeixoto.devpass.model.Item;
import com.devpass.pampasistemas.thiagopeixoto.devpass.model.Resultado;
import com.devpass.pampasistemas.thiagopeixoto.devpass.model.Usuario;
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
public class MundiaFeedFragment extends Fragment {

    private RecyclerView recyclerVideo;
    private List<Video> videos = new ArrayList<>();
    private AdapterVideo adapterVideo;
    private ValueEventListener valueEventListenerVideo;

    private DatabaseReference videoRef;
    private String idUsuarioLogado;


    public MundiaFeedFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_mundia_feed, container, false);

        //Configurações iniciais
        idUsuarioLogado = UsuarioFirebase.getIdentificadorUsuario();
        videoRef = ConfiguracaoFirebase.getFirebase()
                .child("feed-video")
                .child( idUsuarioLogado );

        //Inicializar componentes
        recyclerVideo = view.findViewById(R.id.recyclerVideos);



        return view;
    }

    private void listarFeed(){

        valueEventListenerVideo = videoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for ( DataSnapshot ds: dataSnapshot.getChildren() ){
                    videos.add( ds.getValue(Video.class) );
                    recuperarVideos();
                }
                if ( dataSnapshot == null){
                    Collections.reverse( videos );
                    adapterVideo.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void recuperarVideos(){

        //Configura recyclerview
        recyclerVideo.setHasFixedSize(true);
        recyclerVideo.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapterVideo = new AdapterVideo(videos, getActivity() );
        recyclerVideo.setAdapter( adapterVideo );

        //Configura evento de clique
       recyclerVideo.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getActivity(),
                        recyclerVideo,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {

                                Video video = videos.get(position);
                                String idVideo = video.getVideoID();

                                Intent a = new Intent(getActivity(), VideoActivity.class);
                                 a.putExtra("videos", idVideo);
                                 a.putExtra("desc", video.getDescricao());
                                 a.putExtra("titulo", video.getTitulo());
                                a.putExtra("idPostagem", video.getId());
                                startActivity(a);



                                Toast.makeText(getContext(),"ID" + idVideo, Toast.LENGTH_SHORT).show();
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
        videos.clear();
        super.onStart();
        listarFeed();


    }

    @Override
    public void onStop() {
        super.onStop();
        videoRef.removeEventListener( valueEventListenerVideo );
    }

}
