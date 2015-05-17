package com.example.thang.mobile_dating_app_v20.Classes;

/**
 * Created by Thang on 5/16/2015.
 */
public class Friend {
    private int avatar;
    private String name;
    private String email;
    private String gender;
    private int age;

    public Friend(String name){
        this.name = name;
    }

    public Friend(int avatar,String name, String email, String gender, int age){
        this.avatar = avatar;
        this.name = name;
        this.email = email;
        this.gender = gender;
        this.age = age;
    }


    public int getAvatar(){
        return this.avatar;
    }

    public String getName(){
        return this.name;
    }

    public void setName(String name){
        this.name = name;
    }

}
