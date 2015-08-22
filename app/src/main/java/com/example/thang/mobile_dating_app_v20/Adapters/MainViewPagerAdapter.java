package com.example.thang.mobile_dating_app_v20.Adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.thang.mobile_dating_app_v20.R;
import com.example.thang.mobile_dating_app_v20.Fragments.Tab1;
import com.example.thang.mobile_dating_app_v20.Fragments.Tab2;
import com.example.thang.mobile_dating_app_v20.Fragments.Tab3;

/**
 * Created by Thang on 5/15/2015.
 */
public class MainViewPagerAdapter extends FragmentStatePagerAdapter {
    String[] title = {"a","b","c"};
    private int[] tabIcon = {
            R.drawable.ic_notifications,
            R.drawable.ic_location_history_black,
            R.drawable.ic_people_black
    };
    private Context context;

    public MainViewPagerAdapter(FragmentManager fm, Context context){
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0){
            Tab1 tab1 = new Tab1();
            return Fragment.instantiate(context, tab1.getClass().getName());
        }else if(position == 1){
            return  new Tab3();
        }else {
            return new Tab2();
        }
    }

    @Override
    public int getCount() {
        return tabIcon.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return title[position];
    }

    public Drawable getIcon(int position){
        return context.getResources().getDrawable(tabIcon[position]);
    }


}
