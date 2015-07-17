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
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.example.thang.mobile_dating_app_v20.Activity.NearbyMapActivity;
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
    private LinearLayout linearLayout;
    private MaterialRippleLayout content;
    private Context context;
    private FrameLayout frameLayout;

    private TextView nearbyFriend;
    private TextView nearbyPerosn;

    public HeaderViewHolder(View itemView) {
        super(itemView);
        mTextView = (TextView) itemView.findViewById(R.id.text);
    }

    public HeaderViewHolder(View itemView, Context context) {
        super(itemView);
        avatar = (ImageView) itemView.findViewById(R.id.avatar);
        name = (TextView) itemView.findViewById(R.id.name);
        progressBar = (ProgressBar) itemView.findViewById(R.id.progress);
        linearLayout = (LinearLayout) itemView.findViewById(R.id.titlebg);
        frameLayout = (FrameLayout) itemView.findViewById(R.id.nearbyItem);
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
                R.drawable.no_avatar) : Utils.generateSmaleBitmap(avatarText,200,200));
    }

    public void bindHeaderItem(String text) {
        mTextView.setText(text);
    }

    public void bindNearbyItem(final Person person) {
        name.setText(person.getFullName());
        avatar.setImageBitmap(person.getAvatar().isEmpty() ? BitmapFactory.decodeResource(context.getResources(),
                R.drawable.no_avatar) : Utils.generateSmaleBitmap(person.getAvatar(), 144, 144));
        frameLayout.setOnClickListener(new View.OnClickListener() {
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
    }
}
