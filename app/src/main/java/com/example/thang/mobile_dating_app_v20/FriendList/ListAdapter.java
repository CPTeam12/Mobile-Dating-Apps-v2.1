package com.example.thang.mobile_dating_app_v20.FriendList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.thang.mobile_dating_app_v20.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Thang on 5/16/2015.
 */
public class ListAdapter extends BaseAdapter {
    private List<Friend> friends;
    private Context context;

    public ListAdapter(List<Friend> friendList, Context context) {
        this.friends = friendList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return friends.size();
    }

    @Override
    public Object getItem(int position) {
        return friends.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        Friend friend = friends.get(position);

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

        holder.txtName.setText(friend.getName());
        holder.chatIcon.setImageResource(R.drawable.ic_stat_chat);
        holder.avatar.setImageResource(R.drawable.avatar);

        holder.chatIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Clicked chat icon", Toast.LENGTH_SHORT).show();
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
