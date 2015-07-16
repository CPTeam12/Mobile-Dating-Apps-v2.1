package com.example.thang.mobile_dating_app_v20.Classes;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Thang on 7/15/2015.
 */
public class Hobby {
    private List<SubHobby> subHobby;
    private String name;

    public Hobby(String name, List<SubHobby> subHobby){
        this.name = name;
        this.subHobby = subHobby;
    }

    public static String toStringFromObject(Hobby hobby) {
        Gson gson = new Gson();
        return  gson.toJson(hobby);
    }

    public static String toStringFromList(List<Hobby> hobbies){
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<Hobby>>() {
        }.getType();
        return gson.toJson(hobbies,listType);
    }

    public static List<Hobby> toList(String string) {
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<Hobby>>() {
        }.getType();
        return gson.fromJson(string, listType);
    }

    public static Hobby toObject(String string){
        Gson gson = new Gson();
        return  gson.fromJson(string, Hobby.class);
    }

    public List<SubHobby> getSubHobby() {
        return subHobby;
    }

    public void setSubHobby(List<SubHobby> subHobby) {
        this.subHobby = subHobby;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
