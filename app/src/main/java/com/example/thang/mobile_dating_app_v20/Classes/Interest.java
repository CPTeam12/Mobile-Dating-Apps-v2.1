package com.example.thang.mobile_dating_app_v20.Classes;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Thang on 7/15/2015.
 */
public class Interest {
    private List<SubInterest> subInterest;
    private String name;

    public Interest(String name, List<SubInterest> subInterest){
        this.name = name;
        this.subInterest = subInterest;
    }

    public static String toStringFromObject(Interest interest) {
        Gson gson = new Gson();
        return  gson.toJson(interest);
    }

    public static String toStringFromList(List<Interest> hobbies){
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<Interest>>() {
        }.getType();
        return gson.toJson(hobbies,listType);
    }

    public static List<Interest> toList(String string) {
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<Interest>>() {
        }.getType();
        return gson.fromJson(string, listType);
    }

    public static Interest toObject(String string){
        Gson gson = new Gson();
        return  gson.fromJson(string, Interest.class);
    }

    public List<SubInterest> getSubInterest() {
        return subInterest;
    }

    public void setSubInterest(List<SubInterest> subInterest) {
        this.subInterest = subInterest;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
