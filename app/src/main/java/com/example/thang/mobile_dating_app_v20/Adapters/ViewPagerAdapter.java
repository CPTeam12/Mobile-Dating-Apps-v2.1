package com.example.thang.mobile_dating_app_v20.Adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.thang.mobile_dating_app_v20.R;
import com.example.thang.mobile_dating_app_v20.Fragments.Tab1;
import com.example.thang.mobile_dating_app_v20.Fragments.Tab2;
import com.example.thang.mobile_dating_app_v20.Fragments.Tab3;

/**
 * Created by Thang on 5/15/2015.
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter{
    String[] title = {"a","b","c"};
    private int[] tabIcon = {
            R.drawable.ic_notifications,
            R.drawable.ic_people_black,
            R.drawable.ic_location_history_black
    };
    private Context context;

    public ViewPagerAdapter(FragmentManager fm, Context context){
        super(fm);
        this.context = context;
    }
    @Override
    public Fragment getItem(int position) {
        if (position == 0){
            return new Tab1();
        }else if(position == 1){
            return  new Tab2();
        }else {
            return new Tab3();
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
