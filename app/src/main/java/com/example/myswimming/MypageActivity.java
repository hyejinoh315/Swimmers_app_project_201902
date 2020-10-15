package com.example.myswimming;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myswimming.add_set.YoutubeActivity;
import com.example.myswimming.preLogin.LoginActivity;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.kakao.kakaolink.v2.KakaoLinkResponse;
import com.kakao.kakaolink.v2.KakaoLinkService;
import com.kakao.network.ErrorResult;
import com.kakao.network.callback.ResponseCallback;
import com.kakao.util.helper.log.Logger;

import java.util.HashMap;
import java.util.Map;

public class MypageActivity extends AppCompatActivity {
    public static Activity myPage;

    private ImageView logout, back; // 상단 버튼 아이콘 (뒤로가기와 로그아웃)
    private TextView myPageId, userName; // 로그인 id 값을 받아와서 작성 될 텍스트뷰
    private Button edit, goToPlayer, goToMap; // 정보수정 버튼과 영상 보기 버튼
    private ImageView profile; //프로필 이미지
    TextView gender, age, tall, kg, torso; // 내 정보들
    //String myId; // 인텐트로 넘어오는 (로그인시)id 입력 값
    HomeActivity home = (HomeActivity) HomeActivity.home; // 로그아웃시에 홈 액티비티(다른 액티비티)를 소멸시켜야해서 선언해준다
    //친구초대버튼

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        myPage = MypageActivity.this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);

        Button kakaoLinkBtn = findViewById(R.id.kakaoLinkBtn);
        kakaoLinkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String templateId = "15644";

                Map<String, String> templateArgs = new HashMap<>();
                templateArgs.put("template_arg1", "value1");
                templateArgs.put("template_arg2", "value2");

                Map<String, String> serverCallbackArgs = new HashMap<>();
                serverCallbackArgs.put("user_id", "${current_user_id}");
                serverCallbackArgs.put("product_id", "${shared_product_id}");
                //??????뭐지이새끼는왜..??
                KakaoLinkService.getInstance().sendCustom(MypageActivity.this, templateId, templateArgs, new ResponseCallback<KakaoLinkResponse>() {
                    @Override
                    public void onFailure(ErrorResult errorResult) {
                        Logger.e(errorResult.toString());
                    }

                    @Override
                    public void onSuccess(KakaoLinkResponse result) {
                        // 템플릿 밸리데이션과 쿼터 체크가 성공적으로 끝남. 톡에서 정상적으로 보내졌는지 보장은 할 수 없다. 전송 성공 유무는 서버콜백 기능을 이용하여야 한다.
                    }
                });

            }
        });

        //Intent intent = getIntent();
        //myId = intent.getExtras().getString("id"); // 인텐트 메세지를 받아서
        myPageId = findViewById(R.id.mapage_id);
        //myPageId.setText(myId); // 마이페이지의 아이디 텍스트뷰에 적용시킨다 -> 쉐어드를 사용하여 죽은 코드

        profile = findViewById(R.id.profileImage);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener cameraListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //doTakePhotoAction();
                    }
                };
                DialogInterface.OnClickListener albumListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //doTakeAlbumAction();
                    }
                };
                new AlertDialog.Builder(MypageActivity.this)
                        .setTitle("프로필")
                        .setPositiveButton("사진 촬영", cameraListener)
                        .setNeutralButton("앨범에서 사진 선택", albumListener)
                        .show();
            }
        });

        logout = findViewById(R.id.icon_out);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { // 로그아웃 모양의 버튼을 클릭하면
                AlertDialog.Builder builder = new AlertDialog.Builder(MypageActivity.this);
                builder.setMessage("로그아웃 하시겠습니까?");
                builder.setTitle("로그아웃 알림창")
                        .setCancelable(true) // 뒤로가기 눌렀을 때 취소 가능한지, (false)면 no 버튼을 눌러야 취소됨
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) { // 긍정버튼
                                Intent intent = new Intent(MypageActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish(); // 누르면 로그인 액티비티로 전환되며 액티비티 종료
                                home.finish(); // 떠있는 액티비티 모두 종료
                            }
                        })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel(); // 부정버튼 누르면 단순히 다이얼로그만 취소
                            }
                        });
                AlertDialog alert = builder.create(); // (동적) 위의 설정을 토대로 알림창을 만든다
                alert.setTitle("SWIMMERS"); // 타이틀을 지정해주고
                alert.show(); // 알림창을 보여준다
            }
        });
        back = findViewById(R.id.icon_back); //뒤로가기 아이콘
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed(); // 뒤로가기 아이콘을 클릭했을 때 뒤로가기 버튼 눌른 것과 같은 액션을 준다
            }
        });

        edit = findViewById(R.id.myEditButton); // 정보수정 버튼
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MypageActivity.this, MyEditActivity.class);
                intent.putExtra("myPageId", myPageId.getText().toString());
                intent.putExtra("name", userName.getText().toString());
                intent.putExtra("gender", gender.getText().toString());
                intent.putExtra("age", age.getText().toString());
                intent.putExtra("tall", tall.getText().toString());
                intent.putExtra("kg", kg.getText().toString());
                intent.putExtra("torso", torso.getText().toString());
                startActivityForResult(intent, 333);
            }
        });
        age = findViewById(R.id.myAge);
        tall = findViewById(R.id.myHeight);
        kg = findViewById(R.id.myWeight);
        torso = findViewById(R.id.myTorso);

        gender = findViewById(R.id.myGender);

        goToPlayer = findViewById(R.id.myPlayButton);
        goToPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MypageActivity.this, YoutubeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        goToMap = findViewById(R.id.mapsButton);
        goToMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MypageActivity.this, MapsActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //반환된 코드 값으로 결과를 다르게 만듦
        if (requestCode == 333) { //반환코드
            if (resultCode == RESULT_OK) { //RESULT_OK 결과코드 OK 반환시
                String genderRe = data.getStringExtra("gender");
                String ageRe = data.getStringExtra("age");
                String tallRe = data.getStringExtra("tall");
                String kgRe = data.getStringExtra("kg");
                String torsoRe = data.getStringExtra("torso");

                gender.setText(genderRe);
                age.setText(ageRe);
                tall.setText(tallRe);
                kg.setText(kgRe);
                torso.setText(torsoRe);
            }
            if (resultCode == RESULT_CANCELED) {
                // RESULT_CANCELED 코드 반환시 행동없음
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("MypageActivity", "onResume");

        if(!LoginActivity.etId.equals("") && !LoginActivity.etId.equals(null)) {

            SharedPreferences pref = getSharedPreferences("USER", MODE_PRIVATE);
            String details = pref.getString(LoginActivity.etId, "");

            JsonParser parser = new JsonParser();//값을 넣어주기 위해 파싱한다
            JsonElement element = parser.parse(details);
            String idP = element.getAsJsonObject().get("id").getAsString();
            String nameP = element.getAsJsonObject().get("name").getAsString();
            String genderP = element.getAsJsonObject().get("gender").getAsString();
            String ageP = element.getAsJsonObject().get("age").getAsString();
            String tallP = element.getAsJsonObject().get("tall").getAsString();
            String kgP = element.getAsJsonObject().get("kg").getAsString();
            String torsoP = element.getAsJsonObject().get("torso").getAsString();

            userName = findViewById(R.id.myPage_name);
            myPageId = findViewById(R.id.mapage_id);//텍스트뷰인 아이디 란에

            userName.setText(nameP);
            myPageId.setText(idP);//저장된 값을 넣어준다

            gender.setText(genderP);
            age.setText(ageP);
            tall.setText(tallP);
            kg.setText(kgP);
            torso.setText(torsoP);
        }
    }

    @Override
    protected void onPause() { //중지 단계에서 저장하기
        super.onPause();
        Log.d("MypageActivity", "onPause");
    }
}
