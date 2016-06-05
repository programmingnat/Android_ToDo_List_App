package com.imaginat.androidtodolist.customlayouts;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.imaginat.androidtodolist.R;
import com.imaginat.androidtodolist.businessModels.ToDoListItemManager;

/**
 * Created by nat on 4/26/16.
 */
public class ActionListFragment extends Fragment implements ToDoListRecyclerAdapter.IHandleListClicks,MoreOptionsDialogFragment.MoreOptionsDialogListener {



    public interface IChangeActionBarTitle {
        public void onUpdateTitle(String title);
    }


    private static String TAG = ActionListFragment.class.getName();
    private String mListId = null;
    ToDoListRecyclerAdapter mAdapter;
    RelativeLayout mTheAddingLayout;
    RecyclerView mRecyclerView;
    IChangeActionBarTitle mIChangeActionBarTitle;
    ToDoListOptionsFragment.IGeoOptions mIGeoOptions;
    ToDoListItemManager mToDoListItemManager;

    public void setIGeoOptions(ToDoListOptionsFragment.IGeoOptions IGeoOptions) {
        mIGeoOptions = IGeoOptions;
    }

    public void setListId(String id) {
        mListId = id;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.todos_list_fragment, container, false);
        setHasOptionsMenu(true);

        mToDoListItemManager = ToDoListItemManager.getInstance(getContext());
        mToDoListItemManager.loadAllRemindersForList(mListId);
        String listName = mToDoListItemManager.getListName(mListId);
        mTheAddingLayout = (RelativeLayout) view.findViewById(R.id.addItemOverlayView);
        //mAddListOverlayView = (TextView) mTheAddingLayout.findViewById(R.id.addListEditText);
        mAdapter = new ToDoListRecyclerAdapter((Context) getActivity(), mToDoListItemManager.getReminders(), this);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.theRecyclerView);
        recyclerView.setAdapter(mAdapter);
        mRecyclerView = recyclerView;
        MyLinearLayoutManager linearLayoutManager = new MyLinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);


        mAdapter.notifyDataSetChanged();

        if (mIChangeActionBarTitle != null) {
            if(listName==null) {
                mIChangeActionBarTitle.onUpdateTitle("REMINDERS");
            }else{
                mIChangeActionBarTitle.onUpdateTitle(listName);
            }
        }


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
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mIChangeActionBarTitle = (IChangeActionBarTitle) context;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        //duplicate logic, move to method?
      /*  LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        int lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
        int lastIndex=mReminders.size()-1;
        Toast.makeText(getContext(),"lastVisible vs lastIndex "+lastVisibleItem+" vs "+lastIndex,Toast.LENGTH_SHORT).show();
        if(linearLayoutManager.findLastVisibleItemPosition()<lastIndex){
            mTheAddingLayout.setVisibility(View.VISIBLE);
        }else{
            mTheAddingLayout.setVisibility(View.GONE);
        }*/
        //set height and width
    }

    public void toggleEdit() {
        Toast.makeText(getContext(), "toggleEdit called", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void handleClick(String data) {


        Log.d(TAG, "INSIDE ActionListFragment");
        Toast.makeText(getContext(), "CLICK", Toast.LENGTH_SHORT).show();
//        FragmentTransaction ft = getFragmentManager().beginTransaction();
//        ft.replace(R.id.my_frame, new ActionListFragment());
//        ft.setTransition(FragmentTransaction.TRANSIT_NONE);
//        ft.addToBackStack(null);
//        ft.commit();
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
        Log.d(TAG, "Attempting to create new reminder " + mListId + " " + data);
        UpdateDatabaseTask updateDatabaseTask = new UpdateDatabaseTask();
        updateDatabaseTask.execute("CREATE", data);


    }

    @Override
    public void handleClickToUpdateReminder(String id, String data) {
        Log.d(TAG, "Attempting to update reminder " + id + " with " + data);
        if(id==null  || data==null){
            return;
        }
        UpdateDatabaseTask updateDatabaseTask = new UpdateDatabaseTask();
        updateDatabaseTask.execute("UPDATE", id, data);

    }

    @Override
    public void handleClickToUpdateCheckStatus(String listID,String id, boolean isChecked) {
        Log.d(TAG, "Attempting to update reminder " + id + " with " + isChecked);
        if(id==null){
            return;
        }
        UpdateDatabaseTask updateDatabaseTask = new UpdateDatabaseTask();
        updateDatabaseTask.execute("UPDATE_CHECK", id, isChecked?"CHECKED":"UNCHECKED");
    }

    @Override
    public void handleDeleteButton(String id) {
        UpdateDatabaseTask updateDatabaseTask = new UpdateDatabaseTask();
        updateDatabaseTask.execute("DELETE", id);
    }

    @Override
    public void handleShowMoreOptions(String listID,String reminderID) {
        MoreOptionsDialogFragment newFragment = new MoreOptionsDialogFragment();
        newFragment.setMoreOptionsDialogListener(this);
        newFragment.setListID(listID);
        newFragment.setReminderID(reminderID);
        newFragment.show(getActivity().getSupportFragmentManager(), "options");
       
    }
    @Override
    public void onDeleteButton(String listID,String reminderID) {
        UpdateDatabaseTask updateDatabaseTask = new UpdateDatabaseTask();
        updateDatabaseTask.execute("DELETE", reminderID);
    }

    @Override
    public void onMoreOptionsButton(String listID,String reminderID) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ToDoListOptionsFragment toDoListOptionsFragment = new ToDoListOptionsFragment();
        toDoListOptionsFragment.setItemID(reminderID);
        toDoListOptionsFragment.setListID(listID);
        toDoListOptionsFragment.setIGeoOptions(mIGeoOptions);
        ft.replace(R.id.my_frame, toDoListOptionsFragment);
        ft.setTransition(FragmentTransaction.TRANSIT_NONE);
        ft.addToBackStack(null);
        ft.commit();
    }


    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem item = menu.findItem(R.id.menu_item_new_list);
        item.setVisible(false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        final int MENU_ITEM_ITEM1 = 100;
        MenuItem m = menu.add(Menu.NONE, MENU_ITEM_ITEM1, Menu.NONE, "SHOW COMPLETED TASKS");
        m.setIcon(android.R.drawable.ic_menu_edit);
        m.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM);
    }


    private class UpdateDatabaseTask extends AsyncTask<String, String, String> {


        @Override
        protected String doInBackground(String... params) {
            if (params[0].equals("CREATE")) {
                mToDoListItemManager.createNewReminder(mListId, params[1]);
            } else if (params[0].equals("UPDATE")) {
                Log.d(TAG,"UPDATING doInBackground called with args "+params[1]+" "+params[2]);
                mToDoListItemManager.updateReminder(mListId, params[1], params[2]);
            } else if (params[0].equals("DELETE")) {
                mToDoListItemManager.deleteReminder(mListId, params[1]);

            }else if(params[0].equals("UPDATE_CHECK")){
                mToDoListItemManager.markAsCompleted(mListId,params[1],params[2]);
            }


            return params[0];
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //don't have to call because the edit text is already changed to represent it
            if (s.equals("UPDATE_CHECK")==false) {
                mAdapter.notifyDataSetChanged();
            }
            //
        }
    }

    //========================================================================
    //prevent error animating when updating list
    private class MyLinearLayoutManager extends LinearLayoutManager {

        @Override
        public boolean supportsPredictiveItemAnimations() {
            return false;
        }

        public MyLinearLayoutManager(Context context) {
            super(context);
        }

        public MyLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
            super(context, orientation, reverseLayout);
        }
    }
    //=========================================================================



}
