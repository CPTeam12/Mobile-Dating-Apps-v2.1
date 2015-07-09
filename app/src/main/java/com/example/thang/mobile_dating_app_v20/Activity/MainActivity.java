package com.example.thang.mobile_dating_app_v20.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.example.thang.mobile_dating_app_v20.Classes.DBHelper;
import com.example.thang.mobile_dating_app_v20.Classes.Person;
import com.example.thang.mobile_dating_app_v20.Classes.Utils;
import com.example.thang.mobile_dating_app_v20.Notification.RegistrationIntentService;
import com.example.thang.mobile_dating_app_v20.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.accountswitcher.AccountHeader;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;


public class MainActivity extends ActionBarActivity {
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "MainActivity";

    public static final String URL_CLOUD = "http://datingappservice2.jelastic.skali.net/datingapp";
    private static final String URL_UPDATE_LOCATION = URL_CLOUD + "/Service/updatelocation?";
    private static String PACKAGE_NAME = "com.example.thang.mobile_dating_app_v20.Fragments.";
    private int currentItem = -1;
    public Drawer.Result result = null;

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        return super.onCreateView(parent, name, context, attrs);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        //get current Account from database
        DBHelper dbHelper = DBHelper.getInstance(getApplicationContext());
        Person person = dbHelper.getCurrentUser();
        String email;
        String fullname;
        email = person.getEmail();
        fullname = person.getFullName();
        AccountHeader.Result headerResult = new AccountHeader()
                .withActivity(this)
                .withHeaderBackground(R.drawable.blueboken)
                .addProfiles(new ProfileDrawerItem().
                                withName(fullname).
                                withEmail(email).
                                withIcon(person.getAvatar().isEmpty() ? getResources().getDrawable(R.drawable.no_avatar)
                                        : new BitmapDrawable(Utils.decodeBase64StringToBitmap(person.getAvatar())))
                )
                .build();

        result = new Drawer()
                .withActivity(this)
                .withAccountHeader(headerResult)
//                .withHeader(R.layout.header)
                .withActionBarDrawerToggle(true)
                .withActionBarDrawerToggleAnimated(true)
                .withTranslucentStatusBar(true)
                .withToolbar(toolbar)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.nav_home).withIcon(GoogleMaterial.Icon.gmd_home).withIdentifier(1),
                        new PrimaryDrawerItem().withName(R.string.nav_profile).withIcon(GoogleMaterial.Icon.gmd_account_circle).withIdentifier(2),

                        new PrimaryDrawerItem().withName(R.string.nav_logout).withIcon(GoogleMaterial.Icon.gmd_exit_to_app).withIdentifier(3),
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem().withName(R.string.nav_about).withIcon(GoogleMaterial.Icon.gmd_email).withIdentifier(4),
                        new SecondaryDrawerItem().withName(R.string.nav_setting).withIcon(GoogleMaterial.Icon.gmd_settings).withIdentifier(5)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id, IDrawerItem drawerItem) {
                        if (drawerItem != null) {
                            switch (drawerItem.getIdentifier()) {
                                case 1:
                                    switchFragment(1, "Dating App", "Home");
                                    break;
                                case 2:
                                    Bundle dataBundle = new Bundle();
                                    dataBundle.putString("ProfileOf", DBHelper.USER_FLAG_CURRENT);
                                    Intent intent1 = new Intent(getApplicationContext(), ProfileActivity.class);
                                    intent1.putExtras(dataBundle);
                                    startActivity(intent1);
                                    break;
                                case 3:
                                    DBHelper.getInstance(getApplicationContext()).deleteData();
                                    Intent intent4 = new Intent(getApplicationContext(), LoginActivity.class);
                                    finish();
                                    startActivity(intent4);
                                    break;
                                case 4:
                                    switchFragment(4, "Test", "HeaderFragment");
                                    break;
                                case 5:
                                    Intent intent2 = new Intent(getApplicationContext(), SettingActivity.class);
                                    startActivity(intent2);
                                    break;
                            }
                        }
                    }
                })
                .withSavedInstance(savedInstanceState)
                .build();

        if (savedInstanceState == null) {
            currentItem = -1;
            result.setSelectionByIdentifier(1);
        }
        //check for GCM
        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(MainActivity.this, RegistrationIntentService.class);
            startService(intent);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    @Override
    public void onBackPressed() {
        if (result != null && result.isDrawerOpen()) {
            result.closeDrawer();
        } else if (result != null && currentItem != 1) {
            result.setSelection(0);
        } else if (result != null) {
            super.onBackPressed();
        } else {
            super.onBackPressed();
        }
    }

    public void switchFragment(int itemId, String title, String fragment) {
        if (currentItem == itemId) {
            // Don't allow re-selection of the currently active item
            return;
        }
        currentItem = itemId;
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(title);
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                .replace(R.id.mainFragment, Fragment.instantiate(MainActivity.this, PACKAGE_NAME + fragment))
                .commit();
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(null, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }
}
