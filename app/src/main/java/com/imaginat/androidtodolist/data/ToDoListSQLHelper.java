package com.imaginat.androidtodolist.data;

import android.content.Context;
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
}
