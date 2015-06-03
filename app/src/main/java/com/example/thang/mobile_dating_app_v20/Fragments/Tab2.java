package com.example.thang.mobile_dating_app_v20.Fragments;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.thang.mobile_dating_app_v20.Adapters.ListAdapter;
import com.example.thang.mobile_dating_app_v20.Classes.Person;
import com.example.thang.mobile_dating_app_v20.R;


import java.util.LinkedList;
import java.util.List;

/**
 * Created by Thang on 5/15/2015.
 */
public class Tab2 extends Fragment  {
    private ListView mList;
    private ListAdapter mAdapter;
    private List<Person> persons = new LinkedList<>();
    private Context context;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab_2, container, false);
        mList = (ListView) v.findViewById(R.id.friendlist);

        Person person = new Person();
        person.setFullName("Thang Pham");
        persons.add(person);
        persons.add(person);
        persons.add(person);
        persons.add(person);
        persons.add(person);
        persons.add(person);
        persons.add(person);
        persons.add(person);
        persons.add(person);
        persons.add(person);
        if (!persons.isEmpty()) {
            mAdapter = new ListAdapter(persons, getActivity());
            mList.setAdapter(mAdapter);

            if (mList != null) {

                mList.setClickable(true);
            }

        }
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getActivity().setTitle("Chat");
                final FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.addToBackStack(null);
                ft.hide(Tab2.this);
                ft.add(R.id.mainFragment, new Chat(), "Chat");
                ft.setBreadCrumbTitle("Chat");
                ft.commit();
            }
        });

        return v;
    }

}
