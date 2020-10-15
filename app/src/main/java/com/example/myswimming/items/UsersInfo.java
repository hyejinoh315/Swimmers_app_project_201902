package com.example.myswimming.items;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class UsersInfo { //회원들 고유한 정보들
    public String userEmail, userPassword, userName, userGender, userAge, userTall, userWeight, userTorso;
    String userJson;

    public UsersInfo(String userEmail, String userPassword, String userName,
                     String userGender, String userAge, String userTall,
                     String userWeight, String userTorso) {
        this.userEmail = userEmail;
        this.userPassword = userPassword;
        this.userName = userName;
        this.userGender = userGender;
        this.userAge = userAge;
        this.userTall = userTall;
        this.userWeight = userWeight;
        this.userTorso = userTorso;
    }

    public String makeJson(int x) {
        //리스트형식의 개인 정보
        Gson gson = new Gson();
        JsonObject object = new JsonObject();
        object.addProperty("id", userEmail);
        object.addProperty("pw", userPassword);
        object.addProperty("name", userName);
        object.addProperty("gender", userGender);
        object.addProperty("age", userAge);
        object.addProperty("tall", userTall);
        object.addProperty("kg", userWeight);
        object.addProperty("torso", userTorso);
        String json = gson.toJson(object);
/*        //최종 식별자 + 개인정보를 저장 json
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(userEmail, "[" + json + "]");
        userJson = gson.toJson(jsonObject);
*/        //String userJson -> 전체 회원을 배열로 묶는다
        switch (x) {
            case 1:
                //한명의 유저의 정보만 배열로 묶어서 이용한다
                return json;
            /*case 2:
                //데이터베이스?에 이용한다
                return userJson;
        */}
        return "";
    }

    public void 파싱 (String json){
        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(json);
        String idP = element.getAsJsonObject().get("id").getAsString();
        String pwP = element.getAsJsonObject().get("pw").getAsString();
        String nameP = element.getAsJsonObject().get("name").getAsString();
        String genderP = element.getAsJsonObject().get("gender").getAsString();
        String ageP = element.getAsJsonObject().get("age").getAsString();
        String tallP = element.getAsJsonObject().get("tall").getAsString();
        String kgP = element.getAsJsonObject().get("kg").getAsString();
        String torsoP = element.getAsJsonObject().get("torso").getAsString();
    }
}
