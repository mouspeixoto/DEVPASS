package com.devpass.pampasistemas.thiagopeixoto.devpass.adapter;


import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.devpass.pampasistemas.thiagopeixoto.devpass.R;
import com.devpass.pampasistemas.thiagopeixoto.devpass.helper.ConfiguracaoFirebase;
import com.devpass.pampasistemas.thiagopeixoto.devpass.model.Candidaturas;
import com.devpass.pampasistemas.thiagopeixoto.devpass.model.PostagemCurtida;
import com.devpass.pampasistemas.thiagopeixoto.devpass.model.QuantidadeCand;
import com.devpass.pampasistemas.thiagopeixoto.devpass.model.Usuario;
import com.devpass.pampasistemas.thiagopeixoto.devpass.model.Vagas;
import com.google.android.exoplayer2.util.Log;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.like.LikeButton;
import com.like.OnLikeListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.support.constraint.Constraints.TAG;

/**
 * Created by Thiago Peixoto
 */

public class AdapterVagas extends RecyclerView.Adapter<AdapterVagas.MyViewHolder> {

    private List<Vagas> listaUsuario;
    private Context context;

    public AdapterVagas(List<Vagas> l, Context c) {
        this.listaUsuario = l;
        this.context = c;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder( ViewGroup parent, int i) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_vagas, parent, false);
        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Vagas vagas = listaUsuario.get(position);
        final String idVaga = vagas.getIdVaga();

        holder.titulo.setText( vagas.getTituloVaga() );

        holder.salario.setText( "R$: " +vagas.getSalario() + ",00" );
        holder.estado.setText(vagas.getEstadod());
        holder.cidade.setText(vagas.getCidade());
        holder.ratingBar.
                setRating(vagas.getAvaliacaoEmpresa());
        holder.tipo.setText(vagas.getTipoVaga());

        if( vagas.getFotoUsuario() != null ){
            Uri uri = Uri.parse( vagas.getFotoUsuario() );
            Glide.with(context).load(uri).into(holder.foto);
        }else {
            holder.foto.setImageResource(R.drawable.avatar);
        }

        Log.e(TAG, "ID" + idVaga);

        //Recuperar dados da postagem curtida
        DatabaseReference candidaturasRef = ConfiguracaoFirebase.getFirebase()
                .child("qtd-candidaturas")
                .child( idVaga );
        candidaturasRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                int qtdCand = 0;
                if( dataSnapshot.hasChild("qtdCandidaturas") ){
                    QuantidadeCand candidaturas = dataSnapshot.getValue( QuantidadeCand.class );
                    qtdCand = candidaturas.getQtdCandidaturas();

                }

                Log.e(TAG, "QTD" + qtdCand);

                if ( qtdCand > 1){
                    holder.qtdCandidaturas.setText( qtdCand + " Candidatos");
                }else {
                    holder.qtdCandidaturas.setText( qtdCand + " Candidato");
                }

            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    @Override
    public int getItemCount() {
        return listaUsuario.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        CircleImageView foto;
        TextView titulo, salario, estado, cidade, tipo, qtdCandidaturas;
        RatingBar ratingBar;

        public MyViewHolder(View itemView) {
            super(itemView);

            foto = itemView.findViewById(R.id.EmpresaVaga);
            titulo = itemView.findViewById(R.id.tituloVaga);
            salario = itemView.findViewById(R.id.salarioVaga);
            estado = itemView.findViewById(R.id.estadoVaga);
            cidade = itemView.findViewById(R.id.cidadeVaga);
            tipo = itemView.findViewById(R.id.tipoVaga);
            qtdCandidaturas = itemView.findViewById(R.id.qtdCandidaturas);
            ratingBar = itemView.findViewById(R.id.avaliacaoEmpresa);

        }
    }
}
