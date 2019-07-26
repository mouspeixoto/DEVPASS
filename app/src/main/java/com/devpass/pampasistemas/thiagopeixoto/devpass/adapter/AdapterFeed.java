package com.devpass.pampasistemas.thiagopeixoto.devpass.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.devpass.pampasistemas.thiagopeixoto.devpass.model.BoostFire;
import com.devpass.pampasistemas.thiagopeixoto.devpass.model.GanhosPostagem;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.devpass.pampasistemas.thiagopeixoto.devpass.R;
import com.devpass.pampasistemas.thiagopeixoto.devpass.activity.ComentariosActivity;
import com.devpass.pampasistemas.thiagopeixoto.devpass.helper.ConfiguracaoFirebase;
import com.devpass.pampasistemas.thiagopeixoto.devpass.helper.UsuarioFirebase;
import com.devpass.pampasistemas.thiagopeixoto.devpass.model.Feed;
import com.devpass.pampasistemas.thiagopeixoto.devpass.model.PostagemCurtida;
import com.devpass.pampasistemas.thiagopeixoto.devpass.model.Usuario;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.sackcentury.shinebuttonlib.ShineButton;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Thiago Peixoto
 */

public class AdapterFeed extends RecyclerView.Adapter<AdapterFeed.MyViewHolder> {
    String TAG = "MainActivity";
    private List<Feed> listaFeed;
    int[] videoIndexs = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
    private Context context;
    private static final String API_KEY_GOOGLE = "AIzaSyDqZvVzGhiydIoLm0r9a6J_naAzOGz-Vws";


    public AdapterFeed(List<Feed> listaFeed, Context context) {
        this.listaFeed = listaFeed;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_feed_test, parent, false);
        return new MyViewHolder(itemLista);

    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        final Feed feed = listaFeed.get(position);
        final Usuario usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();

        //Carrega dados do feed
        Uri uriFotoUsuario = Uri.parse( feed.getFotoUsuario() );


        String caminhoFoto = feed.getFotoPostagem();
        if( caminhoFoto != null){
            Log.e(TAG, "Nao nulo " + caminhoFoto);
            final Uri uriFotoPostagem = Uri.parse( feed.getFotoPostagem() );
            Glide.with( context ).load( uriFotoPostagem ).into(holder.fotoPostagem);
        }else {
            Log.e(TAG, "nulo " + caminhoFoto);
        }

        Glide.with( context ).load( uriFotoUsuario ).into(holder.fotoPerfil);
        holder.descricao.setText( feed.getDescricao() );
        holder.nome.setText( feed.getNomeUsuario() );
        holder.userStatus.setText( feed.getUserStatus());


