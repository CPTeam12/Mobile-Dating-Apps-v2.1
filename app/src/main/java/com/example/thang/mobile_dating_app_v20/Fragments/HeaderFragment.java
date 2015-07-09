package com.example.thang.mobile_dating_app_v20.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.thang.mobile_dating_app_v20.Adapters.NearbyAdapter;
import com.example.thang.mobile_dating_app_v20.Classes.DBHelper;
import com.example.thang.mobile_dating_app_v20.Classes.Person;
import com.example.thang.mobile_dating_app_v20.R;
import com.tonicartos.superslim.LayoutManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Thang on 7/8/2015.
 */
public class HeaderFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.header_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        List<Person> persons = new ArrayList<>();
        persons = DBHelper.getInstance(getActivity()).getAllFriends();
        Person person = new Person();
        person.setFullName("123");
        person.setAvatar("");
        persons.add(person);
        Person person1= new Person();
        person1.setFullName("456");
        person1.setAvatar("");
        persons.add(person1);

        NearbyAdapter nearbyAdapter = new NearbyAdapter(getActivity(),persons);

        RecyclerView recyclerView =
                (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LayoutManager(getActivity()));

        recyclerView.setAdapter(nearbyAdapter);
    }
}
