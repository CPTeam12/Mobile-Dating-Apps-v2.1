package com.example.thang.mobile_dating_app_v20.Cards;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.thang.mobile_dating_app_v20.R;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardThumbnail;

/**
 * Created by Thang on 5/26/2015.
 */
public class CardPerson extends Card {
    protected TextView mTitle;
    protected TextView mSecondaryTitle;
    //    protected ImageView mImageView;
    protected int resourceIdThumbnail;
//    protected int count;

    protected String title;
    protected String secondaryTitle;
//    protected int imgSource;

    public CardPerson(Context context) {
        this(context, R.layout.card_customer_inner_layout);
    }

    public CardPerson(Context context, int innerLayout) {
        super(context, innerLayout);
    }

    public void init() {
        //Add thumbnail
        CardThumbnail cardThumbnail = new CardThumbnail(mContext);

        if (resourceIdThumbnail == 0)
            cardThumbnail.setDrawableResource(R.drawable.no_avatar);
        else {
            cardThumbnail.setDrawableResource(resourceIdThumbnail);
        }

        addCardThumbnail(cardThumbnail);

//        //Only for test, some cards have different clickListeners
//        if (count == 12) {
//
//            setTitle(title + " No Click");
//            setClickable(false);
//
//        } else if (count == 20) {
//
//            setTitle(title + " Partial Click");
//            addPartialOnClickListener(Card.CLICK_LISTENER_CONTENT_VIEW, new OnCardClickListener() {
//                @Override
//                public void onClick(Card card, View view) {
//                    Toast.makeText(getContext(), "Partial click Listener card=" + title, Toast.LENGTH_SHORT).show();
//                }
//            });
//
//        } else {
//
//            //Add ClickListener
//            setOnClickListener(new OnCardClickListener() {
//                @Override
//                public void onClick(Card card, View view) {
//                    Toast.makeText(getContext(), "Click Listener card=" + title, Toast.LENGTH_SHORT).show();
//                }
//            });
//
//        }
//
//        //Swipe
//        if (count > 5 && count < 13) {
//
//            setTitle(title + " Swipe enabled");
        setSwipeable(true);
        setOnSwipeListener(new OnSwipeListener() {
            @Override
            public void onSwipe(Card card) {
                Toast.makeText(getContext(), "Removed card=" + title, Toast.LENGTH_SHORT).show();
            }
        });
//        }

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

//        if (mImageView != null)
//            mImageView.setImageResource(imgSource);

    }

//    public int getImgSource() {
//        return imgSource;
//    }
//
//    public void setImgSource(int imgSource) {
//        this.imgSource = imgSource;
//    }

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

//    public int getCount() {
//        return count;
//    }
//
//    public void setCount(int count) {
//        this.count = count;
//    }
}
