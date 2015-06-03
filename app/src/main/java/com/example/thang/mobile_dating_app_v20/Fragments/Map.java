package com.example.thang.mobile_dating_app_v20.Fragments;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.thang.mobile_dating_app_v20.Classes.MapTracker;
import com.example.thang.mobile_dating_app_v20.Classes.Person;
import com.example.thang.mobile_dating_app_v20.Classes.Utils;
import com.example.thang.mobile_dating_app_v20.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Thang on 5/19/2015.
 */
public class Map extends Fragment {
    GoogleMap map;
    SupportMapFragment mf;
    MapTracker tracker;
    List<Person> persons = new ArrayList<Person>();
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.map,container,false);
        mf = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.googlemap);
        map = mf.getMap();
        map.setMyLocationEnabled(true);
        tracker = new MapTracker(getActivity().getBaseContext());
        if(tracker.canGetLocation()) {
            double latitude = tracker.getLatitude();
            double longitude = tracker.getLongitude();
            LatLng latLng = new LatLng(latitude, longitude);
            map.addMarker(new MarkerOptions().position(latLng));
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 8));
            map.animateCamera(CameraUpdateFactory.zoomTo(18));

            persons.add(new Person(10.85391688,106.62529707));
            persons.add(new Person(10.852526,106.62918091));
            persons.add(new Person(10.8494492,106.63203478));
            persons.add(new Person(10.85756262,106.62731409));
            persons.add(new Person(10.85206237,106.62647724));
            scanNearBy(persons);
        } else {
            tracker.showSettingsAlert();
        }
        return v;
    }

    public List<Person> scanNearBy(List<Person> persons){
        Person me = new Person(tracker.getLatitude(),tracker.getLongitude());
        Utils u = new Utils();
        List<Person> list = new ArrayList<Person>();
        //if(tracker.canGetLocation()){
        for(int i=0; i< persons.size();i++){
            Person you = persons.get(i);
            if(u.isNearLocation(me,you)){
                MarkerOptions options = new MarkerOptions()
                        .position(new LatLng(you.getLatitude(), you.getLongitude()))
                        .title("I'm Here")
                        .icon(BitmapDescriptorFactory
                                .fromBitmap(u.getCircleBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.avatar)))).anchor(0.5f, 1);

                map.addMarker(options);
            }
        }
        //}
        return list;
    }
}
