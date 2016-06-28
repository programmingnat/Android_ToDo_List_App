package com.imaginat.androidtodolist.models;

/**
 * Created by nat on 4/15/16.
 */
//can be used to display search results

public class AListItem implements IListItem {
    private String mText;

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText = text;
    }


}
