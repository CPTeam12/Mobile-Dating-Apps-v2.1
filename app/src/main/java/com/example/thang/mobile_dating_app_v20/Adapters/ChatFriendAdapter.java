package com.example.thang.mobile_dating_app_v20.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.thang.mobile_dating_app_v20.Activity.ChatActivity;
import com.example.thang.mobile_dating_app_v20.Classes.Message;
import com.example.thang.mobile_dating_app_v20.Classes.Utils;
import com.example.thang.mobile_dating_app_v20.R;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Thang on 7/4/2015.
 */
public class ChatFriendAdapter extends BaseAdapter {
    private List<Message> messages = new ArrayList<>();
    private Context context;

    public ChatFriendAdapter(List<Message> messages, Context context) {
        this.messages = messages;
        this.context = context;
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Object getItem(int i) {
        return messages.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ViewHolder holder;
        final Message message = messages.get(i);

        LayoutInflater inflater = LayoutInflater.from(context);
        if (message.getItem().equals(Message.CHAT_FRIEND_ITEM)) {
            convertView = inflater.inflate(R.layout.chat_friend_item, viewGroup, false);
        } else {
            convertView = inflater.inflate(R.layout.chat_me_item, viewGroup, false);
        }

        holder = new ViewHolder();
        holder.avatar = (CircleImageView) convertView.findViewById(R.id.avatar);
        holder.txtMessage = (TextView) convertView.findViewById(R.id.message);

        convertView.setTag(holder);

        holder.txtMessage.setText(message.getMessage());
        holder.avatar.setImageBitmap(message.getAvatar().isEmpty() ? BitmapFactory.decodeResource(context.getResources(),
                R.drawable.no_avatar) : Utils.decodeBase64StringToBitmap(message.getAvatar()));

        return convertView;
    }

    private class ViewHolder {
        public CircleImageView avatar;
        public TextView txtMessage;
    }
}
