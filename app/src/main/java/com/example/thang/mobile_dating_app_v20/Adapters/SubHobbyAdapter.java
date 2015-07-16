package com.example.thang.mobile_dating_app_v20.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.thang.mobile_dating_app_v20.Classes.SubHobby;
import com.example.thang.mobile_dating_app_v20.R;

import java.util.List;

/**
 * Created by Thang on 5/17/2015.
 */
public class SubHobbyAdapter extends BaseAdapter {
    private List<SubHobby> subHobbies;
    private Context context;

    public SubHobbyAdapter(Context context, List<SubHobby> subHobby) {
        super();
        this.subHobbies = subHobby;
        this.context = context;
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
        final SubHobby subHobby = subHobbies.get(position);
        final GribHolder holder;

        LayoutInflater inflater = LayoutInflater.from(context);
        convertView = inflater.inflate(R.layout.subhobby_item, parent, false);

        holder = new GribHolder();
        holder.imageContent = (ImageView) convertView.findViewById(R.id.picture);
        holder.name = (TextView) convertView.findViewById(R.id.name);
        holder.selected = (ImageView) convertView.findViewById(R.id.selected);

        if (subHobby.getIsSelected()) {
            holder.selected.setVisibility(View.VISIBLE);
            holder.name.setVisibility(View.GONE);
        } else {
            holder.selected.setVisibility(View.GONE);
            holder.name.setVisibility(View.VISIBLE);
        }

        holder.hobbyItem = (FrameLayout) convertView.findViewById(R.id.hobby_item);
        holder.name.setText(subHobby.getName());

        return convertView;
    }

    class GribHolder {
        ImageView imageContent;
        TextView name;
        ImageView selected;
        FrameLayout hobbyItem;

        public GribHolder() {

        }
    }
}
