package com.example.thang.mobile_dating_app_v20.Classes;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.thang.mobile_dating_app_v20.Activity.MainActivity;
import com.example.thang.mobile_dating_app_v20.R;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Thang on 6/30/2015.
 */
public class LocationTracker  {
    private static final String URL_UPDATE_LOCATION = MainActivity.URL_CLOUD + "/Service/updatelocation?";
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1000; //in meters
    private static final long MIN_TIME_CHANGE_FOR_UPDATES = 1000 * 60 *2; //in milliseconds
    Context context;
    Location currentBestLocation;

    public LocationTracker(Context context) {
        this.context = context;
    }

    public void getCurrentLocation() {

        final LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        // Register the listener with the Location Manager to receive location updates
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                makeUseOfNewLocation(location);

                if(currentBestLocation == null){
                    currentBestLocation = location;
                }

                String url = URL_UPDATE_LOCATION + "email=" + DBHelper.getInstance(context).getCurrentUser().getEmail()
                        + "&longtitude=" + currentBestLocation.getLongitude() + "&latitude=" + currentBestLocation.getLatitude();
                ConnectionTool connectionTool = new ConnectionTool(context);
                if (connectionTool.isNetworkAvailable()) {
                    new updateLocationTask().execute(url);
                } else {
                    new MaterialDialog.Builder(context)
                            .title(R.string.error_connection_title)
                            .content(R.string.error_connection)
                            .titleColorRes(R.color.md_red_400)
                            .show();
                }
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        //high priority for network, which is reduces battery usage
        if (isNetworkEnabled) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    MIN_TIME_CHANGE_FOR_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListener);
        }
        if(isGPSEnabled) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    MIN_TIME_CHANGE_FOR_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListener);
        }

    }

    private class updateLocationTask extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... params) {
            return ConnectionTool.makeGetRequest(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.isEmpty()) {
                Toast.makeText(context, context.getString(R.string.loading_error), Toast.LENGTH_SHORT).show();
            }
        }
    }

    void makeUseOfNewLocation(Location location) {
        if ( isBetterLocation(location, currentBestLocation) ) {
            currentBestLocation = location;
        }
    }

    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > MIN_TIME_CHANGE_FOR_UPDATES;
        boolean isSignificantlyOlder = timeDelta < -MIN_TIME_CHANGE_FOR_UPDATES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /**
     * Checks whether two providers are the same
     */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

 }
