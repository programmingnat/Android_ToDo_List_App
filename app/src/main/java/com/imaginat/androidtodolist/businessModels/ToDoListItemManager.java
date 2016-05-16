package com.imaginat.androidtodolist.businessModels;

import android.content.Context;
import android.database.Cursor;

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

            mReminders.add(listItem);
            c.moveToNext();
        }
        mReminders.add(null);
    }
}
