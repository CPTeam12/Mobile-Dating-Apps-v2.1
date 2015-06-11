package com.example.thang.mobile_dating_app_v20.Classes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Thang on 5/31/2015.
 */
public class Person implements Serializable{
    //Profile
    //private String username;
    private String password;
    private String fullName;
    private String email;
    private int age;
    private String gender;
    private String avatar;
    private int phone;
    private String address;

    //facebook
    private int facebookId;


    //Position
    private double longitude;
    private double latitude;
    private int lastKnown;

    //dating search options
    private boolean dating_men;
    private boolean dating_women;
    private String dating_profile;
    private int dating_age;

    public Person(){

    }

    public Person(double latitude,double longitude ){
        this.longitude = longitude;
        this.latitude = latitude;
    }



    public Person( String password, String fullName, String email, int age, String gender) {
        //this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.email = email;
        this.age = age;
        this.gender = gender;
    }

//    public String getUsername() {
//        return username;
//    }
//
//    public void setUsername(String username) {
//        this.username = username;
//    }

    public int getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(int facebookId) {
        this.facebookId = facebookId;
    }
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getPhone() {
        return phone;
    }

    public void setPhone(int phone) {
        this.phone = phone;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public int getLastKnown() {
        return lastKnown;
    }

    public void setLastKnown(int lastKnown) {
        this.lastKnown = lastKnown;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString(){
        return " Fullname: " + fullName + "; Gender: " + gender;
    }




}
