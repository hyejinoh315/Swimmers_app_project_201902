package com.example.myswimming;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class DayPlusActivity extends AppCompatActivity {
    private static final String TAG = "DayPlusActivity";
    private EditText theTitle, theMemo; // 메모 가능한 변수들
    private TextView theDate;
    private TextView save; // go to calendar button + intent Data

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_plus);

        theDate = findViewById(R.id.event_date); // 이벤트 일정
        theTitle = findViewById(R.id.event_title); // 이벤트 제목
        theMemo = findViewById(R.id.event_memo); // 이벤트 메모

        Intent dateIntent = getIntent(); // 엑스트라 인텐트 값을 받기 위해(처음에는 캘린더 클릭해서 날짜값만 받아오느라 변수명이 dateIntent 인데, 수정시에 받는 인텐트와는 관련 없다)
        String date = dateIntent.getStringExtra("date"); // key = date(일정)
        theDate.setText(date); // 캘린더 터치를 통해 받아온 인텐트 값을 이벤트 일정에 넣어 준다
        String title = dateIntent.getStringExtra("title"); //
        theTitle.setText(title); // 제목과 메모는, 수정시에 넘어온 값을 넣어준다
        String memo = dateIntent.getStringExtra("memo");
        theMemo.setText(memo);
        final int index = dateIntent.getIntExtra("index", 0);
        //defaultValue 적는 이유는, index 값이 수정할 때 말고 인텐트로 넘어오지 않으니까 그때마다 0을 받아 보내줌(아니면 null 값이 나오나?)
        /*인텐트로 넘어온 어레이리스트의 인덱스값을 받아와서 다시 보내줘야 함*/
        save = findViewById(R.id.daySave);
        if (dateIntent.hasExtra("index")){
            save.setText("수정");
            save.setTextColor(Color.RED);
        }//인덱스 값이 인텐트로 넘어오면 "저장" -> "수정" 텍스트 변화
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { // 저장버튼 누르면
                Intent intent = new Intent();
                intent.putExtra("title", theTitle.getText().toString()); // 작성된
                intent.putExtra("date", theDate.getText().toString());   // 텍스트를
                intent.putExtra("memo", theMemo.getText().toString());   // 엑스트라로
                intent.putExtra("index", index);
                Log.d(TAG, "onClick: index: " + index);
                setResult(RESULT_OK, intent); // RESULT_OK 코드 반환 및 인텐트 전달
                finish(); // 메모되는 액티비티도 종료
            }
        });

        TextView cancleButton = findViewById(R.id.dayCancle);
        cancleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED); // 취소버튼 누르면 RESULT_CANCELED 코드 반환
                finish(); // 메모되는 액티비티도 종료
            }
        });
    }

}
