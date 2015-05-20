package com.example.thang.mobile_dating_app_v20.Fragments;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.thang.mobile_dating_app_v20.Classes.Friend;
import com.example.thang.mobile_dating_app_v20.Adapters.ListAdapter;
import com.example.thang.mobile_dating_app_v20.R;


import java.util.LinkedList;
import java.util.List;

/**
 * Created by Thang on 5/15/2015.
 */
public class Tab2 extends Fragment  {
    private ListView mList;
    private ListAdapter mAdapter;
    private List<Friend> friends = new LinkedList<>();
    private Context context;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab_2, container, false);
        mList = (ListView) v.findViewById(R.id.friendlist);

        Friend friend = new Friend("Thang Pham");
        friends.add(friend);
        friends.add(friend);
        friends.add(friend);
        friends.add(friend);
        friends.add(friend);
        friends.add(friend);
        friends.add(friend);
        friends.add(friend);
        friends.add(friend);
        friends.add(friend);
        if (!friends.isEmpty()) {
            mAdapter = new ListAdapter(friends, getActivity());
            mList.setAdapter(mAdapter);

            if (mList != null) {

                mList.setClickable(true);
            }

        }
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getActivity(), "Clicked" + friends.get(position).getName(), Toast.LENGTH_SHORT).show();
            }
        });

        return v;
    }

}
