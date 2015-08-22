package com.example.thang.mobile_dating_app_v20.Activity;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewManager;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.example.thang.mobile_dating_app_v20.Classes.Person;
import com.example.thang.mobile_dating_app_v20.Fragments.FriendTab1;
import com.example.thang.mobile_dating_app_v20.Fragments.FriendTab2;
import com.example.thang.mobile_dating_app_v20.R;
import com.example.thang.mobile_dating_app_v20.Views.SlidingTabLayout;

/**
 * Created by Thang on 8/6/2015.
 */
public class MutualFriendActivity extends ActionBarActivity {
    SlidingTabLayout slidingTabLayout;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mutual_friend);

        Bundle bundle = getIntent().getExtras();
        Person person = (Person) bundle.getSerializable("Person");
        String flag = bundle.getString("Flag");

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setTitle(person.getFullName());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
         slidingTabLayout = (SlidingTabLayout) findViewById(R.id.tabs);
        slidingTabLayout.setDistributeEvenly(true);

        String title[] = {"Bạn Chung", "Tất Cả"};
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), title, 2);
        viewPager.setAdapter(viewPagerAdapter);

        slidingTabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.AccentColor);
            }
        });
        slidingTabLayout.setViewPager(viewPager);

        if (flag.equals("MutualFriend")) {
            viewPager.setCurrentItem(0);
        } else {
            viewPager.setCurrentItem(1);
        }
    }

    private class ViewPagerAdapter extends FragmentStatePagerAdapter {
        String title[];
        int tabNum;

        public ViewPagerAdapter(FragmentManager fm, String title[], int tabNum) {
            super(fm);
            this.tabNum = tabNum;
            this.title = title;
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                FriendTab1 friendTab1 = new FriendTab1();
                return friendTab1;
            } else {
                FriendTab2 friendTab2 = new FriendTab2();
                return friendTab2;
            }
        }

        @Override
        public int getCount() {
            return tabNum;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return title[position];
        }
    }


}
