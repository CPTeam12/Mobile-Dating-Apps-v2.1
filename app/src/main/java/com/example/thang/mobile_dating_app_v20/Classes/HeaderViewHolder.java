package com.example.thang.mobile_dating_app_v20.Classes;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.example.thang.mobile_dating_app_v20.Activity.NearbyMapActivity;
import com.example.thang.mobile_dating_app_v20.Activity.NewProfileActivity;
import com.example.thang.mobile_dating_app_v20.R;

import org.w3c.dom.Text;

/**
 * Created by Thang on 7/8/2015.
 */
public class HeaderViewHolder extends RecyclerView.ViewHolder {
    private TextView mTextView;
    private ImageView avatar;
    private TextView name;
    private ProgressBar progressBar;
    private RelativeLayout linearLayout;
    private Context context;

    private TextView nearbyFriend;
    private TextView nearbyPerosn;
    private TextView subName;

    public HeaderViewHolder(View itemView) {
        super(itemView);
        mTextView = (TextView) itemView.findViewById(R.id.text);
    }

    public HeaderViewHolder(View itemView, Context context) {
        super(itemView);
        avatar = (ImageView) itemView.findViewById(R.id.avatar);
        name = (TextView) itemView.findViewById(R.id.name);
        progressBar = (ProgressBar) itemView.findViewById(R.id.progress);
        linearLayout = (RelativeLayout) itemView.findViewById(R.id.titlebg);

        //new item
        subName = (TextView) itemView.findViewById(R.id.subname);

        this.context = context;
    }

    public HeaderViewHolder(View itemView, Context context, boolean isStart) {
        super(itemView);
        nearbyFriend = (TextView) itemView.findViewById(R.id.friend_nearby);
        nearbyPerosn = (TextView) itemView.findViewById(R.id.people_nearby);
        this.context = context;
        avatar = (ImageView) itemView.findViewById(R.id.start_nearby_avatar);
    }

    public void bindStartItem(String nearbyFriend, String nearbyPerosn, String avatarText) {
        this.nearbyFriend.setText(nearbyFriend);
        this.nearbyPerosn.setText(nearbyPerosn);
        avatar.setImageBitmap(avatarText.isEmpty() ? BitmapFactory.decodeResource(context.getResources(),
                R.drawable.no_avatar) : Utils.generateSmaleBitmap(avatarText, 200, 200));
    }

    public void bindHeaderItem(String text) {
        mTextView.setText(text);
    }

    public void bindNearbyItem(final Person person) {

        avatar.setImageBitmap(person.getAvatar().isEmpty() ? BitmapFactory.decodeResource(context.getResources(),
                R.drawable.no_avatar) : Utils.generateSmaleBitmap(person.getAvatar(), 144, 144));
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle dataBundle = new Bundle();
                dataBundle.putDouble("latitude", person.getLatitude());
                dataBundle.putDouble("longitude", person.getLongitude());

                Intent intent = new Intent(context, NearbyMapActivity.class);
                intent.putExtras(dataBundle);
                context.startActivity(intent);
            }
        });
        name.setText(person.getFullName());

        String gender = "";
        if (person.getGender().equals("Male")) {
            gender = context.getResources().getString(R.string.register_gender_male);
        } else {
            gender = context.getResources().getString(R.string.register_gender_female);
        }
        String title = "";
        if (person.getPercent() != 0){
            title = " | " + String.format("%.0f",person.getPercent() * 100) + "%";
        }
        String briefInfo = context.getResources().getString(R.string.friend_info, gender, person.getAge()) + title;
        subName.setText(briefInfo);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("email", person.getEmail());
                bundle.putString("ProfileOf", "Person");

                Intent intent = new Intent(context, NewProfileActivity.class);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });
    }


}
