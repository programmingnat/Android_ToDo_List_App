package com.imaginat.androidtodolist.google.location;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.imaginat.androidtodolist.google.Constants;

/**
 * Created by nat on 5/25/16.
 */
public class GeofenceReceiver extends WakefulBroadcastReceiver {
    private String TAG = GeofenceReceiver.class.getSimpleName();
    private Context mContext;
    private Intent broadcastIntent = new Intent();

    @Override
    public void onReceive(Context context, Intent intent) {
        this.mContext=context;


        //Toast.makeText(context,"Geofence receiver reached",Toast.LENGTH_SHORT).show();
        Log.d(TAG,"GeofenceReceiver reached");
        /*Intent myIntent = new Intent(context, GeofenceTransitionsIntentService.class);
        intent.putExtra(Constants.THE_TEXT,intent.getStringExtra(Constants.THE_TEXT));
        startWakefulService(context,myIntent);*/


        Intent newIntent = new Intent(intent);
        newIntent.setClass(context.getApplicationContext(),GeofenceTransitionsIntentService.class);
        intent.putExtra(Constants.THE_TEXT,"THIS IS A test");
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // addGeofences() and removeGeofences().

        startWakefulService(context,newIntent);
        setResultCode(Activity.RESULT_OK);

    }


}
