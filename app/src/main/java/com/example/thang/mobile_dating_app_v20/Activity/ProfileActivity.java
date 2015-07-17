package com.example.thang.mobile_dating_app_v20.Activity;

import android.annotation.TargetApi;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.thang.mobile_dating_app_v20.Classes.ConnectionTool;
import com.example.thang.mobile_dating_app_v20.Classes.DBHelper;
import com.example.thang.mobile_dating_app_v20.Classes.Person;
import com.example.thang.mobile_dating_app_v20.Classes.Utils;
import com.example.thang.mobile_dating_app_v20.Fragments.EditProfile;
import com.example.thang.mobile_dating_app_v20.R;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.github.ksoichiro.android.observablescrollview.ObservableListView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.nineoldandroids.view.ViewHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class ProfileActivity extends BaseActivity implements ObservableScrollViewCallbacks {

    private static final float MAX_TEXT_SCALE_DELTA = 0.3f;

    private ImageView mImageView;
    private View mOverlayView;
    private View mListBackgroundView;
    private TextView mTitleView;
    private ProgressBar spinner;

    private int mActionBarSize;
    private int mFlexibleSpaceShowFabOffset;
    private int mFlexibleSpaceImageHeight;

    ObservableListView listView;
    Person person = new Person();
    private String URL_FIND = MainActivity.URL_CLOUD + "/Service/getbyemail?email=";
    private String URL_FRIEND_REQUEST = MainActivity.URL_CLOUD + "/Service/makefriendrequest?";
    private String URL_UNFRIEND = MainActivity.URL_CLOUD + "/Service/deleterelationship?";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

//        if (Build.VERSION.SDK_INT >= 21) {
//            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
//                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            //setup animation
//            Explode explode = new Explode();
//            explode.setDuration(2000);
//            getWindow().setEnterTransition(explode);
//
//            Fade fade = new Fade();
//            fade.setDuration(2000);
//            getWindow().setReturnTransition(fade);
//        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.profile_tool_bar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mFlexibleSpaceImageHeight = getResources().getDimensionPixelSize(R.dimen.flexible_space_image_height);
        mFlexibleSpaceShowFabOffset = getResources().getDimensionPixelSize(R.dimen.flexible_space_show_fab_offset);
        mActionBarSize = getActionBarSize();
        mImageView = (ImageView) findViewById(R.id.profile_avatar);
        mOverlayView = findViewById(R.id.overlay);

        mTitleView = (TextView) findViewById(R.id.title);
        mListBackgroundView = findViewById(R.id.list_background);
        spinner = (ProgressBar) findViewById(R.id.spinner);
        listView = (ObservableListView) findViewById(R.id.list);
        listView.setScrollViewCallbacks(this);

        // Set padding view for ListView. This is the flexible space.
        View paddingView = new View(this);
        AbsListView.LayoutParams lp = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT,
                mFlexibleSpaceImageHeight);
        paddingView.setLayoutParams(lp);

        // This is required to disable header's list selector effect
        paddingView.setClickable(true);
        listView.addHeaderView(paddingView);

        //init fab button
        FloatingActionButton fabAddFriend = (FloatingActionButton) findViewById(R.id.fab_addfriend);
        FloatingActionButton fabProfileEditor = (FloatingActionButton) findViewById(R.id.fab_editor);
        FloatingActionsMenu fabFriendEditor = (FloatingActionsMenu) findViewById(R.id.multiple_actions_down);
        FloatingActionButton fabBlockFriend = (FloatingActionButton) findViewById(R.id.fab_blockfriend);
        FloatingActionButton fabUnFriend = (FloatingActionButton) findViewById(R.id.fab_unfriend);

        //gone as default
        fabAddFriend.setVisibility(View.GONE);
        fabFriendEditor.setVisibility(View.GONE);
        fabProfileEditor.setVisibility(View.GONE);
               spinner.setVisibility(View.GONE);

        //get current user avatar
        Bundle bundle = getIntent().getExtras();
        String flag = bundle.getString("ProfileOf");
        DBHelper dbHelper = DBHelper.getInstance(getApplicationContext());

        if (flag.equals(DBHelper.USER_FLAG_CURRENT)) {
            person = dbHelper.getCurrentUser();
            //show edit fab, onclick to update avatar
            fabProfileEditor.setVisibility(View.VISIBLE);
            fabProfileEditor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.addToBackStack(null);
                    ft.replace(R.id.profileFragment, new EditProfile());
                    ft.commit();
                }
            });
            //updated
            mImageView.setImageBitmap(person.getAvatar().isEmpty() ? BitmapFactory.decodeResource(getResources(),
                    R.drawable.no_avatar) : Utils.decodeBase64StringToBitmap(person.getAvatar()));
            setFriendAdapter(listView, person);
            mTitleView.setText(person.getFullName());
        } else {
            person = dbHelper.getPersonByEmail(bundle.getString("email"));
            //setup diaglog
            final MaterialDialog unfriendDialog = new MaterialDialog.Builder(this)
                    .title(R.string.dialog_unfriend_title)
                    .content(R.string.dialog_unfriend_content)
                    .positiveText(R.string.dialog_unfriend_positive)
                    .negativeText(R.string.dialog_unfriend_negative)
                    .autoDismiss(false)
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            //delete friend in SQLite
                            DBHelper.getInstance(getApplicationContext()).deletePerson(person.getEmail());
                            String param = "from=" + DBHelper.getInstance(getApplicationContext()).getCurrentUser().getEmail() + "&to=" + person.getEmail();
                            new unfriendTask().execute(URL_UNFRIEND + param);
                            dialog.dismiss();
                            onBackPressed();
                            Toast.makeText(getApplicationContext(), R.string.dialog_unfriended, Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onNegative(MaterialDialog dialog) {
                            dialog.dismiss();
                        }
                    }).build();
            //if person != null mean this is local friend
            if (person.getEmail() != null) {
                fabFriendEditor.setVisibility(View.VISIBLE);
                fabUnFriend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        unfriendDialog.show();
                    }
                });

            } else {
                //unknown friend, get from service
                fabAddFriend.setVisibility(View.VISIBLE);
                //send friend request onclick listener
                fabAddFriend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String userEmail = DBHelper.getInstance(getApplicationContext()).getCurrentUser().getEmail();
                        String friendEmail = person.getEmail();
                        String param = URL_FRIEND_REQUEST + "from=" + userEmail + "&to=" + friendEmail;
                        new makeFriendRequestTask().execute(param);
                    }
                });
            }

            //get avatar from service
            new getProfileTask().execute(URL_FIND + bundle.getString("email"));

        }
        //animation for menu fab
        final View subLayer = findViewById(R.id.sub_layer);
        subLayer.setVisibility(View.GONE);

        final FloatingActionsMenu fabMenu = (FloatingActionsMenu) findViewById(R.id.multiple_actions_down);

        FloatingActionsMenu.OnFloatingActionsMenuUpdateListener listener = new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
            @Override
            public void onMenuExpanded() {
                subLayer.setVisibility(View.VISIBLE);
                //subLayer.setBackgroundColor(getResources().getColor(R.color.black_layer));
            }

            @Override
            public void onMenuCollapsed() {
                subLayer.setVisibility(View.GONE);
                //subLayer.setBackgroundColor(getResources().getColor(R.color.no_layer));
            }
        };

        subLayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fabMenu.isExpanded()) {
                    fabMenu.collapse();
                }
            }
        });

        fabMenu.setOnFloatingActionsMenuUpdateListener(listener);

        setTitle(null);
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
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
    public void onScrollChanged(int scrollY, boolean b, boolean b1) {
        // Translate overlay and image
        float flexibleRange = mFlexibleSpaceImageHeight - mActionBarSize;
        int minOverlayTransitionY = mActionBarSize - mOverlayView.getHeight();
        ViewHelper.setTranslationY(mOverlayView, ScrollUtils.getFloat(-scrollY, minOverlayTransitionY, 0));
        ViewHelper.setTranslationY(mImageView, ScrollUtils.getFloat(-scrollY / 2, minOverlayTransitionY, 0));

        // Translate list background
        ViewHelper.setTranslationY(mListBackgroundView, Math.max(0, -scrollY + mFlexibleSpaceImageHeight));

        // Change alpha of overlay
        ViewHelper.setAlpha(mOverlayView, ScrollUtils.getFloat((float) scrollY / flexibleRange, 0, 1));

        // Scale title text
        float scale = 1 + ScrollUtils.getFloat((flexibleRange - scrollY) / flexibleRange, 0, MAX_TEXT_SCALE_DELTA);
        setPivotXToTitle();
        ViewHelper.setPivotY(mTitleView, 0);
        ViewHelper.setScaleX(mTitleView, scale);
        ViewHelper.setScaleY(mTitleView, scale);

        // Translate title text
        int maxTitleTranslationY = (int) (mFlexibleSpaceImageHeight - mTitleView.getHeight() * scale);
        int titleTranslationY = maxTitleTranslationY - scrollY;
        ViewHelper.setTranslationY(mTitleView, titleTranslationY);
    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void setPivotXToTitle() {
        Configuration config = getResources().getConfiguration();
        if (Build.VERSION_CODES.JELLY_BEAN_MR1 <= Build.VERSION.SDK_INT
                && config.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL) {
            ViewHelper.setPivotX(mTitleView, findViewById(android.R.id.content).getWidth());
        } else {
            ViewHelper.setPivotX(mTitleView, 0);
        }
    }

    private class getProfileTask extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
            ConnectionTool connectionTool = new ConnectionTool(ProfileActivity.this);
            if (!connectionTool.isNetworkAvailable()) {
                new MaterialDialog.Builder(ProfileActivity.this)
                        .title(R.string.error_connection_title)
                        .content(R.string.error_connection)
                        .titleColorRes(R.color.md_red_400)
                        .show();
            }else{
                spinner.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected String doInBackground(String... params) {
            return ConnectionTool.makeGetRequest(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                //start parsing jsonResponse
                JSONObject jsonObject = new JSONObject(result);
                List<Person> personList = ConnectionTool.fromJSON(jsonObject);
                if (personList != null) {
                    person = personList.get(0);
                }
                mImageView.setImageBitmap(person.getAvatar().isEmpty() ? BitmapFactory.decodeResource(getResources(),
                        R.drawable.no_avatar) : Utils.decodeBase64StringToBitmap(person.getAvatar()));
                setFriendAdapter(listView, person);
                mTitleView.setText(person.getFullName());
                //hide spinner
                spinner.setVisibility(View.GONE);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class makeFriendRequestTask extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
            ConnectionTool connectionTool = new ConnectionTool(ProfileActivity.this);
            if (connectionTool.isNetworkAvailable()) {

            } else {
                new MaterialDialog.Builder(ProfileActivity.this)
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
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.profile_friend_request) + " " + person.getFullName(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private class unfriendTask extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {

            ConnectionTool connectionTool = new ConnectionTool(getApplicationContext());
            if (!connectionTool.isNetworkAvailable()) {
                new MaterialDialog.Builder(getApplicationContext())
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
            if (result.isEmpty()) {
                Toast.makeText(getApplicationContext(), getString(R.string.loading_error), Toast.LENGTH_SHORT).show();
            }
        }
    }

}
