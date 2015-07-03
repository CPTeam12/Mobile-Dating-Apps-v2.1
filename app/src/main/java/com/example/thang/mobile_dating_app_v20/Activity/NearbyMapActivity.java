package com.example.thang.mobile_dating_app_v20.Activity;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.thang.mobile_dating_app_v20.Classes.ConnectionTool;
import com.example.thang.mobile_dating_app_v20.Classes.DBHelper;
import com.example.thang.mobile_dating_app_v20.Classes.MapTracker;
import com.example.thang.mobile_dating_app_v20.Classes.Person;
import com.example.thang.mobile_dating_app_v20.Classes.Utils;
import com.example.thang.mobile_dating_app_v20.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class NearbyMapActivity extends Activity implements OnMapReadyCallback {
    private String URL_NEARBY_PERSON = MainActivity.URL_CLOUD + "/Service/getnearbynonotify?";
    private GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_map);

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.nearby_map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_nearby_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.map = googleMap;
        map.setMyLocationEnabled(true);

        //get person's location and move camera
        Bundle bundle = getIntent().getExtras();
        double latitude = bundle.getDouble("latitude");
        double longitude = bundle.getDouble("longitude");
        LatLng latLng = new LatLng(latitude, longitude);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
        map.animateCamera(CameraUpdateFactory.zoomTo(18));

        //show all nearby person on the map
        if (ConnectionTool.isNetworkAvailable()) {
            String url = URL_NEARBY_PERSON + "email=" + DBHelper.getInstance(this).getCurrentUser().getEmail();
            new getNearbyPersonTask().execute(url);
        } else {
            //show dialog if network connection is not available
            new MaterialDialog.Builder(this)
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
                    List<Person> persons = ConnectionTool.fromJSON(jsonObject);
                    if (persons != null) {
                        Utils utils = new Utils();
                        for (Person person : persons) {

                            MarkerOptions options = new MarkerOptions()
                                    .position(new LatLng(person.getLatitude(), person.getLongitude()))
                                    .title(person.getFullName())
                                    .snippet(person.getLastKnown())
                                    .icon(BitmapDescriptorFactory
                                            .fromBitmap(utils.getCircleBitmap(person.getAvatar().isEmpty() ? BitmapFactory.decodeResource(getResources(),
                                                    R.drawable.no_avatar) : Utils.decodeBase64StringToBitmap(person.getAvatar())))).anchor(0.5f, 1);
                            map.addMarker(options);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.loading_error), Toast.LENGTH_SHORT).show();
            }
        }
    }


}
