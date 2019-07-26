package com.devpass.pampasistemas.thiagopeixoto.devpass.activity;

import android.os.Bundle;

import com.devpass.pampasistemas.thiagopeixoto.devpass.model.Video;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.devpass.pampasistemas.thiagopeixoto.devpass.R;


public class PlayerActivity extends YouTubeBaseActivity
             implements YouTubePlayer.OnInitializedListener {

    private YouTubePlayerView youTubePlayerView;
    private String idVideo;
    private Video video;
    private static final String GOOGLE_API_KEY = "AIzaSyDqZvVzGhiydIoLm0r9a6J_naAzOGz-Vws";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        //Configura componentes
        youTubePlayerView = findViewById(R.id.playerVideo);

         Bundle bundle = getIntent().getExtras();
        if( bundle != null ){
          //  video = (Video) bundle.getSerializable("videos") ;
            idVideo = bundle.getString("videos");
            youTubePlayerView.initialize(GOOGLE_API_KEY, this);
        }



    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {

     //   youTubePlayer.setFullscreen(true);
     //   youTubePlayer.setShowFullscreenButton(false);
     //   youTubePlayer.loadVideo( idVideo );

    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

    }
}
