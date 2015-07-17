package com.example.thang.mobile_dating_app_v20.Cards;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.thang.mobile_dating_app_v20.Activity.MainActivity;
import com.example.thang.mobile_dating_app_v20.Activity.NewProfileActivity;
import com.example.thang.mobile_dating_app_v20.Activity.ProfileActivity;
import com.example.thang.mobile_dating_app_v20.Classes.ConnectionTool;
import com.example.thang.mobile_dating_app_v20.Classes.DBHelper;
import com.example.thang.mobile_dating_app_v20.R;

import it.gmariotti.cardslib.library.internal.Card;

/**
 * Created by Thang on 5/26/2015.
 */
public class CardFriendRequest extends Card {
    private String URL_FRIEND_ACCEPT = MainActivity.URL_CLOUD + "/Service/acceptfriend?";
    private String URL_FRIEND_DENY = MainActivity.URL_CLOUD + "/Service/deleterelationship?";

    private TextView mTitle;
    private TextView mSecondaryTitle;
    private ImageView mImageView;
    private TextView actionMessage;
    private Context context;

    private Button accept;
    private Button deny;

    private Bitmap bitMap;
    private String title;
    private String secondaryTitle;
    private String email;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSecondaryTitle(String secondaryTitle) {
        this.secondaryTitle = secondaryTitle;
    }

    public void setBitMap(Bitmap bitMap) {
        this.bitMap = bitMap;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public void setContext(Context context) {
        this.context = context;
    }

    public CardFriendRequest(Context context) {
        this(context, R.layout.card_friendrequest_inner_layout);
    }

    public CardFriendRequest(Context context, int innerLayout) {
        super(context, innerLayout);
    }

    public void init(String title, String description) {
        setTitle(title);
        setSecondaryTitle(description);
        setShadow(false);
        setSwipeable(false);
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {
        //Retrieve elements
        mTitle = (TextView) parent.findViewById(R.id.carddemo_myapps_main_inner_title);
        mSecondaryTitle = (TextView) parent.findViewById(R.id.carddemo_myapps_main_inner_secondaryTitle);
        mImageView = (ImageView) parent.findViewById(R.id.card_avatar);
        accept = (Button) parent.findViewById(R.id.friend_accept);
        deny = (Button) parent.findViewById(R.id.friend_deny);
        actionMessage = (TextView) parent.findViewById(R.id.friend_accepted);

        if (mTitle != null)
            mTitle.setText(title);

        if (mSecondaryTitle != null)
            mSecondaryTitle.setText(secondaryTitle);

        if (mImageView != null){
            mImageView.setImageBitmap(bitMap);
            mImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("email", email);
                    bundle.putString("ProfileOf", "Person");

                    Intent intent = new Intent(context, NewProfileActivity.class);
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                }
            });
        }


        if (accept != null)
            accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    accept.setVisibility(View.GONE);
                    deny.setVisibility(View.GONE);
                    actionMessage.setVisibility(View.VISIBLE);
                    String param = "from=" + DBHelper.getInstance(context).getCurrentUser().getEmail() + "&to=" + email;
                    new getFriendRequest().execute(URL_FRIEND_ACCEPT + param);
                }
            });

        if (deny != null)
            deny.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    accept.setVisibility(View.GONE);
                    deny.setVisibility(View.GONE);
                    actionMessage.setText(R.string.friend_deny_request);
                    actionMessage.setVisibility(View.VISIBLE);
                    String param = "from=" + DBHelper.getInstance(context).getCurrentUser().getEmail() + "&to=" + email;
                    new denyFriendRequest().execute(URL_FRIEND_DENY + param);
                }
            });
    }

    private class getFriendRequest extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {

            ConnectionTool connectionTool = new ConnectionTool(context);
            if (!connectionTool.isNetworkAvailable()) {
                new MaterialDialog.Builder(context)
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
            //start parsing jsonResponse
            if (result.isEmpty()) {
                Toast.makeText(context, context.getString(R.string.loading_error), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class denyFriendRequest extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {

            ConnectionTool connectionTool = new ConnectionTool(context);
            if (!connectionTool.isNetworkAvailable()) {
                new MaterialDialog.Builder(context)
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
            //start parsing jsonResponse
            if (result.isEmpty()) {
                Toast.makeText(context, context.getString(R.string.loading_error), Toast.LENGTH_SHORT).show();
            }
        }
    }


}
