package com.example.myswimming.items;

import com.example.myswimming.preLogin.LoginActivity;

public class SizeBoardInfo {
    public String title, time, indexNo;//게시판 리사이클러뷰 아이템(외부+내부)
    public String fit, grade, memo, gender, age, tall, kg, torso; //게시판 내부에서만 사용될 애들
    public String 식별자;
    public String brandName, productName, productSize;//수정시 필요해

    public SizeBoardInfo(String brandName, String productName, String productSize, String time, String indexNo,
                            String fit, String grade, String memo,
                         String gender, String age, String tall, String kg, String torso){
        this.title = brandName + " " + productName + " " + productSize + " size ";
        // 형식 예시 : Nike 이미써블 26 size , 펑키타 스위트베놈 걸14 size
        this.time = time;
        this.indexNo = indexNo;
        this.fit = fit;
        this.grade = grade;
        this.memo = memo;
        this.gender = gender;
        this.age = age;
        this.tall = tall;
        this.kg = kg;
        this.torso = torso;
        this.식별자 = LoginActivity.etId;
        this.brandName = brandName;
        this.productName = productName;
        this.productSize = productSize;
    }
}