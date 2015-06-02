package com.example.thang.mobile_dating_app_v20.Fragments;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.thang.mobile_dating_app_v20.Classes.Friend;
import com.example.thang.mobile_dating_app_v20.Classes.Map_Tracker;
import com.example.thang.mobile_dating_app_v20.Classes.Utils;
import com.example.thang.mobile_dating_app_v20.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by Thang on 5/19/2015.
 */
public class Map extends Fragment {
    GoogleMap map;
    SupportMapFragment mf;
    Map_Tracker tracker;
    List<Friend> friends = new ArrayList<Friend>();
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
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 8));
        map.animateCamera(CameraUpdateFactory.zoomTo(18));
        } else {
            tracker.showSettingsAlert();
        }

        friends.add(new Friend(10.85391688,106.62529707));
        friends.add(new Friend(10.852526,106.62918091));
        friends.add(new Friend(10.8494492,106.63203478));
        friends.add(new Friend(10.85756262,106.62731409));
        friends.add(new Friend(10.85206237,106.62647724));
        scanNearBy(friends);
        return v;
    }

    public void scanNearBy(List<Friend> friends){
        Friend me = new Friend(tracker.getLatitude(),tracker.getLongitude());
        Utils u = new Utils();
        List<Friend> list = new ArrayList<Friend>();
        for(int i=0; i< friends.size();i++){
            Friend you = friends.get(i);
            if(u.isNearLocation(me,you)){
                MarkerOptions options = new MarkerOptions()
                        .position(new LatLng(you.getLatitude(), you.getLongitude()))
                        .title("I'm Here")
                        .icon(BitmapDescriptorFactory
                                .fromBitmap(u.getCircleBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.avatar)))).anchor(0.5f, 1);

                map.addMarker(options);
            }
        }
    }
}
