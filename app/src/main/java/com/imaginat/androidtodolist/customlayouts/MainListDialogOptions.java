package com.imaginat.androidtodolist.customlayouts;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.imaginat.androidtodolist.R;

/**
 * Created by nat on 6/6/16.
 */
public class MainListDialogOptions extends DialogFragment {

    public interface IUseMainListDialogOptions{
        public void deleteList(String id);
        public void transferListViaNFC(String id);
    }

    IUseMainListDialogOptions mIUseMainListDialogOptions;

    public IUseMainListDialogOptions getIUseMainListDialogOptions() {
        return mIUseMainListDialogOptions;
    }

    public void setIUseMainListDialogOptions(IUseMainListDialogOptions IUseMainListDialogOptions) {
        mIUseMainListDialogOptions = IUseMainListDialogOptions;
    }

    String mListID=null;

    public String getListID() {
        return mListID;
    }

    public void setListID(String listID) {
        mListID = listID;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.main_list_dialog, null);
        builder.setView(view);
//        builder
//
//                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        // User cancelled the dialog
//                    }
//                });
        // Create the AlertDialog object and return it

        final Button shareNFCButton = (Button)view.findViewById(R.id.mainlist_shareList);
        shareNFCButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIUseMainListDialogOptions.transferListViaNFC(mListID);
                dismiss();
            }
        });
        Button deleteButton = (Button)view.findViewById(R.id.mainlist_DeleteList);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIUseMainListDialogOptions.deleteList(mListID);
                dismiss();
            }
        });
        return builder.create();
    }
}
