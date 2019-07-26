package com.devpass.pampasistemas.thiagopeixoto.devpass.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.devpass.pampasistemas.thiagopeixoto.devpass.R;
import com.devpass.pampasistemas.thiagopeixoto.devpass.helper.ConfiguracaoFirebase;
import com.devpass.pampasistemas.thiagopeixoto.devpass.model.Feed;
import com.devpass.pampasistemas.thiagopeixoto.devpass.model.Item;
import com.devpass.pampasistemas.thiagopeixoto.devpass.model.Video;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Thiago Peixoto
 */

public class AdapterVideo extends RecyclerView.Adapter<AdapterVideo.MyViewHolder> {

    private List<Video> videos = new ArrayList<>();
    private Context context;



    public AdapterVideo(List<Video> videos, Context context) {
        this.videos = videos;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemVideo = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_video, parent, false);
        return new AdapterVideo.MyViewHolder(itemVideo);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        Video video = videos.get( position );
        Feed feed = new Feed();



        Log.e("Id Feed", "DADOS : " + video.getId());
        Log.e("Id Feed", "DADOS : " + video.getDescricao());
        Log.e("Id Feed", "DADOS : " + video.getVideoID());
        Log.e("Id Feed", "DADOS : " + video.getCapa());
        Log.e("Id Feed", "DADOS : " + video.getTitulo());
        Log.e("Id Feed", "DADOS : " + video.getData());
        Log.e("Id Feed", "DADOS : " + video.getFotoPostagem());


        holder.titulo.setText( video.getTitulo());
        String url = video.getFotoPostagem();
        Picasso.get().load(url).into(holder.capa);
        holder.nomeUsuario.setText( video.getNomeUsuario() );
        holder.statusUser.setText( video.getUserStatus());

        Uri uriFotoUsuario = Uri.parse( video.getFotoUsuario() );
        Glide.with( context ).load( uriFotoUsuario ).into(holder.fotoPerfil);



    }

    @Override
    public int getItemCount() {
        return videos.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView titulo, nomeUsuario, statusUser;
        TextView descricao;
        TextView data;
        ImageView capa;
        YouTubePlayerView youTubePlayerView;
        String idVideo;
        CircleImageView fotoPerfil;

        public MyViewHolder(View itemView) {
            super(itemView);

           // youTubePlayerView = itemView.findViewById(R.id.playerVideo);
            titulo = itemView.findViewById(R.id.desc_video);
            capa = itemView.findViewById(R.id.video_image);

            fotoPerfil   = itemView.findViewById(R.id.user_image_video);
            statusUser = itemView.findViewById(R.id.user_status_video);
            nomeUsuario = itemView.findViewById(R.id.user_name);

        }
    }

}
