package com.example.thang.mobile_dating_app_v20.Activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.method.CharacterPickerDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.thang.mobile_dating_app_v20.Classes.ConnectionTool;
import com.example.thang.mobile_dating_app_v20.Classes.DBHelper;
import com.example.thang.mobile_dating_app_v20.Classes.Hobby;
import com.example.thang.mobile_dating_app_v20.Classes.Person;
import com.example.thang.mobile_dating_app_v20.Classes.Utils;
import com.example.thang.mobile_dating_app_v20.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rengwuxian.materialedittext.validation.RegexpValidator;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class RegisterActivity extends ActionBarActivity {
    private static final String URL_REGISTER = MainActivity.URL_CLOUD + "/Service/register";
    private static final String URL_INITIAL_FB = MainActivity.URL_CLOUD + "/Service/registerfacebook";
    private MaterialDialog.Builder dialogBuilder;
    private MaterialDialog materialDialog;

    MaterialEditText email;
    MaterialEditText password;
    MaterialEditText confirm_password;
    MaterialEditText fullname;
    MaterialEditText birthyear;
    MaterialEditText datingage;
    RadioButton rb_male, rb_female;
    Button accept, back;
    CheckBox cb_male, cb_female;
    String facebookLogin = "";
    List<Person> friends = new ArrayList<Person>();
    String flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        email = (MaterialEditText) findViewById(R.id.register_email);
        password = (MaterialEditText) findViewById(R.id.register_password);
        confirm_password = (MaterialEditText) findViewById(R.id.register_confirm_password);
        fullname = (MaterialEditText) findViewById(R.id.register_fullname);
        birthyear = (MaterialEditText) findViewById(R.id.register_birthyear);
        datingage = (MaterialEditText) findViewById(R.id.register_datingage);
        rb_male = (RadioButton) findViewById(R.id.register_male_rb);
        rb_female = (RadioButton) findViewById(R.id.register_female_rb);
        back = (Button) findViewById(R.id.register_back_login);
        cb_male = (CheckBox) findViewById(R.id.register_male_cb);
        cb_female = (CheckBox) findViewById(R.id.register_female_cb);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Gson gson = new Gson();
            Person person = gson.fromJson(bundle.getString("currentUser"), Person.class);

            //get friend list from LoginActivity
            Type listType = new TypeToken<ArrayList<Person>>() {
            }.getType();
            friends = gson.fromJson(bundle.getString("friends"), listType);

            flag = bundle.getString("flag");
            if (flag != null) {
                password.setVisibility(View.GONE);
                confirm_password.setVisibility(View.GONE);
                email.setEnabled(false);
            }
            if (person.getFacebookId() != null) facebookLogin = person.getFacebookId();
            email.setText(person.getEmail());
            fullname.setText(person.getFullName());
            String gender = person.getGender();
            if (gender.equalsIgnoreCase("Male")) {
                rb_male.setChecked(true);
                rb_female.setChecked(false);
            } else {
                rb_male.setChecked(false);
                rb_female.setChecked(true);
            }
        }

        birthyear.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    createDialog().show();
                }
            }
        });

        accept = (Button) findViewById(R.id.register_accept);
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateLogin()) {
                    Person person = new Person();
                    person.setEmail(email.getText().toString());
                    person.setFullName(fullname.getText().toString());
                    person.setAge(Calendar.getInstance().get(Calendar.YEAR) - Integer.parseInt(birthyear.getText().toString()));
                    person.setHobbies("");
                    person.setDatingAge(Integer.parseInt(datingage.getText().toString()));
                    if (cb_male.isChecked()) {
                        person.setDatingMen(true);
                    } else {
                        person.setDatingMen(false);
                    }
                    if (cb_female.isChecked()) {
                        person.setDatingWomen(true);
                    } else {
                        person.setDatingWomen(false);
                    }
                    if (rb_male.isChecked()) {
                        person.setGender("Male");
                    } else {
                        person.setGender("Female");
                    }

                    //only set password when not register as facebook
                    if (flag == null) person.setPassword(password.getText().toString());

                    person.setFacebookId(facebookLogin);
                    List<Person> currentUser = new ArrayList<Person>();
                    List<Person> friendUser = new ArrayList<Person>();
                    currentUser.add(person);
                    friendUser.add(person);
                    for (Person p : friends) {
                        friendUser.add(p);
                    }

                    //register
                    new registerNew().execute(currentUser);

                    //set relationship for facebook
                    if (flag != null) new setRelationshipFriends().execute(friendUser);
                }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                trimCache(getApplicationContext());
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

    }

    private class registerNew extends AsyncTask<List<Person>, Integer, String> {
        DBHelper dbHelper = DBHelper.getInstance(getApplicationContext());

        @Override
        protected void onPreExecute() {
            ConnectionTool connectionTool = new ConnectionTool(RegisterActivity.this);
            if (connectionTool.isNetworkAvailable()) {
                dialogBuilder = new MaterialDialog.Builder(RegisterActivity.this)
                        .cancelable(false)
                        .content(R.string.progress_dialog)
                        .progress(true, 0);
                materialDialog = dialogBuilder.build();
                materialDialog.show();

            } else {
                new MaterialDialog.Builder(RegisterActivity.this)
                        .title(R.string.error_connection_title)
                        .content(R.string.error_connection)
                        .titleColorRes(R.color.md_red_400)
                        .show();
            }
        }

        @Override
        protected String doInBackground(List<Person>... persons) {
            List<Person> p = persons[0];
            String response;
            for (Person person : p) {
                Log.i(null, String.valueOf(person.isDatingMen()) + "--" + String.valueOf(person.isDatingWomen()) );
                //get image facebook for each person
                if (!person.getFacebookId().isEmpty()) {
                    try {
                        String url = "http://graph.facebook.com/" + person.getFacebookId() + "/picture?type=large&redirect=false";
                        String rp = ConnectionTool.makeGetRequest(url);
                        JSONObject jobj = new JSONObject(rp).getJSONObject("data");
                        String imageURL = jobj.getString("url").replace("\\u003d", "=").replace("\\u0026", "&");
                        URLConnection conn = new URL(imageURL).openConnection();
                        conn.connect();
                        InputStream is = conn.getInputStream();
                        BufferedInputStream bis = new BufferedInputStream(is, 8192);
                        Bitmap bm = Utils.resizeBitmap(BitmapFactory.decodeStream(bis));
                        person.setAvatar(Utils.encodeBitmapToBase64String(bm));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }

            // checking register new account or set relationship for user's friends
            return ConnectionTool.makePostRequest(URL_REGISTER, p);
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
                    //insert current user into database
                    dbHelper.insertPerson(personList.get(0), dbHelper.USER_FLAG_CURRENT);
                    //move to hobby activity
                    Intent intent = new Intent(RegisterActivity.this, HobbyActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.register_duplicate_email),
                            Toast.LENGTH_SHORT).show();
                    email.setText("");
                    password.setText("");
                    confirm_password.setText("");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    private class setRelationshipFriends extends AsyncTask<List<Person>, Integer, String> {
        DBHelper dbHelper = DBHelper.getInstance(getApplicationContext());

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(List<Person>... persons) {
            List<Person> p = persons[0];
            String response;
            for (Person person : p) {
                //get image facebook for each person
                if (person.getFacebookId() != null) {
                    try {
                        String url = "http://graph.facebook.com/" + person.getFacebookId() + "/picture?type=large&redirect=false";
                        String rp = ConnectionTool.makeGetRequest(url);
                        JSONObject jobj = new JSONObject(rp).getJSONObject("data");
                        String imageURL = jobj.getString("url").replace("\\u003d", "=").replace("\\u0026", "&");
                        URLConnection conn = new URL(imageURL).openConnection();
                        conn.connect();
                        InputStream is = conn.getInputStream();
                        BufferedInputStream bis = new BufferedInputStream(is, 8192);
                        Bitmap bm = Utils.resizeBitmap(BitmapFactory.decodeStream(bis));
                        person.setAvatar(Utils.encodeBitmapToBase64String(bm));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
            return ConnectionTool.makePostRequest(URL_INITIAL_FB, p);
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
                    //insert friend
                    for (int i = 0; i < personList.size(); i++) {
                        dbHelper.insertPerson(personList.get(i), dbHelper.USER_FLAG_FRIENDS);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_register, menu);
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

//    public static void trimCache(Context context) {
//        File dir = context.getCacheDir();
//        if(dir!= null && dir.isDirectory()){
//            File[] children = dir.listFiles();
//            if (children == null) {
//                // Either dir does not exist or is not a directory
//            } else {
//                File temp;
//                for (int i = 0; i < children.length; i++) {
//                    temp = children[i];
//                    temp.delete();
//                }
//            }
//        }
//    }

    private boolean validateLogin() {
        //username.validate("^(?=\\s*\\S).*$","Username cannot empty.");
        String error = getResources().getString(R.string.error_field_required);
        //boolean a = email.validateWith(new RegexpValidator(error, "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$"));
        boolean a = email.validateWith(new RegexpValidator(error, "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"));
        boolean b = password.validateWith(new RegexpValidator(error, "^(?=\\s*\\S).*$"));
        boolean c = confirm_password.validateWith(new RegexpValidator(error, "^(?=\\s*\\S).*$"));
        boolean d = fullname.validateWith(new RegexpValidator(error, "^(?=\\s*\\S).*$"));
        boolean e = birthyear.validateWith(new RegexpValidator(getResources().getString(R.string.error_invalid_year), "^(\\d{4})([\\.|,]\\d{1,2})?$"));
        boolean f = datingage.validateWith(new RegexpValidator(getResources().getString(R.string.error_invalid_age), "^(\\d{2})([\\.|,]\\d{1,2})?$"));
        boolean g = password.getText().toString().equals(confirm_password.getText().toString());
        if (!password.getText().toString().equals(confirm_password.getText().toString())) {
            confirm_password.setError(getResources().getText(R.string.error_password));
        }
        if (Integer.parseInt(datingage.getText().toString()) < 18) {
            datingage.setError(getResources().getText(R.string.error_dating_age));
        }
        if (flag != null) return (a && d && e && f);
        return (a && b && c && d && e && f && g);
    }

    protected Dialog createDialog() {
        return new DatePickerDialog(this, dateSetListener,
                1993, 10, 3);
    }

    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            String date = selectedDay + "/" + selectedMonth + "/" + selectedYear;
            //Toast.makeText(RegisterActivity.this, date, Toast.LENGTH_LONG).show();
            birthyear.setText(String.valueOf(selectedYear));
        }
    };
}