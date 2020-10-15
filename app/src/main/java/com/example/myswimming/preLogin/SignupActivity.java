package com.example.myswimming.preLogin;

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
import com.example.myswimming.items.SizeReplyInfo;
import com.example.myswimming.items.UsersInfo;

import java.util.Map;

public class SignupActivity extends AppCompatActivity { //register Activity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        final EditText userName = findViewById(R.id.registerName);
        final EditText id = findViewById(R.id.registerID);
        final EditText pw = findViewById(R.id.registerPW);
        final EditText check = findViewById(R.id.registerPW2); //비밀번호 동일한지 확인
        Button signUpButton = findViewById(R.id.registerJoinButton);
        Button checkButton = findViewById(R.id.중복확인버튼);
        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getSharedPreferences("USER", MODE_PRIVATE).contains(id.getText().toString())){
                    Toast.makeText(SignupActivity.this, "이미 존재하는 회원입니다", Toast.LENGTH_SHORT).show();
                } else {
                    if (!android.util.Patterns.EMAIL_ADDRESS.matcher(id.getText().toString()).matches()) {
                        Toast.makeText(SignupActivity.this, "올바른 이메일 형식이 아닙니다", Toast.LENGTH_SHORT).show();
                    }  else
                    Toast.makeText(SignupActivity.this, "가입 가능한 이메일 입니다", Toast.LENGTH_SHORT).show();
                }
            }
        });
        /*
        <<추가로 생성하고싶은 기능>>
        버튼을 누를 수 있는 경우에 색변화를 주고, 누르면 아이디와 비밀번호가 저장된다
        다음 회원가입시 아이디가 중복인지 체크한다 -> 다르면 경고문구가 뜬다
        비밀번호는 6자리 이상으로 설정하고 비밀번호, 확인란이 같지 않으면 경고문구가 뜬다
        ->저장되는 값을 인텐트로 넘겨준다??
        */
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { // 회원가입 버튼을 누르면 메소드 발생
                if(id.getText().toString().equals("") || pw.getText().toString().equals("")
                        || userName.getText().toString().equals("")){
                    Toast.makeText(SignupActivity.this, "모든 항목을 채워주세요", Toast.LENGTH_SHORT).show();
                }else {
                    /**/
                    SharedPreferences pref = getSharedPreferences("USER", MODE_PRIVATE);

                    String newId = id.getText().toString();
                    String newPW = pw.getText().toString();
                    String newUser = userName.getText().toString();
                    String pwCheck = check.getText().toString();
                    //비밀번호와 사용자 이름 넣으면 넘어가도록 설정하기

                    if (newPW.equals(pwCheck) && !getSharedPreferences("USER", MODE_PRIVATE).contains(id.getText().toString())
                            && android.util.Patterns.EMAIL_ADDRESS.matcher(newId).matches()) {

                            UsersInfo user = new UsersInfo(newId, newPW, newUser, "", "", "", "", "");

                            String details = user.makeJson(1);//1명 유저 정보를 json 형식으로 저장함

                            Log.d("가입화면", "to make Json: 디버깅: " + details);

                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString(newId, details); //1인 개인정보를 전부 담고 있다

                            editor.commit();
                            /**/
                            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                            startActivity(intent);

                            finish(); // 회원가입 화면은 뒤로가기 혹은 아이디가 만들어지면 소멸됨

                    } else if (!newPW.equals(pwCheck)) { //비밀번호와 비밀번호 확인이 맞지 않을 경우 넘어갈 수 없음
                        Toast.makeText(SignupActivity.this, "비밀번호를 확인해 주세요", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(SignupActivity.this, "중복확인 해주세요", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("SignupActivity", "onRestart");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("SignupActivity", "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("SignupActivity", "onResume");
    }

    @Override
    protected void onPause() { //중지 단계에서 저장하기
        super.onPause();
        Log.d("SignupActivity", "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("SignupActivity", "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("SignupActivity", "onDestroy");
    }
}
