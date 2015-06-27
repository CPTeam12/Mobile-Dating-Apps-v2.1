package com.example.thang.mobile_dating_app_v20.Fragments;


import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.thang.mobile_dating_app_v20.Activity.MainActivity;
import com.example.thang.mobile_dating_app_v20.Adapters.ChatListAdapter;
import com.example.thang.mobile_dating_app_v20.Adapters.GribAdapter;
import com.example.thang.mobile_dating_app_v20.Classes.ConnectionTool;
import com.example.thang.mobile_dating_app_v20.Classes.DBHelper;
import com.example.thang.mobile_dating_app_v20.Classes.MapTracker;
import com.example.thang.mobile_dating_app_v20.Classes.Message;
import com.example.thang.mobile_dating_app_v20.Classes.Person;
import com.example.thang.mobile_dating_app_v20.Classes.Utils;
import com.example.thang.mobile_dating_app_v20.R;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

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
import java.util.concurrent.ExecutionException;

/**
 * Created by Thang on 5/15/2015.
 */
public class Tab3 extends Fragment implements OnRefreshListener {

    private List<Person> persons = new ArrayList<>();
    private MapTracker tracker;
    private int numColumns = 2;
    private static final int DISTANCE = 1000; //in meter
    private String URL_NEARBY_PERSON = MainActivity.URL_CLOUD + "/Service/getnearby?";
    private GridView gridView;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab_3, container, false);

        tracker = new MapTracker(getActivity());

        ProgressBar mProgress = (ProgressBar) v.findViewById(R.id.progress);
        gridView = (GridView) v.findViewById(R.id.gridView);
        TextView title = (TextView) v.findViewById(R.id.tab_title);
        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_layout);
        title.setText(persons.size() + " " + getResources().getString(R.string.tab1_title));
        gridView.setNumColumns(numColumns);

        //pull down to refresh
        swipeRefreshLayout.setColorSchemeResources(R.color.AccentColor);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                double longtitude = tracker.getLongitude();
                double latitude = tracker.getLatitude();
                String url = URL_NEARBY_PERSON + "email=" + DBHelper.getInstance(getActivity()).getCurrentUser().getEmail()
                        + "&longtitude=" + longtitude + "&latitude=" + latitude;
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
        double longtitude = tracker.getLongitude();
        double latitude = tracker.getLatitude();
        String url = URL_NEARBY_PERSON + "email=" + DBHelper.getInstance(getActivity()).getCurrentUser().getEmail()
                + "&longtitude=" + longtitude + "&latitude=" + latitude;
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
                        for (Person person : persons) {
                            new calculateDistantTask().execute(person);
                        }
                        GribAdapter gribAdapter = new GribAdapter(getActivity(), persons, numColumns);
                        gridView.setAdapter(gribAdapter);

                        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                getActivity().getSupportFragmentManager()
                                        .beginTransaction()
                                        .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                                        .replace(R.id.mainFragment, Fragment.instantiate(getActivity(), "com.example.thang.mobile_dating_app_v20.Fragments.Map"))
                                        .commit();
                            }
                        });
                        swipeRefreshLayout.setRefreshing(false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
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
            String url = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=" + from + "&destinations=" + to + "&key=AIzaSyBrMkSinF3VVL3z_E5if1G_jU8cdLDBsKU";
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
        protected void onPostExecute(JSONObject result) {
            super.onPostExecute(result);
            if (result != null) {
                try {
                    JSONObject jobj = result.getJSONArray("rows").getJSONObject(0).getJSONArray("elements").getJSONObject(0).getJSONObject("distance");
                    int distance = jobj.getInt("value");
                    if (distance > DISTANCE) {
                        //remove this person from person list
                        persons.remove(person);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
            }
        }
    }

}
