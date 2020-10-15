package com.example.myswimming.add_set;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.myswimming.R;
import com.example.myswimming.adapters.VideoAdapter;
import com.example.myswimming.items.VideoInfo;
import com.example.myswimming.preLogin.LoginActivity;
import com.example.myswimming.기타.PlayActivity;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class YoutubeActivity extends YouTubeBaseActivity {
    YouTubePlayerView player;
    YouTubePlayer.OnInitializedListener listener;

    private ImageView back; // 뒤로가기 버튼
    private RecyclerView videoRecyclerView; // 유튭추가되는 리사이클러뷰 (video_row 레이아웃)
    private RecyclerView.LayoutManager videoLayoutManager; // 리사이클러뷰 정렬 형태 - 리니어 레이아웃
    private ArrayList<VideoInfo> videoInfoArrayList;// = new ArrayList<>(); // 리사이클러뷰가 담길 리스트
    private VideoAdapter adapter; // 사용할 어댑터
    private String uri; // 리사이클러뷰 내부에 사용될 변수들
    String sharedText, title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube);

        player = findViewById(R.id.myPlayer);
        back = findViewById(R.id.icon_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed(); // 뒤로가기 아이콘을 클릭했을 때 뒤로가기 버튼 눌른 것과 같은 액션을 함
            }
        });
        //데이터불러오기
        SharedPreferences pref = getSharedPreferences(LoginActivity.etId+"video", MODE_PRIVATE);
        Gson gson =  new Gson();
        String json = pref.getString("video", "");
        Type type = new TypeToken<ArrayList<VideoInfo>>(){}.getType();
        videoInfoArrayList = gson.fromJson(json, type);
        if(videoInfoArrayList == null){
            videoInfoArrayList = new ArrayList<>();
        }



// 인텐트를 얻어오고, 액션과 MIME 타입을 가져온다
        final Intent intent = getIntent();
        String action = intent.getAction();
        String intentType = intent.getType();

// 인텐트 정보가 있는 경우 실행
        if (Intent.ACTION_SEND.equals(action) && intentType != null) {
            if ("text/plain".equals(intentType)) {
                sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
                title = intent.getStringExtra(Intent.EXTRA_PACKAGE_NAME);
                if(sharedText.contains("youtu.be")) {
                    final EditText et = new EditText(YoutubeActivity.this);
                    et.setHint("TITLE");
                    et.setTextSize(16);
                    et.setText(title);
                    new AlertDialog.Builder(this)
                            .setTitle("SWIMMERS")
                            //.setMessage(sharedText )
                            .setMessage("북마크할 영상 제목을 입력해 주세요")
                            .setView(et)
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    videoInfoArrayList.add(new VideoInfo(et.getText().toString(), sharedText.substring(17)));
                                    adapter.notifyDataSetChanged();

                                    SharedPreferences pref = getSharedPreferences(LoginActivity.etId + "video", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = pref.edit();
                                    Gson gson = new Gson();
                                    String json = gson.toJson(videoInfoArrayList);
                                    editor.putString("video", json);
                                    editor.commit();

                                    Log.d("저장디버깅", "onClick: " + json);
                                }
                            })
                            .setCancelable(false)
                            .create()
                            .show();
                } else{
                    //유튜브가 아닌 다른 것을 공유했을 때 아무일도 일어나지 않음.
                }
            }
        }

        listener = new YouTubePlayer.OnInitializedListener() { //문제 : 최초 1회만 실행된다
            //팀장님 조언 : 복잡하게 만들어 놨는데 굳이 리스너를 따로 써야하나? 바로 재생되도록.
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                Log.e("주소불러오기디버깅", "onClick: "+ uri);
                youTubePlayer.loadVideo(uri);
            }
            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

            }
        };

        //리사이클러뷰를 선언하고, 리사이클러뷰의 레이아웃 크기를 변경하지 않는다
        videoRecyclerView = findViewById(R.id.videoRecyclerView);
        videoRecyclerView.setHasFixedSize(true);
        //리니어레이아웃매니저를 사용하겠다고 선언
        videoLayoutManager = new LinearLayoutManager(this);
        videoRecyclerView.setLayoutManager(videoLayoutManager);
        //어댑터를 지정한다
        adapter = new VideoAdapter(videoInfoArrayList);
        videoRecyclerView.setAdapter(adapter);

        //어댑터 내부에 메뉴 생성 => 액티비티에서 받아온다
        adapter.setOnItemClickListener(new VideoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(final VideoInfo videoInfo) {
                //플레이 클릭 메소드
                uri = videoInfo.uri;
                Log.d("주소불러오기디버깅", "onClick: "+videoInfo.uri +"//"+ uri);

                Intent i = new Intent(YoutubeActivity.this, PlayActivity.class);
                i.putExtra("uri", uri);
                startActivity(i);

                //player.initialize("AIzaSyCV-cPfHIpuUTLVpt7CAnnl8QP93-TaxV4", listener);



            }

            @Override
            public void onDeleteClick(final int position) {
                //삭제메소드
                new AlertDialog.Builder(YoutubeActivity.this)
                        .setTitle("SWIMMERS")
                        .setMessage("영상을 즐겨찾기에서 제거하시겠습니까?")
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                removeItem(position);
                                //데이터저장
                                SharedPreferences pref = getSharedPreferences(LoginActivity.etId + "video", MODE_PRIVATE);
                                SharedPreferences.Editor editor = pref.edit();
                                Gson gson = new Gson();
                                String json = gson.toJson(videoInfoArrayList);
                                editor.putString("video", json);
                                editor.commit();
                                Log.d("저장디버깅", "onClick: " + json);
                            }
                        })
                        .setNegativeButton("NO", null)
                        .create()
                        .show();

            }
        });


    }
    public void removeItem(int position){
        videoInfoArrayList.remove(position);
        adapter.notifyItemRemoved(position);
    }

}