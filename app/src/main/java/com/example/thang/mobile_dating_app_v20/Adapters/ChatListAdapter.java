package com.example.thang.mobile_dating_app_v20.Adapters;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.thang.mobile_dating_app_v20.Classes.Message;
import com.example.thang.mobile_dating_app_v20.R;
import com.squareup.picasso.Picasso;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.List;

/**
 * Created by Man Huynh Khuong on 5/14/2015.
 */
public class ChatListAdapter extends ArrayAdapter<Message> {
    private String mUserId;

    public ChatListAdapter(Context context, String userId, List<Message> messages) {
        super(context, 0, messages);
        this.mUserId = userId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Message message = (Message)getItem(position);
        final boolean isMe = message.getUserId().equals(mUserId);
        if(isMe){
            convertView = LayoutInflater.from(getContext()).
                    inflate(R.layout.chat_item_me, parent, false);
            final ViewHolder holder1 = new ViewHolder();
            holder1.imageRight = (ImageView)convertView.findViewById(R.id.ivProfileRight);
            holder1.body = (TextView)convertView.findViewById(R.id.tvBody_me);
            convertView.setTag(holder1);
            }
        else{
            convertView = LayoutInflater.from(getContext()).
                        inflate(R.layout.chat_item, parent, false);
            final ViewHolder holder1 = new ViewHolder();
            holder1.imageLeft = (ImageView)convertView.findViewById(R.id.ivProfileLeft);
            holder1.body = (TextView)convertView.findViewById(R.id.tvBody);
            convertView.setTag(holder1);
        }
        final ViewHolder holder = (ViewHolder)convertView.getTag();
        // Show-hide image based on the logged-in user.
        // Display the profile image to the right for our user, left for other users.
        if (isMe) {
            holder.imageRight.setVisibility(View.VISIBLE);
//            holder.imageLeft.setVisibility(View.GONE);
            holder.body.setGravity(Gravity.RIGHT);
            final ImageView profileView = holder.imageRight;
            Picasso.with(getContext()).load(getProfileUrl(message.getUserId())).into(profileView);
            holder.body.setText(message.getBody());
            return convertView;
        } else {
            holder.imageLeft.setVisibility(View.VISIBLE);
//            holder.imageRight.setVisibility(View.GONE);
            holder.body.setGravity(Gravity.LEFT);
            final ImageView profileView = holder.imageLeft;
            Picasso.with(getContext()).load(getProfileUrl(message.getUserId())).into(profileView);
            holder.body.setText(message.getBody());
            return convertView;
        }
    }

    // Create a gravatar image based on the hash value obtained from userId
    private static String getProfileUrl(final String userId) {
        String hex = "";
        try {
            final MessageDigest digest = MessageDigest.getInstance("MD5");
            final byte[] hash = digest.digest(userId.getBytes());
            final BigInteger bigInt = new BigInteger(hash);
            hex = bigInt.abs().toString(16);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "http://www.gravatar.com/avatar/" + hex + "?d=identicon";
    }

    final class ViewHolder {
        public ImageView imageLeft;
        public ImageView imageRight;
        public TextView body;
    }
}
