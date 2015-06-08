package com.example.thang.mobile_dating_app_v20.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.thang.mobile_dating_app_v20.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Thang on 5/28/2015.
 */
public class ProfileAdapter extends BaseAdapter {
    private Map<String, String> profile = new HashMap<String, String>();
    private Context context;
    private static final String[] PROFILE_CASE = {"AGE", "GENDER", "EMAIL", "AVATAR", "PHONE", "ADDRESS"};

    public ProfileAdapter(Map<String, String> profile, Context context) {
        this.profile = profile;
        this.context = context;
    }

    @Override
    public int getCount() {
        return profile.size();
    }

    @Override
    public Object getItem(int position) {
        return profile.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;


        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.profile_item, parent, false);

            holder = new ViewHolder();
            holder.profileIcon = (ImageView) convertView.findViewById(R.id.profile_icon);
            holder.profileText = (TextView) convertView.findViewById(R.id.profile_text);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        switch (PROFILE_CASE[position]) {
            case "EMAIL":
                holder.profileIcon.setImageResource(R.drawable.profile_email);
                holder.profileText.setText(profile.get(PROFILE_CASE[position]));
                break;
            case "AGE":
                holder.profileIcon.setImageResource(R.drawable.profile_age);
                holder.profileText.setText(profile.get(PROFILE_CASE[position]));
                break;
            case "GENDER":
                holder.profileIcon.setImageResource(R.drawable.profile_gender);
                holder.profileText.setText(profile.get(PROFILE_CASE[position]));
                break;
            case "AVATAR":
                holder.profileIcon.setImageResource(R.drawable.ic_account_black);
                holder.profileText.setText(profile.get(PROFILE_CASE[position]));
                break;
            case "ADDRESS":
                holder.profileIcon.setImageResource(R.drawable.profile_location);
                holder.profileText.setText(profile.get(PROFILE_CASE[position]));
                break;
            case "PHONE":
                holder.profileIcon.setImageResource(R.drawable.profile_phone);
                holder.profileText.setText(profile.get(PROFILE_CASE[position]));
                break;

        }

        return convertView;
    }

    private class ViewHolder {
        public ImageView profileIcon;
        public TextView profileText;
    }


}
