package com.example.thang.mobile_dating_app_v20.Adapters;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.thang.mobile_dating_app_v20.Classes.ChatItem;
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
    private List<ChatItem> chatItems = new ArrayList<>();

    private Context context;

    public ChatFriendAdapter(List<ChatItem> chatItems, Context context) {
        this.chatItems = chatItems;
        this.context = context;
    }

    @Override
    public int getCount() {
        return chatItems.size();
    }

    @Override
    public Object getItem(int i) {
        return chatItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {

        ViewHolder holder;
        //final Message message = messages.get(i);
        final ChatItem chatItem = chatItems.get(i);

        LayoutInflater inflater = LayoutInflater.from(context);
        if (chatItem.isDivider()){
            convertView = inflater.inflate(R.layout.chat_divider, viewGroup, false);

            holder = new ViewHolder();
            holder.time = (TextView) convertView.findViewById(R.id.time_divider);

            convertView.setTag(holder);
            holder.time.setText(chatItem.getTime());

        }else{
            Message message = chatItem.getMessage();
            if (message.getItem().equals(Message.CHAT_FRIEND_ITEM)) {
                convertView = inflater.inflate(R.layout.chat_friend_item, viewGroup, false);
            } else {
                convertView = inflater.inflate(R.layout.chat_me_item, viewGroup, false);
            }

            holder = new ViewHolder();
            holder.avatar = (CircleImageView) convertView.findViewById(R.id.avatar);
            holder.txtMessage = (TextView) convertView.findViewById(R.id.message);
            holder.time = (TextView) convertView.findViewById(R.id.chat_time);

            convertView.setTag(holder);

            holder.txtMessage.setText(message.getMessage());
            holder.avatar.setImageBitmap(message.getAvatar().isEmpty() ? BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.no_avatar) : Utils.generateSmaleBitmap(message.getAvatar(),50,50));
            holder.time.setText(chatItem.getTime());

        }

        return convertView;
    }

    private class ViewHolder {
        public CircleImageView avatar;
        public TextView txtMessage;
        public TextView time;
    }


}
