package com.imaginat.androidtodolist.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by nat on 5/10/16.
 * useed to display the titles of the list
 */
public class ListTitle extends AListItem {
    private String list_id=null;
    private int mIcon;

    public int getIcon() {
        return mIcon;
    }

    public void setIcon(int icon) {
        mIcon = icon;
    }

    public ListTitle(){
        super();
    }

    public String getList_id() {
        return list_id;
    }

    public void setList_id(String list_id) {
        this.list_id = list_id;
    }

    public JSONObject toJSON()throws JSONException {
        JSONObject listTitleJSON = new JSONObject();
        listTitleJSON.put("list_title",getText());
        listTitleJSON.put("list_id",list_id);
        return listTitleJSON;

    }

    @Override
    public String toString() {
        return getText();
    }
}
