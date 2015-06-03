package com.example.thang.mobile_dating_app_v20.Adapters;

import android.content.Context;
import android.graphics.Point;
import android.support.v7.graphics.Palette;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.example.thang.mobile_dating_app_v20.Classes.Person;
import com.example.thang.mobile_dating_app_v20.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Thang on 5/17/2015.
 */
public class GribAdapter extends BaseAdapter {
    private List<Person> persons;
    private Context context;
    private final int column;
    private boolean usePalette = true;

    public GribAdapter(Context context, List<Person> persons, int column) {
        super();
        this.persons = persons;
        this.context = context;
        this.column = column;
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
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.abc_fade_in);
        Person person = persons.get(position);

        WindowManager wm = (WindowManager) context.getSystemService(context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int imageWidth = (width / column);

        final GribHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.nearby_item, parent, false);
            holder = new GribHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (GribHolder) convertView.getTag();
        }

        holder.name.setText(person.getFullName());
        holder.avatar.startAnimation(animation);
        holder.avatar.setImageDrawable(context.getResources().getDrawable(R.drawable.avatar));



        return convertView;
    }

    class GribHolder {
        final ImageView avatar;
        final TextView name;
        final ProgressBar progressBar;
        final LinearLayout linearLayout;
        final MaterialRippleLayout content;

        public GribHolder(View v) {
            avatar = (ImageView) v.findViewById(R.id.avatar);
            name = (TextView) v.findViewById(R.id.name);
            progressBar = (ProgressBar) v.findViewById(R.id.progress);
            linearLayout = (LinearLayout) v.findViewById(R.id.titlebg);
            content = (MaterialRippleLayout) v.findViewById(R.id.walls_ripple);
        }
    }
}
