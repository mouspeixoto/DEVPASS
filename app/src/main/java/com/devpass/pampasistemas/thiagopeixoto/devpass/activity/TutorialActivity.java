package com.devpass.pampasistemas.thiagopeixoto.devpass.activity;

import android.graphics.Color;
import android.os.Bundle;

import com.devpass.pampasistemas.thiagopeixoto.devpass.R;
import com.heinrichreimersoftware.materialintro.app.IntroActivity;
import com.heinrichreimersoftware.materialintro.slide.FragmentSlide;
import com.heinrichreimersoftware.materialintro.slide.SimpleSlide;

public class TutorialActivity extends IntroActivity {

    @Override protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        addSlide(new FragmentSlide.Builder()
                .background(R.color.preto_trasparente)
                .fragment(R.layout.intro_1)
                .build()
        );
    }

}
