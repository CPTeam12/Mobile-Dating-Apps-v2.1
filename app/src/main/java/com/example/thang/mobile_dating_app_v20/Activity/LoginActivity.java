package com.example.thang.mobile_dating_app_v20.Activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.thang.mobile_dating_app_v20.Classes.ConnectionTool;
import com.example.thang.mobile_dating_app_v20.Classes.DBHelper;
import com.example.thang.mobile_dating_app_v20.Classes.Person;
import com.example.thang.mobile_dating_app_v20.R;
import com.facebook.FacebookSdk;
import com.facebook.login.widget.LoginButton;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rengwuxian.materialedittext.validation.RegexpValidator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class LoginActivity extends ActionBarActivity {
    private static final String URL_DOMAIN = "http://192.168.1.17:8084/DatingAppService/Service/auth?";
    private TextView loginError;
    private MaterialEditText username;
    private MaterialEditText password;
    private int TIME_OUT = 5000000;
    private MaterialDialog.Builder dialogBuilder;
    private MaterialDialog materialDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //initial view
        //FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);

//        final LoginButton button = (LoginButton) findViewById(R.id.login_button);
//        button.setBackgroundResource(R.drawable.facebook_login);

        loginError = (TextView) findViewById(R.id.login_error);
        username = (MaterialEditText) findViewById(R.id.login_email);
        password = (MaterialEditText) findViewById(R.id.password);

        Button mPlusSignInButton = (Button) findViewById(R.id.goolge_sign_in);
        mPlusSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Login google + function
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        Button signIn = (Button) findViewById(R.id.email_sign_in_button);
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Login function
                if (validateLogin())
                    checkLogin();

            }
        });

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
        boolean a = username.validateWith(new RegexpValidator(error, "^(?=\\s*\\S).*$"));
        boolean b = password.validateWith(new RegexpValidator(error, "^(?=\\s*\\S).*$"));
        return (a && b);
    }

    public void checkLogin() {
        String urlParams = "username=" + username.getText().toString().trim() +
                "&password=" + password.getText().toString().trim();
        new DownloadTextTask().execute(URL_DOMAIN + urlParams);
    }

    private class DownloadTextTask extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
            ConnectionTool connectionTool = new ConnectionTool(LoginActivity.this);
            if (connectionTool.isNetworkAvailable()) {
//                boolean temp = connectionTool.isConnectedToServer(URL_DOMAIN, TIME_OUT);
//                if (temp != true) {
//                    MaterialDialog.Builder dialog = new MaterialDialog.Builder(LoginActivity.this);
//                    dialog.title(R.string.error_connection_title)
//                            .content(R.string.error_connection)
////                        .positiveText(R.string.action_tryagain)
//                            .titleColor(R.color.md_red_400)
//                            .show();
//                } else {
//                    new MaterialDialog.Builder(LoginActivity.this)
//                            .content(R.string.progress_dialog)
//                            .progress(true, 0)
//                            .show();
//                }
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
                    //insert into database
                    DBHelper dbHelper = new DBHelper(getApplicationContext());
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
}
