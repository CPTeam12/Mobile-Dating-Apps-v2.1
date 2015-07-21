package com.example.thang.mobile_dating_app_v20.Fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.thang.mobile_dating_app_v20.Adapters.HobbyAdapter;
import com.example.thang.mobile_dating_app_v20.Classes.DBHelper;
import com.example.thang.mobile_dating_app_v20.Classes.Hobby;
import com.example.thang.mobile_dating_app_v20.Classes.Person;
import com.example.thang.mobile_dating_app_v20.R;
import com.github.ksoichiro.android.observablescrollview.ObservableListView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;

import java.util.List;

/**
 * Created by Thang on 7/16/2015.
 */
public class ProfileTab2 extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_tab2, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        Person person = (Person) bundle.getSerializable("Person");

        if (person.getEmail() != null) {
            String hobbyString = person.getHobbies();
            List<Hobby> hobbyList = Hobby.toList(hobbyString);

            ObservableListView listView = (ObservableListView) view.findViewById(R.id.scroll);
            HobbyAdapter hobbyAdapter = new HobbyAdapter(getActivity(), hobbyList, HobbyAdapter.FLAG_DETAIL);
            listView.setAdapter(hobbyAdapter);

            Activity parentActivity = getActivity();
            listView.setTouchInterceptionViewGroup((ViewGroup) parentActivity.findViewById(R.id.container));
            if (parentActivity instanceof ObservableScrollViewCallbacks) {
                listView.setScrollViewCallbacks((ObservableScrollViewCallbacks) parentActivity);
            }
        }
    }
}
