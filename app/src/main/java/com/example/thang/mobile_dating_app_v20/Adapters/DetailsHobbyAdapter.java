package com.example.thang.mobile_dating_app_v20.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.thang.mobile_dating_app_v20.Classes.SubHobby;
import com.example.thang.mobile_dating_app_v20.R;

import java.util.List;

/**
 * Created by Thang on 7/17/2015.
 */
public class DetailsHobbyAdapter extends BaseAdapter {
    Context context;
    List<SubHobby> subHobbies;

    public DetailsHobbyAdapter(Context context, List<SubHobby> subHobbies) {
        this.context = context;
        this.subHobbies = subHobbies;
    }

    @Override
    public int getCount() {
        return subHobbies.size();
    }

    @Override
    public Object getItem(int position) {
        return subHobbies.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SubHobby subHobby = subHobbies.get(position);

        LayoutInflater inflater = LayoutInflater.from(context);
        convertView = inflater.inflate(R.layout.sub_hobby_detail_item, parent, false);
        TextView name = (TextView) convertView.findViewById(R.id.name);
        ImageView bullet = (ImageView) convertView.findViewById(R.id.bullet);
        name.setText(subHobby.getName());
//        if (subHobby.getIsSelected()) {
//            name.setText(subHobby.getName());
//        }else{
//            name.setVisibility(View.GONE);
//            bullet.setVisibility(View.GONE);
//        }

        return convertView;
    }
}
