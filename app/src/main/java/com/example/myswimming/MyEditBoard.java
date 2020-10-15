package com.example.myswimming;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.myswimming.preLogin.LoginActivity;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class MyEditBoard extends AppCompatActivity {
    private EditText name, age, tall, kg, torso;
    private Button save;
    private RadioGroup gender2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_edit_board);

        final SharedPreferences pref = getSharedPreferences("USER", MODE_PRIVATE);
        String details = pref.getString(LoginActivity.etId, "");

        JsonParser parser = new JsonParser();//값을 넣어주기 위해 파싱한다
        JsonElement element = parser.parse(details);

        String nameP = element.getAsJsonObject().get("name").getAsString();

        name = findViewById(R.id.myNameEdit);
        age = findViewById(R.id.myAgeEdit);
        tall = findViewById(R.id.myHeightEdit);
        kg = findViewById(R.id.myWeightEdit);
        torso = findViewById(R.id.myTorsoEdit);

        name.setText(nameP);

        gender2 = findViewById(R.id.genderGroup);
        save = findViewById(R.id.editOkButton);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { // 저장버튼 누르면
                if(age.getText().toString().equals("") || tall.getText().toString().equals("")
                        || kg.getText().toString().equals("") || torso.getText().toString().equals("")
                        || name.getText().toString().equals("")) {
                    Toast.makeText(MyEditBoard.this, "항목을 모두 작성해 주세요", Toast.LENGTH_SHORT).show();

                } else {

                    RadioButton radioButton = findViewById(gender2.getCheckedRadioButtonId());
                    String male_female = radioButton.getText().toString();
                    //Log.d("EDIT_PAGE", "onClick: 인텐트디버깅: " + male_female);

                    String jsonString = pref.getString(LoginActivity.etId, "");


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
                            .edit().putString(LoginActivity.etId, jsonObject.toString()).commit();

                    finish();
                }
            }
        });

    }
}
