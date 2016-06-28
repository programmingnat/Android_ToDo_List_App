package com.imaginat.androidtodolist.models;

/**
 * Created by nat on 4/17/16.
 * Anything that shows up in a list has a text associated
 * but classes that implment this class can have a different display method
 */
public interface IListItem {
    public String getText();
    public void setText(String s);
}
