package com.imaginat.androidtodolist.customlayouts;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.imaginat.androidtodolist.R;

/**
 * Created by nat on 5/1/16.
 */
public class AddListFragment extends Fragment {


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_new_list, container, false);
        setHasOptionsMenu(true);


        return view;

    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem item = menu.findItem(R.id.menu_item_new_list);
        item.setEnabled(false);
        //Toast.makeText(getContext(),"Attempted to disable", Toast.LENGTH_SHORT).show();
    }
}
