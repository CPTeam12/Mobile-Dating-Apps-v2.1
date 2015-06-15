package com.example.thang.mobile_dating_app_v20.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.thang.mobile_dating_app_v20.Activity.SearchActivity;
import com.example.thang.mobile_dating_app_v20.R;

import java.util.List;

/**
 * Created by Thang on 6/14/2015.
 */
public class ExampleAdapter extends CursorAdapter {
    private List<String> items;
    private TextView text;

    public ExampleAdapter(Context context, Cursor cursor, List<String> items) {
        super(context,cursor,false);
        this.items = items;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.search_item, parent, false);

        text = (TextView) view.findViewById(R.id.search_name);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        text.setText(items.get(cursor.getPosition()));
    }
}
