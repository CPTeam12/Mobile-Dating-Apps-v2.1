package com.example.thang.mobile_dating_app_v20.Fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.thang.mobile_dating_app_v20.Activity.MainActivity;
import com.example.thang.mobile_dating_app_v20.Activity.NewProfileActivity;
import com.example.thang.mobile_dating_app_v20.Adapters.MutualFriendAdapter;
import com.example.thang.mobile_dating_app_v20.Classes.ConnectionTool;
import com.example.thang.mobile_dating_app_v20.Classes.DBHelper;
import com.example.thang.mobile_dating_app_v20.Classes.Person;
import com.example.thang.mobile_dating_app_v20.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Thang on 8/7/2015.
 */
public class FriendTab2 extends Fragment {
    private ListView listView;
    private ProgressBar spinner;
    private LinearLayout noFriend;

    private String URL_MUTUAL = MainActivity.URL_CLOUD + "/Service/getfriend?";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.friend_tab1, container, false);

        listView = (ListView) view.findViewById(R.id.friendlist);
        spinner = (ProgressBar) view.findViewById(R.id.spinner);
        spinner.setVisibility(View.GONE);
        noFriend = (LinearLayout) view.findViewById(R.id.no_friend_item);
        noFriend.setVisibility(View.GONE);

        Bundle bundle = getActivity().getIntent().getExtras();
        Person person = (Person) bundle.getSerializable("Person");

        String url = URL_MUTUAL + "&email=" + person.getEmail();
        new GetMutualFriendTask().execute(url);

        return view;
    }

    private class GetMutualFriendTask extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
            ConnectionTool connectionTool = new ConnectionTool(getActivity());
            if (connectionTool.isNetworkAvailable()) {
                spinner.setVisibility(View.VISIBLE);
            } else {
                new MaterialDialog.Builder(getActivity())
                        .title(R.string.error_connection_title)
                        .content(R.string.error_connection)
                        .titleColorRes(R.color.md_red_400)
                        .show();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            return ConnectionTool.makeGetRequest(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
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
                    if (persons.isEmpty()) {
                        noFriend.setVisibility(View.VISIBLE);
                    } else {
                        MutualFriendAdapter adapter = new MutualFriendAdapter(getActivity(), persons);
                        listView.setAdapter(adapter);
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Person person = (Person) listView.getItemAtPosition(position);
                                Bundle dataBundle = new Bundle();
                                //check if current user or not
                                String currentUser = DBHelper.getInstance(getActivity()).getCurrentUser().getEmail();
                                if (currentUser.equals(person.getEmail())) {
                                    dataBundle.putString("ProfileOf", DBHelper.USER_FLAG_CURRENT);
                                } else {
                                    dataBundle.putString("ProfileOf", DBHelper.USER_FLAG_FRIENDS);
                                }
                                dataBundle.putString("email", person.getEmail());

                                Intent intent1 = new Intent(getActivity(), NewProfileActivity.class);
                                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent1.putExtras(dataBundle);
                                getActivity().startActivity(intent1);
                            }
                        });
                    }
                } else {
                    noFriend.setVisibility(View.VISIBLE);
                }

            } else {
                Toast.makeText(getActivity(), R.string.error_connection_title, Toast.LENGTH_SHORT).show();
            }

            spinner.setVisibility(View.GONE);
        }
    }
}
