package com.devpass.pampasistemas.thiagopeixoto.devpass.activity_menu_principal;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.devpass.pampasistemas.thiagopeixoto.devpass.R;
import com.devpass.pampasistemas.thiagopeixoto.devpass.activity.CriarPostagemActivity;
import com.devpass.pampasistemas.thiagopeixoto.devpass.activity.MenuPrincipalActivity;
import com.devpass.pampasistemas.thiagopeixoto.devpass.helper.ConfiguracaoFirebase;
import com.devpass.pampasistemas.thiagopeixoto.devpass.helper.UsuarioFirebase;
import com.devpass.pampasistemas.thiagopeixoto.devpass.model.Noticias;
import com.devpass.pampasistemas.thiagopeixoto.devpass.model.Usuario;
import com.devpass.pampasistemas.thiagopeixoto.devpass.model.Vagas;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class CriarVagasActivity extends AppCompatActivity {

    private TextInputEditText textTituloVaga,textEstado, textCidade, textLocal, textSalario, textTipoVaga, textCargo, textDescVaga;
    private Button buttonAnunciar;
    private String idUsuarioLogado;
    private String nomeUsuario, localFotoUsuario;

    private Usuario usuarioLogado;
    private DatabaseReference firebaseRef;
    private DatabaseReference usuariosRef;
    private DatabaseReference usuarioLogadoRef;
    private ValueEventListener valueEventListenerPerfil;
    private ValueEventListener valueEventListenerEmpresa;
    private DatabaseReference postagensUsuarioRef;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criar_vagas);

        //Configuraçoes Iniciais
        usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();
        firebaseRef = ConfiguracaoFirebase.getFirebase();
        usuariosRef = firebaseRef.child("usuarios");



        //Inicializar componentes de tela
        inicializarComponentes();

        buttonAnunciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                publicarNoticia();
            }
        });

    }

    private void abrirDialogCarregamento(String titulo){

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle( titulo );
        alert.setCancelable(false);
        alert.setView(R.layout.carregamento);

        dialog = alert.create();
        dialog.show();

    }

    private void publicarNoticia() {

        DatabaseReference usuarioRef = ConfiguracaoFirebase.getFirebase()
                .child("usuarios")
                .child( usuarioLogado.getId() );
        usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                final Usuario usuario = dataSnapshot.getValue(Usuario.class);

                abrirDialogCarregamento("Salvando postagem");
                final Vagas vagas = new Vagas();
                // vagas.setIdVaga(idUsuarioLogado);
                String valor = textSalario.toString();

                vagas.setDescricaoVaga(textDescVaga.getText().toString());
                vagas.setTituloVaga(textTituloVaga.getText().toString());
                vagas.setEstadod(textEstado.getText().toString());
                vagas.setCidade(textCidade.getText().toString());
                vagas.setCargo(textCargo.getText().toString());
                vagas.setTipoVaga(textTipoVaga.getText().toString());
                vagas.setSalario(textSalario.getText().toString());
                vagas.setLocal(textLocal.getText().toString());
                vagas.setNomeUsuario(usuario.getNome());
                vagas.setFotoUsuario(usuario.getCaminhoFoto());
                vagas.setAvaliacaoEmpresa(usuario.getAvaliation());



                //Salvar postagem
                if (vagas.salvar()) {


                    Toast.makeText(CriarVagasActivity.this,
                            "Sucesso ao salvar postagem!" + usuario.getAvaliation(),
                            Toast.LENGTH_SHORT).show();
                    dialog.cancel();
                    startActivity(new Intent(getApplicationContext(), MenuPrincipalActivity.class));
                    finish();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



            }




    private void inicializarComponentes(){
        textTipoVaga = findViewById(R.id.textTipoVaga);
        textTituloVaga = findViewById(R.id.textTituloVagas);
        textEstado = findViewById(R.id.textEstado);
        textCidade = findViewById(R.id.textCidade);
        textLocal = findViewById(R.id.textLocal);
        textSalario = findViewById(R.id.textSalario);
        textCargo = findViewById(R.id.textCargo);
        textDescVaga = findViewById(R.id.textDescVaga);
        buttonAnunciar = findViewById(R.id.buttonAnunciar);
    }

}
