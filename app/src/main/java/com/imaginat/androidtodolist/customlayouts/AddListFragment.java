package com.imaginat.androidtodolist.customlayouts;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.imaginat.androidtodolist.R;
import com.imaginat.androidtodolist.businessModels.ListManager;

/**
 * Created by nat on 5/1/16.
 */
public class AddListFragment extends Fragment {

    private static final String TAG=AddListFragment.class.getSimpleName();
    private EditText mEditTextOfListName;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_new_list, container, false);
        setHasOptionsMenu(true);
        mEditTextOfListName = (EditText)view.findViewById(R.id.addNameOfNewList_EditText);
        Button doneButton = (Button)view.findViewById(R.id.doneButton);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"onClick called");
                if(mEditTextOfListName.getText()==null || mEditTextOfListName.getText().length()==0){
                    //indicate error
                    mEditTextOfListName.setError("FILL thIS IN");
                }

                String newListName = mEditTextOfListName.getText().toString();
                ListManager listManager = ListManager.getInstance(getContext());
                listManager.createNewList(newListName);
            }
        });

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
