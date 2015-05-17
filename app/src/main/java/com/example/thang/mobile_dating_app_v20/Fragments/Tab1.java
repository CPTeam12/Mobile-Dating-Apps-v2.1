package com.example.thang.mobile_dating_app_v20.Fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.thang.mobile_dating_app_v20.Adapters.GribAdapter;
import com.example.thang.mobile_dating_app_v20.Classes.Friend;
import com.example.thang.mobile_dating_app_v20.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Thang on 5/15/2015.
 */
public class Tab1 extends Fragment {
    private static final int DEFAULT_COLUMNS_PORTRAIT = 2;
    private static final int DEFAULT_COLUMNS_LANDSCAPE = 3;
    public static final String NAME = "name";
    public static final String WALL = "wall";

    private List<Friend> friends = new ArrayList<>();
    private int numColumns = 2;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab_1, container, false);

        int mColumnCountPortrait = DEFAULT_COLUMNS_PORTRAIT;
        Friend friend = new Friend("ThangPHam");
        friends.add(friend);
        friends.add(friend);
        friends.add(friend);
        friends.add(friend);
        friends.add(friend);

        ProgressBar mProgress = (ProgressBar) v.findViewById(R.id.progress);
        final GridView gridView = (GridView) v.findViewById(R.id.gridView);
        TextView title = (TextView) v.findViewById(R.id.tab_title);
        title.setText(friends.size() + " " + getResources().getString(R.string.tab1_title));
        gridView.setNumColumns(numColumns);
        final GribAdapter gribAdapter = new GribAdapter(getActivity(), friends, numColumns);
        gridView.setAdapter(gribAdapter);

        if (mProgress != null)
            mProgress.setVisibility(View.GONE);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getActivity(), "Clicked" + friends.get(position).getName(), Toast.LENGTH_SHORT).show();
            }
        });

        return v;
    }
}
