package com.example.myswimming.preLogin;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.myswimming.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("MainActivity", "onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
/*키해시 발급 메소드 -> 카카오 API 사용시
        try{
            PackageInfo info = getPackageManager().getPackageInfo("com.example.myswimming", PackageManager.GET_SIGNATURES);
            for(Signature signature : info.signatures){
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.e("KeyHash", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }*/
        Handler handler = new Handler() { //속도 지연 후 자동 소멸을 위해 핸들러를 사용하였다
            public void handleMessage(Message msg) {
                //handleMessage 메소드를 구현하는 것으로 쓰레드를 만들 수 있음
                super.handleMessage(msg);
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish(); // 액티비티 보여지기만 하고 자동으로 종료하기
            }
        };
        handler.sendEmptyMessageDelayed(0, 500); // 몇 초 지연할지 설정 1000 = 1초
    }

    //액티비티 생명주기(생성순서) create -> start -> resume ->(소멸순서) pause -> stop -> destroy
    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("MainActivity", "onRestart");
    }
    @Override
    protected void onStart() {
        super.onStart();
        Log.d("MainActivity", "onStart");
    }
    @Override
    protected void onResume() {
        super.onResume();
        Log.d("MainActivity", "onResume");
    }
    @Override
    protected void onPause() { //중지 단계에서 저장하기
        super.onPause();
        Log.d("MainActivity", "onPause");
    }
    @Override
    protected void onStop() {
        super.onStop();
        Log.d("MainActivity", "onStop");
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("MainActivity", "onDestroy");
    }
}
