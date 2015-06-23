package com.example.thang.mobile_dating_app_v20.Activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
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
import com.example.thang.mobile_dating_app_v20.Classes.Utils;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.plus.Plus;
import com.google.gson.Gson;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rengwuxian.materialedittext.validation.RegexpValidator;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LoginActivity extends ActionBarActivity implements GoogleApiClient.OnConnectionFailedListener, ConnectionCallbacks {
    private static final String URL_AUTH = MainActivity.URL_CLOUD + "/Service/auth?";
    private static final String URL_CHECK_FB = MainActivity.URL_CLOUD + "/Service/facebook";

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
            if (dialogBuilder == null) {
                dialogBuilder = new MaterialDialog.Builder(LoginActivity.this)
                        .cancelable(false)
                        .content(R.string.progress_dialog)
                        .progress(true, 0);
                materialDialog = dialogBuilder.build();
                materialDialog.show();
            }
            final List<Person> friends = new ArrayList<Person>();
            GraphRequest request = GraphRequest.newMyFriendsRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONArrayCallback() {
                @Override
                public void onCompleted(JSONArray jsonArray, GraphResponse graphResponse) {

                    try {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject j = jsonArray.getJSONObject(i);
                            Person p = new Person();
                            p.setFacebookId(j.getString("id"));
                            p.setFullName(j.getString("name"));
                            friends.add(p);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            request.executeAsync();
            final DBHelper helper = DBHelper.getInstance(getApplicationContext());
            request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                @Override
                public void onCompleted(JSONObject jsonObject, GraphResponse graphResponse) {
                    try {
                        Person person = new Person();
                        person.setEmail(jsonObject.getString("email"));
                        person.setFullName(jsonObject.getString("name"));
                        person.setGender(jsonObject.getString("gender"));
                        person.setFacebookId(jsonObject.getString("id"));
                        List<Person> currentPerson = new ArrayList<Person>();
                        currentPerson.add(person);
                        List<List<Person>> data = new ArrayList<List<Person>>();
                        data.add(currentPerson);
                        data.add(friends);
                        new checkExistedAccount().execute(data);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,email,gender, birthday");
            request.setParameters(parameters);
            request.executeAsync();
        }

        @Override
        public void onCancel() {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_connection), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(FacebookException e) {
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //initial view
//        PackageInfo info;
//        try {
//            info = getPackageManager().getPackageInfo("com.example.thang.mobile_dating_app_v20", PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest md;
//                md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                String something = new String(Base64.encode(md.digest(), 0));
//                //String something = new String(Base64.encodeBytes(md.digest()));
//                Log.e("hash key", something);
//            }
//        } catch (PackageManager.NameNotFoundException e1) {
//            Log.e("name not found", e1.toString());
//        } catch (NoSuchAlgorithmException e) {
//            Log.e("no such an algorithm", e.toString());
//        } catch (Exception e) {
//            Log.e("exception", e.toString());
//        }
        //---KhuongMH----
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
        //---End KhuongMH---
        //check whether this user have already logged in or not
        DBHelper dbHelper = new DBHelper(getApplicationContext());
        Person person = dbHelper.getCurrentUser();
        if (person.getEmail() == null) {
            loginError = (TextView) findViewById(R.id.login_error);
            email = (MaterialEditText) findViewById(R.id.login_email);
            password = (MaterialEditText) findViewById(R.id.password);

            Button register = (Button) findViewById(R.id.register_button);
            register.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                    startActivity(intent);
                }
            });
            Button signIn = (Button) findViewById(R.id.email_sign_in_button);
            signIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (validateLogin())
                        checkLogin();

                }
            });
        } else {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
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
        boolean a = email.validateWith(new RegexpValidator(error, "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$"));
        boolean b = password.validateWith(new RegexpValidator(error, "^(?=\\s*\\S).*$"));
        return (a && b);
    }

    public void checkLogin() {
        String urlParams = "email=" + email.getText().toString().trim() +
                "&password=" + password.getText().toString().trim();
        ConnectionTool connectionTool = new ConnectionTool(this);
        if (connectionTool.isNetworkAvailable()) {
            new loginTask().execute(URL_AUTH + urlParams);
        } else {
            new MaterialDialog.Builder(this)
                    .title(R.string.error_connection_title)
                    .content(R.string.error_connection)
                    .titleColorRes(R.color.md_red_400)
                    .show();
        }
    }

    private class loginTask extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
            dialogBuilder = new MaterialDialog.Builder(LoginActivity.this)
                    .cancelable(false)
                    .content(R.string.progress_dialog)
                    .progress(true, 0);
            materialDialog = dialogBuilder.build();
            materialDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            return ConnectionTool.makeGetRequest(params[0]);
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
                    DBHelper dbHelper = DBHelper.getInstance(getApplicationContext());
                    dbHelper.insertPerson(personList.get(0), dbHelper.USER_FLAG_CURRENT);

                    //move to main activity
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_invalid_login), Toast.LENGTH_SHORT).show();
//                    loginError.setText(getResources().getString(R.string.error_invalid_login));
//                    loginError.setVisibility(View.VISIBLE);
                    password.setText("");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

