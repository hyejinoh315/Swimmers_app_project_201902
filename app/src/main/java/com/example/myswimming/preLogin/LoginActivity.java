package com.example.myswimming.preLogin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myswimming.기타.BackPressCloseHandler;
import com.example.myswimming.HomeActivity;
import com.example.myswimming.R;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class LoginActivity extends AppCompatActivity {
    public static String etId; //**로그인 성공시의 아이디값이 키값이므로 전역변수 선언됨
    private BackPressCloseHandler backPressCloseHandler; // 로그인 화면에서 뒤로가기를 누르면 종료메세지가 우선 뜨고,
    // 2초 내로 뒤로가기 누르면 또 종료시킨다는 메소드를 사용하기 위해 선언
    private EditText id, pw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        backPressCloseHandler = new BackPressCloseHandler(this);
        //현재 액티비티에서 뒤로가기 버튼을 default 값이 아닌 커스텀하여 쓸 것이다 (내가 커스텀한 클래스)
        id = findViewById(R.id.login_id);
        pw = findViewById(R.id.login_pw);

        RelativeLayout lonINButton = findViewById(R.id.button_login); // 하늘색 로그인 버튼
        lonINButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //클릭하면 벌어지는 일들
                if (id.getText().toString().equals("") || pw.getText().toString().equals("")) {
                    Toast.makeText(LoginActivity.this, "아이디와 비밀번호를 입력해 주세요", Toast.LENGTH_SHORT).show();
                    // 아이디 값과 비밀번호 값이 하나라도 비어 있으면 발생함
                } else if (getSharedPreferences("USER", MODE_PRIVATE).contains(id.getText().toString())) { // 아이디, 비밀번호가 들어가 있다면 & 존재하는 아이디라면

                    etId = id.getText().toString();
                    String etPw = pw.getText().toString();

                    SharedPreferences pref = getSharedPreferences("USER", MODE_PRIVATE);
                    /*입력된 키캆통해 json파싱하여 아이디와 패스워드가 일치한지 확인하는거 만들기.
                     * 맞으면 로그인이 된다*/
                    String details = pref.getString(etId, "비밀번호 틀렸음");
                    JsonParser parser = new JsonParser();
                    JsonElement element = parser.parse(details); // 아이디를 통해 가져온 디테일들을 파싱한다
                    String pwP = element.getAsJsonObject().get("pw").getAsString(); // pw(키캆)의 밸류를 스트링으로 얻어옴

                    if (details.equals("비밀번호 틀렸음") || !etPw.equals(pwP)) {
                        Toast.makeText(LoginActivity.this, "아이디와 비밀번호를 확인해 주세요", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        //intent.putExtra("id", etId);
                        // 엑스트라 인텐트를 홈액티비티로 보낸다(이 값은 홈에 캐치만 한다)이거는 !!마이페이지!! 에서 받을것임 홈이 아니라..
                        startActivity(intent);
                        finish(); // 로그인 실행되는 동시에 로그인 액티비티는 종료한다
                    }
                } else {
                    //아이디 존재하지 않음
                    Toast.makeText(LoginActivity.this, "없는 아이디", Toast.LENGTH_SHORT).show();
                }
            }
        });

        TextView signUpButton = findViewById(R.id.signUpButton); // 가입하기 버튼
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                //누르면 회원가입 액티비티로 전환된다
                startActivity(intent); //회원가입 화면 전환시 로그인 액티비티는 소멸되지 않고 대기한다
            }
        });

        TextView findPWButton = findViewById(R.id.findPWButton); // 비밀번호 찾기 버튼
        findPWButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, FindPWActivity.class);
                //누르면 비밀번호 찾기 액티비티로 전환된다
                startActivity(intent); //비밀번호 찾기 화면 전환시 로그인 액티비티는 소멸되지 않고 대기한다
            }
        });
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed(); // <- default 값이므로 주석처리
        backPressCloseHandler.onBackPressed();
        //뒤로가기버튼 한번 누르면 확인 메세지 뜨도록 설정->2초안에 뒤로가기 누르면 종료됨
    }

}
