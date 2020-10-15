package com.example.myswimming.preLogin;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myswimming.R;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class FindPWActivity extends AppCompatActivity {
    public static Activity findPW; // 다음 액티비티에서 종료하는 메소드를 쓰기 위해 스태틱 선언

    private EditText findPWEmail, findPWName; // 유저 입력하는 공간
    private Button findPWButton; // 입력하면 활성화 되는 버튼(->조건문으로 조정)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        findPW = FindPWActivity.this; //선언된 변수에 FindPWActivity 를 담아준다

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_pw);

        findPWButton = findViewById(R.id.findPWButton);
        findPWEmail = findViewById(R.id.findPWEmail);
        findPWName = findViewById(R.id.findPWName);

        findPWButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { // 비밀번호 찾기 버튼을 눌렀을 때
                if (findPWEmail.getText().toString().equals("")
                     ||findPWName.getText().toString().equals("")) {
                    Toast.makeText(FindPWActivity.this, "이메일과 이름을 입력해 주세요", Toast.LENGTH_SHORT).show();
                    // 빈칸이 있다면 버튼 눌러도 다른 액티비티로 넘어가지지 않는다
                } else if(getSharedPreferences("USER", MODE_PRIVATE).contains(findPWEmail.getText().toString())) {
                    //존재하는 회원 아이디를 입력했을 경우
                    SharedPreferences pref = getSharedPreferences("USER", MODE_PRIVATE);
                    String details = pref.getString(findPWEmail.getText().toString(), "");
                    Log.d("비밀번호찾기 디버깅", "onClick: "+details +"/"+findPWEmail.getText().toString());
                    JsonParser parser = new JsonParser();
                    JsonElement element = parser.parse(details);
                    String nameP = element.getAsJsonObject().get("name").getAsString();
                    String pwP = element.getAsJsonObject().get("pw").getAsString();

                    if (nameP.equals(findPWName.getText().toString())) {
                        Intent intent = new Intent(FindPWActivity.this, FindPW2Activity.class);
                        //비밀번호 찾기 페이지로 넘어가진다
                        intent.putExtra("password", pwP);
                        startActivity(intent);
                        finish();
                    } else {//아이디는 존재하는데 이름이 틀렸어
                        Toast.makeText(FindPWActivity.this, "이메일과 이름을 확인해 주세요", Toast.LENGTH_SHORT).show();
                    }
                }else{//존재하지 않은 회원 아이디를 입력
                    Toast.makeText(FindPWActivity.this, "이메일과 이름을 확인해 주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
