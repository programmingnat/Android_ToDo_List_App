package com.imaginat.androidtodolist.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

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
    public void insertIntoListTable(String listName){
        ContentValues values = new ContentValues();
        values.put(DbSchema.lists_table.cols.LIST_TITLE,listName);
        SQLiteDatabase db= this.getWritableDatabase();

        db.insert(DbSchema.lists_table.NAME,
                null,
                values);
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
    public void updateReminder(String listID,String reminderID,String text){
        ContentValues values = new ContentValues();
        values.put(DbSchema.reminders_table.cols.REMINDER_TEXT,text);
        SQLiteDatabase db= this.getWritableDatabase();

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
}
