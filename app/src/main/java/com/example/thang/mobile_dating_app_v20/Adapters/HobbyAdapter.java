package com.example.thang.mobile_dating_app_v20.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.thang.mobile_dating_app_v20.Classes.Hobby;
import com.example.thang.mobile_dating_app_v20.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Thang on 7/13/2015.
 */
public class HobbyAdapter extends BaseAdapter {
    private List<Hobby> hobbies;
    private Context context;

    public HobbyAdapter(Context context, List<Hobby> hobbies) {
        this.hobbies = hobbies;
        this.context = context;
    }

    @Override
    public int getCount() {
        return hobbies.size();
    }

    @Override
    public Object getItem(int i) {
        return hobbies.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final String hobby = hobbies.get(i).getName();
        final ViewHolder viewHolder;

        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.hobby_item, viewGroup, false);
            viewHolder = new ViewHolder();
            viewHolder.image = (CircleImageView) view.findViewById(R.id.picture);
            viewHolder.title = (TextView) view.findViewById(R.id.title);
            viewHolder.expand = (ImageView) view.findViewById(R.id.expand);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.title.setText(hobby);

        List<String> subHobby = new ArrayList<>();
        if (hobby.equals("Âm nhạc")) {
            viewHolder.image.setImageResource(R.drawable.music);
        } else if (hobby.equals("Phim ảnh")) {
            viewHolder.image.setImageResource(R.drawable.movie);
        } else if (hobby.equals("Sách")) {
            viewHolder.image.setImageResource(R.drawable.book);
        } else if (hobby.equals("Thể thao")) {
            viewHolder.image.setImageResource(R.drawable.sport);
        } else if (hobby.equals("Thức ăn")) {
            viewHolder.image.setImageResource(R.drawable.food);
        } else if (hobby.equals("Thú cưng")) {
            viewHolder.image.setImageResource(R.drawable.pet);
        } else {
            viewHolder.image.setImageResource(R.drawable.drink);
        }

//        final SubHobbyAdapter gribAdapter = new SubHobbyAdapter(context, subHobby);
//        viewHolder.subHobby.setAdapter(gribAdapter);
//        viewHolder.expand.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (viewHolder.subHobby.getVisibility() == View.GONE) {
//                    viewHolder.subHobby.setVisibility(View.VISIBLE);
//                    viewHolder.expand.setImageResource(R.drawable.ic_close);
//                } else {
//                    viewHolder.subHobby.setVisibility(View.GONE);
//                    viewHolder.expand.setImageResource(R.drawable.ic_add);
//                }
//            }
//        });

//        final List<String> finalSubHobby = subHobby;
//        viewHolder.subHobby.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Toast.makeText(context, finalSubHobby.get(i),Toast.LENGTH_SHORT).show();
//                ImageView selected = (ImageView) view.findViewById(R.id.selected);
//                gribAdapter.setTemp(i);
//                selected.setVisibility(View.VISIBLE);
//                selected.setTag(View.VISIBLE);
//            }
//        });

        return view;
    }

    class ViewHolder {
        TextView title;
        ImageView expand;
        CircleImageView image;

        public ViewHolder() {

        }
    }


}
