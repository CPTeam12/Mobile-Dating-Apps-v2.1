package com.example.thang.mobile_dating_app_v20.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.thang.mobile_dating_app_v20.Adapters.SubHobbyAdapter;
import com.example.thang.mobile_dating_app_v20.Classes.Hobby;
import com.example.thang.mobile_dating_app_v20.R;

import java.util.List;

/**
 * Created by Thang on 7/14/2015.
 */
public class SubHobbyFragment extends Fragment {
    private GridView gridView;
    private Hobby hobby;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String temp = getArguments().getString("Hobby");
        hobby = Hobby.toObject(temp);

        ActionBar actionBar = ((ActionBarActivity)getActivity()).getSupportActionBar();
        actionBar.setTitle(hobby.getName());
        actionBar.setSubtitle(R.string.hobby_choose);

        SubHobbyAdapter adapter = new SubHobbyAdapter(getActivity(), hobby.getSubHobby());
        gridView.setAdapter(adapter);
        gridView.setNumColumns(3);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                int select = view.findViewById(R.id.selected).getVisibility();
                if (select == View.GONE) {
                    view.findViewById(R.id.selected).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.name).setVisibility(View.GONE);
                    hobby.getSubHobby().get(position).setIsSelected(true);
                } else {
                    view.findViewById(R.id.selected).setVisibility(View.GONE);
                    view.findViewById(R.id.name).setVisibility(View.VISIBLE);
                    hobby.getSubHobby().get(position).setIsSelected(false);
                }
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sub_hobby_fragment, container, false);
        gridView = (GridView) view.findViewById(R.id.gridview);
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //read share reference
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        String sharePref = sharedPref.getString("Hobby", "");
        List<Hobby> hobbies = Hobby.toList(sharePref);

        for(Hobby item : hobbies){
            if (item.getName().equals(hobby.getName())){
                item.setSubHobby(hobby.getSubHobby());
            }
        }

        //update share reference
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("Hobby", Hobby.toStringFromList(hobbies));
        editor.commit();
    }
}
