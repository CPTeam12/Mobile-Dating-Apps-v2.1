package com.example.thang.mobile_dating_app_v20.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Toast;

import com.example.thang.mobile_dating_app_v20.R;

/**
 * Created by Thang on 5/17/2015.
 */
public class Setting extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    public static final String KEY_SETTING_DATTING_MEN = "setting_dating_men";
    public static final String KEY_SETTING_DATTING_WOMEN = "setting_dating_women";
    public static final String KEY_SETTING_DATTING_AGE = "setting_dating_age";
    public static final String KEY_SETTING_DATTING_PROFILE = "setting_dating_profile";
    public static final String KEY_SETTING_PRIVACY_LOCATION = "setting_privacy_location";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //TODO: get setting data from db then presetting for preference
        addPreferencesFromResource(R.xml.settings);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Toast.makeText(getActivity(), "Some thing changed", Toast.LENGTH_LONG);
    }


    @Override
    public void onPause() {
        super.onPause();
        //TODO: code onPause for preference setting
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean dating_men = sharedPref.getBoolean(Setting.KEY_SETTING_DATTING_MEN, false);
        boolean dating_women = sharedPref.getBoolean(Setting.KEY_SETTING_DATTING_WOMEN, false);
        String age = sharedPref.getString(Setting.KEY_SETTING_DATTING_AGE,"").toString();
        String profile = sharedPref.getString(Setting.KEY_SETTING_DATTING_PROFILE,"").toString();
        String location = sharedPref.getString(Setting.KEY_SETTING_PRIVACY_LOCATION,"").toString();
        Toast.makeText(getActivity(), "Pause", Toast.LENGTH_LONG);
    }
}
