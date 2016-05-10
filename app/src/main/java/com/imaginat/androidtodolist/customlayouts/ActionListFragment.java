package com.imaginat.androidtodolist.customlayouts;

import android.content.Context;
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
import com.imaginat.androidtodolist.businessModels.IListItem;
import com.imaginat.androidtodolist.businessModels.ToDoListItem;

import java.util.ArrayList;

/**
 * Created by nat on 4/26/16.
 */
public class ActionListFragment extends Fragment implements ReminderListRecycleAdapter.IHandleListClicks {

    public interface IChangeActionBarTitle{
        public void onUpdateTitle(String title);
    }
    private static String TAG= MainListFragment.class.getName();
    ToDoListRecyclerAdapter mAdapter;
    ArrayList<IListItem> mReminders;
    RelativeLayout mTheAddingLayout;
    RecyclerView mRecyclerView;
    IChangeActionBarTitle mIChangeActionBarTitle;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View  view= inflater.inflate(R.layout.todos_list_fragment, container, false);
        setHasOptionsMenu(true);
        mTheAddingLayout =(RelativeLayout)view.findViewById(R.id.addItemOverlayView);
        //mAddListOverlayView = (TextView) mTheAddingLayout.findViewById(R.id.addListEditText);
        mAdapter = new ToDoListRecyclerAdapter((Context)getActivity(),mReminders=new ArrayList<IListItem>(),this);
        RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.theRecyclerView);
        recyclerView.setAdapter(mAdapter);
        mRecyclerView=recyclerView;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);




        for(int i=0;i<10;i++){
            ToDoListItem r = new ToDoListItem();
            r.setText("To do list item "+i);
            mReminders.add(r);
        }


        mAdapter.notifyDataSetChanged();

        if(mIChangeActionBarTitle!=null){
            mIChangeActionBarTitle.onUpdateTitle("TEST LIST");
        }


        //mAddListOverlayView.setHeight(itemFromList.getHeight());
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int lastVisiblePosition = layoutManager.findLastVisibleItemPosition();
                int lastIndex = mReminders.size()-1;
                if(lastVisiblePosition<lastIndex){
                    Toast.makeText(getActivity(),"I should display extra text on bottom",Toast.LENGTH_SHORT).show();
                    mTheAddingLayout.setVisibility(View.VISIBLE);
                }else{
                    Toast.makeText(getActivity(),"I should NOT display extra text on bottom",Toast.LENGTH_SHORT).show();
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
        }catch(Exception ex){
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

    public void toggleEdit(){
        Toast.makeText(getContext(),"toggleEdit called",Toast.LENGTH_SHORT).show();
    }
    @Override
    public void handleClick(String data) {

        if(data.equals("MORE_OPTIONS")){
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.my_frame, new ToDoListOptionsFragment());
            ft.setTransition(FragmentTransaction.TRANSIT_NONE);
            ft.addToBackStack(null);
            ft.commit();
        }
        Log.d(TAG,"INSIDE ActionListFragment");
        Toast.makeText(getContext(),"CLICK",Toast.LENGTH_SHORT).show();
//        FragmentTransaction ft = getFragmentManager().beginTransaction();
//        ft.replace(R.id.my_frame, new ActionListFragment());
//        ft.setTransition(FragmentTransaction.TRANSIT_NONE);
//        ft.addToBackStack(null);
//        ft.commit();
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
        final int MENU_ITEM_ITEM1=100;
        MenuItem m = menu.add(Menu.NONE, MENU_ITEM_ITEM1, Menu.NONE, "Item name");
        m.setIcon(android.R.drawable.ic_menu_edit);
        m.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM);
    }
}
