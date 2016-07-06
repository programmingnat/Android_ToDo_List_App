package com.imaginat.androidtodolist.google.location;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import com.imaginat.androidtodolist.google.Constants;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by nat on 5/17/16.
 */
public class FetchCoordinatesIntentService extends IntentService{
    private static final String TAG = "FetchCoordIntentService";

    /**
     * The receiver where results are forwarded from this service.
     */
    protected ResultReceiver mReceiver;

    /**
     * This constructor is required, and calls the super IntentService(String)
     * constructor with the name for a worker thread.
     */
    public FetchCoordinatesIntentService() {
        // Use the TAG to name the worker thread.
        super(TAG);
    }

    /**
     * Tries to get the location address using a Geocoder. If successful, sends an address to a
     * result receiver. If unsuccessful, sends an error message instead.
     * Note: We define a {@link android.os.ResultReceiver} in * MainActivity to process content
     * sent from this service.
     *
     * This service calls this method from the default worker thread with the intent that started
     * the service. When this method returns, the service automatically stops.
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG,"FetchCoordinatesIntent called");
        String errorMessage = "";

        mReceiver = intent.getParcelableExtra(Constants.RECEIVER);
        String reminderID = intent.getStringExtra(Constants.REMINDER_ID);
        String listID = intent.getStringExtra(Constants.LIST_ID);
        String alarmTag = intent.getStringExtra(Constants.ALARM_TAG);

        // Check if receiver was properly registered.
        if (mReceiver == null) {
            Log.wtf(TAG, "No receiver received. There is nowhere to send the results.");
            return;
        }

        // Get the location passed to this service through an extra.
        String addressToLookup = intent.getStringExtra(Constants.LOCATION_DATA_EXTRA);

        Log.d(TAG,"addressToLookup: "+addressToLookup);
        // Make sure that the location data was really sent over through an extra. If it wasn't,
        // send an error error message and return.
        if (addressToLookup == null) {
            errorMessage = "No address data provided";
            Log.wtf(TAG, errorMessage);
            deliverResultToReceiver(Constants.FAILURE_RESULT, null,alarmTag,reminderID,listID);
            return;
        }

        // Errors could still arise from using the Geocoder (for example, if there is no
        // connectivity, or if the Geocoder is given illegal location data). Or, the Geocoder may
        // simply not have an address for a location. In all these cases, we communicate with the
        // receiver using a resultCode indicating failure. If an address is found, we use a
        // resultCode indicating success.

        // The Geocoder used in this sample. The Geocoder's responses are localized for the given
        // Locale, which represents a specific geographical or linguistic region. Locales are used
        // to alter the presentation of information such as numbers or dates to suit the conventions
        // in the region they describe.
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        // Address found using the Geocoder.
        List<Address> addresses = null;

        try {
            // Using getFromLocation() returns an array of Addresses for the area immediately
            // surrounding the given latitude and longitude. The results are a best guess and are
            // not guaranteed to be accurate.
            addresses = geocoder.getFromLocationName(addressToLookup,5);

        } catch (IOException ioException) {
            // Catch network or other I/O problems.
            errorMessage = "ioException called";
            Log.e(TAG, errorMessage, ioException);
        } catch (IllegalArgumentException illegalArgumentException) {
            // Catch invalid latitude or longitude values.
            errorMessage = "Invalid longitude and latitude";

        }

        // Handle case where no address was found.
        if (addresses == null || addresses.size()  == 0) {
            if (errorMessage.isEmpty()) {
                errorMessage = "no address found";
                Log.e(TAG, errorMessage);
            }


            deliverResultToReceiver(Constants.FAILURE_RESULT, null,alarmTag,reminderID,listID);
        } else {

            if (addresses==null) {
                return;
            }
            Address location=addresses.get(0);

            Location l = new Location("");
            l.setLatitude(location.getLatitude());
            l.setLongitude(location.getLongitude());



            Log.i(TAG, "looking for coordinates from address");
            deliverResultToReceiver(Constants.SUCCESS_RESULT, l,alarmTag,reminderID,listID);
        }
    }

    /**
     * Sends a resultCode and message to the receiver.
     */
    private void deliverResultToReceiver(int resultCode, Location location,String alarmTag,String reminderID,String listID) {
        Log.d(TAG,"deliverResulToReceiver called");
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.RESULT_DATA_KEY, location);
        bundle.putString(Constants.ALARM_TAG,alarmTag);
        bundle.putString(Constants.REMINDER_ID,reminderID);
        bundle.putString(Constants.LIST_ID,listID);
        mReceiver.send(resultCode, bundle);
    }
}


