package com.imaginat.androidtodolist.businessModels;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by nat on 5/1/16.
 */
public class ToDoListItem implements IListItem {

    private String mText;
    private String mReminder_id;
    private String mList_id;
    private boolean isCalendarAlarm=false;
    private boolean isGeoFenceAlarm=false;
    private boolean isCompleted =false;

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    private String mAlarmID;
    private int mReminderID,mAlarmDay,mAlarmMonth,mAlarmYear,mAlarmHour,mAlarmMin;
    private boolean isCalendarAlarmActive;

    private String mGeoFenceAlarm_id;
    private String mStreet, mCity, mState, mZip,mLatitude,mLongitude, mAlarmTag,mMeterRadius;

    public boolean isGeoAlarmActive() {
        return isGeoAlarmActive;
    }

    public void setGeoAlarmActive(boolean geoAlarmActive) {
        isGeoAlarmActive = geoAlarmActive;
    }

    public String getGeoFenceAlarm_id() {
        return mGeoFenceAlarm_id;
    }

    public void setGeoFenceAlarm_id(String geoFenceAlarm_id) {
        mGeoFenceAlarm_id = geoFenceAlarm_id;
    }

    public String getStreet() {
        return mStreet;
    }

    public void setStreet(String street) {
        mStreet = street;
    }

    public String getCity() {
        return mCity;
    }

    public void setCity(String city) {
        mCity = city;
    }

    public String getState() {
        return mState;
    }

    public void setState(String state) {
        mState = state;
    }

    public String getZip() {
        return mZip;
    }

    public void setZip(String zip) {
        mZip = zip;
    }

    public String getLatitude() {
        return mLatitude;
    }

    public void setLatitude(String latitude) {
        mLatitude = latitude;
    }

    public String getLongitude() {
        return mLongitude;
    }

    public void setLongitude(String longitude) {
        mLongitude = longitude;
    }

    public String getAlarmTag() {
        return mAlarmTag;
    }

    public void setAlarmTag(String alarmTag) {
        mAlarmTag = alarmTag;
    }

    public String getMeterRadius() {
        return mMeterRadius;
    }

    public void setMeterRadius(String meterRadius) {
        mMeterRadius = meterRadius;
    }

    private boolean isGeoAlarmActive;

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


    public JSONObject toJSON() throws JSONException{

        JSONObject todoListItem_json = new JSONObject();
        todoListItem_json.put("text",mText);
        todoListItem_json.put("reminderID",mReminder_id);
        todoListItem_json.put("listID",mList_id);
        //todoListItem_json.put("isCompleted", isCompleted);
/*
        if(isCalendarAlarm()) {
            todoListItem_json.put("isCalendarAlarm", isCalendarAlarm);
            todoListItem_json.put("isGeoFenceAlarm", isGeoFenceAlarm);
            todoListItem_json.put("alarmReminderID", mReminderID);
            todoListItem_json.put("alarmDay", mAlarmDay);
            todoListItem_json.put("alarmMonth", mAlarmMonth);
            todoListItem_json.put("alarmYear", mAlarmYear);
            todoListItem_json.put("alarmHour", mAlarmHour);
            todoListItem_json.put("alarmMin", mAlarmMin);
            todoListItem_json.put("isCalendarAlarmActive", isCalendarAlarmActive);
        }
        if(isGeoFenceAlarm) {
            todoListItem_json.put("geoFenceAlarm", mGeoFenceAlarm_id);
            todoListItem_json.put("street", mStreet);
            todoListItem_json.put("city", mCity);
            todoListItem_json.put("state", mState);
            todoListItem_json.put("zip", mZip);
            todoListItem_json.put("latitude", mLatitude);
            todoListItem_json.put("longitude", mLongitude);
            todoListItem_json.put("geoAlarmTag", mAlarmTag);
            todoListItem_json.put("radius", mMeterRadius);
        }

*/
        return todoListItem_json;
    }
}
