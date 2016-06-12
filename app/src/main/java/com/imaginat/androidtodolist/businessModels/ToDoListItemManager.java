package com.imaginat.androidtodolist.businessModels;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.imaginat.androidtodolist.data.DbSchema;
import com.imaginat.androidtodolist.data.ToDoListSQLHelper;
import com.imaginat.androidtodolist.google.FenceData;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by nat on 5/12/16.
 */
public class ToDoListItemManager {

    private static final String TAG = ListManager.class.getSimpleName();
    public static ToDoListItemManager instance;
    private ToDoListSQLHelper mSqlHelper;
    private ArrayList<ToDoListItem> mReminders;
    private boolean mHideCompleted=true;

    private ToDoListItemManager(Context context) {
        mSqlHelper = ToDoListSQLHelper.getInstance(context);
        mReminders = new ArrayList<ToDoListItem>();

        for (int i = 0; i < 10; i++) {
            ToDoListItem r = new ToDoListItem();
            r.setText("To do list item " + i);
            mReminders.add(r);

        }
        mReminders.add(null);

    }

    public static ToDoListItemManager getInstance(Context context) {
        if (instance == null) {
            instance = new ToDoListItemManager(context);
        }

        return instance;
    }

    public String getListName(String id){
       return mSqlHelper.getListName(id);
    }


    public void setHideCompleted(boolean v){
        mHideCompleted=v;
    }
    public boolean getHideCompleted(){
        return mHideCompleted;
    }
    public ArrayList<String>getRemindersByListTitle(String s){
        Cursor c = mSqlHelper.getListIDByTitle(s);
        c.moveToFirst();
        String listID=null;
        while(c.isAfterLast()==false){
           listID= c.getString(0);
            c.moveToNext();
        }
        //String listID=c.getString(0);
       return getRemindersByListID(listID);
    }
    public ArrayList<String>getRemindersByListID(String listID){

        ArrayList<String>listItems = new ArrayList<>();
        Cursor c = mSqlHelper.getAllReminderForThisList(listID,mHideCompleted);

        c.moveToFirst();
        while (c.isAfterLast() == false) {
            int colIndex = c.getColumnIndex(DbSchema.reminders_table.cols.REMINDER_TEXT);
            String text = c.getString(colIndex);

            Log.d(TAG,"Adding "+text);
            listItems.add(text);
            c.moveToNext();
        }
        return listItems;
    }
    public ArrayList<String> getListTitles(){
        Cursor c =mSqlHelper.getAllListNames();
        c.moveToFirst();
        ArrayList<String>al = new ArrayList<>();
        while(c.isAfterLast()==false){
            al.add(c.getString(c.getColumnIndex(DbSchema.lists_table.cols.LIST_TITLE)));
            c.moveToNext();
        }
        return al;
    }

    public ArrayList<ToDoListItem> getReminders() {
        return mReminders;
    }

    public ArrayList<ToDoListItem>findRemindersBasedOnQuery(String searchQuery){
       Cursor c= mSqlHelper.searchReminders(searchQuery);
        ArrayList<ToDoListItem>results = new ArrayList<>();
        c.moveToFirst();

        Log.d(TAG,"Result of findRemindersBasedOnQuery is "+c.getCount());
        while(c.isAfterLast()==false){

            int colIndex = c.getColumnIndex(DbSchema.reminders_table.cols.REMINDER_ID);
            String reminder_id = c.getString(colIndex);
            colIndex = c.getColumnIndex(DbSchema.reminders_table.cols.REMINDER_TEXT);
            String text = c.getString(colIndex);
            Log.d(TAG,"found"+text);
            colIndex=c.getColumnIndex(DbSchema.reminders_table.cols.LIST_ID);
            String listID = c.getString(colIndex);
            ToDoListItem listItem = new ToDoListItem(text, reminder_id);
            listItem.setListId(listID);
            colIndex=c.getColumnIndex(DbSchema.reminders_table.cols.IS_COMPLETED);
            int isCompleted = c.getInt(colIndex);
            listItem.setCompleted(isCompleted==1?true:false);
            mReminders.add(listItem);
            c.moveToNext();
        }
        return results;
    }
    public void createNewReminder(String listId, String text) {


        //add it
        mSqlHelper.insertReminderIntoList(listId, text);
        loadAllRemindersForList(listId);


    }

    public void deleteReminder(String listId, String reminderId) {
        mSqlHelper.deleteReminder(listId, reminderId);
        loadAllRemindersForList(listId);
    }

    public void updateReminder(String listId, String reminderID, String text) {
        Log.d(TAG,"UPDATE todolistManager update manager with "+listId+" "+reminderID+" "+text);
        mSqlHelper.updateReminder(listId, reminderID, text);
        loadAllRemindersForList(listId);
    }

