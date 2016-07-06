package com.imaginat.androidtodolist.models.list;

import android.content.Context;

import com.imaginat.androidtodolist.data.ToDoListSQLHelper;
import com.imaginat.androidtodolist.models.ListTitle;

import java.util.ArrayList;
import java.util.concurrent.Callable;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by nat on 7/5/16.
 */
public class ListOfListTitles implements IList<ListTitle> {

    private static final String TAG = ListOfListTitles.class.getSimpleName();
    private ToDoListSQLHelper mSqlHelper;


    public ListOfListTitles(Context context){
        mSqlHelper = ToDoListSQLHelper.getInstance(context);
    }
    @Override
    public Observable<ArrayList<ListTitle>> getAllListItems() {

        return Observable.fromCallable(new Callable<ArrayList<ListTitle>>(){

            @Override
            public ArrayList<ListTitle> call() throws Exception {
                return mSqlHelper.getAllListNames2();
            }
        });
    }



    //create new list
    public Observable<String> createNewList(String listName,int iconID){

        return Observable.fromCallable(new Callable<String>(){

            @Override
            public String call() throws Exception {
                return mSqlHelper.insertIntoListTable(listName,iconID);
            }
        }).observeOn(AndroidSchedulers.mainThread());
    }
}
