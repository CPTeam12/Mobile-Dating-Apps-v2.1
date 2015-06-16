package com.example.thang.mobile_dating_app_v20.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.thang.mobile_dating_app_v20.Activity.MainActivity;
import com.example.thang.mobile_dating_app_v20.Activity.ProfileActivity;
import com.example.thang.mobile_dating_app_v20.Activity.SearchActivity;
import com.example.thang.mobile_dating_app_v20.Activity.SettingActivity;
import com.example.thang.mobile_dating_app_v20.Adapters.ListAdapter;
import com.example.thang.mobile_dating_app_v20.Classes.ConnectionTool;
import com.example.thang.mobile_dating_app_v20.Classes.DBHelper;
import com.example.thang.mobile_dating_app_v20.Classes.Person;
import com.example.thang.mobile_dating_app_v20.R;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Thang on 5/15/2015.
 */
public class Tab2 extends Fragment {
    private ListView mList;
    private ListAdapter mAdapter;

    private ProgressBar spiner;
    private TextView empty;
    //dialog
    private MaterialDialog.Builder dialogBuilder;
    private MaterialDialog materialDialog;
    //Http request
    private static final String HTTP_URL = "http://datingappservice2.groundctrl.nl/datingapp/Service/getfriend?";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab_2, container, false);
        mList = (ListView) v.findViewById(R.id.friendlist);
        spiner = (ProgressBar) v.findViewById(R.id.spin_loader);
        empty = (TextView) v.findViewById(R.id.no_friend_item);
        //show spin loader, hide textview
        spiner.setVisibility(View.GONE);
        empty.setVisibility(View.GONE);
        empty.setText("You have no friend");
        //fab search friend
        FloatingActionButton fab_search = (FloatingActionButton)v.findViewById(R.id.fab_search);
        fab_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent2);
            }
        });

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        DBHelper dbHelper = DBHelper.getInstance(getActivity());
        List<Person> personsList = dbHelper.getAllFriends();
        //TODO: handle get data when user had no friends
        if (personsList.isEmpty()) {
            //get JSON list friend from webservice and insert into SQLite
            String urlParams = "email=" + dbHelper.getCurrentUser().getEmail();
            new DownloadJSONTask().execute(HTTP_URL + urlParams);
        } else {
            mAdapter = new ListAdapter(personsList, getActivity());
            mList.setAdapter(mAdapter);
            if (mList != null) {
                mList.setClickable(true);
            }
            mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Person person = (Person) mList.getItemAtPosition(position);
                    Bundle dataBundle = new Bundle();
                    dataBundle.putString("ProfileOf", DBHelper.USER_FLAG_FRIENDS);
                    dataBundle.putString("email", person.getEmail());
                    Intent intent1 = new Intent(getActivity(),ProfileActivity.class);
                    intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent1.putExtras(dataBundle);
                    getActivity().startActivity(intent1);
                    //Chat of Khuong
//                    getActivity().setTitle("Chat");
//                    final FragmentTransaction ft = getFragmentManager().beginTransaction();
//                    ft.addToBackStack(null);
//                    ft.hide(Tab2.this);
//                    Fragment chat = new Chat();
//                    Bundle bundle = new Bundle();
//                    bundle.putString("friend", person.getEmail());
//                    chat.setArguments(bundle);
//                    ft.add(R.id.mainFragment, chat, "Chat");
//                    ft.setBreadCrumbTitle("Chat");
//                    ft.commit();
                }
            });
        }
    }

    private class DownloadJSONTask extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
            //show spin loader
            spiner.setVisibility(View.VISIBLE);
//            ConnectionTool connectionTool = new ConnectionTool(getActivity());
//            if (connectionTool.isNetworkAvailable()) {
//                dialogBuilder = new MaterialDialog.Builder(getActivity())
//                        .cancelable(false)
//                        .content(R.string.progress_dialog)
//                        .progress(true, 0);
//                materialDialog = dialogBuilder.build();
//                materialDialog.show();
//
//            } else {
//                new MaterialDialog.Builder(getActivity())
//                        .title(R.string.error_connection_title)
//                        .content(R.string.error_connection)
//                        .titleColorRes(R.color.md_red_400)
//                        .show();
//            }
        }

        @Override
        protected String doInBackground(String... params) {
            int i = 0;
            return ConnectionTool.makeGetRequest(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                //hide spin loader
                spiner.setVisibility(View.GONE);
                //start parsing jsonResponse
                JSONObject jsonObject = new JSONObject(result);
                List<Person> persons = ConnectionTool.fromJSON(jsonObject);
                if (persons != null) {
                    //insert user's friends into database
                    DBHelper dbHelper = DBHelper.getInstance(getActivity());
                    for (Person person : persons) {
                        dbHelper.insertPerson(person, DBHelper.USER_FLAG_FRIENDS);
                    }

                    mAdapter = new ListAdapter(persons, getActivity());
                    mList.setAdapter(mAdapter);
                    if (mList != null) {
                        mList.setClickable(true);
                    }
                    mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Person person = (Person) mList.getItemAtPosition(position);
                            getActivity().setTitle("Chat");
                            final FragmentTransaction ft = getFragmentManager().beginTransaction();
                            ft.addToBackStack(null);
                            ft.hide(Tab2.this);
                            Fragment chat = new Chat();
                            Bundle bundle = new Bundle();
                            Gson gson = new Gson();
                            String json = gson.toJson(person);
                            bundle.putString("json", json);
                            chat.setArguments(bundle);
                            ft.add(R.id.mainFragment, chat, "Chat");
                            ft.setBreadCrumbTitle("Chat");
                            ft.commit();
                        }
                    });
                } else {
                    //if not show show no item textview
                    empty.setVisibility(View.VISIBLE);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