    public void markAsCompleted(String listId,String reminderID,String value){
        mSqlHelper.updateCheckMark(listId,reminderID,value.equals("CHECKED")?1:0);
    }
    public ArrayList<ToDoListItem>getAllRemindersForList(String listID,boolean hideCompleted){
        ArrayList<ToDoListItem>todoList = new ArrayList<>();
        fillArrayListWithRemindersForList(listID,hideCompleted,todoList);
        return todoList;
    }
    public void loadAllRemindersForList(String listID) {
        fillArrayListWithRemindersForList(listID,mHideCompleted,mReminders);
    }

    private void fillArrayListWithRemindersForList(String listID,boolean hideCompleted, ArrayList<ToDoListItem> toDoListToFill){
        Cursor c = mSqlHelper.getAllReminderForThisList(listID,hideCompleted);
        toDoListToFill.clear();
        c.moveToFirst();
        while (c.isAfterLast() == false) {
            int colIndex = c.getColumnIndex(DbSchema.reminders_table.cols.REMINDER_ID);
            String reminder_id = c.getString(colIndex);
            colIndex = c.getColumnIndex(DbSchema.reminders_table.cols.REMINDER_TEXT);
            String text = c.getString(colIndex);
            ToDoListItem listItem = new ToDoListItem(text, reminder_id);
            listItem.setListId(listID);
            colIndex=c.getColumnIndex(DbSchema.reminders_table.cols.IS_COMPLETED);
            int isCompleted = c.getInt(colIndex);
            listItem.setCompleted(isCompleted==1?true:false);
            toDoListToFill.add(listItem);
            c.moveToNext();
        }
        toDoListToFill.add(null);
    }
    public ToDoListItem getSingleListItem(String listID, String reminderID) {
        Cursor c = mSqlHelper.getReminderByIDs(listID, reminderID);
        if (c.getCount() == 0) {
            return null;
        }
        c.moveToFirst();
        //the text
        int colIndex = c.getColumnIndex(DbSchema.reminders_table.cols.REMINDER_TEXT);
        ToDoListItem listItem = new ToDoListItem(c.getString(colIndex), reminderID);

        colIndex=c.getColumnIndex(DbSchema.reminders_table.cols.IS_COMPLETED);
        int isCompleted = c.getInt(colIndex);
        listItem.setCompleted(isCompleted==1?true:false);
        //the calendar alarm
        colIndex = c.getColumnIndex(DbSchema.calendarAlarm_table.cols.CALENDAR_ALARM_ID);
        String calendarAlarmID = c.getString(colIndex);
        if (calendarAlarmID == null) {
            listItem.setIsCalendarAlarm(false);
        } else {
            listItem.setIsCalendarAlarm(true);
            String substring = listItem.getText().substring(0, 5);
            String id = substring + "_L" + listID + "I" + reminderID;
            colIndex = c.getColumnIndex(DbSchema.calendarAlarm_table.cols.MONTH);
            int month = c.getInt(colIndex);
            colIndex = c.getColumnIndex(DbSchema.calendarAlarm_table.cols.DAY);
            int day = c.getInt(colIndex);
            colIndex = c.getColumnIndex(DbSchema.calendarAlarm_table.cols.YEAR);
            int year = c.getInt(colIndex);
            colIndex = c.getColumnIndex(DbSchema.calendarAlarm_table.cols.HOUR);
            int hour24 = c.getInt(colIndex);
            colIndex = c.getColumnIndex(DbSchema.calendarAlarm_table.cols.MINUTES);
            int minutes = c.getInt(colIndex);
            colIndex = c.getColumnIndex(DbSchema.calendarAlarm_table.cols.IS_ACTIVE);
            int isActive = c.getInt(colIndex);

            listItem.setCalendarAlarmInfo(id, Integer.parseInt(reminderID), day, month, year, hour24, minutes, isActive);
        }
        //the geolocation alarm
        colIndex = c.getColumnIndex(DbSchema.geoFenceAlarm_table.cols.GEOFENCE_ALARM_ID);
        String geoFenceAlarmID = c.getString(colIndex);
        if (geoFenceAlarmID == null) {
            listItem.setIsGeoFenceAlarm(false);
        } else {
            listItem.setIsGeoFenceAlarm(true);
            //geoFenceAlarm_id,
            colIndex = c.getColumnIndex(DbSchema.geoFenceAlarm_table.cols.GEOFENCE_ALARM_ID);
            String geoID = c.getString(colIndex);
            listItem.setGeoFenceAlarm_id(geoID);
            //street
            colIndex = c.getColumnIndex(DbSchema.geoFenceAlarm_table.cols.STREET);
            String street = c.getString(colIndex);
            listItem.setStreet(street);
            //city
            colIndex = c.getColumnIndex(DbSchema.geoFenceAlarm_table.cols.CITY);
            String city = c.getString(colIndex);
            listItem.setCity(city);
            //state
            colIndex = c.getColumnIndex(DbSchema.geoFenceAlarm_table.cols.STATE);
            String state = c.getString(colIndex);
            listItem.setState(state);
            //zip
            colIndex = c.getColumnIndex(DbSchema.geoFenceAlarm_table.cols.ZIPCODE);
            String zipCode = c.getString(colIndex);
            listItem.setZip(zipCode);
            //latitude
            colIndex = c.getColumnIndex(DbSchema.geoFenceAlarm_table.cols.LATITUDE);
            String latitude = c.getString(colIndex);
            listItem.setLatitude(latitude);
            //longiguted
            colIndex = c.getColumnIndex(DbSchema.geoFenceAlarm_table.cols.LONGITUDE);
            String longitude = c.getString(colIndex);
            listItem.setLongitude(longitude);
            //alarmTag
            colIndex = c.getColumnIndex(DbSchema.geoFenceAlarm_table.cols.ALARM_TAG);
            String alarmTag = c.getString(colIndex);
            listItem.setAlarmTag(alarmTag);
            //meterRadius
            colIndex = c.getColumnIndex(DbSchema.geoFenceAlarm_table.cols.RADIUS);
            String radius = c.getString(colIndex);
            listItem.setMeterRadius(radius);
            //isAlarmActive
            colIndex = c.getColumnIndex(DbSchema.geoFenceAlarm_table.cols.IS_ACTIVE);
            int tempValue = c.getInt(colIndex);
            boolean isActive = tempValue == 1 ? true : false;
            listItem.setGeoAlarmActive(isActive);
        }
        return listItem;

    }


