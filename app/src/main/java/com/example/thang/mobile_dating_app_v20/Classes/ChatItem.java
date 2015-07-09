package com.example.thang.mobile_dating_app_v20.Classes;

/**
 * Created by Thang on 7/8/2015.
 */
public class ChatItem {
    private  Message message;
    private String time;
    private boolean isDivider;

    public ChatItem(Message message, boolean isDivider, String time){
        this.message = message;
        this.time = time;
        this.isDivider = isDivider;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isDivider() {
        return isDivider;
    }

    public void setIsDivider(boolean isDivider) {
        this.isDivider = isDivider;
    }
}
