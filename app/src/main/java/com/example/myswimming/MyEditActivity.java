package com.example.myswimming;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import com.example.myswimming.preLogin.LoginActivity;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class MyEditActivity extends AppCompatActivity {
    private EditText name, age, tall, kg, torso;
    private Button save, pwChange, userDelete;
    private RadioGroup gender2;
    HomeActivity home = (HomeActivity) HomeActivity.home; // 로그아웃시에 홈 액티비티(다른 액티비티)를 소멸시켜야해서 선언해준다
    MypageActivity myPage = (MypageActivity) MypageActivity.myPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_edit);
        SharedPreferences pref = getSharedPreferences("USER", MODE_PRIVATE);
        String details = pref.getString(LoginActivity.etId, "");

        JsonParser parser = new JsonParser();//값을 넣어주기 위해 파싱한다
        JsonElement element = parser.parse(details);

        String nameP = element.getAsJsonObject().get("name").getAsString();
        Intent getIntent = getIntent();
        final String id = getIntent.getStringExtra("myPageId");

        name = findViewById(R.id.myNameEdit);
        age = findViewById(R.id.myAgeEdit);
        tall = findViewById(R.id.myHeightEdit);
        kg = findViewById(R.id.myWeightEdit);
        torso = findViewById(R.id.myTorsoEdit);

        String ageI = getIntent.getStringExtra("age");
        String tallI = getIntent.getStringExtra("tall");
        String kgI = getIntent.getStringExtra("kg");
        String torsoI = getIntent.getStringExtra("torso");

        name.setText(nameP);
        age.setText(ageI);
        tall.setText(tallI);
        kg.setText(kgI);
        torso.setText(torsoI);

        gender2 = findViewById(R.id.genderGroup);
        if(getIntent.getStringExtra("gender") != null) {
            String genderI = getIntent.getStringExtra("gender");
            if (genderI.equals("남성")) {
                RadioButton femaleButton = findViewById(R.id.femaleRadio);
                femaleButton.setChecked(false);
                RadioButton maleButton = findViewById(R.id.maleRadio);
                maleButton.setChecked(true);
            }
        }
        userDelete = findViewById(R.id.userDeleteButton);
        userDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MyEditActivity.this);
                builder.setTitle("SWIMMERS");
                builder.setMessage("정말로 탈퇴하시겠습니까?\n탈퇴시 작성된 게시물과 댓글은 자동으로 삭제되지 않습니다.");
                final EditText nowPW = new EditText(MyEditActivity.this);
                nowPW.setHint("현재 사용중인 비밀번호를 입력해 주세요");
                builder.setView(nowPW);
                builder.setPositiveButton("탈퇴하기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //현재창의 비밀번호 값이 동일하고 & 수정, 확인이 동일하면 저장되며 넘어감
                        SharedPreferences pref = getSharedPreferences("USER", MODE_PRIVATE);
                        String details = pref.getString(LoginActivity.etId, "");
                        JsonParser parser = new JsonParser();
                        JsonElement element = parser.parse(details);
                        String pwP = element.getAsJsonObject().get("pw").getAsString();

                        if(nowPW.getText().toString().equals(pwP)){
                            pref.edit().remove(LoginActivity.etId).commit();
                            Log.d("삭제 디버깅", "onClick: " + pref.getString(LoginActivity.etId, ""));
                            //비밀번호 변경 토스트 띄우기
                            Toast.makeText(MyEditActivity.this, "탈퇴가 완료되었습니다", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MyEditActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                            home.finish();
                            myPage.finish();
                            dialog.dismiss();
                        } else {//비밀번호가 일치하지 않는 경우 회원 탈퇴가 이루어지지 않는다
                            Toast.makeText(MyEditActivity.this, "비밀번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                //취소 버튼 설정
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                //다이얼로그 창 띄우기
                builder.show();
            }
        });
        pwChange = findViewById(R.id.pwEditButton);
        pwChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(MyEditActivity.this);
                builder.setTitle("비밀번호 변경");

                LinearLayout layout = new LinearLayout(MyEditActivity.this);
                layout.setOrientation(LinearLayout.VERTICAL);
                final EditText nowPW = new EditText(MyEditActivity.this);
                final EditText editPW = new EditText(MyEditActivity.this);
                final EditText editPWCheck = new EditText(MyEditActivity.this);

                nowPW.setHint("현재 사용중인 비밀번호를 입력해 주세요");
                editPW.setHint("수정할 비밀번호를 입력해주세요");
                editPWCheck.setHint("수정할 비밀번호를 재입력해주세요");

                nowPW.setTransformationMethod(PasswordTransformationMethod.getInstance());
                editPW.setTransformationMethod(PasswordTransformationMethod.getInstance());
                editPWCheck.setTransformationMethod(PasswordTransformationMethod.getInstance());

                layout.addView(nowPW);
                layout.addView(editPW);
                layout.addView(editPWCheck);

                builder.setView(layout);
                //저장 버튼 설정
                builder.setPositiveButton("EDIT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //현재창의 비밀번호 값이 동일하고 & 수정, 확인이 동일하면 저장되며 넘어감
                        SharedPreferences pref = getSharedPreferences("USER", MODE_PRIVATE);
                        String details = pref.getString(LoginActivity.etId, "");
                        JsonParser parser = new JsonParser();
                        JsonElement element = parser.parse(details);
                        String pwP = element.getAsJsonObject().get("pw").getAsString();

                        if(editPW.getText().toString().equals(editPWCheck.getText().toString())
                                && nowPW.getText().toString().equals(pwP)){
                            element.getAsJsonObject().addProperty("pw", editPW.getText().toString());
                            pref.edit().putString(LoginActivity.etId, element.getAsJsonObject().toString()).commit();
                            //비밀번호 변경 토스트 띄우기
                            Toast.makeText(MyEditActivity.this, "비밀번호 변경이 완료 되었습니다", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }if(!nowPW.getText().toString().equals(pwP)) {
                            //사용중인 비밀번호 맞지 않음
                            Toast.makeText(MyEditActivity.this, "사용중인 비밀번호를 다시 입력해 주세요", Toast.LENGTH_SHORT).show();
                        }if(!editPW.getText().toString().equals(editPWCheck.getText().toString())){
                            //수정에 필요한 두 값이 맞지 않음
                            Toast.makeText(MyEditActivity.this, "변경할 비밀번호 확인 값이 일치하지 않습니다", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                //취소 버튼 설정
                builder.setNegativeButton("CANCLE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                //다이얼로그 창 띄우기
                builder.show();
            }
        });

        save = findViewById(R.id.editOkButton);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { // 저장버튼 누르면
                if(age.getText().toString().equals("") || tall.getText().toString().equals("")
                        || kg.getText().toString().equals("") || torso.getText().toString().equals("")
                        || name.getText().toString().equals("")) {
                    Toast.makeText(MyEditActivity.this, "항목을 모두 작성해 주세요", Toast.LENGTH_SHORT).show();

                } else {

                    RadioButton radioButton = findViewById(gender2.getCheckedRadioButtonId());
                    String male_female = radioButton.getText().toString();
                    //Log.d("EDIT_PAGE", "onClick: 인텐트디버깅: " + male_female);

                    Intent intent = new Intent();
                    intent.putExtra("gender", male_female);
                    intent.putExtra("age", age.getText().toString()); // 작성된
                    intent.putExtra("tall", tall.getText().toString()); // 문자를
                    intent.putExtra("kg", kg.getText().toString()); // 텍스트로 변환후
                    intent.putExtra("torso", torso.getText().toString()); // 엑스트라로 전달

                    SharedPreferences pref = getSharedPreferences("USER", MODE_PRIVATE);
                    String jsonString = pref.getString(id, "");

                    Log.d("디버깅", "onClick: jsonString: " + jsonString + "/" + id);

                    JsonParser parser = new JsonParser();
                    JsonElement element = parser.parse(jsonString);

//                Log.d("디버깅", "onClick: element: " + ;);
                    JsonObject jsonObject = element.getAsJsonObject();
                    jsonObject.addProperty("name", name.getText().toString());
                    jsonObject.addProperty("gender", male_female);
                    jsonObject.addProperty("age", age.getText().toString());
                    jsonObject.addProperty("tall", tall.getText().toString());
                    jsonObject.addProperty("kg", kg.getText().toString());
                    jsonObject.addProperty("torso", torso.getText().toString());

                    Log.d("디버깅", "jsonString-2 : " + jsonObject.toString());

                    getSharedPreferences("USER", MODE_PRIVATE)
                            .edit().putString(id, jsonObject.toString()).commit();

                    Log.d("디버깅", "getSharedPreferences-store : " + getSharedPreferences("USER", MODE_PRIVATE).getString(id, "null"));

                    setResult(RESULT_OK, intent); // RESULT_OK 코드 반환 및 인텐트 전달
                    finish(); // 메모되는 액티비티도 종료
                    //Log.d("MyEditActivity", "onClick: "+age.getText().toString());
                    }
            }
        });
    }
}
