package com.example.thang.mobile_dating_app_v20.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.thang.mobile_dating_app_v20.Activity.ChatActivity;
import com.example.thang.mobile_dating_app_v20.Classes.DBHelper;
import com.example.thang.mobile_dating_app_v20.Classes.Person;
import com.example.thang.mobile_dating_app_v20.Classes.Utils;
import com.example.thang.mobile_dating_app_v20.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Thang on 8/6/2015.
 */
public class MutualFriendAdapter extends BaseAdapter {
    List<Person> persons;
    Context context;

    public MutualFriendAdapter(Context context, List<Person> persons) {
        this.persons = persons;
        this.context = context;
    }

    @Override
    public int getCount() {
        return persons.size();
    }

    @Override
    public Object getItem(int position) {
        return persons.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        final Person person = persons.get(position);

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.mutual_friend_item, parent, false);

            holder = new ViewHolder();
            holder.avatar = (ImageView) convertView.findViewById(R.id.avatar);
            holder.txtName = (TextView) convertView.findViewById(R.id.txtName);
            holder.txtSubInfo = (TextView) convertView.findViewById(R.id.txtSubInfo);
            holder.friendMarker = (LinearLayout) convertView.findViewById(R.id.friend_marker);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.txtName.setText(person.getFullName());
        holder.avatar.setImageBitmap(person.getAvatar().isEmpty() ? BitmapFactory.decodeResource(context.getResources(),
                R.drawable.no_avatar) : Utils.generateSmaleBitmap(person.getAvatar(), 80, 80));

        String localPersonEmail = DBHelper.getInstance(context).getPersonByEmail(person.getEmail()).getEmail();
        String currentPersonEmail = DBHelper.getInstance(context).getCurrentUser().getEmail();
        if (localPersonEmail != null && !localPersonEmail.equals(currentPersonEmail)) {
            //not friend and not current user
            holder.friendMarker.setVisibility(View.VISIBLE);
        }

        String gender = "";
        if (person.getGender().equals("Male")) {
            gender = context.getString(R.string.register_gender_male);
        } else {
            gender = context.getString(R.string.register_gender_female);
        }

        String briefInfo = context.getString(R.string.friend_info, gender, person.getAge());

        holder.txtSubInfo.setText(briefInfo);

        return convertView;
    }

    private class ViewHolder {
        public ImageView avatar;
        public TextView txtName;
        public TextView txtSubInfo;
        public LinearLayout friendMarker;
        //public ImageView chatIcon;
    }
}
