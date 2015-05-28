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
    private double longitude;
    private double latitude;

    public Friend(double longitude,double latitude){
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public Friend(String name) {
        this.name = name;
    }

    public Friend(int avatar, String name, String email, String gender, int age) {
        this.avatar = avatar;
        this.name = name;
        this.email = email;
        this.gender = gender;
        this.age = age;
    }


    public int getAvatar() {
        return this.avatar;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAvatar(int avatar) {
        this.avatar = avatar;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longtitude) {
        this.longitude = longtitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
}
