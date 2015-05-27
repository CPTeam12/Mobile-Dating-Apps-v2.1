package com.example.thang.mobile_dating_app_v20.Cards;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.thang.mobile_dating_app_v20.R;

import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.prototypes.SectionedCardAdapter;

/**
 * Created by Thang on 5/27/2015.
 */
public class SectionAdapter extends SectionedCardAdapter {
    public SectionAdapter(Context context, CardArrayAdapter cardArrayAdapter) {
        super(context, R.layout.card_custom_sectioned_list, cardArrayAdapter);
    }

    @Override
    protected View getSectionView(int position, View view, ViewGroup parent) {

        MyCardSection section = (MyCardSection)getCardSections().get(position);

        if(section != null){
            //set the title
            TextView textView = (TextView) view.findViewById(R.id.section_title);
            if(textView != null){
                textView.setText(section.getTitle());
            }
        }

        return view;
    }
}
