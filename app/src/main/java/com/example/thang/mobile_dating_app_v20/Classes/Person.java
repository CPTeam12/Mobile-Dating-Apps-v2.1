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
public class Person implements Serializable {

    //Profile
    //private String username;
    private String password;
    private String fullName;
    private String email;
    private int age;
    private String gender;
    private String avatar;
    //optionals
    private String phone;
    private String address;
    private String hobbies;

    //facebook
    private String facebookId;

    //Position
    private double longitude;
    private double latitude;
    private String lastKnown;

    //dating search options
    private boolean datingMen;
    private boolean datingWomen;
    private String datingProfile;
    private int datingAge;

    //notification
    private String registrationID;

    //Matching profile percent
    private double percent;

    public Person() {
    }

    public Person(double latitude, double longitude, String fullName) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.fullName = fullName;
    }

    public String getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(String facebookId) { this.facebookId = facebookId; }

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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
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

    public String getLastKnown() {
        return lastKnown;
    }

    public void setLastKnown(String lastKnown) {
        this.lastKnown = lastKnown;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getHobbies() {
        return hobbies;
    }

    public void setHobbies(String hobbies) {
        this.hobbies = hobbies;
    }

    public boolean isDatingMen() {
        return datingMen;
    }

    public void setDatingMen(boolean datingMen) {
        this.datingMen = datingMen;
    }

    public boolean isDatingWomen() {
        return datingWomen;
    }

    public void setDatingWomen(boolean datingWomen) {
        this.datingWomen = datingWomen;
    }

    public String getDatingProfile() {
        return datingProfile;
    }

    public void setDatingProfile(String datingProfile) {
        this.datingProfile = datingProfile;
    }

    public int getDatingAge() {
        return datingAge;
    }

    public void setDatingAge(int datingAge) {
        this.datingAge = datingAge;
    }

    public String getRegistrationID() {
        return registrationID;
    }

    public void setRegistrationID(String registrationID) {
        this.registrationID = registrationID;
    }

    public double getPercent() {
        return percent;
    }

    public void setPercent(double percent) {
        this.percent = percent;
    }

    @Override
    public String toString() {
        return " Fullname: " + fullName + "; Gender: " + gender;
    }

}
