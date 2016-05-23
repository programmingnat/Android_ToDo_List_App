package com.imaginat.androidtodolist.google;

import android.content.Context;
import android.content.Intent;
import android.location.Location;

/**
 * Created by nat on 5/17/16.
 */
public class GeoCoder {


    public static void startIntentService(Context c,Location location,AddressResultReceiver resultReceiver) {
        Intent intent = new Intent(c, FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, resultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, location);
        c.startService(intent);
    }

    public static void getLocationFromAddress(Context c,String strAddress,String alarmTag,CoordinatesResultReceiver resultReceiver){

        Intent intent = new Intent(c,FetchCoordinatesIntentService.class);
        intent.putExtra(Constants.RECEIVER,resultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA,strAddress);
        intent.putExtra(Constants.ALARM_TAG,alarmTag);
        c.startService(intent);

    }
}
