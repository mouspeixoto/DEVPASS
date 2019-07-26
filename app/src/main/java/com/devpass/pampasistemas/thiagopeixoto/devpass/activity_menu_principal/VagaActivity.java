package com.devpass.pampasistemas.thiagopeixoto.devpass.activity_menu_principal;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.devpass.pampasistemas.thiagopeixoto.devpass.R;
import com.devpass.pampasistemas.thiagopeixoto.devpass.activity.PerfilAmigoActivity;
import com.devpass.pampasistemas.thiagopeixoto.devpass.helper.ConfiguracaoFirebase;
import com.devpass.pampasistemas.thiagopeixoto.devpass.helper.UsuarioFirebase;
import com.devpass.pampasistemas.thiagopeixoto.devpass.model.Candidaturas;
import com.devpass.pampasistemas.thiagopeixoto.devpass.model.PostagemCurtida;
import com.devpass.pampasistemas.thiagopeixoto.devpass.model.QuantidadeCand;
import com.devpass.pampasistemas.thiagopeixoto.devpass.model.Usuario;
import com.devpass.pampasistemas.thiagopeixoto.devpass.model.Vagas;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.like.LikeButton;
import com.like.OnLikeListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class VagaActivity extends AppCompatActivity {

    private TextView textNomeEmpresa,textTituloVaga,textCidadeEstado, textLocal, textSalario, textTipoVaga, textCargo, textDescVaga;
    private CircleImageView fotoEmpresa;
    private Usuario usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();
    private Vagas vagas;
    private String vagaId,idVaga,nomeEmpresa, tituloVaga, cidadeEstado, salario, tipoVaga, descVaga, caminhoFoto;
    private static final String TAG = "MyActivity";
    private int  qtdCandidaturas = 0;
    private Float aFloat;
    private RatingBar ratingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vaga);

        //Configura toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Vaga");
        setSupportActionBar( toolbar );

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black_24dp);

        inicializarComponentes();


        Bundle bundle = getIntent().getExtras();
        if( bundle != null ){


            idVaga = bundle.getString("idVaga");
            descVaga = bundle.getString("desc");
            tituloVaga = bundle.getString("titulo");
            tipoVaga = bundle.getString("tipo");
            cidadeEstado = bundle.getString("cidade");
            salario = bundle.getString("salario");
            aFloat = bundle.getFloat("avaliation");
            caminhoFoto = bundle.getString("fotoUsuario");
            nomeEmpresa = bundle.getString("nomeUsuario");

            //Recuperar foto do usuário

            if( caminhoFoto != null ){
                Uri url = Uri.parse( caminhoFoto );
                Glide.with(VagaActivity.this)
                        .load( url )
                        .into( fotoEmpresa );
            }

            textNomeEmpresa.setText(nomeEmpresa);
            textTituloVaga.setText(tituloVaga);
            textSalario.setText("R$: " + salario + ",00" );
            textTipoVaga.setText(tipoVaga);
            textDescVaga.setText(descVaga);
            textCidadeEstado.setText(cidadeEstado);
            ratingBar.setRating(aFloat);

            toolbar.setTitle(tituloVaga);

            vagaId = idVaga;

            qtdCandidaturas = 0;
            DatabaseReference candRef = ConfiguracaoFirebase.getFirebase()
                    .child("qtd-candidaturas")
                    .child( vagaId );
            candRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if( dataSnapshot.hasChild("qtdCandidaturas") ){
                        QuantidadeCand candidaturas = dataSnapshot.getValue( QuantidadeCand.class );
                        qtdCandidaturas = candidaturas.getQtdCandidaturas();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

 private void salvarCandidatura (){

     //Recuperar dados da postagem curtida
     DatabaseReference curtidasRef = ConfiguracaoFirebase.getFirebase()
             .child("candidaturas")
             .child( usuarioLogado.getId() );
     curtidasRef.addListenerForSingleValueEvent(new ValueEventListener() {
         @Override
         public void onDataChange(DataSnapshot dataSnapshot) {

             final Candidaturas candidaturas = new Candidaturas();
             candidaturas.setIdvaga( vagaId );

             candidaturas.setTituloVaga(tituloVaga);
             candidaturas.setCidadeEstado(cidadeEstado);
             candidaturas.setSalario(salario);
             candidaturas.setEmpresacFoto(caminhoFoto);
             candidaturas.setTipoVaga(tipoVaga);
             candidaturas.setNomeEmpresa(nomeEmpresa);
             candidaturas.setDescVaga(descVaga);
             candidaturas.setAvaliacaoEmpresa(aFloat);

             candidaturas.setUsuario( usuarioLogado );
             candidaturas.setQtdCandidaturas( qtdCandidaturas );

             //Verifica se já foi clicado
             if( dataSnapshot.hasChild( idVaga ) ) {
                 maketoast();
                 Log.i(TAG, "EXCLUIR" + aFloat);
             }else {
                 Log.i(TAG, "SALVAR");
                 candidaturas.salvar();
             }


         }

         @Override
         public void onCancelled(DatabaseError databaseError) {

         }
     });

 }

 public void maketoast (){

        Toast.makeText(this,"JA É UM CANDIDATO", Toast.LENGTH_SHORT).show();
 }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_vaga, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch ( item.getItemId() ){
            case R.id.ic_vaga_candidatar :
                //Cadastra usuario na vaga
                salvarCandidatura();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }

    private void inicializarComponentes(){

        fotoEmpresa = findViewById(R.id.imageEmpresa);
        textNomeEmpresa = findViewById(R.id.textNomeEmpresa);
        textTituloVaga = findViewById(R.id.textVagaTitulo);
        textCidadeEstado = findViewById(R.id.textCidadeEstado);
        textSalario = findViewById(R.id.textSalarioVaga);
        textTipoVaga = findViewById(R.id.textTipodaVaga);
        textDescVaga = findViewById(R.id.textViewDescVaga);
        ratingBar =  findViewById(R.id.ratingBar2);

    }
}
