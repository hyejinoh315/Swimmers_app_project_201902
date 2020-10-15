package com.example.myswimming.items;

import com.example.myswimming.preLogin.LoginActivity;

public class SizeReplyInfo { // 캘린더 이벤트 items
    public String userId, reply, time, 식별자;

    public SizeReplyInfo(String userId, String reply, String time){
        this.userId = userId; //닉네임으로 변경
        this.reply = reply;
        this.time = time;
        this.식별자 = LoginActivity.etId;
    }
}