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

    private final ArrayList<LineItem> lineItems;
    private final Context context;

    public NearbyAdapter(Context context, List<Person> persons) {
        this.context = context;
        lineItems = new ArrayList<>();
        //int sectionManager = -1;
        int headerCount = 1;
        int sectionFirstPosition = 0;
        int totalCount = 0;
        int friendCount = 0;
        int peopleCount = 0;

        //add start item as always
        lineItems.add(new LineItem("", "", true,
                DBHelper.getInstance(context).getCurrentUser(), 0));
        //sectionFirstPosition++;
        String currentHeader = "";

        //for friend
        for (int i = 0; i < persons.size(); i++) {
            Person person = DBHelper.getInstance(context).getPersonByEmail(persons.get(i).getEmail());
            if (person.getEmail() != null) {
                //is friend
                if (!currentHeader.equals(context.getResources().getString(R.string.nearby_friend))) {
                    //add header
                    currentHeader = context.getResources().getString(R.string.nearby_friend);
                    sectionFirstPosition = totalCount + headerCount;
                    headerCount += 1;
                    lineItems.add(new LineItem(currentHeader, true, sectionFirstPosition));
                }
                //add content
                lineItems.add(new LineItem("", false, sectionFirstPosition, persons.get(i)));
                totalCount++;
                friendCount++;
            }
        }

        String nearbyFriend = this.context.getResources().getString(R.string.nearby_start_friend, friendCount);
        lineItems.get(0).start_friend = nearbyFriend;

        //not friend
        for (int i = 0; i < persons.size(); i++) {
            Person person = DBHelper.getInstance(context).getPersonByEmail(persons.get(i).getEmail());
            if (person.getEmail() == null) {
                //is friend
                if (!currentHeader.equals(context.getResources().getString(R.string.nearby_people))) {
                    //add header
                    currentHeader = context.getResources().getString(R.string.nearby_people);
                    sectionFirstPosition = totalCount + headerCount;
                    headerCount += 1;
                    lineItems.add(new LineItem(currentHeader, true, sectionFirstPosition));
                }
                //add content
                lineItems.add(new LineItem("", false, sectionFirstPosition, persons.get(i)));
                totalCount++;
                peopleCount++;
            }
        }

        String nearbyPeople = this.context.getResources().getString(R.string.nearby_start_people, peopleCount);
        lineItems.get(0).start_people = nearbyPeople;
    }

    public boolean isItemHeader(int position) {
        return lineItems.get(position).isHeader;
    }

    @Override
    public HeaderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_START) {
            view = LayoutInflater.from(context)
                    .inflate(R.layout.nearby_start_item, parent, false);
            return new HeaderViewHolder(view, context, true);
        } else if (viewType == VIEW_TYPE_HEADER) {
            view = LayoutInflater.from(context)
                    .inflate(R.layout.header_item, parent, false);
            return new HeaderViewHolder(view);
        } else {
            view = LayoutInflater.from(context)
                    .inflate(R.layout.nearby_item, parent, false);
            return new HeaderViewHolder(view, context);
        }
    }

    @Override
    public void onBindViewHolder(HeaderViewHolder holder, int position) {
        final LineItem item = lineItems.get(position);
        final View itemView = holder.itemView;

        final GridSLM.LayoutParams lp = GridSLM.LayoutParams.from(itemView.getLayoutParams());
        if (item.isStart) {
            holder.bindStartItem(item.start_friend, item.start_people, item.person.getAvatar());
            lp.setSlm(LinearSLM.ID);

        } else if (item.isHeader) {
            holder.bindHeaderItem(item.text);
            lp.setSlm(GridSLM.ID);
        } else {
            holder.bindNearbyItem(item.person);
            lp.setSlm(GridSLM.ID);
        }

        lp.setColumnWidth(context.getResources().getDimensionPixelSize(R.dimen.grid_column_width));
        lp.setFirstPosition(item.sectionFirstPosition);
        itemView.setLayoutParams(lp);
    }

    @Override
    public int getItemCount() {
        return lineItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (lineItems.get(position).isStart) {
            return VIEW_TYPE_START;
        } else if (lineItems.get(position).isHeader) {
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
