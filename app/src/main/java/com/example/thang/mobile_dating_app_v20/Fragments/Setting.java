package com.example.thang.mobile_dating_app_v20.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.view.View;

import com.example.thang.mobile_dating_app_v20.R;

/**
 * Created by Thang on 5/17/2015.
 */
public class Setting extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
    }
}
