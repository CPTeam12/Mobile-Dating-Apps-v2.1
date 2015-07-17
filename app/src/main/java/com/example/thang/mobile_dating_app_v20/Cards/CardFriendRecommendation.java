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
 * Created by Thang on 6/9/2015.
 */
public class CardFriendRecommendation extends Card {
    private Context context;
    private String URL_FRIEND_REQUEST = MainActivity.URL_CLOUD + "/Service/makefriendrequest?";

    private TextView mTitle;
    private TextView mSecondaryTitle;
    private ImageView mImageView;
    private TextView actionMessage;
    private Button friendRequest;

    private String title;
    private String secondaryTitle;
    private Bitmap bitMap;

    private String email;
    private String fullName;

    public CardFriendRecommendation(Context context) {
        super(context, R.layout.card_friendrecommendation_inner_layout);
    }

    public CardFriendRecommendation(Context context, int innerLayout) {
        super(context, innerLayout);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setBitMap(Bitmap bitMap) {
        this.bitMap = bitMap;
    }

    public void setSecondaryTitle(String mSecondaryTitle) {
        this.secondaryTitle = mSecondaryTitle;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public void setContext(Context context) {
        this.context = context;
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
        actionMessage = (TextView) parent.findViewById(R.id.friend_requested);
        friendRequest = (Button) parent.findViewById(R.id.friend_request);

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


        if (friendRequest != null)
            friendRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    actionMessage.setText(R.string.friend_request);
                    actionMessage.setVisibility(View.VISIBLE);
                    friendRequest.setVisibility(View.GONE);

                    String param = URL_FRIEND_REQUEST + "from=" + DBHelper.getInstance(context).getCurrentUser().getEmail()
                            + "&to=" + email;
                    new makeFriendRequestTask().execute(param);
                }
            });
    }

    private class makeFriendRequestTask extends AsyncTask<String, Integer, String> {
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
            if (!result.isEmpty())
                Toast.makeText(context, context.getResources().getString(R.string.profile_friend_request) + fullName,
                        Toast.LENGTH_SHORT).show();
        }
    }
}
