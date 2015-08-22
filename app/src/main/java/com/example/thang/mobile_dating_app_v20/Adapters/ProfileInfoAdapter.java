package com.example.thang.mobile_dating_app_v20.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.thang.mobile_dating_app_v20.Classes.DBHelper;
import com.example.thang.mobile_dating_app_v20.R;
import com.example.thang.mobile_dating_app_v20.Views.SquareImageView;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Thang on 5/28/2015.
 */
public class ProfileInfoAdapter extends BaseAdapter {
    private Map<String, String> profile = new HashMap<String, String>();
    private Context context;
    private static String[] PROFILE_CASE = {"EMAIL", "AGE", "GENDER", "PHONE", "ADDRESS"};

    public ProfileInfoAdapter(Map<String, String> profile, Context context) {
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
//        if (flag.equals(DBHelper.USER_FLAG_CURRENT) ){
//            PROFILE_CASE = PROFILE_CASE1;
//        }else{
//            PROFILE_CASE = PROFILE_CASE2;
//        }
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.profile_item, parent, false);

            holder = new ViewHolder();
            holder.profileIcon = (SquareImageView) convertView.findViewById(R.id.profile_icon);
            holder.profileText = (TextView) convertView.findViewById(R.id.profile_text);
            holder.profileHeaderText = (TextView) convertView.findViewById(R.id.profile_header_text);
            holder.friendItem = (LinearLayout) convertView.findViewById(R.id.friend_item);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        switch (PROFILE_CASE[position]) {
            case "EMAIL":
                holder.profileIcon.setImageResource(R.drawable.profile_email);
                holder.profileText.setText("-");
                if (profile.get(PROFILE_CASE[position]) != null) {
                    holder.profileText.setText(profile.get(PROFILE_CASE[position]));
                }
                holder.profileHeaderText.setText(R.string.prompt_email);
                break;
            case "AGE":
                holder.profileIcon.setImageResource(R.drawable.profile_birthday);
                holder.profileText.setText("-");
                if (profile.get(PROFILE_CASE[position]) != null) {
                    holder.profileText.setText(profile.get(PROFILE_CASE[position]));
                }
                holder.profileHeaderText.setText(R.string.profile_age);
                break;
            case "GENDER":
                holder.profileIcon.setImageResource(R.drawable.profile_gender);
                holder.profileText.setText("-");
                if (profile.get(PROFILE_CASE[position]) != null) {
                    String gender = profile.get(PROFILE_CASE[position]).equals("Female") ?
                            context.getString(R.string.register_gender_female) :
                            context.getString(R.string.register_gender_male);
                    holder.profileText.setText(gender);
                }
                holder.profileHeaderText.setText(R.string.register_gender);
                break;
            case "ADDRESS":
                holder.profileIcon.setImageResource(R.drawable.profile_address);
                holder.profileText.setText("-");
                if (profile.get(PROFILE_CASE[position]) != null) {
                    if (!profile.get(PROFILE_CASE[position]).isEmpty()) {
                        holder.profileText.setText(profile.get(PROFILE_CASE[position]));
                    }
                }
                holder.profileHeaderText.setText(R.string.profile_address);
                break;
            case "PHONE":
                holder.profileIcon.setImageResource(R.drawable.profile_phone);
                holder.profileText.setText("-");
                if (profile.get(PROFILE_CASE[position]) != null) {
                    if (!profile.get(PROFILE_CASE[position]).isEmpty()) {
                        holder.profileText.setText(profile.get(PROFILE_CASE[position]));
                    }
                }
                holder.profileHeaderText.setText(R.string.profile_phone);
                break;
            default:
                holder.friendItem.setVisibility(View.GONE);
        }

        return convertView;
    }

    private class ViewHolder {
        public LinearLayout friendItem;
        public SquareImageView profileIcon;
        public TextView profileText;
        public TextView profileHeaderText;
    }


}
