package com.imaginat.androidtodolist.businessModels;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.imaginat.androidtodolist.data.DbSchema;
import com.imaginat.androidtodolist.data.ToDoListSQLHelper;

import java.util.ArrayList;

/**
 * Created by nat on 5/10/16.
 */
public class ListManager {

    private static final String TAG = ListManager.class.getSimpleName();
    public static ListManager instance;
    private ToDoListSQLHelper mSqlHelper;
    private ArrayList<ListTitle>mListTitles;



    private ListManager(Context context){
        mSqlHelper = ToDoListSQLHelper.getInstance(context);
        mListTitles = new ArrayList<ListTitle>();
    }

    public static ListManager  getInstance(Context context){
        if(instance==null){
            instance = new ListManager(context);
        }

        return instance;
    }

    public ArrayList<ListTitle> getListTitles(){
        return mListTitles;
    }
    public void createNewList(String listName){
        //check if list name already exists
        int countFound  = mSqlHelper.doesListNameExist(listName);

        //if yes, modify list name
        if(countFound>0){
            Log.d(TAG,"createNewList found countFoud "+countFound);
            listName+="("+countFound+")";
        }else{
            Log.d(TAG,"created new list NOT found");
        }

        //add it
        mSqlHelper.insertIntoListTable(listName);

    }

    public void updateAllListTitles(){
        Cursor c = mSqlHelper.getAllListNames();
        mListTitles.clear();
        c.moveToFirst();
        while(c.isAfterLast()==false) {
            int colIndex = c.getColumnIndex(DbSchema.lists_table.cols.LIST_ID);
            int list_id = c.getInt(colIndex);
            colIndex = c.getColumnIndex(DbSchema.lists_table.cols.LIST_TITLE);
            String title = c.getString(colIndex);
            ListTitle lt = new ListTitle();
            lt.setText(title);
            lt.setList_id(Integer.toString(list_id));
            mListTitles.add(lt);
            c.moveToNext();
        }
    }
}
