package com.example.thang.mobile_dating_app_v20.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.thang.mobile_dating_app_v20.Classes.DBHelper;
import com.example.thang.mobile_dating_app_v20.Classes.HeaderViewHolder;
import com.example.thang.mobile_dating_app_v20.Classes.Person;
import com.example.thang.mobile_dating_app_v20.R;
import com.tonicartos.superslim.GridSLM;
import com.tonicartos.superslim.LinearSLM;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Thang on 7/8/2015.
 */
public class NearbyAdapter extends RecyclerView.Adapter<HeaderViewHolder> {
    private static final int VIEW_TYPE_START = 0;
    private static final int VIEW_TYPE_HEADER = 1;
    private static final int VIEW_TYPE_CONTENT = 2;

    private final ArrayList<LineItem> mItems;
    private final Context mContext;

    public NearbyAdapter(Context context, List<Person> persons) {
        mContext = context;
        mItems = new ArrayList<>();
        //int sectionManager = -1;
        int headerCount = 1;
        int sectionFirstPosition = 0;

        //add start item as always
        String nearbyFriend = mContext.getResources().getString(R.string.nearby_start_friend, 5);
        String nearbyPeople = mContext.getResources().getString(R.string.nearby_start_people, 5);

        mItems.add(new LineItem(nearbyFriend, nearbyPeople, true,
                DBHelper.getInstance(context).getCurrentUser(),0));
        //sectionFirstPosition++;
        for (int i = 0; i < persons.size(); i++) {
            if (i == 0 || i == 1) {
                //sectionManager = (sectionManager + 1) % 2;
                sectionFirstPosition = i + headerCount;
                headerCount += 1;
                mItems.add(new LineItem("Your Friend", true, sectionFirstPosition));
            }
            mItems.add(new LineItem("You might known", false, sectionFirstPosition, persons.get(i)));

        }
    }

    public boolean isItemHeader(int position) {
        return mItems.get(position).isHeader;
    }

    @Override
    public HeaderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_START) {
            view = LayoutInflater.from(mContext)
                    .inflate(R.layout.nearby_start_item, parent, false);
            return new HeaderViewHolder(view, mContext, true);
        } else if (viewType == VIEW_TYPE_HEADER) {
            view = LayoutInflater.from(mContext)
                    .inflate(R.layout.header_demo, parent, false);
            return new HeaderViewHolder(view);
        } else {
            view = LayoutInflater.from(mContext)
                    .inflate(R.layout.nearby_item, parent, false);
            return new HeaderViewHolder(view, mContext);
        }
    }

    @Override
    public void onBindViewHolder(HeaderViewHolder holder, int position) {
        final LineItem item = mItems.get(position);
        final View itemView = holder.itemView;

        final GridSLM.LayoutParams lp = GridSLM.LayoutParams.from(itemView.getLayoutParams());
        if (item.isStart) {
            holder.bindStartItem(item.start_friend, item.start_people, item.person.getAvatar());
            lp.setSlm(LinearSLM.ID);

        }
        else if (item.isHeader) {
            holder.bindHeaderItem(item.text);
            lp.setSlm(GridSLM.ID);
        } else {
            holder.bindNearbyItem(item.person.getFullName(), item.person.getAvatar());
            lp.setSlm(GridSLM.ID);
        }

        lp.setColumnWidth(mContext.getResources().getDimensionPixelSize(R.dimen.grid_column_width));
        lp.setFirstPosition(item.sectionFirstPosition);
        itemView.setLayoutParams(lp);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (mItems.get(position).isStart) {
            return VIEW_TYPE_START;
        } else if (mItems.get(position).isHeader) {
            return VIEW_TYPE_HEADER;
        } else {
            return VIEW_TYPE_CONTENT;
        }
    }

    private static class LineItem {

        public boolean isStart;
        public int sectionFirstPosition;
        public boolean isHeader;
        public String text;
        public Person person;
        public String start_friend;
        public String start_people;

        public LineItem(String text, boolean isHeader, int sectionFirstPosition) {
            this.isHeader = isHeader;
            this.text = text;
            this.sectionFirstPosition = sectionFirstPosition;
            this.person = null;
            this.isStart = false;
        }

        public LineItem(String text, boolean isHeader, int sectionFirstPosition, Person person) {
            this.isHeader = isHeader;
            this.text = text;
            this.sectionFirstPosition = sectionFirstPosition;
            this.person = person;
            this.isStart = false;
        }

        public LineItem(String start_friend, String start_people, boolean isStart, Person person, int sectionFirstPosition) {
            this.person = person;
            this.isStart = isStart;
            this.start_friend = start_friend;
            this.start_people = start_people;
            this.sectionFirstPosition = sectionFirstPosition;
            this.isHeader = false;
        }

    }
}
