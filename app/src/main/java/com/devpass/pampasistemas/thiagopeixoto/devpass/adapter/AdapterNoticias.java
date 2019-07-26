package com.devpass.pampasistemas.thiagopeixoto.devpass.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.devpass.pampasistemas.thiagopeixoto.devpass.R;
import com.devpass.pampasistemas.thiagopeixoto.devpass.activity.ComentariosActivity;
import com.devpass.pampasistemas.thiagopeixoto.devpass.activity.VisualizarNoticiaActivity;
import com.devpass.pampasistemas.thiagopeixoto.devpass.activity.VisualizarPostagemActivity;
import com.devpass.pampasistemas.thiagopeixoto.devpass.helper.ConfiguracaoFirebase;
import com.devpass.pampasistemas.thiagopeixoto.devpass.helper.UsuarioFirebase;
import com.devpass.pampasistemas.thiagopeixoto.devpass.model.Feed;
import com.devpass.pampasistemas.thiagopeixoto.devpass.model.Noticias;
import com.devpass.pampasistemas.thiagopeixoto.devpass.model.PostagemCurtida;
import com.devpass.pampasistemas.thiagopeixoto.devpass.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.like.LikeButton;
import com.like.OnLikeListener;

import java.io.Serializable;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Thiago Peixoto
 */

public class AdapterNoticias  extends RecyclerView.Adapter<AdapterNoticias.MyViewHolder>{

    private List<Noticias> listaNoticias;
    private Context context;

    public AdapterNoticias(List<Noticias> listaNoticias, Context context) {
        this.listaNoticias = listaNoticias;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_noticia_new, parent, false);
        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Noticias noticias = listaNoticias.get(position);
        final Usuario usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();

        //Carrega dados do feed
        Uri uriFotoPostagem = Uri.parse( noticias.getFotoNoticia() );

        Glide.with( context ).load( uriFotoPostagem ).into(holder.fotoPostagem);

     //   holder.descricao.setText( noticias.getDescricao() );
        holder.titulo.setText( noticias.getTituloNoticia() );

        //Adiciona evento de clique nos comentários
      /*  holder.visualizarComentario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, ComentariosActivity.class);
                i.putExtra("idNoticia", noticias.getIdNoticia() );
                context.startActivity( i );
            }
        }); */



    }

    @Override
    public int getItemCount() {
        return listaNoticias.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView titulo, descricao, qtdCurtidas;
        ImageView fotoPostagem, visualizarComentario;
        LikeButton likeButton;

        public MyViewHolder(View itemView) {
            super(itemView);

            titulo = itemView.findViewById(R.id.post_titulo);
            fotoPostagem = itemView.findViewById(R.id.post_img);
            qtdCurtidas  = itemView.findViewById(R.id.textQtdCurtidasPostagem);
            descricao    = itemView.findViewById(R.id.textDescricaoPostagem);

           /* fotoPostagem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view ) {
                    Intent i = new Intent(context.getApplicationContext(), VisualizarNoticiaActivity.class );
                    i.putExtra("noticia", (Serializable) listaNoticias);
                    context.startActivity(i);
                }
            }); */



        }
    }




}
