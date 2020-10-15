package com.example.myswimming;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.example.myswimming.add_set.DdayActivity;
import com.example.myswimming.add_set.SizeActivity;
import com.example.myswimming.add_set.YoutubeActivity;
import com.example.myswimming.preLogin.LoginActivity;
import com.example.myswimming.기타.BackPressCloseHandler;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

public class HomeActivity extends YouTubeBaseActivity {
    public static Activity home; // 마이페이지(다른 액티비티)에서 로그아웃 버튼을 누르면 홈 액티비티도 종료시켜야 하기 때문에 스태틱 선언
    private BackPressCloseHandler backPressCloseHandler; // 뒤로가기 누르면 종료 메세지 옵션을 주기 위해 선언(로그인 액티비티와 동일)
    CardView mypage, size, dday, training; // 클릭가능한 카드뷰 생성했으므로 버튼기능을 부여해주기 위해 선언
    private EditText searchText;// 유튜브로 넘어갈 검색어를 입력하는 에딧텍스트
    private Button sButton, like; // 유튜브 찾기 버튼 , 좋아요(공유)한 영상 리스트
    ImageView img1, img2, img3, img4;
    int idx = 0;
    Thread thread;
    Handler handler;
    boolean check;

    YouTubePlayerView you1;
    YouTubePlayer.OnInitializedListener listener;
    FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        home = HomeActivity.this; // 껍데기 변수에 HomeActivity를 넣어줌 -> 다른 액티비티에서 호출, 메소드 이용 가능하다
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        SharedPreferences pref = getSharedPreferences(LoginActivity.etId+"유튜브도움말", MODE_PRIVATE);
        check = pref.getBoolean("유튜브도움말", false);

        Log.e("유튜브도움말", "onCreate: 1" + pref.getBoolean("유튜브도움말", false));
        if(!check){
            AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
            builder.setTitle("SWIMMERS")
                    .setMessage("배우고 싶은 영법을 검색하고 즐겨찾기에 추가해 보세요!\n\n" +
                            "1. 검색 결과로 나온 유튜브 영상의 우측 상단 버튼을 클릭합니다\n\n" +
                            "2. 공유하기 버튼을 클릭하여 스위머즈 앱으로 공유합니다\n\n" +
                            "3. 제목을 설정한 뒤 확인 버튼을 누르면 즐겨찾기에 추가가 됩니다");
            builder.setNegativeButton("다시보지 않을래요", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SharedPreferences pref = getSharedPreferences(LoginActivity.etId+"유튜브도움말", MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
/*에디터 선언 안하고 하드코딩으로 했더니 저장이 안됐었음*/
                    editor.putBoolean("유튜브도움말", true);
                    editor.commit();
                    Log.e("유튜브도움말", "onCreate: 2" + pref.getBoolean("유튜브도움말", false));
                    dialog.dismiss();
                }
            })
            .setPositiveButton("닫기", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).show();
        }

        //Toast.makeText(this, "로그인 되었습니다", Toast.LENGTH_SHORT).show();
        // 홈화면이 처음 생성될 때 나타나는 메세지이다(자동 로그인*구현해야함* / 수동 로그인)

        backPressCloseHandler = new BackPressCloseHandler(this);
        //현재 액티비티에서 커스텀 뒤로가기를 사용할 것이므로 선언한다

        like= findViewById(R.id.likeButton);
        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, YoutubeActivity.class);
                startActivity(intent);
            }
        });

        mypage = findViewById(R.id.mypage);
        mypage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, MypageActivity.class);
                //intent.putExtra("id", LoginActivity.id.getText().toString()); // 로그인 할 때 입력한 id 값을 받아와서
                startActivity(intent);                                          // 마이페이지 창에 id 값으로 보여준다
            }
        });

        size = findViewById(R.id.size);
        size.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, SizeActivity.class);
                startActivity(intent);
            }
        });

        dday = findViewById(R.id.dday);
        dday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, DdayActivity.class);
                startActivity(intent);
            }
        });

        training = findViewById(R.id.training);
        training.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, TrainingActivity.class);
                startActivity(intent);
            }
        });
        img1 = findViewById(R.id.you1);
        img2 = findViewById(R.id.you2);
        img3 = findViewById(R.id.you3);
        img4 = findViewById(R.id.you4);

        GlideDrawableImageViewTarget gif1 = new GlideDrawableImageViewTarget(img1);
        Glide.with(this).load(R.drawable.gif_free1).into(gif1);//자
        GlideDrawableImageViewTarget gif2 = new GlideDrawableImageViewTarget(img2);
        Glide.with(this).load(R.drawable.gif_back1).into(gif2);//배
        GlideDrawableImageViewTarget gif3 = new GlideDrawableImageViewTarget(img3);
        Glide.with(this).load(R.drawable.gif_buff1).into(gif3);//접
        GlideDrawableImageViewTarget gif4 = new GlideDrawableImageViewTarget(img4);
        Glide.with(this).load(R.drawable.gif_frog1).into(gif4);//평

        searchText = findViewById(R.id.searchText);

        sButton = findViewById(R.id.sButton);
        sButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEARCH);
                intent.setPackage("com.google.android.youtube");
                intent.putExtra("query", searchText.getText().toString());
                Log.d("디버깅2", "onClick: " + searchText.getText().toString() );
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                searchText.setText(null);
            }
        });

        handler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                img();
            }
        };

        thread = new Thread() {
            //run은 jvm이 쓰레드를 채택하면, 해당 쓰레드의 run메서드를 수행한다.
            public void run() {
                super.run();
                while (true) {
                    try {
                        Thread.sleep(5000);
                        handler.sendEmptyMessage(0);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        thread.start();

    }

    public void img(){
        idx += 1;

        if(idx > 3){
            idx = 0;
        }
        //Log.d("인덱스디버깅", "img: " + idx);

        if(idx == 0){ // 자유형 배우기
            img1.setVisibility(View.VISIBLE);
            img2.setVisibility(View.INVISIBLE);
            img3.setVisibility(View.INVISIBLE);
            img4.setVisibility(View.INVISIBLE);
        } else if(idx == 1){ // 배영 배우기
            img1.setVisibility(View.INVISIBLE);
            img2.setVisibility(View.VISIBLE);
            img3.setVisibility(View.INVISIBLE);
            img4.setVisibility(View.INVISIBLE);
        }else if (idx == 2){ // 접영 배우기
            img1.setVisibility(View.INVISIBLE);
            img2.setVisibility(View.INVISIBLE);
            img3.setVisibility(View.VISIBLE);
            img4.setVisibility(View.INVISIBLE);
        } else if (idx == 3){ // 평영 배우기
            img1.setVisibility(View.INVISIBLE);
            img2.setVisibility(View.INVISIBLE);
            img3.setVisibility(View.INVISIBLE);
            img4.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        backPressCloseHandler.onBackPressed();
        //뒤로가기버튼 한번 누르면 확인 메세지 뜨도록 설정->2초안에 뒤로가기 누르면 종료됨
        //(로그인 액티비티와 동일)
    }
}
