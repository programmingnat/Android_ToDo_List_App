package com.imaginat.androidtodolist.businessModels;

import android.content.Context;
import android.os.AsyncTask;

import java.util.ArrayList;

/**
 * Created by nat on 6/13/16.
 */
public class TheDeleter extends AsyncTask<String,Void,Boolean> {

    public interface IUseTheDeleter{
        public void deletionCompleted(boolean result,String s);
    }
    private ToDoListItemManager mToDoListItemManager=null;
    private ListManager mListManager;
    private IUseTheDeleter mDeleterInterface;

    public TheDeleter(Context context){
        mToDoListItemManager = ToDoListItemManager.getInstance(context);
        mListManager = ListManager.getInstance(context);
    }
    public void setDeletionCompleteCallback(IUseTheDeleter idelete){
        mDeleterInterface=idelete;
    }
    @Override
    protected Boolean doInBackground(String... params) {
        if(params[0].equals("ALL")){

            //get all the list ids
            ArrayList<ListTitle>allTitles =  mListManager.getListTitles();
            for(ListTitle title:allTitles){
                deleteList(title.getList_id());
            }

        }else{

            deleteList(params[0]);
        }
        return true;
    }


    private void deleteList(String id){
        //stop alarm calendar
        //stop alarm geofences

        //delete from all sub tables
        //then delete from list table


        mToDoListItemManager.deleteAll(id);



    }
    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        if(mDeleterInterface!=null){

            mDeleterInterface.deletionCompleted(aBoolean.booleanValue(),"deletion completed");
        }
    }
}
