package com.example.thang.mobile_dating_app_v20.Activity;

import android.content.res.TypedArray;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.widget.ListView;

import com.example.thang.mobile_dating_app_v20.Adapters.ProfileAdapter;
import com.example.thang.mobile_dating_app_v20.Classes.Friend;
import com.example.thang.mobile_dating_app_v20.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Thang on 5/27/2015.
 */
public abstract class BaseActivity extends AppCompatActivity {

    protected int getActionBarSize() {
        TypedValue typedValue = new TypedValue();
        int[] textSizeAttr = new int[]{R.attr.actionBarSize};
        int indexOfAttrTextSize = 0;
        TypedArray a = obtainStyledAttributes(typedValue.data, textSizeAttr);
        int actionBarSize = a.getDimensionPixelSize(indexOfAttrTextSize, -1);
        a.recycle();
        return actionBarSize;
    }

    //new function
    protected void setFriendAdapter(ListView listView, Friend friend) {
        Map<String, String> friendHashMap = transferToMap(friend);
        ProfileAdapter profileAdapter = new ProfileAdapter(friendHashMap,getApplicationContext());
        if (!friendHashMap.isEmpty()){
            listView.setAdapter(profileAdapter);
            if (listView!=null){
                listView.setClickable(true);
            }
        }
    }

    private Map<String, String> transferToMap(Friend friend){
        Map<String, String> transfer = new HashMap<>();
        transfer.put("NAME",friend.getName());
        transfer.put("AGE",String.valueOf(friend.getAge()));
        transfer.put("EMAIL",friend.getEmail());
        transfer.put("GENDER",friend.getGender());
        transfer.put("GENDER1",friend.getGender());
        transfer.put("GENDER2",friend.getGender());
        transfer.put("GENDER3",friend.getGender());
        transfer.put("GENDER4",friend.getGender());
        transfer.put("GENDER5",friend.getGender());
        return transfer;
    }



}

