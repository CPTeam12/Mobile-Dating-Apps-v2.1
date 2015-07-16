package com.example.thang.mobile_dating_app_v20.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.thang.mobile_dating_app_v20.Adapters.HobbyAdapter;
import com.example.thang.mobile_dating_app_v20.Classes.ConnectionTool;
import com.example.thang.mobile_dating_app_v20.Classes.DBHelper;
import com.example.thang.mobile_dating_app_v20.Classes.Hobby;
import com.example.thang.mobile_dating_app_v20.Classes.Person;
import com.example.thang.mobile_dating_app_v20.Classes.SubHobby;
import com.example.thang.mobile_dating_app_v20.Fragments.SubHobbyFragment;
import com.example.thang.mobile_dating_app_v20.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HobbyActivity extends ActionBarActivity {
    private static final String URL_UPDATE_HOBBY = MainActivity.URL_CLOUD + "/Service/updateprofile";
    private MaterialDialog.Builder dialogBuilder;
    private MaterialDialog materialDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hobby);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.hobby_choose);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        ListView listView = (ListView) findViewById(R.id.listView);

        String[] array = getResources().getStringArray(R.array.hobbies);
        Toast.makeText(this, array.toString(), Toast.LENGTH_LONG).show();
        List<Hobby> hobbies = new ArrayList<>();
        for (String item : array) {
            String[] tempArray;
            if (item.equals("Âm nhạc")) {
                tempArray = getResources().getStringArray(R.array.Music);
            } else if (item.equals("Phim ảnh")) {
                tempArray = getResources().getStringArray(R.array.Movie);
            } else if (item.equals("Sách")) {
                tempArray = getResources().getStringArray(R.array.Book);
            } else if (item.equals("Thể thao")) {
                tempArray = getResources().getStringArray(R.array.Sport);
            } else if (item.equals("Thức ăn")) {
                tempArray = getResources().getStringArray(R.array.Food);
            } else if (item.equals("Thú cưng")) {
                tempArray = getResources().getStringArray(R.array.Pet);
            } else {//drink
                tempArray = getResources().getStringArray(R.array.Drink);
            }
            List<SubHobby> subHobbies = new ArrayList<>();
            for (int i = 0; i < tempArray.length; i++) {
                subHobbies.add(new SubHobby(tempArray[i], false));
            }
            hobbies.add(new Hobby(item, subHobbies));
        }

        HobbyAdapter hobbyAdapter = new HobbyAdapter(this, hobbies);
        listView.setAdapter(hobbyAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                //get from share reference
                SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                String sharePref = sharedPref.getString("Hobby", "");
                //Toast.makeText(getApplicationContext(), sharePref, Toast.LENGTH_SHORT).show();
                //Log.i("Hobby: ", sharePref);

                List<Hobby> hobbies = Hobby.toList(sharePref);
                Hobby hobby = hobbies.get(position);

                Bundle bundle = new Bundle();
                bundle.putString("Hobby", Hobby.toStringFromObject(hobby));
                Fragment fragment = new SubHobbyFragment();
                fragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                        .replace(R.id.mainFragment, fragment)
                        .addToBackStack("Hobby")
                        .commit();
            }
        });

        //initial Share reference
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("Hobby", Hobby.toStringFromList(hobbies));
        editor.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_hobby, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        boolean isEmpty = true;

        //noinspection SimplifiableIfStatement
        if (id == R.id.finish) {
            SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
            String sharePref = sharedPref.getString("Hobby", "");
            List<Hobby> hobbies = Hobby.toList(sharePref);
            for (Hobby hobby : hobbies) {
                List<SubHobby> subHobbies = hobby.getSubHobby();
                for (SubHobby subHobby : subHobbies) {
                    if (subHobby.getIsSelected()) {
                        isEmpty = false;
                    }
                }
            }
            //check if user are choose at least one
            if (isEmpty) {
                //show dialog
                new MaterialDialog.Builder(this)
                        .content(R.string.dialog_hobby)
                        .positiveText(R.string.dialog_ok)
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                dialog.dismiss();
                            }
                        }).show();
            } else {
                //process to update hobbies
                ConnectionTool connectionTool = new ConnectionTool(HobbyActivity.this);
                if (!connectionTool.isNetworkAvailable()) {
                    new MaterialDialog.Builder(HobbyActivity.this)
                            .title(R.string.error_connection_title)
                            .content(R.string.error_connection)
                            .titleColorRes(R.color.md_red_400)
                            .show();
                }else{
                    List<Person> persons = new ArrayList<>();
                    Person person = DBHelper.getInstance(getApplicationContext()).getCurrentUser();
                    person.setHobbies(sharePref);
                    persons.add(person);
                    new UpdateHobbyTask().execute(persons);
                }
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private class UpdateHobbyTask extends AsyncTask<List<Person>, Integer, String> {
        DBHelper dbHelper = DBHelper.getInstance(getApplicationContext());

        @Override
        protected void onPreExecute() {
            dialogBuilder = new MaterialDialog.Builder(HobbyActivity.this)
                    .cancelable(false)
                    .content(R.string.progress_dialog)
                    .progress(true, 0);
            materialDialog = dialogBuilder.build();
            materialDialog.show();
        }

        @Override
        protected String doInBackground(List<Person>... persons) {
            List<Person> personList = persons[0];
            return ConnectionTool.makePostRequest(URL_UPDATE_HOBBY, personList);
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
                if(personList != null){
                    //update current user into database
                    DBHelper helper = new DBHelper(getApplicationContext());
                    helper.updatePerson(personList.get(0));
                    //move to main activity
                    Intent intent = new Intent(HobbyActivity.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(),getResources().
                            getText(R.string.error_connection),Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
