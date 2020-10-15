package com.example.myswimming.add_set;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import com.example.myswimming.MyEditBoard;
import com.example.myswimming.R;
import com.example.myswimming.SizePlusActivity;
import com.example.myswimming.adapters.SizeBoardAdapter;
import com.example.myswimming.items.SizeBoardInfo;
import com.example.myswimming.preLogin.LoginActivity;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class SizeActivity extends AppCompatActivity {
    private ImageView back; // 사이즈 레이아웃에 있는 뒤로가기 아이콘(이미지)
    FloatingActionButton writeButton;
    //제목 시간 인덱스를 저장한다
    RecyclerView sizeRecyclerView; // 일정추가되는 리사이클러뷰 (size_row 레이아웃)
    RecyclerView.LayoutManager boardLayoutManager; // 리사이클러뷰 정렬 형태 - 리니어 레이아웃
    ArrayList<SizeBoardInfo> sizeBoardArrayList;// = new ArrayList<>(); // 리사이클러뷰가 담길 리스트
    SizeBoardAdapter boardAdapter;// 사용할 어댑터
    String brand, product, size, fit, grade, memo, index, time; // 리사이클러뷰 내부에 사용될 변수들

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        loadData();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_size);

        back = findViewById(R.id.icon_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed(); // 뒤로가기 아이콘을 클릭했을 때 뒤로가기 버튼 눌른 것과 같은 액션을 함
            }
        });

        writeButton = findViewById(R.id.editButton);
        /*사용자가 마이페이지에서 정보를 입력하지 않으면
        버튼을 누를수 없도록 만들고, (알림창: 정보 입력이 필요합니다. 페이지를 이동하시겠습니까? -> 인텐트로 넘어가기(이 액티비티 안죽음))
        완료하면 다시 게시판 페이지로 넘어와서 버튼 클릭이 가능하게 구현하고!
         */
        writeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences pref = getSharedPreferences("USER", MODE_PRIVATE);
                String details = pref.getString(LoginActivity.etId, "");

                JsonParser parser = new JsonParser();//값을 넣어주기 위해 파싱한다
                JsonElement element = parser.parse(details);

                String torsoP = element.getAsJsonObject().get("torso").getAsString();

                if(!torsoP.equals("")) {
                    Intent intent = new Intent(SizeActivity.this, SizePlusActivity.class);
                    startActivityForResult(intent, 315);
                } else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(SizeActivity.this);
                    builder.setTitle("SWIMMERS");
                    builder.setMessage("사이즈 공유 게시판은 회원 정보를 입력 후 작성 가능합니다.\n지금 바로 작성하시겠습니까?");
                    builder.setPositiveButton("페이지 이동", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(SizeActivity.this, MyEditBoard.class);
                            startActivity(intent);
                        }
                    });
                    builder.setNegativeButton("나중에 변경", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        sizeRecyclerView = findViewById(R.id.sizeRecyclerView);

        boardLayoutManager = new LinearLayoutManager(this);
        ((LinearLayoutManager)boardLayoutManager).setReverseLayout(true);//뷰 위로 추가 되게 하는 메소드
        sizeRecyclerView.setLayoutManager(boardLayoutManager);

        boardAdapter = new SizeBoardAdapter(sizeBoardArrayList);
        sizeRecyclerView.setAdapter(boardAdapter);

        boardAdapter.setOnItemClickListener(new SizeBoardAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(SizeBoardInfo boardInfo) {
                //Log.d("아이템 디버깅", "onItemClick: " + boardInfo.title + "/" + boardInfo.time + "/" + boardInfo.fit + "/" + boardInfo.grade + "/" + boardInfo.memo);

                Intent intent = new Intent(SizeActivity.this, SizeBoardActivity.class);
                intent.putExtra("boardTitle", boardInfo.title);
                intent.putExtra("systemTime", boardInfo.time);
                intent.putExtra("sizeFit", boardInfo.fit);
                intent.putExtra("productGrade", boardInfo.grade);
                intent.putExtra("userMemo", boardInfo.memo);
                intent.putExtra("gender", boardInfo.gender);
                intent.putExtra("age", boardInfo.age);
                intent.putExtra("tall", boardInfo.tall);
                intent.putExtra("kg", boardInfo.kg);
                intent.putExtra("torso", boardInfo.torso);

                intent.putExtra("id", boardInfo.식별자);
                intent.putExtra("brandName", boardInfo.brandName);      //수정시
                intent.putExtra("productName", boardInfo.productName);  //필요한
                intent.putExtra("productSize", boardInfo.productSize);  //항목들
                intent.putExtra("index", sizeBoardArrayList.indexOf(boardInfo));
                Log.d("게시글클릭 인덱스 디버깅", "onItemClick: " + sizeBoardArrayList.indexOf(boardInfo));

                startActivityForResult(intent, 5959);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 315) {
            if(resultCode == RESULT_OK) {
                brand = data.getStringExtra("brandName");
                product = data.getStringExtra("productName");
                size = data.getStringExtra("productSize");
                fit = data.getStringExtra("sizeFit");
                grade = data.getStringExtra("productGrade");
                memo = data.getStringExtra("userMemo");
                time = data.getStringExtra("systemTime");
                index = Integer.toString(sizeBoardArrayList.size()+1);
                //Log.d("디버깅", "게시판: " + brand+product+size+fit+grade+memo+time);


                //Log.d("리스트 사이즈 디버깅", ""+sizeBoardArrayList.size()); // 리스트사이즈를 알기 위한 로그
                sizeBoardArrayList.add(new SizeBoardInfo(brand, product, size, time, index, fit, grade, memo,
                        data.getStringExtra("gender"), data.getStringExtra("age"),
                        data.getStringExtra("tall"), data.getStringExtra("kg"), data.getStringExtra("torso")));
                //Log.d("리스트 사이즈 디버깅", ""+sizeBoardArrayList.size()); // 리스트사이즈++

                boardAdapter.notifyItemChanged(0);
                saveData();
            }
        }
        if(requestCode == 5959){
            if(resultCode == RESULT_OK){ //삭제시 받아오는 코드
                int index = data.getIntExtra("index", 0);
                removeBoard(index);
                saveData();
            }
            if(resultCode == RESULT_FIRST_USER){//수정시 받아오는 코드

                brand=data.getStringExtra("brandName");
                Log.d("디버깅3", "onActivityResult: " +brand );
                product=data.getStringExtra("productName");
                size=data.getStringExtra("productSize");
                time=data.getStringExtra("systemTime");
                //idx
                String a = data.getStringExtra("sizeFit");
                if(a.equals(" 제 몸에 아주 타이트해요")) fit = "아주 타이트해요";
                if(a.equals(" 제 몸에 약간 타이트해요")) fit = "약간 타이트해요";
                if(a.equals(" 제 몸에 약간 커요")) fit = "약간 커요";
                if(a.equals(" 제 몸에 아주 커요")) fit = "아주 커요";
                if(a.equals(" 제 몸에 적당해요")) fit = "적당해요";
                grade=data.getStringExtra("productGrade");
                memo=data.getStringExtra("userMemo");
                int index = data.getIntExtra("index", 0);

                SizeBoardInfo sizeBoardInfo = new SizeBoardInfo(brand, product, size, time, "", fit, grade, memo,
                        data.getStringExtra("gender"), data.getStringExtra("age"),
                        data.getStringExtra("tall"), data.getStringExtra("kg"), data.getStringExtra("torso"));

                sizeBoardArrayList.set(index, sizeBoardInfo);
                boardAdapter.notifyDataSetChanged();

                saveData();

            }
        }
    }

    private void saveData() {
        SharedPreferences pref = getSharedPreferences("BOARD", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(sizeBoardArrayList);
        Log.d("게시판 데이터 저장 디버깅", "saveData: " + json);
        editor.putString("board list", json);
        editor.apply();
    }

    private void loadData() {
        SharedPreferences pref = getSharedPreferences("BOARD", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = pref.getString("board list", "");
        Log.d("전체 보드 (게시판) 디버깅", "loadData: \n" + json);
        Type type = new TypeToken<ArrayList<SizeBoardInfo>>(){}.getType();
        sizeBoardArrayList = gson.fromJson(json, type);

        if (sizeBoardArrayList == null){
            sizeBoardArrayList = new ArrayList<>();
        }
    }

    public void removeBoard(int position){
        sizeBoardArrayList.remove(position);
        boardAdapter.notifyItemRemoved(position);
        Log.d("게시글삭제 포지션 디버깅", "onItemClick: " + position);
    }
}