package com.devpass.pampasistemas.thiagopeixoto.devpass.activity;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.devpass.pampasistemas.thiagopeixoto.devpass.R;
import com.devpass.pampasistemas.thiagopeixoto.devpass.model.Noticias;
import com.devpass.pampasistemas.thiagopeixoto.devpass.model.Postagem;
import com.devpass.pampasistemas.thiagopeixoto.devpass.model.Usuario;

import de.hdodenhof.circleimageview.CircleImageView;

public class VisualizarNoticiaActivity extends AppCompatActivity {

    private TextView textPerfilPostagem,textTitulo,
            textDescricaoPostagem,textVisualizarComentariosPostagem;
    private ImageView imagePostagemSelecionada;
    private CircleImageView imagePerfilPostagem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizar_noticia);

        inicializarComponentes();

        //Configura toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Visualizar noticia");
        setSupportActionBar( toolbar );

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_voltar_24dp);


        //Recupera dados da activity
        Bundle bundle = getIntent().getExtras();
        if( bundle != null ) {

            Noticias noticias = (Noticias) bundle.getSerializable("noticia");

            //Exibe dados da postagem
            Uri uriPostagem = Uri.parse(noticias.getFotoNoticia());
            Glide.with(VisualizarNoticiaActivity.this)
                    .load(uriPostagem)
                    .into(imagePostagemSelecionada);
            textDescricaoPostagem.setText(noticias.getDescricao());





    }




}

    private void inicializarComponentes() {
        textTitulo = findViewById(R.id.textTituloNoticia);
        textDescricaoPostagem = findViewById(R.id.textDescricaoPostagem);
        imagePostagemSelecionada = findViewById(R.id.imageNoticia);
    }

}
