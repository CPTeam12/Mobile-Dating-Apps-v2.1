package com.example.thang.mobile_dating_app_v20.Activity;

import android.content.res.TypedArray;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.widget.ListView;

import com.example.thang.mobile_dating_app_v20.Adapters.ProfileAdapter;
import com.example.thang.mobile_dating_app_v20.Classes.Friend;
import com.example.thang.mobile_dating_app_v20.Classes.Person;
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
    protected void setFriendAdapter(ListView listView, Person person) {
        Map<String, String> friendHashMap = transferToMap(person);
        ProfileAdapter profileAdapter = new ProfileAdapter(friendHashMap,getApplicationContext());
        if (!friendHashMap.isEmpty()){
            listView.setAdapter(profileAdapter);
            if (listView!=null){
                listView.setClickable(true);
            }
        }
    }

    private Map<String, String> transferToMap(Person person){
        Map<String, String> transfer = new HashMap<>();
        transfer.put("AGE",String.valueOf(person.getAge()));
        transfer.put("EMAIL",person.getEmail());
        transfer.put("GENDER",person.getGender());
        transfer.put("AVATAR",person.getAvatar());
        transfer.put("PHONE",String.valueOf(person.getPhone()));
        transfer.put("ADDRESS",person.getAddress());
        return transfer;
    }

}

