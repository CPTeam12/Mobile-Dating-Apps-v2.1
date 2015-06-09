package com.example.thang.mobile_dating_app_v20.Fragments;

import com.example.thang.mobile_dating_app_v20.R;
import com.example.thang.mobile_dating_app_v20.Adapters.ViewPagerAdapter;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import it.neokree.materialtabs.MaterialTab;
import it.neokree.materialtabs.MaterialTabHost;
import it.neokree.materialtabs.MaterialTabListener;

/**
 * Created by Thang on 5/15/2015.
 */
public class Home extends Fragment implements MaterialTabListener{
    ViewPager pager;
    ViewPagerAdapter adapter;
    MaterialTabHost tabs;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup v = (ViewGroup) inflater.inflate(R.layout.homefragment, container, false);
        ActionBar toolbar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        //View v = inflater.inflate(R.layout.homefragment,container,false);

        tabs = (MaterialTabHost) v.findViewById(R.id.materialTabHost);
        adapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager(), getActivity());

        pager = (ViewPager) v.findViewById(R.id.pager);
        pager.setAdapter(adapter);
        pager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                tabs.setSelectedNavigationItem(position);
            }
        });

        for (int i = 0; i < adapter.getCount(); i++) {
            tabs.addTab(
                    tabs.newTab()
                            .setIcon(adapter.getIcon(i))
                            .setTabListener(this)
            );
        }

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Toolbar appbar = (Toolbar) getActivity().findViewById(R.id.tool_bar);
            appbar.setElevation(0);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Toolbar appbar = (Toolbar) getActivity().findViewById(R.id.tool_bar);
            appbar.setElevation((int) getResources().getDimension(R.dimen.toolbar_elevation));
        }
    }

    @Override
    public void onTabSelected(MaterialTab materialTab) {
        pager.setCurrentItem(materialTab.getPosition());
    }

    @Override
    public void onTabReselected(MaterialTab materialTab) {

    }

    @Override
    public void onTabUnselected(MaterialTab materialTab) {

    }
}
