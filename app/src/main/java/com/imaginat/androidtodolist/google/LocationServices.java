package com.imaginat.androidtodolist.google;

import android.content.IntentSender;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

/**
 * Created by nat on 5/16/16.
 */
public class LocationServices implements LocationListener {

    public interface ILocationServiceClient{
        public void displayDialogBasedOnStatus(Status status)  throws IntentSender.SendIntentException;
    }


    private GoogleApiClient mGoogleApiClient;
    private static final String TAG= LocationServices.class.getSimpleName();
    private static final int REQUEST_FINE_LOCATION = 0;
    final static int REQUEST_LOCATION = 199;
    private ILocationServiceClient mServiceClient;
    private LocationRequest mLocationRequest;
    private Location mLocation;

    public LocationServices(GoogleApiClient apiClient,ILocationServiceClient serviceClient){
        mGoogleApiClient = apiClient;
        mServiceClient = serviceClient;
    }




    public void createLocationRequest() throws SecurityException{
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        PendingResult<LocationSettingsResult> result =
                com.google.android.gms.location.LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient,
                        builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) throws SecurityException {
                final Status status = result.getStatus();
                //final LocationSettingsStates h = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can
                        // initialize location requests here.
                        Log.d(TAG, "Location Setting Requst status code is success");
                        com.google.android.gms.location.LocationServices.FusedLocationApi.requestLocationUpdates(
                                mGoogleApiClient, mLocationRequest, LocationServices.this);
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        Log.d(TAG, "Location Setting Requst status code is resolution required");
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            mServiceClient.displayDialogBasedOnStatus(status);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.d(TAG, "Location Setting Requst status code is setting change unavaible");
                        // Location settings are not satisfied. However, we have no way
                        // to fix the settings so we won't show the dialog.

                        break;
                }
            }
        });

    }
    public void startLocationUpdates() throws SecurityException {
        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        com.google.android.gms.location.LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }
    public void stopLocationUpdates() {
        com.google.android.gms.location.LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    public Location getLocation() {
        return mLocation;
    }

    public void setLocation(Location location) {
        mLocation = location;
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "location " + location.getLatitude() + " " + location.getLongitude());
        mLocation = location;

    }


}
