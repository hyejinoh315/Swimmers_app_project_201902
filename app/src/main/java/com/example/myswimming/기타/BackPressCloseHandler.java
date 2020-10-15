package com.example.myswimming.기타;

import android.app.Activity;
import android.widget.Toast;

public class BackPressCloseHandler { // 뒤로가기 버튼 두번 누르면 종료시키는 메소드 만들기 위해 생성된 클래스
    private long backKeyClickTime = 0; //초를 의미 -> 지연시간을 보태어 (0+지연시간) 미만의 시간동안 뒤로가기 두번 클릭하면 종료됨
    private Activity activity;

    public BackPressCloseHandler(Activity activity) {
        this.activity = activity; // 이 메소드가 선언된 곳의 (현재) 액티비티
    }

    public void showToast(){ // 뒤로가기를 한번 눌렀을 때 등장하는 메소드(토스트)
        Toast.makeText(activity, "뒤로 가기 버튼을 한번 더 누르면 종료됩니다", Toast.LENGTH_SHORT).show();
    }

    public void onBackPressed() {
        if(System.currentTimeMillis() > backKeyClickTime + 2000) {
            backKeyClickTime = System.currentTimeMillis();
            showToast(); // 뒤로가기 버튼을 한번 누르면 보여지는 메소드,
            return; // 2초가 넘어가면 아무 일도 일어나지 않는다
        }
        else
            activity.finish(); // (0+지연시간) 이하의 시간동안 버튼이 두번 눌리면 현재 액티비티가 종료된다
    }

}
