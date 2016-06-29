package com.imaginat.androidtodolist.customlayouts.alarm;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.imaginat.androidtodolist.R;
import com.imaginat.androidtodolist.customlayouts.list.ToDoListRecyclerAdapter;
import com.imaginat.androidtodolist.models.ToDoListItem;
import com.imaginat.androidtodolist.managers.ToDoListItemManager;

import java.util.ArrayList;

/**
 * Created by nat on 5/29/16.
 */
public class AlarmsTriggeredListFragment extends Fragment implements ToDoListRecyclerAdapter.IHandleListClicks {

    private static String TAG = AlarmsTriggeredListFragment.class.getName();


    ToDoListRecyclerAdapter mAdapter;
    RelativeLayout mTheAddingLayout;
    RecyclerView mRecyclerView;
    ToDoListOptionsFragment.IGeoOptions mIGeoOptions;
    ToDoListItemManager mToDoListItemManager;
    ArrayList<String>mSelectedtags;
    ArrayList<ToDoListItem>mTheSelectedItems;

    public void setSelectedTags(ArrayList<String> selectedTags){
        mSelectedtags=selectedTags;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.todos_list_fragment, container, false);


        mToDoListItemManager = ToDoListItemManager.getInstance(getContext());
        mTheSelectedItems=mToDoListItemManager.getGeoFencedTriggeredItems(mSelectedtags);

        mTheAddingLayout = (RelativeLayout) view.findViewById(R.id.addItemOverlayView);
        //mAddListOverlayView = (TextView) mTheAddingLayout.findViewById(R.id.addListEditText);
        mAdapter = new ToDoListRecyclerAdapter((Context) getActivity(),mTheSelectedItems, this);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.theRecyclerView);
        recyclerView.setAdapter(mAdapter);
        mRecyclerView = recyclerView;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);


        mAdapter.notifyDataSetChanged();



        //mAddListOverlayView.setHeight(itemFromList.getHeight());
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int lastVisiblePosition = layoutManager.findLastVisibleItemPosition();
                int lastIndex = mToDoListItemManager.getReminders().size() - 1;
                if (lastVisiblePosition < lastIndex) {
                    Toast.makeText(getActivity(), "I should display extra text on bottom", Toast.LENGTH_SHORT).show();
                    mTheAddingLayout.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(getActivity(), "I should NOT display extra text on bottom", Toast.LENGTH_SHORT).show();
                    mTheAddingLayout.setVisibility(View.GONE);
                }

            }
        });

        return view;
    }

    @Override
    public void handleClick(String data) {

    }

    @Override
    public void handleMoreOptions(String list_id, String item_id) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ToDoListOptionsFragment toDoListOptionsFragment = new ToDoListOptionsFragment();
        toDoListOptionsFragment.setItemID(item_id);
        toDoListOptionsFragment.setListID(list_id);
        toDoListOptionsFragment.setIGeoOptions(mIGeoOptions);
        ft.replace(R.id.my_frame, toDoListOptionsFragment);
        ft.setTransition(FragmentTransaction.TRANSIT_NONE);
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    public void handleClickToCreateNewReminder(String data) {
        //does nothing here
    }

    @Override
    public void handleClickToUpdateReminder(String id, String data) {

    }

    @Override
    public void handleClickToUpdateCheckStatus(String listId,String id, boolean isChecked) {

    }

    @Override
    public void handleDeleteButton(String id) {

    }

    @Override
    public void handleShowMoreOptions(String listID,String reminderID) {

    }
}
