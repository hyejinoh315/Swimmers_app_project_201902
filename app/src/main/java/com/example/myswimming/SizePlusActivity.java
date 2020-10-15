package com.example.myswimming;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myswimming.preLogin.LoginActivity;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class SizePlusActivity extends AppCompatActivity {

    private EditText brand, product, size, fit, grade, memo; // 편집 가능한 변수들
    private TextView save, cancle; // 클릭 가능한 (버튼)텍스트
    private ArrayAdapter brandAdapter, fitA, gradeA;//스피너 값을 받아오기 위해 선언됨
    private Spinner brand2, fit2, grade2; // 위와 동일

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_size_plus);

        //brand = findViewById(R.id.sizeBrandName); -> 텍스트뷰에서 스피너로 변경, 죽은 변수
        product = findViewById(R.id.sizeProductName);
        size = findViewById(R.id.sizeProductSize);
        //fit = findViewById(R.id.sizeFit);
        //grade = findViewById(R.id.sizeGrade);
        memo = findViewById(R.id.sizeMemo);

        brand2 = findViewById(R.id.sizeBrandName);
        brandAdapter = ArrayAdapter.createFromResource(this, R.array.brandName, android.R.layout.simple_spinner_dropdown_item);
        brand2.setAdapter(brandAdapter);
        fit2 = findViewById(R.id.sizeFit);
        fitA = ArrayAdapter.createFromResource(this, R.array.fit, android.R.layout.simple_spinner_dropdown_item);
        fit2.setAdapter(fitA);
        grade2 = findViewById(R.id.sizeGrade);
        gradeA = ArrayAdapter.createFromResource(this, R.array.grade, android.R.layout.simple_spinner_dropdown_item);
        grade2.setAdapter(gradeA);

        save = findViewById(R.id.sizePlusSave);
        cancle = findViewById(R.id.sizePlusCancle);


        Intent getIntent = getIntent();// 수정시 인텐트를 받아와야함
        if (getIntent.hasExtra("수정")) {
            save.setText("수정");
            save.setTextColor(Color.RED);
            product.setText(getIntent.getStringExtra("productName"));
            size.setText(getIntent.getStringExtra("productSize"));
            memo.setText(getIntent.getStringExtra("userMemo"));
            //스피너 분류작업
            String a = getIntent.getStringExtra("brandName");
            String b = getIntent.getStringExtra("sizeFit");
            String c = getIntent.getStringExtra("productGrade");
            classification(a,b,c);
            final String time = getIntent.getStringExtra("systemTime");
            //수정시 세이브 클릭
            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (brand2.getSelectedItem().toString().equals("브랜드명을 선택해 주세요") || product.getText().toString().equals("") ||
                            size.getText().toString().equals("") || fit2.getSelectedItem().toString().equals("핏감을 선택해 주세요") ||
                            grade2.getSelectedItem().toString().equals("평점을 선택해 주세요")) {
                        Toast.makeText(SizePlusActivity.this, "항목을 모두 입력해 주세요", Toast.LENGTH_SHORT).show();
                    } else {
                        SharedPreferences pref = getSharedPreferences("USER", MODE_PRIVATE);

                        //사용자 정보를 파싱해서 인텐트로 넘기기
                        String details = pref.getString(LoginActivity.etId, "");
                        Log.d("디버깅", "onClick: 보드작성 액티비티에서 로그인 id별로 값을 제대로 받아오는지 확인: " + details);

                        JsonParser parser = new JsonParser();//값을 넣어주기 위해 파싱한다
                        JsonElement element = parser.parse(details);
                        String genderP = element.getAsJsonObject().get("gender").getAsString(); // 파싱된 성별 값
                        String ageP = element.getAsJsonObject().get("age").getAsString(); // 파싱된 나이
                        String tallP = element.getAsJsonObject().get("tall").getAsString(); // 파싱된 키
                        String kgP = element.getAsJsonObject().get("kg").getAsString(); // 파싱된 몸무게
                        String torsoP = element.getAsJsonObject().get("torso").getAsString(); // 파싱된 토르소값

                        Intent intent = new Intent();
                        intent.putExtra("brandName", brand2.getSelectedItem().toString());
                        intent.putExtra("productName", product.getText().toString());
                        intent.putExtra("productSize", size.getText().toString());
                        Log.d("디버깅1", "onClick: "+ brand2.getSelectedItem().toString());
                        String title = brand2.getSelectedItem().toString()+ " " +product.getText().toString()+ " " + size.getText().toString() + " size ";
                        intent.putExtra("boardTitle",title);
                        intent.putExtra("sizeFit", fit2.getSelectedItem().toString());
                        intent.putExtra("productGrade", grade2.getSelectedItem().toString());
                        intent.putExtra("userMemo", memo.getText().toString());
                        intent.putExtra("systemTime", time);

                        intent.putExtra("gender", genderP);
                        intent.putExtra("age", ageP);
                        intent.putExtra("tall", tallP);
                        intent.putExtra("kg", kgP);
                        intent.putExtra("torso", torsoP); // 파싱되어 얻어진 사용자 정보를 인텐트로 넘김 -> sizeActivity

                        intent.putExtra("수정완료", ""); //수정이 완료되어 보내진 인텐트 값으로 게시물 목록<->게시물 저장

                        setResult(RESULT_OK, intent); //10001 반환코드
                        finish();
                    }
                }
            });
        }else {
//저장시 세이브 클릭
            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (brand2.getSelectedItem().toString().equals("브랜드명을 선택해 주세요") || product.getText().toString().equals("") ||
                            size.getText().toString().equals("") || fit2.getSelectedItem().toString().equals("핏감을 선택해 주세요") ||
                            grade2.getSelectedItem().toString().equals("평점을 선택해 주세요")) {
                        Toast.makeText(SizePlusActivity.this, "항목을 모두 입력해 주세요", Toast.LENGTH_SHORT).show();
                    } else {
                        //if (brand2.getSelectedItem().toString().equals("기타")){
                        //"스피너 브랜드명 기타일 경우 입력하게 하면 어떨까"}
                        SharedPreferences pref = getSharedPreferences("USER", MODE_PRIVATE);

                        //사용자 정보를 파싱해서 인텐트로 넘기기
                        String details = pref.getString(LoginActivity.etId, "");
                        Log.d("디버깅", "onClick: 보드작성 액티비티에서 로그인 id별로 값을 제대로 받아오는지 확인: " + details);

                        JsonParser parser = new JsonParser();//값을 넣어주기 위해 파싱한다
                        JsonElement element = parser.parse(details);
                        String genderP = element.getAsJsonObject().get("gender").getAsString(); // 파싱된 성별 값
                        String ageP = element.getAsJsonObject().get("age").getAsString(); // 파싱된 나이
                        String tallP = element.getAsJsonObject().get("tall").getAsString(); // 파싱된 키
                        String kgP = element.getAsJsonObject().get("kg").getAsString(); // 파싱된 몸무게
                        String torsoP = element.getAsJsonObject().get("torso").getAsString(); // 파싱된 토르소값

                        String inTime = new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new java.util.Date());
                        Intent intent = new Intent();
                        intent.putExtra("brandName", brand2.getSelectedItem().toString());
                        intent.putExtra("productName", product.getText().toString());
                        intent.putExtra("productSize", size.getText().toString());
                        intent.putExtra("sizeFit", fit2.getSelectedItem().toString());
                        intent.putExtra("productGrade", grade2.getSelectedItem().toString());
                        intent.putExtra("userMemo", memo.getText().toString());
                        intent.putExtra("systemTime", inTime);

                        intent.putExtra("gender", genderP);
                        intent.putExtra("age", ageP);
                        intent.putExtra("tall", tallP);
                        intent.putExtra("kg", kgP);
                        intent.putExtra("torso", torsoP); // 파싱되어 얻어진 사용자 정보를 인텐트로 넘김 -> sizeActivity

                        setResult(RESULT_OK, intent); //315 반환코드
                        finish();
                    }
                }
            });
        }
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish(); //뒤로가기랑 같은 결과, 취소 반환환
            }
        });
    }

    private void classification (String a, String b, String c){ //스피너 분류작업
        if (a.equals("나이키")) brand2.setSelection(1); if (a.equals("돌핀")) brand2.setSelection(2);
        if (a.equals("랠리")) brand2.setSelection(3); if (a.equals("르망고")) brand2.setSelection(4);
        if (a.equals("미즈노")) brand2.setSelection(5); if (a.equals("센티")) brand2.setSelection(6);
        if (a.equals("스포티")) brand2.setSelection(7); if (a.equals("스피도")) brand2.setSelection(8);
        if (a.equals("스완스")) brand2.setSelection(9); if (a.equals("아레나")) brand2.setSelection(10);
        if (a.equals("제이키드")) brand2.setSelection(11); if (a.equals("토네이도")) brand2.setSelection(12);
        if (a.equals("티에스나인")) brand2.setSelection(13); if (a.equals("펑키타")) brand2.setSelection(14);
        if (a.equals("후그")) brand2.setSelection(15); if (a.equals("기타")) brand2.setSelection(16);
        if (b.equals("아주 타이트해요")) fit2.setSelection(1); if (b.equals("약간 타이트해요")) fit2.setSelection(2);
        if (b.equals("적당해요")) fit2.setSelection(3); if (b.equals("약간 커요")) fit2.setSelection(4);
        if (b.equals("아주 커요")) fit2.setSelection(5); if (c.equals("5")) grade2.setSelection(1);
        if (c.equals("4")) grade2.setSelection(2); if (c.equals("3")) grade2.setSelection(3);
        if (c.equals("2")) grade2.setSelection(4); if (c.equals("1")) grade2.setSelection(5);
    }
}
