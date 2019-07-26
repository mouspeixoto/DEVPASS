package com.devpass.pampasistemas.thiagopeixoto.devpass.activity_menu_principal;

import android.content.Context;
import android.net.Uri;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.devpass.pampasistemas.thiagopeixoto.devpass.R;
import com.devpass.pampasistemas.thiagopeixoto.devpass.helper.ConfiguracaoFirebase;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.util.FitPolicy;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class VisualizarCurriculoActivity extends AppCompatActivity  {

    PDFView pdfView;
    DatabaseReference databaseReference;
    private DatabaseReference firebaseRef;
    String TAG = "URL = ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizar_curriculo);

        firebaseRef = ConfiguracaoFirebase.getFirebase();
        databaseReference = firebaseRef.child("pdf");

        pdfView = findViewById(R.id.pdfView);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String string = dataSnapshot.getValue().toString();
                Log.e(TAG, "  " + string);

                new RetrivePdfStrem().execute(string);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });





    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    class RetrivePdfStrem extends AsyncTask<String,Void, InputStream>{
            @Override
            protected InputStream doInBackground(String... strings) {
                InputStream inputStream = null;
                try {
                    URL url = new URL(strings[0]);
                    HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
                    if (urlConnection.getResponseCode() == 200){
                        inputStream = new BufferedInputStream(urlConnection.getInputStream());

                    }
                }catch (IOException e){
                    return null;
                }
                return inputStream;
            }
            protected void onPostExecute(InputStream inputStream){
                pdfView.fromStream(inputStream).load();
            }
        }


    }

