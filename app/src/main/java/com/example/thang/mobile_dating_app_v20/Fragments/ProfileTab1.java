package com.example.thang.mobile_dating_app_v20.Fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.thang.mobile_dating_app_v20.Adapters.ProfileInfoAdapter;
import com.example.thang.mobile_dating_app_v20.Classes.Person;
import com.example.thang.mobile_dating_app_v20.R;
import com.github.ksoichiro.android.observablescrollview.ObservableListView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Thang on 7/16/2015.
 */
public class ProfileTab1 extends Fragment {


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_tab1, container, false);

        Activity parentActivity = getActivity();
        ObservableListView listView = (ObservableListView) view.findViewById(R.id.scroll);
        //set adapter
        Bundle bundle = getArguments();
        Person person = (Person) bundle.getSerializable("Person");
        setFriendAdapter(listView, person);

        listView.setTouchInterceptionViewGroup((ViewGroup) parentActivity.findViewById(R.id.container));
        if (parentActivity instanceof ObservableScrollViewCallbacks) {
            listView.setScrollViewCallbacks((ObservableScrollViewCallbacks) parentActivity);
        }

        return view;
    }

    private void setFriendAdapter(ListView listView, Person person) {
        Map<String, String> friendHashMap = transferToMap(person);
        ProfileInfoAdapter profileInfoAdapter = new ProfileInfoAdapter(friendHashMap, getActivity());
        if (!friendHashMap.isEmpty()) {
            listView.setAdapter(profileInfoAdapter);
            if (listView != null) {
                listView.setClickable(true);
            }
        }
    }

    private Map<String, String> transferToMap(Person person) {
        Map<String, String> transfer = new HashMap<>();
        transfer.put("AGE", String.valueOf(person.getAge()));
        transfer.put("EMAIL", person.getEmail());
        transfer.put("GENDER", person.getGender());
        transfer.put("PHONE", String.valueOf(person.getPhone()));
        transfer.put("ADDRESS", person.getAddress());
        return transfer;
    }
}
