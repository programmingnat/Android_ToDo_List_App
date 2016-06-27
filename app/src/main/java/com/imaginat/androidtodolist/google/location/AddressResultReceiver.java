package com.imaginat.androidtodolist.google.location;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;

import com.imaginat.androidtodolist.google.Constants;

/**
 * Created by nat on 5/17/16.
 */
@SuppressLint("ParcelCreator")
public class AddressResultReceiver extends ResultReceiver {
    private static final String TAG = ResultReceiver.class.getSimpleName();

    public AddressResultReceiver(Handler handler) {
        super(handler);
    }

    /**
     *  Receives data sent from FetchAddressIntentService and updates the UI in MainActivity.
     */
    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {

        // Display the address string or an error message sent from the intent service.
        String addressOutput = resultData.getString(Constants.RESULT_DATA_KEY);

        // Show a toast message if an address was found.
        if (resultCode == Constants.SUCCESS_RESULT) {
            Log.d(TAG,"onReceiveResult the address is "+addressOutput);
        }else{
            Log.d(TAG,"onReceiveResult, but is not success");
        }


    }
}