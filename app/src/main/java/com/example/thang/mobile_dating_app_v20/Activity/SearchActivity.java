package com.example.thang.mobile_dating_app_v20.Activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.thang.mobile_dating_app_v20.Adapters.ListAdapter;
import com.example.thang.mobile_dating_app_v20.Classes.ConnectionTool;
import com.example.thang.mobile_dating_app_v20.Classes.Person;
import com.example.thang.mobile_dating_app_v20.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends ActionBarActivity {
    private List<String> nameList = new ArrayList<>();
    private Menu menu;
    private SearchManager searchManager = null;
    private SearchView searchView = null;
    private MaterialDialog.Builder dialogBuilder;
    private MaterialDialog materialDialog;
    private String URL_SEARCH = "http://datingappservice2.groundctrl.nl/datingapp/Service/searchfriend?fullname=";
    private ListView mList;
    private ListAdapter mAdapter;
    private TextView searchResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mList = (ListView) findViewById(R.id.search_list);
        searchResult = (TextView) findViewById(R.id.search_result);
        //as default it gone
        searchResult.setVisibility(View.GONE);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds nameList to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        this.menu = menu;
        // Get the SearchView and set the searchable configuration
        searchManager = (SearchManager) this.getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        //searchView.setIconifiedByDefault(false); //Do not iconify the widget; expand it by default
        searchView.onActionViewExpanded();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                if (!query.trim().isEmpty()) {
                    try {
                        String temp = URLEncoder.encode(query.trim(), "utf-8");
                        new searchTask().execute(URL_SEARCH + temp);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return true;
            }
        });
        List<Person> personsList = new ArrayList<>();

        initListView(personsList);
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

    private void initListView(List<Person> personsList) {
        mAdapter = new ListAdapter(personsList, getApplicationContext());
        mList.setAdapter(mAdapter);
        if (mList != null) {
            mList.setClickable(true);
        }
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Person person = (Person) mList.getItemAtPosition(position);
                Bundle bundle = new Bundle();
                bundle.putString("email", person.getEmail());
                bundle.putString("ProfileOf", "Person");

                Intent intent = new Intent(SearchActivity.this, ProfileActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    private class searchTask extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
            ConnectionTool connectionTool = new ConnectionTool(SearchActivity.this);
            if (connectionTool.isNetworkAvailable()) {
//                dialogBuilder = new MaterialDialog.Builder(SearchActivity.this)
//                        .cancelable(false)
//                        .content(R.string.progress_dialog)
//                        .progress(true, 0);
//                materialDialog = dialogBuilder.build();
//                materialDialog.show();
            } else {
                new MaterialDialog.Builder(SearchActivity.this)
                        .title(R.string.error_connection_title)
                        .content(R.string.error_connection)
                        .titleColorRes(R.color.md_red_400)
                        .show();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            //return ConnectionTool.readJSONFeed(params[0]);
            return ConnectionTool.makeGetRequest(params[0]);
            //return ConnectionTool.makePostRequest(params[0],new Person("a","asd","asd@asd.com",22,"Female"));
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                //close loading dialog
                if (materialDialog != null) {
                    materialDialog.dismiss();
                }
                //start parsing jsonResponse
                JSONObject jsonObject = new JSONObject(result);
                List<Person> personList = ConnectionTool.fromJSON(jsonObject);
                if (personList != null) {
                    mList.setVisibility(View.VISIBLE);
                    searchResult.setVisibility(View.GONE);
                    initListView(personList);
                } else {
                    mList.setVisibility(View.GONE);
                    searchResult.setVisibility(View.VISIBLE);
                    //Toast.makeText(getApplicationContext(),"fail",Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
