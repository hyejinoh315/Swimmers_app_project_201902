package com.example.myswimming.기타;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.myswimming.R;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;


public class PlayActivity extends YouTubeBaseActivity {

    private YouTubePlayerView myPlayer;
    YouTubePlayer.OnInitializedListener listener;
    Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        myPlayer = findViewById(R.id.myPlayer1);

        intent = getIntent();

        listener = new YouTubePlayer.OnInitializedListener() { //문제 : 최초 1회만 실행된다
            //팀장님 조언 : 복잡하게 만들어 놨는데 굳이 리스너를 따로 써야하나? 바로 재생되도록.
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                String uri = intent.getStringExtra("uri");
                Log.e("주소불러오기디버깅", "onClick: "+ uri);
                youTubePlayer.loadVideo(uri);
            }
            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

            }
        };

        myPlayer.initialize("AIzaSyCV-cPfHIpuUTLVpt7CAnnl8QP93-TaxV4", listener);
    }

}
