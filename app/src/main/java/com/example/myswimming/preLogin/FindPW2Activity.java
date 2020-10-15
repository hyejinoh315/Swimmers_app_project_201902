package com.example.myswimming.preLogin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.myswimming.R;

public class FindPW2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_pw2);

        setTitle("비밀번호 찾기 화면"); // 이거는 디버깅인가? 뭐지?

        Intent intent = getIntent(); // 엑스트라 인텐트를 받기 위해
        String password = intent.getExtras().getString("password");

        TextView users = findViewById(R.id.text_view_user);
        // 입력받은 이메일과 이름을 보여지게 하기 위한 껍데기 텍스트뷰
        // 받은 인텐트 값을 넣어준다
        users.setText("비밀번호 찾기 화면입니다");
        TextView pw = findViewById(R.id.text_view_pw);
        pw.setText("회원님의 비밀번호는 "+ password + "입니다.");

        Button goToLogin = findViewById(R.id.goToLogin); // 로그인 하러가기 버튼
        goToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //눌렀을시에 이전(대기)액티비티인 pw찾기 액티비티를 종료시키고
                Intent intentOut = new Intent(FindPW2Activity.this, LoginActivity.class);
                startActivity(intentOut); // 로그인 액티비티로 이동한다
                finish(); // 현재 액티비티 종료
            }
        });


    }
}
