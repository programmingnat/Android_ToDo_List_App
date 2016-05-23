package com.imaginat.androidtodolist.businessModels;

import android.util.Log;

/**
 * Created by nat on 5/1/16.
 */
public class ToDoListItem implements IListItem {

    private String mText;
    private String mReminder_id;
    private String mList_id;
    private boolean isCalendarAlarm=false;
    private boolean isGeoFenceAlarm=false;
    private String mAlarmID;
    private int mReminderID,mAlarmDay,mAlarmMonth,mAlarmYear,mAlarmHour,mAlarmMin;
    private boolean isCalendarAlarmActive;



    public ToDoListItem(){

    }
    public ToDoListItem(String text, String item_id) {
        mText = text;
        mReminder_id = item_id;

    }

    public String getListId() {
        return mList_id;
    }

    public void setListId(String list_id) {
        mList_id = list_id;
    }

    public boolean isCalendarAlarm() {
        return isCalendarAlarm;
    }

    public boolean isCalendarAlarmActive() {
        return isCalendarAlarmActive;
    }

    public int getAlarmMonth() {
        return mAlarmMonth;
    }

    public int getAlarmYear() {
        return mAlarmYear;
    }

    public int getAlarmHour() {
        return mAlarmHour;
    }

    public int getAlarmMin() {
        return mAlarmMin;
    }

    public int getAlarmDay() {
        return mAlarmDay;
    }

    public void setIsCalendarAlarm(boolean isCalendarAlarm) {
        this.isCalendarAlarm = isCalendarAlarm;
    }

    public void setCalendarAlarmInfo(String alarmID,int reminderID,int alarmDay,int alarmMonth,int alarmYear,int alarmHour,int alarmMinutes,int isActive){
        mAlarmID=alarmID;
        mReminderID=reminderID;
        mAlarmDay =alarmDay;
        mAlarmMonth=alarmMonth;
        mAlarmYear =alarmYear;
        mAlarmHour=alarmHour;
        mAlarmMin=alarmMinutes;
        isCalendarAlarmActive=isActive==1?true:false;
        setIsCalendarAlarm(true);
        Log.d("ToDoListItemManager","set mAlarmID:"+alarmID+" mReminderID:"+mReminderID+" mAlarmDay:"+mAlarmDay+" mAlarmMonth:"+mAlarmMonth+" mAlarmYear"+mAlarmYear);
        Log.d("ToDoListItemManager","alarmYear:"+alarmYear+" alarmHour"+alarmHour+" alarmYear"+alarmYear);
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
