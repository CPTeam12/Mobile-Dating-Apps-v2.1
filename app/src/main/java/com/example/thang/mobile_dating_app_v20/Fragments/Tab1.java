package com.example.thang.mobile_dating_app_v20.Fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.thang.mobile_dating_app_v20.Cards.CardPerson;
import com.example.thang.mobile_dating_app_v20.Cards.MyCardSection;
import com.example.thang.mobile_dating_app_v20.Cards.SectionAdapter;
import com.example.thang.mobile_dating_app_v20.Classes.Friend;
import com.example.thang.mobile_dating_app_v20.R;

import java.util.ArrayList;
import java.util.List;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.view.CardListView;

/**
 * Created by Thang on 5/15/2015.
 */
public class Tab1 extends Fragment {
    private static final int DEFAULT_COLUMNS_PORTRAIT = 2;
    private static final int DEFAULT_COLUMNS_LANDSCAPE = 3;
    public static final String NAME = "name";
    public static final String WALL = "wall";

    private List<Friend> friends = new ArrayList<>();
    private int numColumns = 2;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab_1, container, false);

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initCards();
    }

    private void initCards() {

        //Init an array of Cards
        ArrayList<Card> cards = new ArrayList<Card>();
        for (int i = 0; i < 10; i++) {
            CardPerson card = new CardPerson(this.getActivity());
            card.setTitle("Person name " + i);
            card.setSecondaryTitle("Description...");
//            card.setImgSource(R.drawable.avatar);
//            card.setCount(i);
            if (i == 5 || i == 6){
                card.setResourceIdThumbnail(R.drawable.avatar);
            }
            card.init();
            cards.add(card);
        }

        CardArrayAdapter mCardArrayAdapter = new CardArrayAdapter(getActivity(), cards);
        //define card section
        List<MyCardSection> sections = new ArrayList<>();
        sections.add(new MyCardSection(0,getResources().getString(R.string.card_section_request)));
        sections.add(new MyCardSection(3,getResources().getString(R.string.card_section_recommend)));

        MyCardSection[] myCardSections = new MyCardSection[sections.size()];

        //define section adapter
        SectionAdapter sectionAdapter =  new SectionAdapter(getActivity(), mCardArrayAdapter);
        sectionAdapter.setCardSections(sections.toArray(myCardSections));

        CardListView listView = (CardListView) getActivity().findViewById(R.id.myList);
        if (listView != null) {
            listView.setExternalAdapter(sectionAdapter,mCardArrayAdapter);
        }
    }

}
