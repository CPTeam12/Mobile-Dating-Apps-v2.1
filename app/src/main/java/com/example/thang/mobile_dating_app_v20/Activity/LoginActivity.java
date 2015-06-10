package com.example.thang.mobile_dating_app_v20.Activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.thang.mobile_dating_app_v20.Classes.ConnectionTool;
import com.example.thang.mobile_dating_app_v20.Classes.DBHelper;
import com.example.thang.mobile_dating_app_v20.Classes.Person;
import com.example.thang.mobile_dating_app_v20.R;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rengwuxian.materialedittext.validation.RegexpValidator;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

public class LoginActivity extends ActionBarActivity implements GoogleApiClient.OnConnectionFailedListener, ConnectionCallbacks {
    private static final String URL_DOMAIN = "http://datingappservice2.groundctrl.nl/datingapp/Service/auth?";
    private TextView loginError;
    private MaterialEditText email;
    private MaterialEditText password;
    private int TIME_OUT = 5000000;
    private MaterialDialog.Builder dialogBuilder;
    private MaterialDialog materialDialog;
    //KhuongMH
    private boolean mSignInClicked;
    private GoogleApiClient gac;
    private boolean mIntentInProgress;
    private ConnectionResult cr;
    //facebook
    private CallbackManager callbackManager;
    private FacebookCallback<LoginResult> callback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            final Person p = new Person();
            final DBHelper helper = new DBHelper(getApplicationContext());
            GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                @Override
                public void onCompleted(JSONObject jsonObject, GraphResponse graphResponse) {
                    try {
                        p.setEmail(jsonObject.getString("email"));
                        //p.setUsername(jsonObject.getString("email"));
                        p.setFullName(jsonObject.getString("name"));
                        p.setGender(jsonObject.getString("gender"));
                        helper.insertPerson(p, DBHelper.USER_FLAG_CURRENT);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,email,gender, birthday");
            request.setParameters(parameters);
            request.executeAsync();
            request = GraphRequest.newMyFriendsRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONArrayCallback() {
                @Override
                public void onCompleted(JSONArray jsonArray, GraphResponse graphResponse) {
                    for (int i = 0; i<jsonArray.length();i++){
                        Person p = new Person();
                        try {
                            JSONObject j = jsonArray.getJSONObject(i);
                            p.setFullName(j.getString("name"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        helper.insertPerson(p,DBHelper.USER_FLAG_FRIENDS);
                    }
                }
            });
            request.executeAsync();
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        }

        @Override
        public void onCancel() {
            Toast.makeText(getApplicationContext(),"Cancel", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(FacebookException e) {
            Toast.makeText(getApplicationContext(),e.toString(), Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //initial view
        //KhuongMH
        //facebook
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);
        callbackManager = CallbackManager.Factory.create();
        LoginButton btn = (LoginButton) findViewById(R.id.facebook_sign_in);
        btn.setReadPermissions(Arrays.asList("public_profile, email, user_birthday, user_friends"));
        btn.registerCallback(callbackManager, callback);
        //google plus

        gac = new GoogleApiClient.Builder(this).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API, Plus.PlusOptions.builder().build())
                .addScope(Plus.SCOPE_PLUS_LOGIN).build();

        gac.connect();
        Button mPlusSignInButton = (Button) findViewById(R.id.goolge_sign_in);
        mPlusSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!gac.isConnecting()) {
                    mSignInClicked = true;
                    resolveSignInError();
                }
            }
        });
        gac.connect();
        //check whether this user have already logged in or not
        DBHelper dbHelper = new DBHelper(getApplicationContext());
        Person person = dbHelper.getCurrentUser();
        if (person.getEmail() == null) {
            loginError = (TextView) findViewById(R.id.login_error);
            email = (MaterialEditText) findViewById(R.id.login_email);
            password = (MaterialEditText) findViewById(R.id.password);


            Button signIn = (Button) findViewById(R.id.email_sign_in_button);
            signIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (validateLogin())
                        checkLogin();

                }
            });
        }else{
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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

    private boolean validateLogin() {
        //username.validate("^(?=\\s*\\S).*$","Username cannot empty.");
        String error = getResources().getString(R.string.error_field_required);
        boolean a = email.validateWith(new RegexpValidator(error, "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$"));
        boolean b = password.validateWith(new RegexpValidator(error, "^(?=\\s*\\S).*$"));
        return (a && b);
    }

    public void checkLogin() {
        String urlParams = "email=" + email.getText().toString().trim() +
                "&password=" + password.getText().toString().trim();
        new DownloadTextTask().execute(URL_DOMAIN + urlParams);
    }

    private class DownloadTextTask extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
            ConnectionTool connectionTool = new ConnectionTool(LoginActivity.this);
            if (connectionTool.isNetworkAvailable()) {
                dialogBuilder = new MaterialDialog.Builder(LoginActivity.this)
                        .cancelable(false)
                        .content(R.string.progress_dialog)
                        .progress(true, 0);
                materialDialog = dialogBuilder.build();
                materialDialog.show();

            } else {
                new MaterialDialog.Builder(LoginActivity.this)
                        .title(R.string.error_connection_title)
                        .content(R.string.error_connection)
//                        .positiveText(R.string.action_tryagain)
                        .titleColorRes(R.color.md_red_400)
                        .show();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            int i = 0;
            return ConnectionTool.readJSONFeed(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                //close loading dialog
                if(materialDialog != null){
                    materialDialog.dismiss();
                }
                //start parsing jsonResponse
                JSONObject jsonObject = new JSONObject(result);
                List<Person> personList = ConnectionTool.fromJSON(jsonObject);
                if (personList != null) {
                    //insert current user into database
                    DBHelper dbHelper = DBHelper.getInstance(getApplicationContext());
                    dbHelper.insertPerson(personList.get(0),dbHelper.USER_FLAG_CURRENT);

                    //move to main activity
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    loginError.setText(getResources().getString(R.string.error_invalid_login));
                    loginError.setVisibility(View.VISIBLE);
                    password.setText("");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

//    KhuongMH

    @Override
    public void onConnected(Bundle bundle) {
        mSignInClicked = false;
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);

        if (Plus.PeopleApi.getCurrentPerson(gac) != null){
            com.google.android.gms.plus.model.people.Person p = Plus.PeopleApi.getCurrentPerson(gac);
            Person person = new Person();
            person.setFullName(p.getDisplayName());
            int gender = p.getGender();
            if (gender == 0){
                person.setGender("Male");
            }else{
                person.setGender("Female");
            }
            person.setEmail(Plus.AccountApi.getAccountName(gac));

            //person.setUsername(Plus.AccountApi.getAccountName(gac));
            person.setAvatar(p.getImage().getUrl());
            //insert to SQLite
            DBHelper dbHelper = new DBHelper(getApplicationContext());
            dbHelper.insertPerson(person, dbHelper.USER_FLAG_CURRENT);
        }
        startActivity(intent);
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
        super.onActivityResult(requestCode, responseCode, intent);
        callbackManager.onActivityResult(requestCode, responseCode, intent);
        if (requestCode == 0) {
            if (responseCode != RESULT_OK) {
                mSignInClicked = false;
            }

            mIntentInProgress = false;

            if (!gac.isConnecting()) {
                gac.connect();
            }
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e("debug",connectionResult.getErrorCode()+"");
        if (!connectionResult.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), LoginActivity.this, 0).show();
            return;
        }
        if (!mIntentInProgress) {
            // Store the ConnectionResult for later usage
            cr = connectionResult;
            if (mSignInClicked) {
                // The user has already clicked 'sign-in' so we attempt to
                // resolve all
                // errors until the user is signed in, or they cancel.
                resolveSignInError();
            }
        }
    }

    private class LoadProfileImage extends AsyncTask {
        ImageView downloadedImage;
        public LoadProfileImage(ImageView image) {
            downloadedImage = image;
        }

        protected void onPostExecute(Bitmap result) {
            downloadedImage.setImageBitmap(result);
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            String url = objects[0] + "";
            Bitmap icon = null;
            try {
                InputStream in = new URL(url).openStream();
                icon = BitmapFactory.decodeStream(in);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return icon;
        }
    }


    private void resolveSignInError() {
        if (cr.hasResolution()) {
            try {
                mIntentInProgress = true;
                cr.startResolutionForResult(LoginActivity.this, 0);
            } catch (IntentSender.SendIntentException e) {
                mIntentInProgress = false;
                gac.connect();
            }
        }
    }

}