//    KhuongMH

    private class checkExistedAccount extends AsyncTask<List<List<Person>>, Integer, String> {
        List<Person> currentUser;
        List<Person> friends;

        @Override
        protected String doInBackground(List<List<Person>>... params) {
            currentUser = params[0].get(0);
            friends = params[0].get(1);
            return ConnectionTool.makePostRequest(URL_CHECK_FB, currentUser);
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                //start parsing jsonResponse
                JSONObject jsonObject = new JSONObject(result);
                List<Person> personList = ConnectionTool.fromJSON(jsonObject);
                //close loading dialog
                if (materialDialog != null) {
                    materialDialog.dismiss();
                }
                if (personList == null) {
                    Bundle bundle = new Bundle();
                    Gson gson = new Gson();
                    String jsonCurrentUser = gson.toJson(currentUser.get(0));
                    String jsonFriends = gson.toJson(friends);
                    bundle.putString("currentUser", jsonCurrentUser);
                    bundle.putString("friends", jsonFriends);
                    bundle.putString("flag", "1");
                    Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else {
                    DBHelper dbHelper = DBHelper.getInstance(getApplicationContext());
                    dbHelper.insertPerson(personList.get(0), dbHelper.USER_FLAG_CURRENT);
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        mSignInClicked = false;
        if (Plus.PeopleApi.getCurrentPerson(gac) != null) {
            com.google.android.gms.plus.model.people.Person p = Plus.PeopleApi.getCurrentPerson(gac);
            Person person = new Person();
            person.setFullName(p.getDisplayName());
            int gender = p.getGender();
            if (gender == 0) {
                person.setGender("Male");
            } else {
                person.setGender("Female");
            }
            person.setEmail(Plus.AccountApi.getAccountName(gac));
            URLConnection conn = null;
            try {
                conn = new URL(p.getImage().getUrl()).openConnection();
                conn.connect();
                InputStream is = conn.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(is, 8192);
                Bitmap bm = Utils.resizeBitmap(BitmapFactory.decodeStream(bis));
                person.setAvatar(Utils.encodeBitmapToBase64String(bm));
            } catch (Exception e) {
                e.printStackTrace();
            }
            List<List<Person>> data = new ArrayList<List<Person>>();
            List<Person> currentPerson = new ArrayList<>();
            List<Person> friends = new ArrayList<>();
            currentPerson.add(person);
            data.add(currentPerson);
            data.add(friends);
            new checkExistedAccount().execute(data);
        }
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
        Log.e("debug", connectionResult.getErrorCode() + "");
        if (!connectionResult.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), LoginActivity.this, 0).show();
            return;
        }
        if (!mIntentInProgress) {
            cr = connectionResult;
            if (mSignInClicked) {
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
