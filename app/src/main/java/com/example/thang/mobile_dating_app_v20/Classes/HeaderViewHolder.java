package com.example.thang.mobile_dating_app_v20.Classes;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
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
        //content = (MaterialRippleLayout) itemView.findViewById(R.id.walls_ripple);
        this.context = context;
    }

    public HeaderViewHolder (View itemView, Context context, boolean isStart){
        super(itemView);
        nearbyFriend = (TextView) itemView.findViewById(R.id.friend_nearby);
        nearbyPerosn = (TextView) itemView.findViewById(R.id.people_nearby);
        this.context = context;
        avatar = (ImageView) itemView.findViewById(R.id.start_nearby_avatar);
    }

    public void bindStartItem(String nearbyFriend, String nearbyPerosn,String avatarText) {
        this.nearbyFriend.setText(nearbyFriend);
        this.nearbyPerosn.setText(nearbyPerosn);
        avatar.setImageBitmap(avatarText.isEmpty() ? BitmapFactory.decodeResource(context.getResources(),
                R.drawable.no_avatar) : Utils.decodeBase64StringToBitmap(avatarText));
    }

    public void bindHeaderItem(String text) {
        mTextView.setText(text);
    }

    public void bindNearbyItem(String fullName,String avatarText) {
        name.setText(fullName);
        avatar.setImageBitmap(avatarText.isEmpty() ? BitmapFactory.decodeResource(context.getResources(),
                R.drawable.no_avatar) : Utils.decodeBase64StringToBitmap(avatarText));
    }
}
