package com.example.thang.mobile_dating_app_v20.Classes;

import android.graphics.Bitmap;

/**
 * Created by Thang 07/04/2015
 */

public class Message {
    public static final String CHAT_FRIEND_ITEM = "Friend";
    public static final String CHAT_ME_ITEM = "Me";

    String message;
    String avatar;
    String time;

    String item;

    public Message() {
    }

    public Message(String message, String avatar, String time, String item) {
        this.message = message;
        this.avatar = avatar;
        this.time = time;
        this.item = item;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }
}
