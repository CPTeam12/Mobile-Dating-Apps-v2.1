package com.example.thang.mobile_dating_app_v20.Fragments;

import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ProgressBar;

import com.example.thang.mobile_dating_app_v20.Adapters.ListAdapter;
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
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
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
    private ProgressBar spiner;
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
            for(Person person : persons){

                new DownloadJSONTask().execute(person);
            }
        } else {
            tracker.showSettingsAlert();
        }
        return v;
    }

    private class DownloadJSONTask extends AsyncTask<Person, Void, JSONObject> {
        private Person ppl;
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected JSONObject doInBackground(Person... params) {
            String response;
            ppl = params[0];
            String to = ppl.getLatitude() + "," + ppl.getLongitude();
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
            //spiner.setVisibility(View.GONE);
            if(result != null)
            {
                try
                {
                    JSONObject jobj = result.getJSONArray("rows").getJSONObject(0).getJSONArray("elements").getJSONObject(0).getJSONObject("distance");
                    int distance = jobj.getInt("value");
                    if(distance < 1000){
                        map.addMarker(new MarkerOptions().position(new LatLng(ppl.getLatitude(),ppl.getLongitude())));
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
