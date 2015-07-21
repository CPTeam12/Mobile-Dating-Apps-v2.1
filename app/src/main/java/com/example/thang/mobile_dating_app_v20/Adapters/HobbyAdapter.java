package com.example.thang.mobile_dating_app_v20.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.thang.mobile_dating_app_v20.Classes.Hobby;
import com.example.thang.mobile_dating_app_v20.Classes.SubHobby;
import com.example.thang.mobile_dating_app_v20.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Thang on 7/13/2015.
 */
public class HobbyAdapter extends BaseAdapter {
    public final static String FLAG_REGISTER = "register";
    public final static String FLAG_DETAIL = "detail";

    private List<Hobby> hobbies;
    private Context context;
    private String flag;

    public HobbyAdapter(Context context, List<Hobby> hobbies, String flag) {
        this.hobbies = hobbies;
        this.context = context;
        this.flag = flag;
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
            viewHolder.details = (TextView) view.findViewById(R.id.hobby_details);
            viewHolder.container = view.findViewById(R.id.container);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.title.setText(hobby);
        viewHolder.details.setVisibility(View.GONE);

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

        if (flag.equals(FLAG_DETAIL)) {
            viewHolder.expand.setImageResource(R.drawable.ic_moveup);
            viewHolder.expand.setVisibility(View.GONE);

            String details = "";
            for (SubHobby subHobby : hobbies.get(i).getSubHobby()) {
                if (subHobby.getIsSelected()) {
                    viewHolder.container.setVisibility(View.VISIBLE);
                    viewHolder.details.setVisibility(View.VISIBLE);
                    viewHolder.expand.setVisibility(View.VISIBLE);
                    details += subHobby.getName() + ", ";
                    //System.getProperty("line.separator");
                }
            }

            if (!details.isEmpty()) {
                viewHolder.details.setText(details.substring(0, details.length() - 2));
            } else {
                viewHolder.details.setText("-");
            }


            viewHolder.expand.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (viewHolder.details.getVisibility() == View.GONE) {
                        viewHolder.details.setVisibility(View.VISIBLE);
                        viewHolder.expand.setImageResource(R.drawable.ic_moveup);
                    } else {
                        viewHolder.details.setVisibility(View.GONE);
                        viewHolder.expand.setImageResource(R.drawable.ic_movedown);
                    }
                }
            });
        }
        return view;
    }

    class ViewHolder {
        TextView title;
        ImageView expand;
        CircleImageView image;
        TextView details;
        View container;

        public ViewHolder() {

        }
    }


}
