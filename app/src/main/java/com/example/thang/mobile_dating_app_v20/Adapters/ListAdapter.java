package com.example.thang.mobile_dating_app_v20.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.thang.mobile_dating_app_v20.Activity.ProfileActivity;
import com.example.thang.mobile_dating_app_v20.Classes.DBHelper;
import com.example.thang.mobile_dating_app_v20.Classes.Person;
import com.example.thang.mobile_dating_app_v20.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Thang on 5/16/2015.
 */
public class ListAdapter extends BaseAdapter {
    private List<Person> persons;
    private Context context;

    public ListAdapter(List<Person> persons, Context context) {
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

        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.friend_item,parent,false);

            holder = new ViewHolder();
            holder.avatar = (CircleImageView) convertView.findViewById(R.id.avatar);
            holder.txtName = (TextView) convertView.findViewById(R.id.txtName);
            holder.chatIcon = (ImageView) convertView.findViewById(R.id.chatIcon);

            convertView.setTag(holder);

        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.txtName.setText(person.getFullName());
        holder.chatIcon.setImageResource(R.drawable.ic_stat_chat);
        holder.avatar.setImageResource(R.drawable.avatar);

        holder.chatIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Clicked chat icon", Toast.LENGTH_SHORT).show();
            }
        });

        holder.avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle dataBundle = new Bundle();
                dataBundle.putString("ProfileOf", DBHelper.USER_FLAG_FRIENDS);
                dataBundle.putString("username", person.getEmail());
                Intent intent1 = new Intent(context,ProfileActivity.class);
                intent1.putExtras(dataBundle);
                context.startActivity(intent1);

            }
        });
        return convertView;
    }

    private class ViewHolder{
        public CircleImageView avatar;
        public TextView txtName;
        public ImageView chatIcon;
    }
}
