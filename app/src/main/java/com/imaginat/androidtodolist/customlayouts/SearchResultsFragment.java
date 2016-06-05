package com.imaginat.androidtodolist.customlayouts;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by nat on 6/5/16.
 */
public class SearchResultsFragment extends Fragment implements
        ToDoListRecyclerAdapter.IHandleListClicks,MoreOptionsDialogFragment.MoreOptionsDialogListener{

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void handleClick(String data) {

    }

    @Override
    public void handleMoreOptions(String list_id, String item_id) {

    }

    @Override
    public void handleClickToCreateNewReminder(String data) {

    }

    @Override
    public void handleClickToUpdateReminder(String id, String data) {

    }

    @Override
    public void handleClickToUpdateCheckStatus(String listId, String id, boolean isChecked) {

    }

    @Override
    public void handleDeleteButton(String id) {

    }

    @Override
    public void handleShowMoreOptions(String listId, String reminderID) {

    }

    @Override
    public void onDeleteButton(String listID, String reminderID) {

    }

    @Override
    public void onMoreOptionsButton(String listID, String reminderID) {

    }
}
