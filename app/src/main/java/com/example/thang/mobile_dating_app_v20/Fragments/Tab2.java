package com.example.thang.mobile_dating_app_v20.Fragments;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
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
import com.example.thang.mobile_dating_app_v20.Adapters.ListAdapter;
import com.example.thang.mobile_dating_app_v20.Classes.ConnectionTool;
import com.example.thang.mobile_dating_app_v20.Classes.DBHelper;
import com.example.thang.mobile_dating_app_v20.Classes.Person;
import com.example.thang.mobile_dating_app_v20.R;
import com.getbase.floatingactionbutton.FloatingActionButton;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Thang on 5/15/2015.
 */
public class Tab2 extends Fragment implements OnRefreshListener {
    private ListView mList;
    private ListAdapter mAdapter;
    private TextView empty;
    private SwipeRefreshLayout swipeRefreshLayout;
    //Http request
    private static final String HTTP_URL = MainActivity.URL_CLOUD + "/Service/getfriend?";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab_2, container, false);
        mList = (ListView) v.findViewById(R.id.friendlist);
        empty = (TextView) v.findViewById(R.id.no_friend_item);
        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.AccentColor);
        //hide textview
        empty.setVisibility(View.GONE);
        empty.setText("You have no friend");
        //fab search friend
        FloatingActionButton fab_search = (FloatingActionButton) v.findViewById(R.id.fab_search);
        fab_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent2);
            }
        });
        //pull down to refresh
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                DBHelper dbHelper = DBHelper.getInstance(getActivity());
                //get JSON list friend from webservice and insert into SQLite
                String urlParams = "email=" + dbHelper.getCurrentUser().getEmail();
                ConnectionTool connectionTool = new ConnectionTool(getActivity());
                if (connectionTool.isNetworkAvailable()) {
                    new getFriendTask().execute(HTTP_URL + urlParams);
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
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

//        DBHelper dbHelper = DBHelper.getInstance(getActivity());
//        //get JSON list friend from webservice and insert into SQLite
//        String urlParams = "email=" + dbHelper.getCurrentUser().getEmail();
//        ConnectionTool connectionTool = new ConnectionTool(getActivity());
//        if (connectionTool.isNetworkAvailable()) {
//        new getFriendTask().execute(HTTP_URL + urlParams);
//        } else {
//            new MaterialDialog.Builder(getActivity())
//                    .title(R.string.error_connection_title)
//                    .content(R.string.error_connection)
//                    .titleColorRes(R.color.md_red_400)
//                    .show();
//        }

//        } else {
//            mAdapter = new ListAdapter(personsList, getActivity());
//            mList.setAdapter(mAdapter);
//            if (mList != null) {
//                mList.setClickable(true);
//            }
//            mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    Person person = (Person) mList.getItemAtPosition(position);
//                    Bundle dataBundle = new Bundle();
//                    dataBundle.putString("ProfileOf", DBHelper.USER_FLAG_FRIENDS);
//                    dataBundle.putString("email", person.getEmail());
//                    Intent intent1 = new Intent(getActivity(),ProfileActivity.class);
//                    intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    intent1.putExtras(dataBundle);
//                    getActivity().startActivity(intent1);
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
//                }
//            });
//        }
    }

    @Override
    public void onRefresh() {
        DBHelper dbHelper = DBHelper.getInstance(getActivity());
        //get JSON list friend from webservice and insert into SQLite
        String urlParams = "email=" + dbHelper.getCurrentUser().getEmail();
        ConnectionTool connectionTool = new ConnectionTool(getActivity());
        if (connectionTool.isNetworkAvailable()) {
            new getFriendTask().execute(HTTP_URL + urlParams);
        } else {
            new MaterialDialog.Builder(getActivity())
                    .title(R.string.error_connection_title)
                    .content(R.string.error_connection)
                    .titleColorRes(R.color.md_red_400)
                    .show();
        }
    }

    private class getFriendTask extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... params) {
            int i = 0;
            return ConnectionTool.makeGetRequest(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            swipeRefreshLayout.setRefreshing(false);
            //start parsing jsonResponse
            List<Person> persons = new ArrayList<>();
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(result);
                persons = ConnectionTool.fromJSON(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (persons != null) {
                if (empty.getVisibility() == View.VISIBLE)
                    empty.setVisibility(View.GONE);

                //insert user's friends into database
                DBHelper dbHelper = DBHelper.getInstance(getActivity());
                //delete db before insert new
                dbHelper.deleteAllFriend();
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
                        Bundle dataBundle = new Bundle();
                        dataBundle.putString("ProfileOf", DBHelper.USER_FLAG_FRIENDS);
                        dataBundle.putString("email", person.getEmail());

                        Intent intent1 = new Intent(getActivity(), ProfileActivity.class);
                        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent1.putExtras(dataBundle);
                        getActivity().startActivity(intent1);

//                            getActivity().setTitle("Chat");
//                            final FragmentTransaction ft = getFragmentManager().beginTransaction();
//                            ft.addToBackStack(null);
//                            ft.hide(Tab2.this);
//                            Fragment chat = new Chat();
//                            Bundle bundle = new Bundle();
//                            Gson gson = new Gson();
//                            String json = gson.toJson(person);
//                            bundle.putString("json", json);
//                            chat.setArguments(bundle);
//                            ft.add(R.id.mainFragment, chat, "Chat");
//                            ft.setBreadCrumbTitle("Chat");
//                            ft.commit();
                    }
                });
            } else {
                //if not show show no item textview
                mAdapter = new ListAdapter(persons, getActivity());
                mList.setAdapter(mAdapter);
                if (empty.getVisibility() == View.GONE)
                empty.setVisibility(View.VISIBLE);
            }


        }
    }

}
