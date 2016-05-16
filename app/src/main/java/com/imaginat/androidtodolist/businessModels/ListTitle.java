package com.imaginat.androidtodolist.businessModels;

/**
 * Created by nat on 5/10/16.
 */
public class ListTitle extends AListItem {
    private String list_id=null;

    public ListTitle(){
        super();
    }

    public String getList_id() {
        return list_id;
    }

    public void setList_id(String list_id) {
        this.list_id = list_id;
    }
}
