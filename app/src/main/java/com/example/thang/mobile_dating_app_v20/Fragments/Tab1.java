package com.example.thang.mobile_dating_app_v20.Fragments;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.thang.mobile_dating_app_v20.Activity.MainActivity;
import com.example.thang.mobile_dating_app_v20.Cards.CardFriendRecommendation;
import com.example.thang.mobile_dating_app_v20.Cards.CardFriendRequest;
import com.example.thang.mobile_dating_app_v20.Cards.MyCardSection;
import com.example.thang.mobile_dating_app_v20.Cards.SectionAdapter;
import com.example.thang.mobile_dating_app_v20.Classes.ConnectionTool;
import com.example.thang.mobile_dating_app_v20.Classes.DBHelper;
import com.example.thang.mobile_dating_app_v20.Classes.Person;
import com.example.thang.mobile_dating_app_v20.Classes.Utils;
import com.example.thang.mobile_dating_app_v20.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.view.CardListView;

/**
 * Created by Thang on 5/15/2015.
 */
public class Tab1 extends Fragment implements OnRefreshListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private List<Person> personsRequest = new ArrayList<>();
    private List<Person> personsRecommendation = new ArrayList<>();
    private ArrayList<Card> cards = new ArrayList<Card>();
    private String URL_GET_FRIEND_REQUEST = MainActivity.URL_CLOUD + "/Service/getfriendrequest?email=";
    private String URL_GET_FRIEND_RECOMMENDATION = MainActivity.URL_CLOUD + "/Service/recommendation?email=";
    private SwipeRefreshLayout swipeRefreshLayout;

    private GoogleApiClient mGoogleApiClient ;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab_1, container, false);
        swipeRefreshLayout = (SwipeRefreshLayout ) v.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.AccentColor);

        //google api client
        mGoogleApiClient = new GoogleApiClient
                .Builder(getActivity())
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        //current place
        String placeId = "ChIJXdSq7IkpdTERpgP-QljuR3Q";
        Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeId)
                .setResultCallback(new ResultCallback<PlaceBuffer>() {
                    @Override
                    public void onResult(PlaceBuffer places) {
                        if (places.getStatus().isSuccess()) {
                            final Place myPlace = places.get(0);
                            Log.i(null, "Place found: " + myPlace.getName());
                        }
                        places.release();
                    }
                });
        //pull down to refresh
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                String email = DBHelper.getInstance(getActivity()).getCurrentUser().getEmail();
                ConnectionTool connectionTool = new ConnectionTool(getActivity());
                if (connectionTool.isNetworkAvailable()) {
                    new getFriendRequest().execute(URL_GET_FRIEND_REQUEST + email);
                    new getFriendRecommendation().execute(URL_GET_FRIEND_RECOMMENDATION + email);
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


    private void initCards() {

        //define card adapter
        CardArrayAdapter mCardArrayAdapter = new CardArrayAdapter(getActivity(), cards);
        //define card section
        List<MyCardSection> sections = new ArrayList<>();
        if (personsRequest != null) {
            if (personsRequest.size() > 0)
                sections.add(new MyCardSection(0, getResources().getString(R.string.card_section_request)));
        }
        if (personsRecommendation != null) {
            if (personsRecommendation.size() > 0 && personsRequest != null) {
                sections.add(new MyCardSection(personsRequest.size(), getResources().getString(R.string.card_section_recommend)));
            } else if (personsRecommendation.size() > 0) {
                sections.add(new MyCardSection(0, getResources().getString(R.string.card_section_recommend)));
            }
        }
        MyCardSection[] myCardSections = new MyCardSection[sections.size()];

        //define section adapter
        SectionAdapter sectionAdapter = new SectionAdapter(getActivity(), mCardArrayAdapter);
        sectionAdapter.setCardSections(sections.toArray(myCardSections));

        CardListView listView = (CardListView) getActivity().findViewById(R.id.myList);
        //if (listView != null) {
            listView.setExternalAdapter(sectionAdapter, mCardArrayAdapter);
        //}
    }

    @Override
    public void onRefresh() {
        cards.clear();
        if(personsRecommendation != null) personsRecommendation.clear();
        if(personsRequest != null) personsRequest.clear();

        String email = DBHelper.getInstance(getActivity()).getCurrentUser().getEmail();
        ConnectionTool connectionTool = new ConnectionTool(getActivity());
        if (connectionTool.isNetworkAvailable()) {
            new getFriendRequest().execute(URL_GET_FRIEND_REQUEST + email);
            new getFriendRecommendation().execute(URL_GET_FRIEND_RECOMMENDATION + email);
        } else {
            new MaterialDialog.Builder(getActivity())
                    .title(R.string.error_connection_title)
                    .content(R.string.error_connection)
                    .titleColorRes(R.color.md_red_400)
                    .show();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


    private class getFriendRequest extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... params) {
            return ConnectionTool.makeGetRequest(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            swipeRefreshLayout.setRefreshing(false);
            //start parsing jsonResponse
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(result);
                personsRequest = ConnectionTool.fromJSON(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (personsRequest != null) {
                //initial cardview
                for (Person item : personsRequest) {
                    CardFriendRequest card = new CardFriendRequest(getActivity());
                    card.setCardElevation(0);
                    card.setBackgroundColorResourceId(R.color.md_white_1000);
                    String description = item.getGender() + ", " + item.getAge() +
                            " " + getActivity().getResources().getString(R.string.friend_year_old);
                    card.init(item.getFullName(), description);
                    card.setEmail(item.getEmail());
                    card.setContext(getActivity());
                    if (item.getAvatar().isEmpty()) {
                        card.setBitMap(BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.no_avatar));
                    } else {
                        card.setBitMap(Utils.decodeBase64StringToBitmap(item.getAvatar()));
                    }
                    cards.add(card);
                }
                initCards();
            } else {
                initCards();
            }
        }
    }

    private class getFriendRecommendation extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... params) {
            return ConnectionTool.makeGetRequest(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {

            if(result != null){
                //start parsing jsonResponse
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(result);
                    personsRecommendation = ConnectionTool.fromJSON(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (personsRecommendation != null) {
                    for (Person item : personsRecommendation) {
                        CardFriendRecommendation card = new CardFriendRecommendation(getActivity());
                        card.setCardElevation(0);
                        card.setBackgroundColorResourceId(R.color.md_white_1000);
                        String description = item.getGender() + ", " + item.getAge() + " years old";
                        card.setContext(getActivity());
                        card.setTitle(item.getFullName());
                        card.setSecondaryTitle(description);
                        card.setFullName(item.getFullName());
                        card.setEmail(item.getEmail());
                        card.init(item.getFullName(), description);
                        if (item.getAvatar().isEmpty()) {
                            card.setBitMap(BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.no_avatar));
                        } else {
                            card.setBitMap(Utils.decodeBase64StringToBitmap(item.getAvatar()));
                        }
                        cards.add(card);
                    }
                    initCards();
                } else {
                    initCards();
                }
            }else{
                Toast.makeText(getActivity(), getActivity().getString(R.string.loading_error), Toast.LENGTH_SHORT).show();
            }
        }
    }

}
