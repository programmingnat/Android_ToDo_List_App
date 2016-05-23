package com.imaginat.androidtodolist.businessModels;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.imaginat.androidtodolist.data.DbSchema;
import com.imaginat.androidtodolist.data.ToDoListSQLHelper;

import java.util.ArrayList;

/**
 * Created by nat on 5/12/16.
 */
public class ToDoListItemManager {

    private static final String TAG = ListManager.class.getSimpleName();
    public static ToDoListItemManager instance;
    private ToDoListSQLHelper mSqlHelper;
    private ArrayList<ToDoListItem> mReminders;



    private ToDoListItemManager(Context context){
        mSqlHelper = ToDoListSQLHelper.getInstance(context);
        mReminders = new ArrayList<ToDoListItem>();

        for(int i=0;i<10;i++){
            ToDoListItem r = new ToDoListItem();
            r.setText("To do list item "+i);
            mReminders.add(r);
        }
        mReminders.add(null);

    }

    public static ToDoListItemManager  getInstance(Context context){
        if(instance==null){
            instance = new ToDoListItemManager(context);
        }

        return instance;
    }

    public ArrayList<ToDoListItem> getReminders(){
        return mReminders;
    }
    public void createNewReminder(String listId,String text){


        //add it
        mSqlHelper.insertReminderIntoList(listId, text);
        loadAllRemindersForList(listId);


    }

    public void deleteReminder(String listId,String reminderId){
        mSqlHelper.deleteReminder(listId,reminderId);
        loadAllRemindersForList(listId);
    }
    public void updateReminder(String listId,String reminderID,String text){
        mSqlHelper.updateReminder(listId,reminderID,text);
        loadAllRemindersForList(listId);
    }
    public void loadAllRemindersForList(String listID){
        Cursor c = mSqlHelper.getAllReminderForThisList(listID);
        mReminders.clear();
        c.moveToFirst();
        while(c.isAfterLast()==false) {
            int colIndex = c.getColumnIndex(DbSchema.reminders_table.cols.REMINDER_ID);
            String reminder_id = c.getString(colIndex);
            colIndex = c.getColumnIndex(DbSchema.reminders_table.cols.REMINDER_TEXT);
            String text = c.getString(colIndex);
            ToDoListItem listItem = new ToDoListItem(text,reminder_id);
            listItem.setListId(listID);
            mReminders.add(listItem);
            c.moveToNext();
        }
        mReminders.add(null);
    }
    public ToDoListItem getSingleListItem(String listID,String reminderID){
        Cursor c = mSqlHelper.getReminderByIDs(listID,reminderID);
        if(c.getCount()==0){
            return null;
        }
        c.moveToFirst();
        //the text
        int colIndex = c.getColumnIndex(DbSchema.reminders_table.cols.REMINDER_TEXT);
        ToDoListItem listItem = new ToDoListItem(c.getString(colIndex),reminderID);
        //the calendar alarm
        colIndex = c.getColumnIndex(DbSchema.calendarAlarm_table.cols.CALENDAR_ALARM_ID);
        String calendarAlarmID = c.getString(colIndex);
        if(calendarAlarmID==null){
            listItem.setIsCalendarAlarm(false);
        }else {
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

            listItem.setCalendarAlarmInfo(id,Integer.parseInt(reminderID),day,month,year,hour24,minutes,isActive);
        }
        //the geolocation alarm
        colIndex = c.getColumnIndex(DbSchema.geoFenceAlarm_table.cols.GEOFENCE_ALARM_ID);
        String geoFenceAlarmID = c.getString(colIndex);
        if(geoFenceAlarmID==null){
            listItem.setIsGeoFenceAlarm(false);
        } else{
            listItem.setIsGeoFenceAlarm(true);
        }
        return listItem;

    }


    public void saveCalendarAlarm(String alarmID,String reminderID,
                                       int month, int day,int year,
                                       int hour,int min,boolean isActive){
        int active = isActive?1:0;
        Log.d(TAG,"Sending the following info to be saved: "+alarmID+" DATE: "+month+"."+day+"."+year+" TIME "+hour+":"+min);
        mSqlHelper.saveCalendarAlarm(alarmID,reminderID,month,day,year,hour,min,active);

    }

    public void toggleCalendarAlarm(String alarmID,int onOff){
        mSqlHelper.toggleCalendarAlarm(alarmID,onOff);
    }
}
