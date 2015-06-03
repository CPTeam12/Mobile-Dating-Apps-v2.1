package com.example.thang.mobile_dating_app_v20.Fragments;


import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.thang.mobile_dating_app_v20.Adapters.ChatListAdapter;
import com.example.thang.mobile_dating_app_v20.Adapters.GribAdapter;
import com.example.thang.mobile_dating_app_v20.Classes.Message;
import com.example.thang.mobile_dating_app_v20.Classes.Person;
import com.example.thang.mobile_dating_app_v20.R;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Thang on 5/15/2015.
 */
public class Tab3 extends Fragment {

    private static final int DEFAULT_COLUMNS_PORTRAIT = 2;
    private static final int DEFAULT_COLUMNS_LANDSCAPE = 3;
    public static final String NAME = "name";
    public static final String WALL = "wall";

    private List<Person> persons = new ArrayList<>();
    private int numColumns = 2;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab_3,container,false);

        int mColumnCountPortrait = DEFAULT_COLUMNS_PORTRAIT;

        Person person = new Person();
        person.setFullName("Thang Pham");
        persons.add(person);
        persons.add(person);
        persons.add(person);
        persons.add(person);
        persons.add(person);

        ProgressBar mProgress = (ProgressBar) v.findViewById(R.id.progress);
        final GridView gridView = (GridView) v.findViewById(R.id.gridView);
        TextView title = (TextView) v.findViewById(R.id.tab_title);
        title.setText(persons.size() + " " + getResources().getString(R.string.tab1_title));
        gridView.setNumColumns(numColumns);
        final GribAdapter gribAdapter = new GribAdapter(getActivity(), persons, numColumns);
        gridView.setAdapter(gribAdapter);

        if (mProgress != null)
            mProgress.setVisibility(View.GONE);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(getActivity(), "Clicked" + friends.get(position).getName(), Toast.LENGTH_SHORT).show();
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                        .replace(R.id.mainFragment, Fragment.instantiate(getActivity(), "com.example.thang.mobile_dating_app_v20.Fragments.Map"))
                        .commit();
            }
        });

        return v;
    }

}
