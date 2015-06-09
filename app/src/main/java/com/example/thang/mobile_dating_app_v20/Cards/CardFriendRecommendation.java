package com.example.thang.mobile_dating_app_v20.Cards;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.thang.mobile_dating_app_v20.R;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardThumbnail;

/**
 * Created by Thang on 6/9/2015.
 */
public class CardFriendRecommendation extends Card {
    protected TextView mTitle;
    protected TextView mSecondaryTitle;
    //    protected ImageView mImageView;
    protected int resourceIdThumbnail;
//    protected int count;

    protected String title;
    protected String secondaryTitle;
    //    protected int imgSource;

    public CardFriendRecommendation(Context context) {
        super(context, R.layout.card_friendrecommendation_inner_layout);
    }

    public CardFriendRecommendation(Context context, int innerLayout) {
        super(context, innerLayout);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSecondaryTitle() {
        return secondaryTitle;
    }

    public void setSecondaryTitle(String secondaryTitle) {
        this.secondaryTitle = secondaryTitle;
    }

    public int getResourceIdThumbnail() {
        return resourceIdThumbnail;
    }

    public void setResourceIdThumbnail(int resourceIdThumbnail) {
        this.resourceIdThumbnail = resourceIdThumbnail;
    }

    public void init() {
        //remove shadow
        setShadow(false);
        //Add thumbnail
        CardThumbnail cardThumbnail = new CardThumbnail(mContext);

        if (resourceIdThumbnail == 0)
            cardThumbnail.setDrawableResource(R.drawable.no_avatar);
        else {
            cardThumbnail.setDrawableResource(resourceIdThumbnail);
        }

        addCardThumbnail(cardThumbnail);
        setSwipeable(false);
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {
        //Retrieve elements
        mTitle = (TextView) parent.findViewById(R.id.carddemo_myapps_main_inner_title);
        mSecondaryTitle = (TextView) parent.findViewById(R.id.carddemo_myapps_main_inner_secondaryTitle);
        //mImageView = (ImageView)parent.findViewById(R.id.card_avatar);

        if (mTitle != null)
            mTitle.setText(title);

        if (mSecondaryTitle != null)
            mSecondaryTitle.setText(secondaryTitle);
    }
}
