package com.imaginat.androidtodolist.models.list;

import java.util.ArrayList;

import rx.Observable;

/**
 * Created by nat on 7/5/16.
 */
public interface IList<T> {

    public Observable<ArrayList<T>> getAllListItems();


}
