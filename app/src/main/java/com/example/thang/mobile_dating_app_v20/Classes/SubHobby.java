package com.example.thang.mobile_dating_app_v20.Classes;

/**
 * Created by Thang on 7/15/2015.
 */
public class SubHobby{
    String name;
    boolean isSelected;

    public SubHobby(String name, boolean isSelected){
        this.name = name;
        this.isSelected = isSelected;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getIsSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }
}
