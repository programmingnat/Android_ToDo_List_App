package com.imaginat.androidtodolist.businessModels;

/**
 * Created by nat on 5/1/16.
 */
public class ToDoListItem implements IListItem {

    private String mText;
    private String mReminder_id;
    private boolean isCalendarAlarm=false;
    private boolean isGeoFenceAlarm=false;



    public ToDoListItem(){

    }
    public ToDoListItem(String text, String item_id) {
        mText = text;
        mReminder_id = item_id;

    }

    public boolean isCalendarAlarm() {
        return isCalendarAlarm;
    }

    public void setIsCalendarAlarm(boolean isCalendarAlarm) {
        this.isCalendarAlarm = isCalendarAlarm;
    }

    public boolean isGeoFenceAlarm() {
        return isGeoFenceAlarm;
    }

    public void setIsGeoFenceAlarm(boolean isGeoFenceAlarm) {
        this.isGeoFenceAlarm = isGeoFenceAlarm;
    }

    public String getReminder_id() {
        return mReminder_id;
    }

    public void setReminder_id(String reminder_id) {
        mReminder_id = reminder_id;
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
