package com.example.thang.mobile_dating_app_v20.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.thang.mobile_dating_app_v20.Classes.Map_Tracker;
import com.example.thang.mobile_dating_app_v20.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.zip.Inflater;

/**
 * Created by Thang on 5/19/2015.
 */
public class Map extends Fragment {
    GoogleMap map;
    SupportMapFragment mf;
    Map_Tracker tracker;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.map,container,false);

        mf = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.googlemap);
        map = mf.getMap();
        map.setMyLocationEnabled(true);
        tracker = new Map_Tracker(getActivity().getBaseContext());
        if(tracker.canGetLocation()) {
            double latitude = tracker.getLatitude();
            double longitude = tracker.getLongitude();
            LatLng latLng = new LatLng(latitude, longitude);
            Marker m = map.addMarker(new MarkerOptions().position(latLng));
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 8));
            map.animateCamera(CameraUpdateFactory.zoomTo(15));
        } else {
            tracker.showSettingsAlert();
        }

        return v;
    }
}
