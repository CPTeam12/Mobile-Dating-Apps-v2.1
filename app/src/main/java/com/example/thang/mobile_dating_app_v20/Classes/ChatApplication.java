package com.example.thang.mobile_dating_app_v20.Classes;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

/**
 * Created by Man Huynh Khuong on 5/14/2015.
 */
public class ChatApplication  extends Application{
    public static final String YOUR_APPLICATION_ID = "1mglo31VAlMVSGrVp1lTmPleS07F2FOLUmIEkJBe";
    public static final String YOUR_CLIENT_KEY = "LMBiJMuxQJvULYwVgdZbaO4NIuFsrwfyx64cBqBK";

    @Override
    public void onCreate() {
        ParseObject.registerSubclass(Message.class);
        Parse.enableLocalDatastore(this);
        Parse.initialize(this.getApplicationContext(), YOUR_APPLICATION_ID, YOUR_CLIENT_KEY);

    }
}
