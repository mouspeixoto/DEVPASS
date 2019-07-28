package com.devpass.pampasistemas.thiagopeixoto.devpass.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import com.devpass.pampasistemas.thiagopeixoto.devpass.R;

public class ViewNoticiasActivity extends AppCompatActivity {

    private WebView webViewNoticias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_noticias);

        webViewNoticias = findViewById(R.id.webViewNoticias);


        Bundle bundle = getIntent().getExtras();
        if( bundle != null ){
            String url;
            url = bundle.getString("url");
            if (url != null) {
                webViewNoticias.loadUrl(url);
                Toast.makeText(this,"Sucesso em carregar",Toast.LENGTH_SHORT).show();
            }
        }

    }
}