    public void saveCalendarAlarm(String alarmID, String reminderID,
                                  int month, int day, int year,
                                  int hour, int min, boolean isActive) {
        int active = isActive ? 1 : 0;
        Log.d(TAG, "Sending the following info to be saved: " + alarmID + " DATE: " + month + "." + day + "." + year + " TIME " + hour + ":" + min);
        mSqlHelper.saveCalendarAlarm(alarmID, reminderID, month, day, year, hour, min, active);

    }


    public void toggleCalendarAlarm(String alarmID, int onOff) {
        mSqlHelper.toggleCalendarAlarm(alarmID, onOff);
    }

    //=============================================================================
    public void saveGeoFenceAlarm(String alarmID, String reminderID, HashMap<String, String> data) {
        mSqlHelper.saveGeoFenceAlarm(alarmID, reminderID, data);
    }

    public int getTotalActiveGeoAlarms() {
        int total= mSqlHelper.getActiveGeoAlarmCount();
        return total;
    }

    public void toggleGEOAlarm(String alarmID, int onOff) {
        mSqlHelper.toggleGeoFenceAlarm(alarmID, onOff);
    }

    public ArrayList<FenceData> getActiveFenceData() {
        Cursor c = mSqlHelper.getAllActiveAlarmFenceInfo();
        c.moveToFirst();
        //int nofOfResults = c.getCount();
        ArrayList<FenceData>fencedDatas = new ArrayList<>();
        int count=-1;
        int spike=0;
        int colIndex=0;
        while (c.isAfterLast() == false){
            FenceData fd = new FenceData();

            colIndex=c.getColumnIndex(DbSchema.geoFenceAlarm_table.cols.LATITUDE);
            fd.latitude=c.getDouble(colIndex);
            
            colIndex=c.getColumnIndex(DbSchema.geoFenceAlarm_table.cols.LONGITUDE);
            fd.longitude=c.getDouble(colIndex);

            colIndex=c.getColumnIndex(DbSchema.reminders_table.cols.REMINDER_TEXT);

            String s=c.getString(colIndex);
            int maxLength=98;
            if(s.length()<98){
                maxLength=s.length();
            }
            fd.ninetynineChars=s.substring(0,maxLength);

            colIndex=c.getColumnIndex(DbSchema.geoFenceAlarm_table.cols.ALARM_TAG);
            fd.tag=c.getString(colIndex);

            fencedDatas.add(fd);
            c.moveToNext();
        }

        return fencedDatas;
    }


    public ArrayList<ToDoListItem>getGeoFencedTriggeredItems(ArrayList<String>tags){
        Cursor c= mSqlHelper.getRemindersTriggeredUsingTags(tags);
        c.moveToFirst();
        ArrayList<ToDoListItem>results = new ArrayList<>();
        while(c.isAfterLast()==false){
            int colIndex = c.getColumnIndex(DbSchema.reminders_table.cols.REMINDER_ID);
            String reminder_id = c.getString(colIndex);
            colIndex = c.getColumnIndex(DbSchema.reminders_table.cols.REMINDER_TEXT);
            String text = c.getString(colIndex);
            colIndex=c.getColumnIndex(DbSchema.reminders_table.cols.LIST_ID);
            String listID= c.getString(colIndex);
            ToDoListItem listItem = new ToDoListItem(text, reminder_id);
            listItem.setListId(listID);
            results.add(listItem);
            c.moveToNext();
        }
        return results;
    }

    public void deleteAll(String listID){
        mSqlHelper.deleteAll(listID);
    }
}
