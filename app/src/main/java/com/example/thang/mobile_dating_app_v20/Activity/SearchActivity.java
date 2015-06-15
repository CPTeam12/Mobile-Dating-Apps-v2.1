package com.example.thang.mobile_dating_app_v20.Activity;

import android.annotation.TargetApi;
import android.app.SearchManager;
import android.content.Context;
import android.database.MatrixCursor;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.example.thang.mobile_dating_app_v20.Adapters.ExampleAdapter;
import com.example.thang.mobile_dating_app_v20.R;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends ActionBarActivity {
    private List<String> items = new ArrayList<>();
    private Menu menu;

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
        items.add("abc");
        items.add("adef");
        items.add("alibaba");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        this.menu = menu;
        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) this.getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
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
                loadHistory(query);
                return true;
            }
        });


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

    // History
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void loadHistory(String query) {

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

            // Cursor
            String[] columns = new String[] { "_id", "text" };
            Object[] temp = new Object[] { 0, "default" };

            MatrixCursor cursor = new MatrixCursor(columns);

            for(int i = 0; i < items.size(); i++) {

                temp[0] = i;
                temp[1] = items.get(i);//replaced s with i as s not used anywhere.

                        cursor.addRow(temp);

            }

            // SearchView
            SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

            final SearchView search = (SearchView) menu.findItem(R.id.menu_search).getActionView();

            search.setSuggestionsAdapter(new ExampleAdapter(this, cursor, items));

        }

    }
}
