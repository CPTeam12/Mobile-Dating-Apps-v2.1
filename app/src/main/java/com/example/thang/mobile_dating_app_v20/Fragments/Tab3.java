package com.example.thang.mobile_dating_app_v20.Fragments;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.thang.mobile_dating_app_v20.Activity.MainActivity;
import com.example.thang.mobile_dating_app_v20.Adapters.NearbyAdapter;
import com.example.thang.mobile_dating_app_v20.Classes.ConnectionTool;
import com.example.thang.mobile_dating_app_v20.Classes.DBHelper;
import com.example.thang.mobile_dating_app_v20.Classes.LocationTracker;
import com.example.thang.mobile_dating_app_v20.Classes.Person;
import com.example.thang.mobile_dating_app_v20.Classes.Utils;
import com.example.thang.mobile_dating_app_v20.R;
import com.tonicartos.superslim.LayoutManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Thang on 5/15/2015.
 */
public class Tab3 extends Fragment implements OnRefreshListener {

    private List<Person> persons = new ArrayList<>();
    private int numColumns = 2;
    private static final int DISTANCE = 1000; //in meter
    private String URL_NEARBY_PERSON = MainActivity.URL_CLOUD + "/Service/getnearby?";
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private View noNearby;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab_3, container, false);

        noNearby = (View) v.findViewById(R.id.no_nearby_icon);
        noNearby.setVisibility(View.GONE);
        recyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LayoutManager(getActivity()));
        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_layout);

        //pull down to refresh
        swipeRefreshLayout.setColorSchemeResources(R.color.AccentColor);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                //check for current location
                LocationTracker locationTracker = new LocationTracker(getActivity());
                locationTracker.getCurrentLocation();

                swipeRefreshLayout.setRefreshing(true);
                String url = URL_NEARBY_PERSON + "email=" + DBHelper.getInstance(getActivity()).getCurrentUser().getEmail();
                Log.i(null,url);
                ConnectionTool connectionTool = new ConnectionTool(getActivity());
                if (connectionTool.isNetworkAvailable()) {
                    new getNearbyPersonTask().execute(url);
                } else {
                    new MaterialDialog.Builder(getActivity())
                            .title(R.string.error_connection_title)
                            .content(R.string.error_connection)
                            .titleColorRes(R.color.md_red_400)
                            .show();
                }
            }
        });

        return v;
    }

    @Override
    public void onRefresh() {
        //check for current location
        LocationTracker locationTracker = new LocationTracker(getActivity());
        locationTracker.getCurrentLocation();

        String url = URL_NEARBY_PERSON + "email=" + DBHelper.getInstance(getActivity()).getCurrentUser().getEmail();
        ConnectionTool connectionTool = new ConnectionTool(getActivity());
        if (connectionTool.isNetworkAvailable()) {
            new getNearbyPersonTask().execute(url);
        } else {
            new MaterialDialog.Builder(getActivity())
                    .title(R.string.error_connection_title)
                    .content(R.string.error_connection)
                    .titleColorRes(R.color.md_red_400)
                    .show();
        }
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
                    if (persons != null) {
                        NearbyAdapter nearbyAdapter = new NearbyAdapter(getActivity(), persons);
                        recyclerView.setAdapter(nearbyAdapter);
                        noNearby.setVisibility(View.GONE);
                    } else {
                        noNearby.setVisibility(View.VISIBLE);
                    }
                    swipeRefreshLayout.setRefreshing(false);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                Toast.makeText(getActivity(), getString(R.string.loading_error), Toast.LENGTH_SHORT).show();
            }
        }
    }

//    private class calculateDistantTask extends AsyncTask<Person, Void, JSONObject> {
//        private Person person;
//        Utils u = new Utils();
//
//        @Override
//        protected void onPreExecute() {
//        }
//
//        @Override
//        protected JSONObject doInBackground(Person... params) {
//            String response;
//            person = params[0];
//            String to = person.getLatitude() + "," + person.getLongitude();
//            String from = tracker.getLatitude() + "," + tracker.getLongitude();
//            String url = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=" + from + "&destinations=" + to + "&key=AIzaSyBrMkSinF3VVL3z_E5if1G_jU8cdLDBsKU";
//            try {
//                HttpClient httpclient = new DefaultHttpClient();
//                HttpPost httppost = new HttpPost(url);
//                HttpResponse responce = httpclient.execute(httppost);
//                HttpEntity httpEntity = responce.getEntity();
//                response = EntityUtils.toString(httpEntity);
//                return new JSONObject(response);
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(JSONObject result) {
//            super.onPostExecute(result);
//            if (result != null) {
//                try {
//                    JSONObject jobj = result.getJSONArray("rows").getJSONObject(0).getJSONArray("elements").getJSONObject(0).getJSONObject("distance");
//                    int distance = jobj.getInt("value");
//                    if (distance > DISTANCE) {
//                        //remove this person from person list
//                        persons.remove(person);
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            } else {
//            }
//        }
//    }


    private class sendNotificationTask extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
            noNearby.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(String... params) {
            return ConnectionTool.makeGetRequest(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.isEmpty()) {
                Toast.makeText(getActivity(), getActivity().getString(R.string.loading_error), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
