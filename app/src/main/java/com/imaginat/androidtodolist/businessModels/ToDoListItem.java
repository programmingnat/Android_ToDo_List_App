package com.imaginat.androidtodolist.businessModels;

/**
 * Created by nat on 5/1/16.
 */
public class ToDoListItem implements IListItem {

    private String mText;
    private String mItem_id;
    private boolean mHasAlarm;

    public ToDoListItem(){

    }
    public ToDoListItem(String text, String item_id, boolean hasAlarm) {
        mText = text;
        mItem_id = item_id;
        mHasAlarm = hasAlarm;
    }

    public boolean isHasAlarm() {
        return mHasAlarm;
    }

    public void setHasAlarm(boolean hasAlarm) {
        mHasAlarm = hasAlarm;
    }



    public String getItem_id() {
        return mItem_id;
    }

    public void setItem_id(String item_id) {
        mItem_id = item_id;
    }

    @Override
    public String getText() {
        return mText;
    }

    @Override
    public void setText(String s) {
        mText=s;
    }


}