        //Adiciona evento de clique nos comentários
        holder.visualizarComentario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, ComentariosActivity.class);
                i.putExtra("idPostagem", feed.getId() );
                context.startActivity( i );
            }
        });


        final DatabaseReference boostFireRef = ConfiguracaoFirebase.getFirebase()
                .child("usuarios")
                .child(usuarioLogado.getId());
        boostFireRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int qtdBoostFire = 0;
                if (dataSnapshot.hasChild("boostFire")){
                    BoostFire boostFire = dataSnapshot.getValue(BoostFire.class);
                    qtdBoostFire = boostFire.getBoostFire();
                }

                final Usuario usuario = new Usuario();
                usuario.setBoostFire( qtdBoostFire);

                final BoostFire boostFire = new BoostFire();
             //   boostFire.setBoostFire(qtdBoostFire);

                DatabaseReference chamarGanhos = ConfiguracaoFirebase.getFirebase()
                        .child("ganhos-postagens")
                        .child(feed.getId());
                chamarGanhos.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        int qtdGanhos = 0;
                        if( dataSnapshot.hasChild("qtdGanhos") ){
                            GanhosPostagem ganhosPostagem = dataSnapshot.getValue( GanhosPostagem.class );
                            qtdGanhos = ganhosPostagem.getQtdGanhos();
                            Log.e(TAG, "qtd " + dataSnapshot.getValue());
                            holder.ganhosPublicacao.setText( qtdGanhos * 5 + ",00");
                        }
                        final GanhosPostagem ganhosPostagem = new GanhosPostagem();
                        ganhosPostagem.setFeed( feed );
                        ganhosPostagem.setUsuario( usuarioLogado );
                        ganhosPostagem.setQtdGanhos( qtdGanhos );
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                holder.boostFire.setOnCheckStateChangeListener(new ShineButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(View view, boolean checked) {

                        if (checked == true){

                            Toast.makeText(view.getContext(), "Se tem certeza clique mais uma vez", Toast.LENGTH_SHORT).show();
                            final int valor = usuario.getBoostFire();
                            Log.e(TAG, "valor " + valor);
                            Log.e(TAG, "valor " + usuarioLogado.getId());

                            DatabaseReference ganhosRef = ConfiguracaoFirebase.getFirebase()
                                    .child("ganhos-postagens")
                                    .child(feed.getId() );
                            ganhosRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(final DataSnapshot dataSnapshot) {


                                    int qtdGanhos = 0;
                                    if( dataSnapshot.hasChild("qtdGanhos") ){
                                        GanhosPostagem ganhosPostagem = dataSnapshot.getValue( GanhosPostagem.class );
                                        qtdGanhos = ganhosPostagem.getQtdGanhos();
                                        holder.ganhosPublicacao.setText( qtdGanhos * 5 + ",00");
                                    }
                                    final GanhosPostagem ganhosPostagem = new GanhosPostagem();
                                    ganhosPostagem.setFeed( feed );
                                    ganhosPostagem.setUsuario( usuarioLogado );
                                    ganhosPostagem.setQtdGanhos( qtdGanhos );


                holder.boostFire.setOnCheckStateChangeListener(new ShineButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(View view, boolean checked) {
                        int boost = valor;
                        if ( boost >= 1) {
                            if (checked == true) {
                                ganhosPostagem.salvarGanho();
                                holder.ganhosPublicacao.setText(ganhosPostagem.qtdGanhos * 5 + ",00");

                                int diminuirValor = boost  - 1;

                                DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
                                Map objeto = new HashMap();
                                objeto.put("/usuarios/" + usuarioLogado.getId() + "/boostFire", diminuirValor );
                                firebaseRef.updateChildren( objeto );

                                Log.e(TAG, "Valor B " + diminuirValor);

                                Toast.makeText(view.getContext(), "Você impulsionou e enviou R$ 5,00", Toast.LENGTH_SHORT).show();


                            } else {
                                Toast.makeText(view.getContext(), "Para impulsionar clique novamente", Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "click " + checked);
                            }
                        }else {
                            Toast.makeText(view.getContext(), "Você não possui BoostFire", Toast.LENGTH_SHORT).show();
                            holder.ganhosPublicacao.setText(ganhosPostagem.qtdGanhos * 5 + ",00");
                            Log.e(TAG, "Valor " + valor);
                        }
                    }
                });
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });


                        }
                    }
                });


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        /*
        postagens-curtidas
            + id_postagem
                + qtdCurtidas
                + id_usuario
                    nome_usuario
                    caminho_foto
        * */
        //Recuperar dados da postagem curtida
        DatabaseReference curtidasRef = ConfiguracaoFirebase.getFirebase()
                .child("postagens-curtidas")
                .child( feed.getId() );
        curtidasRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                int qtdCurtidas = 0;
                if( dataSnapshot.hasChild("qtdCurtidas") ){
                    PostagemCurtida postagemCurtida = dataSnapshot.getValue( PostagemCurtida.class );
                    qtdCurtidas = postagemCurtida.getQtdCurtidas();

                }

                //Verifica se já foi clicado
                if( dataSnapshot.hasChild( usuarioLogado.getId() ) ){
                    holder.likeButton.setLiked(true);
                }else {
                    holder.likeButton.setLiked(false);
                }

                //Monta objeto postagem curtida

                final PostagemCurtida curtida = new PostagemCurtida();
                curtida.setFeed( feed );
                curtida.setUsuario( usuarioLogado );
                curtida.setQtdCurtidas( qtdCurtidas );

                //Adiciona eventos para curtir uma foto
                holder.likeButton.setOnLikeListener(new OnLikeListener() {
                    @Override
                    public void liked(LikeButton likeButton) {
                        curtida.salvar();
                        holder.qtdCurtidas.setText( curtida.getQtdCurtidas() + " curtidas" );
                    }

                    @Override
                    public void unLiked(LikeButton likeButton) {
                        curtida.remover();
                        holder.qtdCurtidas.setText( curtida.getQtdCurtidas() + " curtidas" );
                    }
                });

                holder.qtdCurtidas.setText( curtida.getQtdCurtidas() + " curtidas" );

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }

    @Override
    public int getItemCount() {
        return listaFeed.size();
    }



    public class MyViewHolder extends RecyclerView.ViewHolder {

        CircleImageView fotoPerfil;
        TextView nome, descricao, qtdCurtidas, ganhosPublicacao, userStatus;
        ImageView fotoPostagem, visualizarComentario;
        LikeButton likeButton;
        ShineButton boostFire;



        public MyViewHolder(View itemView) {
            super(itemView);



            fotoPerfil   = itemView.findViewById(R.id.user_image);
            fotoPostagem = itemView.findViewById(R.id.feed_image);
            nome         = itemView.findViewById(R.id.user_name);
            qtdCurtidas  = itemView.findViewById(R.id.textQtdCurtidasPostagem);
            descricao    = itemView.findViewById(R.id.desc_feed);
            visualizarComentario    = itemView.findViewById(R.id.imageComentarioFeed);
            likeButton = itemView.findViewById(R.id.likeButtonFeed);
            boostFire = itemView.findViewById(R.id.po_image2);
            ganhosPublicacao = itemView.findViewById(R.id.ganhosPublicação);
            userStatus = itemView.findViewById(R.id.user_status);
        }
    }


}
