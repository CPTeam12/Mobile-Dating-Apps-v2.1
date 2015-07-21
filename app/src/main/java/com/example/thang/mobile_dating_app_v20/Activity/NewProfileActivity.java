package com.example.thang.mobile_dating_app_v20.Activity;

import android.annotation.TargetApi;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.OverScroller;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.thang.mobile_dating_app_v20.Classes.ConnectionTool;
import com.example.thang.mobile_dating_app_v20.Classes.DBHelper;
import com.example.thang.mobile_dating_app_v20.Classes.Person;
import com.example.thang.mobile_dating_app_v20.Classes.Utils;
import com.example.thang.mobile_dating_app_v20.Fragments.EditProfile;
import com.example.thang.mobile_dating_app_v20.Fragments.ProfileTab1;
import com.example.thang.mobile_dating_app_v20.Fragments.ProfileTab2;
import com.example.thang.mobile_dating_app_v20.R;
import com.example.thang.mobile_dating_app_v20.Views.SlidingTabLayout;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.github.ksoichiro.android.observablescrollview.CacheFragmentStatePagerAdapter;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.github.ksoichiro.android.observablescrollview.Scrollable;
import com.github.ksoichiro.android.observablescrollview.TouchInterceptionFrameLayout;
import com.nineoldandroids.view.ViewHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class NewProfileActivity extends ActionBarActivity implements ObservableScrollViewCallbacks {

    private static final float MAX_TEXT_SCALE_DELTA = 0.3f;
    private static final int INVALID_POINTER = -1;

    private ImageView mImageView;
    private View mOverlayView;
    private TextView mTitleView;
    private TouchInterceptionFrameLayout mInterceptionLayout;
    private ViewPager mPager;
    private NavigationAdapter mPagerAdapter;
    private VelocityTracker mVelocityTracker;
    private OverScroller mScroller;
    private float mBaseTranslationY;
    private int mMaximumVelocity;
    private int mActivePointerId = INVALID_POINTER;
    private int mSlop;
    private int mFlexibleSpaceHeight;
    private int mTabHeight;
    private boolean mScrolled;
    private ProgressBar spinner;

    private static Person person;
    private String URL_FIND = MainActivity.URL_CLOUD + "/Service/getbyemail?email=";
    private String URL_FRIEND_REQUEST = MainActivity.URL_CLOUD + "/Service/makefriendrequest?";
    private String URL_UNFRIEND = MainActivity.URL_CLOUD + "/Service/deleterelationship?";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_profile);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        ViewCompat.setElevation(findViewById(R.id.header), getResources().getDimension(R.dimen.toolbar_elevation));
        mPagerAdapter = new NavigationAdapter(getSupportFragmentManager());
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mPagerAdapter);
        mImageView = (ImageView) findViewById(R.id.image);
        mOverlayView = findViewById(R.id.overlay);
        spinner = (ProgressBar) findViewById(R.id.spinner);
        // Padding for ViewPager must be set outside the ViewPager itself
        // because with padding, EdgeEffect of ViewPager become strange.
        mFlexibleSpaceHeight = getResources().getDimensionPixelSize(R.dimen.flexible_space_image_height);
        mTabHeight = getResources().getDimensionPixelSize(R.dimen.tab_height);
        findViewById(R.id.pager_wrapper).setPadding(0, mFlexibleSpaceHeight, 0, 0);
        mTitleView = (TextView) findViewById(R.id.title);
        mTitleView.setText("");
        setTitle(null);

        SlidingTabLayout slidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        slidingTabLayout.setCustomTabView(R.layout.tab_indicator, android.R.id.text1);
        slidingTabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.AccentColor));
        slidingTabLayout.setDistributeEvenly(true);
        slidingTabLayout.setViewPager(mPager);
        ((FrameLayout.LayoutParams) slidingTabLayout.getLayoutParams()).topMargin = mFlexibleSpaceHeight - mTabHeight;

        ViewConfiguration vc = ViewConfiguration.get(this);
        mSlop = vc.getScaledTouchSlop();
        mMaximumVelocity = vc.getScaledMaximumFlingVelocity();
        mInterceptionLayout = (TouchInterceptionFrameLayout) findViewById(R.id.container);
        mInterceptionLayout.setScrollInterceptionListener(mInterceptionListener);
        mScroller = new OverScroller(getApplicationContext());
        ScrollUtils.addOnGlobalLayoutListener(mInterceptionLayout, new Runnable() {
            @Override
            public void run() {
                // Extra space is required to move mInterceptionLayout when it's scrolled.
                // It's better to adjust its height when it's laid out
                // than to adjust the height when scroll events (onMoveMotionEvent) occur
                // because it causes lagging.
                // See #87: https://github.com/ksoichiro/Android-ObservableScrollView/issues/87
                FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mInterceptionLayout.getLayoutParams();
                lp.height = getScreenHeight() + mFlexibleSpaceHeight;
                mInterceptionLayout.requestLayout();

                updateFlexibleSpace();
            }
        });

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
//                    FragmentTransaction ft = getFragmentManager().beginTransaction();
//                    ft.addToBackStack(null);
//                    ft.replace(R.id.profileFragment, new EditProfile());
//                    ft.commit();
                    Intent intent = new Intent(getApplicationContext(), EditProfileActivity.class);
                    startActivity(intent);
                }
            });
            //updated
            mImageView.setImageBitmap(person.getAvatar().isEmpty() ? BitmapFactory.decodeResource(getResources(),
                    R.drawable.no_avatar) : Utils.decodeBase64StringToBitmap(person.getAvatar()));
            //setFriendAdapter(listView, person);
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
        }
    }

    private int getScreenHeight() {
        return findViewById(android.R.id.content).getHeight();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_profile, menu);
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
    public void onScrollChanged(int i, boolean b, boolean b1) {

    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {

    }

    private TouchInterceptionFrameLayout.TouchInterceptionListener mInterceptionListener = new TouchInterceptionFrameLayout.TouchInterceptionListener() {
        @Override
        public boolean shouldInterceptTouchEvent(MotionEvent ev, boolean moving, float diffX, float diffY) {
            if (!mScrolled && mSlop < Math.abs(diffX) && Math.abs(diffY) < Math.abs(diffX)) {
                // Horizontal scroll is maybe handled by ViewPager
                return false;
            }

            Scrollable scrollable = getCurrentScrollable();
            if (scrollable == null) {
                mScrolled = false;
                return false;
            }

            // If interceptionLayout can move, it should intercept.
            // And once it begins to move, horizontal scroll shouldn't work any longer.
            int flexibleSpace = mFlexibleSpaceHeight - mTabHeight;
            int translationY = (int) ViewHelper.getTranslationY(mInterceptionLayout);
            boolean scrollingUp = 0 < diffY;
            boolean scrollingDown = diffY < 0;
            if (scrollingUp) {
                if (translationY < 0) {
                    mScrolled = true;
                    return true;
                }
            } else if (scrollingDown) {
                if (-flexibleSpace < translationY) {
                    mScrolled = true;
                    return true;
                }
            }
            mScrolled = false;
            return false;
        }

        @Override
        public void onDownMotionEvent(MotionEvent ev) {
            mActivePointerId = ev.getPointerId(0);
            mScroller.forceFinished(true);
            if (mVelocityTracker == null) {
                mVelocityTracker = VelocityTracker.obtain();
            } else {
                mVelocityTracker.clear();
            }
            mBaseTranslationY = ViewHelper.getTranslationY(mInterceptionLayout);
            mVelocityTracker.addMovement(ev);
        }

        @Override
        public void onMoveMotionEvent(MotionEvent ev, float diffX, float diffY) {
            int flexibleSpace = mFlexibleSpaceHeight - mTabHeight;
            float translationY = ScrollUtils.getFloat(ViewHelper.getTranslationY(mInterceptionLayout) + diffY, -flexibleSpace, 0);
            MotionEvent e = MotionEvent.obtainNoHistory(ev);
            e.offsetLocation(0, translationY - mBaseTranslationY);
            mVelocityTracker.addMovement(e);
            updateFlexibleSpace(translationY);
        }

        @Override
        public void onUpOrCancelMotionEvent(MotionEvent ev) {
            mScrolled = false;
            mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
            int velocityY = (int) mVelocityTracker.getYVelocity(mActivePointerId);
            mActivePointerId = INVALID_POINTER;
            mScroller.forceFinished(true);
            int baseTranslationY = (int) ViewHelper.getTranslationY(mInterceptionLayout);

            int minY = -(mFlexibleSpaceHeight - mTabHeight);
            int maxY = 0;
            mScroller.fling(0, baseTranslationY, 0, velocityY, 0, 0, minY, maxY);
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    updateLayout();
                }
            });
        }
    };

    private void updateLayout() {
        boolean needsUpdate = false;
        float translationY = 0;
        if (mScroller.computeScrollOffset()) {
            translationY = mScroller.getCurrY();
            int flexibleSpace = mFlexibleSpaceHeight - mTabHeight;
            if (-flexibleSpace <= translationY && translationY <= 0) {
                needsUpdate = true;
            } else if (translationY < -flexibleSpace) {
                translationY = -flexibleSpace;
                needsUpdate = true;
            } else if (0 < translationY) {
                translationY = 0;
                needsUpdate = true;
            }
        }

        if (needsUpdate) {
            updateFlexibleSpace(translationY);

            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    updateLayout();
                }
            });
        }
    }

    private Scrollable getCurrentScrollable() {
        Fragment fragment = getCurrentFragment();
        if (fragment == null) {
            return null;
        }
        View view = fragment.getView();
        if (view == null) {
            return null;
        }
        return (Scrollable) view.findViewById(R.id.scroll);
    }

    private void updateFlexibleSpace() {
        updateFlexibleSpace(ViewHelper.getTranslationY(mInterceptionLayout));
    }

    private int getActionBarSize() {
        TypedValue typedValue = new TypedValue();
        int[] textSizeAttr = new int[]{R.attr.actionBarSize};
        int indexOfAttrTextSize = 0;
        TypedArray a = obtainStyledAttributes(typedValue.data, textSizeAttr);
        int actionBarSize = a.getDimensionPixelSize(indexOfAttrTextSize, -1);
        a.recycle();
        return actionBarSize;
    }

    private void updateFlexibleSpace(float translationY) {
        ViewHelper.setTranslationY(mInterceptionLayout, translationY);
        int minOverlayTransitionY = getActionBarSize() - mOverlayView.getHeight();
        ViewHelper.setTranslationY(mImageView, ScrollUtils.getFloat(-translationY / 2, minOverlayTransitionY, 0));

        // Change alpha of overlay
        float flexibleRange = mFlexibleSpaceHeight - getActionBarSize();
        ViewHelper.setAlpha(mOverlayView, ScrollUtils.getFloat(-translationY / flexibleRange, 0, 1));

        // Scale title text
        float scale = 1 + ScrollUtils.getFloat((flexibleRange + translationY - mTabHeight) / flexibleRange, 0, MAX_TEXT_SCALE_DELTA);
        setPivotXToTitle();
        ViewHelper.setPivotY(mTitleView, 0);
        ViewHelper.setScaleX(mTitleView, scale);
        ViewHelper.setScaleY(mTitleView, scale);
    }

    private Fragment getCurrentFragment() {
        return mPagerAdapter.getItemAt(mPager.getCurrentItem());
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

    /**
     * This adapter provides two types of fragments as an example.
     * {@linkplain #createItem(int)} should be modified if you use this example for your app.
     */
    private class NavigationAdapter extends CacheFragmentStatePagerAdapter {

        private final String[] TITLES = new String[]{"Thông tin chung", "Sở thích"};

        public NavigationAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        protected Fragment createItem(int position) {
            Fragment f;
            Bundle bundle = new Bundle();
            bundle.putSerializable("Person", person);
            switch (position) {
                case 0:
                    f = new ProfileTab1();
                    f.setArguments(bundle);
                    break;
                case 1:
                    f = new ProfileTab2();
                    f.setArguments(bundle);
                    break;
                default:
                    f = new ProfileTab1();
                    break;
            }
            return f;
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
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

    private class makeFriendRequestTask extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
            ConnectionTool connectionTool = new ConnectionTool(NewProfileActivity.this);
            if (connectionTool.isNetworkAvailable()) {

            } else {
                new MaterialDialog.Builder(NewProfileActivity.this)
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

    private class getProfileTask extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
            ConnectionTool connectionTool = new ConnectionTool(NewProfileActivity.this);
            if (!connectionTool.isNetworkAvailable()) {
                new MaterialDialog.Builder(NewProfileActivity.this)
                        .title(R.string.error_connection_title)
                        .content(R.string.error_connection)
                        .titleColorRes(R.color.md_red_400)
                        .show();
            } else {
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
                //setFriendAdapter(listView, person);
                mTitleView.setText(person.getFullName());
                mPager.setAdapter(mPagerAdapter);


                //hide spinner
                spinner.setVisibility(View.GONE);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
