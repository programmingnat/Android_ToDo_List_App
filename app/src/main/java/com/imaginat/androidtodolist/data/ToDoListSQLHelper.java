package com.imaginat.androidtodolist.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by nat on 5/10/16.
 */
public class ToDoListSQLHelper extends SQLiteOpenHelper{

    private static final String TAG = ToDoListSQLHelper.class.getSimpleName();
    private static final int VERSION=1;
    private static final String DATABASE_NAME="mytodolist.db";
    private static ToDoListSQLHelper mInstance;

    private ToDoListSQLHelper(Context context){
        super(context,DATABASE_NAME,null,VERSION);
    }


    public static ToDoListSQLHelper getInstance(Context context){
        if(mInstance==null){
            mInstance = new ToDoListSQLHelper(context.getApplicationContext());
        }


        return mInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        Log.d(TAG,"Inside onCreate of database helper, about to call createCommand");
        db.execSQL(DbSchema.lists_table.createCommand);
        Log.d(TAG, "Inside onCreate of database helper, about to call createCommand "+DbSchema.reminders_table.createCommand);
        db.execSQL(DbSchema.reminders_table.createCommand);
        Log.d(TAG, "Inside onCreate of database helper, about to call createCommand "+DbSchema.calendarAlarm_table.createCommand);
        db.execSQL(DbSchema.calendarAlarm_table.createCommand);
        Log.d(TAG, "Inside onCreate of database helper, about to call createCommand "+DbSchema.geoFenceAlarm_table.createCommand);
        db.execSQL(DbSchema.geoFenceAlarm_table.createCommand);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    //=====================RELATED TO LIST STUFF================================================
    public String getListName(String listID){
        SQLiteDatabase db= this.getReadableDatabase();
        Cursor c = db.query(DbSchema.lists_table.NAME, //table
                new String[]{DbSchema.lists_table.cols.LIST_TITLE}, //columns
                DbSchema.lists_table.cols.LIST_ID+"=?",//select
                new String[]{listID},//selection args
                null,//group
                null,//having
                null,//order
                null);//limit

        if(c==null || c.getCount()==0){
            return null;
        }
        c.moveToFirst();
        return c.getString(0);
    }
    public String insertIntoListTable(String listName){
        ContentValues values = new ContentValues();
        values.put(DbSchema.lists_table.cols.LIST_TITLE,listName);
        SQLiteDatabase db= this.getWritableDatabase();

        long id=db.insert(DbSchema.lists_table.NAME,
                null,
                values);
        return Long.toString(id);
    }

    public int doesListNameExist(String listName){


        SQLiteDatabase db= this.getReadableDatabase();
        Cursor c=db.rawQuery("SELECT count("+DbSchema.lists_table.cols.LIST_ID+") FROM "+DbSchema.lists_table.NAME+
                " WHERE "+DbSchema.lists_table.cols.LIST_TITLE+" =?",new String[]{listName});
        //long cnt  = DatabaseUtils.queryNumEntries(db, DbSchema.lists_table.NAME);
        c.moveToFirst();
        int count=c.getInt(0);
        db.close();
        return  count;
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor c = db.query(DbSchema.lists_table.NAME,//table
//                BusinessDbSchema.BusinessTable.ALL_COLS,
//                "bizName LIKE ?",//select
//                new String[]{"%"+s+"%"},//selection args
//                null,//group
//                null,//having
//                orderBy,//order
//                null);//limit
//
//        return c;
    }

    public Cursor getAllListNames(){
        Log.d(TAG,"inside getAllListNames()");
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query(DbSchema.lists_table.NAME, //table
        DbSchema.lists_table.ALL_COLUMNS, //columns
                null,//select
                null,//selection args
                null,//group
                null,//having
                null,//order
                null);//limit

        return c;
    }

    public Cursor getListIDByTitle(String s){
        Log.d(TAG,"inside getListIDByTitle: "+s);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query(DbSchema.lists_table.NAME, //table
                DbSchema.lists_table.ALL_COLUMNS, //columns
                DbSchema.lists_table.cols.LIST_TITLE+"=?",//select
                new String[]{s},//selection args
                null,//group
                null,//having
                null,//order
                null);//limit
        //Cursor c=db.rawQuery("SELECT * FROM "+DbSchema.lists_table.NAME+" WHERE list_title='test please '",null);
        int total=c.getCount();
        /*int col=c.getColumnIndex(DbSchema.lists_table.cols.LIST_ID);
        return c.getString(col);*/
        return c;
    }
//    public void updateEntry(Business b){
//        Log.d("BusinessTableHelper","Inside updateEntry with "+b.getDescription());
//        Log.d("BusinessTableHelper","the id to update is "+b.getId());
//        SQLiteDatabase db= this.getWritableDatabase();
//        ContentValues cv = getContentValues(b);
//        db.update(BusinessDbSchema.BusinessTable.NAME,
//                cv,
//                "_id=?",
//                new String[]{Integer.toString(b.getId())});
//    }

    //=============================================================================
    public void insertReminderIntoList(String listID,String text){
        ContentValues values = new ContentValues();
        values.put(DbSchema.reminders_table.cols.LIST_ID,listID);
        values.put(DbSchema.reminders_table.cols.REMINDER_TEXT,text);
        SQLiteDatabase db= this.getWritableDatabase();

        db.insert(DbSchema.reminders_table.NAME,
                null,
                values);
    }

    public Cursor getAllReminderForThisList(String listID){
        Log.d(TAG, "inside getAllListNames()");
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query(DbSchema.reminders_table.NAME, //table
                DbSchema.reminders_table.ALL_COLUMNS, //columns
                DbSchema.reminders_table.cols.LIST_ID+"=?",//select
                new String[]{listID},//selection args
                null,//group
                null,//having
                null,//order
                null);//limit

        return c;
    }
    public Cursor getReminderByIDs(String listID,String reminderID){
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM reminders r LEFT OUTER JOIN calendarAlarms ca ON ca.reminder_id=r.reminder_id LEFT OUTER JOIN ";
        sql+=" geoFenceAlarm gfa ON gfa.reminder_id=r.reminder_id WHERE r.list_id=? AND r.reminder_id=?";
        Cursor c = db.rawQuery(sql,new String[]{listID,reminderID});
        return c;

    }
    public void updateReminder(String listID,String reminderID,String text){
        ContentValues values = new ContentValues();
        values.put(DbSchema.reminders_table.cols.REMINDER_TEXT,text);
        SQLiteDatabase db= this.getWritableDatabase();

        db.update(DbSchema.reminders_table.NAME,
                values,
                DbSchema.reminders_table.cols.LIST_ID + "=? AND " + DbSchema.reminders_table.cols.REMINDER_ID + "=?",
                new String[]{listID, reminderID});
    }

    public void updateCheckMark(String listID,String reminderID,int checkValue){
        ContentValues values = new ContentValues();
        values.put(DbSchema.reminders_table.cols.IS_COMPLETED,checkValue);
        SQLiteDatabase db = this.getWritableDatabase();
        db.update(DbSchema.reminders_table.NAME,
                values,
                DbSchema.reminders_table.cols.LIST_ID + "=? AND " + DbSchema.reminders_table.cols.REMINDER_ID + "=?",
                new String[]{listID, reminderID});
    }
    public void deleteReminder(String listID,String reminderID){
        SQLiteDatabase db= this.getWritableDatabase();
        db.delete(DbSchema.reminders_table.NAME,
                DbSchema.reminders_table.cols.LIST_ID + "=? AND " + DbSchema.reminders_table.cols.REMINDER_ID + "=?",
                new String[]{listID, reminderID});
    }
    //============================================================================================
    public void saveGeoFenceAlarm(String alarmID, String reminderID, HashMap<String,String>data){
        Log.d(TAG,"saveGeoFenceAlarm Called");

        ContentValues values = new ContentValues();
        Iterator it = data.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry pair = (Map.Entry)it.next();
            values.put((String)pair.getKey(),(String)pair.getValue());
        }

        Log.d(TAG,"attempting to update first WHERE ALARM_TAG is "+alarmID+" and reminderID is "+reminderID);
        SQLiteDatabase db= this.getWritableDatabase();
        int noOfRowsAffected=db.update(DbSchema.geoFenceAlarm_table.NAME,
                values,
                DbSchema.geoFenceAlarm_table.cols.ALARM_TAG + "=? AND " + DbSchema.geoFenceAlarm_table.cols.REMINDER_ID + "=?",
                new String[]{alarmID, reminderID});

        if(noOfRowsAffected>0){
            Log.d(TAG,"saveGeoFenceAlarm, noOfRowsAffected "+noOfRowsAffected+" exiting");
            return;
        }
        Log.d(TAG,"attempting to insert");
        values.put("meterRadius","100");
        db.insert(DbSchema.geoFenceAlarm_table.NAME,
                null,
                values);

    }
    public void toggleGeoFenceAlarm(String requestID,int onOff){
        ContentValues values = new ContentValues();
        values.put(DbSchema.geoFenceAlarm_table.cols.IS_ACTIVE,onOff);

        SQLiteDatabase db= this.getWritableDatabase();
        int noOfRowsAffected=db.update(DbSchema.geoFenceAlarm_table.NAME,
                values,
                DbSchema.geoFenceAlarm_table.cols.ALARM_TAG + "=?",
                new String[]{requestID});
    }
    public void deleteGeoFenceAlarm(String geoFenceAlarmID){
        SQLiteDatabase db= this.getWritableDatabase();
        db.delete(DbSchema.calendarAlarm_table.NAME,
                DbSchema.calendarAlarm_table.cols.CALENDAR_ALARM_ID+ "=?",
                new String[]{geoFenceAlarmID});
    }

    public int getActiveGeoAlarmCount(){
        SQLiteDatabase db = this.getReadableDatabase();
        String sql="SELECT COUNT("+ DbSchema.geoFenceAlarm_table.cols.GEOFENCE_ALARM_ID+
                ") FROM "+DbSchema.geoFenceAlarm_table.NAME+" WHERE "+DbSchema.geoFenceAlarm_table.cols.IS_ACTIVE+"=?";
        Log.d(TAG,sql);

        Cursor c = db.rawQuery(sql,new String[]{"1"});
        c.moveToFirst();
        //int result = c.getInt(0);
        return c.getInt(0);

    }
    public Cursor getAllActiveAlarmFenceInfo(){
        SQLiteDatabase db = this.getReadableDatabase();
        String sql="SELECT "+DbSchema.reminders_table.cols.REMINDER_TEXT+","+ DbSchema.geoFenceAlarm_table.cols.LATITUDE+","+DbSchema.geoFenceAlarm_table.cols.LONGITUDE+
                ","+DbSchema.geoFenceAlarm_table.cols.ALARM_TAG+
                " FROM "+DbSchema.geoFenceAlarm_table.NAME+" alarm INNER JOIN "+DbSchema.reminders_table.NAME+" r ON r.reminder_id=alarm.reminder_id  WHERE "+DbSchema.geoFenceAlarm_table.cols.IS_ACTIVE+"=?";
        Log.d(TAG,sql);
        Cursor c = db.rawQuery(sql,new String[]{"1"});
        return c;

    }
    public Cursor getRemindersTriggeredUsingTags(ArrayList<String> tags){

        int totalTags  = tags.size();
        if(totalTags==0){
            return null;
        }
        String args[] = new String[totalTags];
        String sqlTags="";
        for(int i=0;i<totalTags;i++){
            sqlTags+="?";
            args[i]=tags.get(i);
            if(i<totalTags-1){
                sqlTags+=",";
            }

        }
        String sql = "SELECT * FROM "+
                DbSchema.reminders_table.NAME+" r INNER JOIN "+
                DbSchema.geoFenceAlarm_table.NAME+" geo ON geo."+
                DbSchema.geoFenceAlarm_table.cols.REMINDER_ID+"= r."+DbSchema.reminders_table.cols.REMINDER_ID+
                " WHERE "+
                DbSchema.geoFenceAlarm_table.cols.ALARM_TAG +" IN ("+
                sqlTags
                +")";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(sql,args);
        return c;

    }
    //============================================================================================
    public void saveCalendarAlarm(String alarmID, String reminderID,int month,int day, int year,int hour,int min,int isActive){
        //UPDATE FIRST
        ContentValues values = new ContentValues();
        values.put(DbSchema.calendarAlarm_table.cols.MONTH,month);
        values.put(DbSchema.calendarAlarm_table.cols.DAY,day);
        values.put(DbSchema.calendarAlarm_table.cols.YEAR,year);
        values.put(DbSchema.calendarAlarm_table.cols.HOUR,hour);
        values.put(DbSchema.calendarAlarm_table.cols.MINUTES,min);
        values.put(DbSchema.calendarAlarm_table.cols.IS_ACTIVE,isActive);

        SQLiteDatabase db= this.getWritableDatabase();

        int noOfRowsAffected=db.update(DbSchema.calendarAlarm_table.NAME,
                values,
                DbSchema.calendarAlarm_table.cols.REMINDER_ID + "=? AND " + DbSchema.calendarAlarm_table.cols.CALENDAR_ALARM_ID + "=?",
                new String[]{reminderID,alarmID});
        Log.d(TAG,"SEEING IF I CAN UPDATE reminder_id is "+reminderID+" and calrmID is "+alarmID);
        //IF UPDATE DOESNT WORK THEN INSERT
        if(noOfRowsAffected>0){
            Log.d(TAG,"UPDATNG, not need to INSERT");
            return;
        }
        //if it gets here, no update occurred, insert it
        values.put(DbSchema.calendarAlarm_table.cols.CALENDAR_ALARM_ID,alarmID);
        values.put(DbSchema.calendarAlarm_table.cols.REMINDER_ID,reminderID);


        Log.d(TAG,"INSERTING NEW ENTRY");
        db.insert(DbSchema.calendarAlarm_table.NAME,
                null,
                values);
    }

    public void toggleCalendarAlarm(String calendarAlarmID,int onOff){
        ContentValues values = new ContentValues();
        values.put(DbSchema.calendarAlarm_table.cols.IS_ACTIVE,onOff);

        SQLiteDatabase db= this.getWritableDatabase();
        int noOfRowsAffected=db.update(DbSchema.calendarAlarm_table.NAME,
                values,
                DbSchema.calendarAlarm_table.cols.CALENDAR_ALARM_ID + "=?",
                new String[]{calendarAlarmID});
    }
    public void deleteCalendarAlarm(String calendarAlarmID){
        SQLiteDatabase db= this.getWritableDatabase();
        db.delete(DbSchema.calendarAlarm_table.NAME,
                DbSchema.calendarAlarm_table.cols.CALENDAR_ALARM_ID+ "=?",
                new String[]{calendarAlarmID});
    }
}
