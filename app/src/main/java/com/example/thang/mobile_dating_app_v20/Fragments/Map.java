package com.example.thang.mobile_dating_app_v20.Fragments;

import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.thang.mobile_dating_app_v20.Activity.MainActivity;
import com.example.thang.mobile_dating_app_v20.Classes.ConnectionTool;
import com.example.thang.mobile_dating_app_v20.Classes.DBHelper;
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

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

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
    private static final int DISTANCE = 1000; //in meter
    private String URL_NEARBY_PERSON = MainActivity.URL_CLOUD + "/Service/getnearby?";
    private ProgressBar spiner;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.map,container,false);
        mf = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.googlemap);
        map = mf.getMap();
        map.setMyLocationEnabled(true);
        tracker = new MapTracker(getActivity());
        if(tracker.canGetLocation()) {
            double latitude = tracker.getLatitude();
            double longitude = tracker.getLongitude();
            //Toast.makeText(getActivity(), " " + longitude + latitude,Toast.LENGTH_LONG).show();
            LatLng latLng = new LatLng(latitude, longitude);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 8));
            map.animateCamera(CameraUpdateFactory.zoomTo(18));

            String url = URL_NEARBY_PERSON + "email="+ DBHelper.getInstance(getActivity()).getCurrentUser().getEmail()
                    + "&longtitude=" + longitude + "&latitude=" +latitude;
            new getNearbyPersonTask().execute(url);

//            persons.add(new Person(12.687632, 108.053871, "abc"));
//            persons.add(new Person(12.688773, 108.054847, "def"));
//            persons.add(new Person(12.685669, 108.056049, "ghi"));
//            persons.add(new Person(10.85756262, 106.62731409, " "));
//            persons.add(new Person(10.85206237, 106.62647724, " "));
//            for(Person person : persons){
//                new calculateDistantTask().execute(person);
//            }

        } else {
            tracker.showSettingsAlert();
        }
        return v;
    }

    private class getNearbyPersonTask extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... params) {
            return ConnectionTool.makeGetRequest(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            if (!result.isEmpty()) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    persons = ConnectionTool.fromJSON(jsonObject);
                    if (persons != null){
                        for(Person person : persons){
                            new calculateDistantTask().execute(person);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }else{
                Toast.makeText(getActivity(), getString(R.string.loading_error), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class calculateDistantTask extends AsyncTask<Person, Void, JSONObject> {
        private Person person;
        Utils u = new Utils();
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected JSONObject doInBackground(Person... params) {
            String response;
            person = params[0];
            String to = person.getLatitude() + "," + person.getLongitude();
            String from = tracker.getLatitude() + "," + tracker.getLongitude();
            String url = "https://maps.googleapis.com/maps/api/distancematrix/json?origins="+ from + "&destinations=" + to + "&key=AIzaSyBrMkSinF3VVL3z_E5if1G_jU8cdLDBsKU";
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(url);
                HttpResponse responce = httpclient.execute(httppost);
                HttpEntity httpEntity = responce.getEntity();
                response = EntityUtils.toString(httpEntity);
                return new JSONObject(response);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject result)
        {
            super.onPostExecute(result);
            if(result != null)
            {
                try
                {
                    JSONObject jobj = result.getJSONArray("rows").getJSONObject(0).getJSONArray("elements").getJSONObject(0).getJSONObject("distance");
                    int distance = jobj.getInt("value");
                    if(distance <= DISTANCE){
                        MarkerOptions options = new MarkerOptions()
                                .position(new LatLng(person.getLatitude(), person.getLongitude()))
                                .title(person.getFullName())
                                .icon(BitmapDescriptorFactory
                                        .fromBitmap(u.getCircleBitmap(person.getAvatar().isEmpty() ? BitmapFactory.decodeResource(getResources(),
                                                R.drawable.no_avatar) : Utils.decodeBase64StringToBitmap(person.getAvatar())))).anchor(0.5f, 1);

                        map.addMarker(options);
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            else
            {
            }
        }
    }


}
