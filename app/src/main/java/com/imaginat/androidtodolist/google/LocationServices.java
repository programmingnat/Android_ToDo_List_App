package com.imaginat.androidtodolist.google;

import android.content.IntentSender;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by nat on 5/16/16.
 */
public class LocationServices implements LocationListener {

    public interface ILocationServiceClient {
        public void displayDialogBasedOnStatus(Status status) throws IntentSender.SendIntentException;
    }


    private GoogleApiClient mGoogleApiClient;
    private static final String TAG = LocationServices.class.getSimpleName();
    private static final int REQUEST_FINE_LOCATION = 0;
    final static int REQUEST_LOCATION = 199;
    private ILocationServiceClient mServiceClient;
    private LocationRequest mLocationRequest;
    private Location mLocation;


    //=======================GEOFENCE============
    protected ArrayList<Geofence> mGeofenceList;


    public LocationServices(GoogleApiClient apiClient, ILocationServiceClient serviceClient) {
        mGoogleApiClient = apiClient;
        mServiceClient = serviceClient;
        mGeofenceList = new ArrayList<Geofence>();
    }


    public void createLocationRequest() throws SecurityException {
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
    //=========================GEO FENCE STUFF==================================    /**

    public void addToGeoFenceList(String requestID,double latitude,double longitude) {


        mGeofenceList.add(new Geofence.Builder()
                // Set the request ID of the geofence. This is a string to identify this
                // geofence.
                .setRequestId(requestID)

                // Set the circular region of this geofence.
                .setCircularRegion(
                        latitude,
                        longitude,
                        Constants.GEOFENCE_RADIUS_IN_METERS
                )

                // Set the expiration duration of the geofence. This geofence gets automatically
                // removed after this period of time.
                .setExpirationDuration(Constants.GEOFENCE_EXPIRATION_IN_MILLISECONDS)

                // Set the transition types of interest. Alerts are only generated for these
                // transition. We track entry and exit transitions in this sample.
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_EXIT)

                // Create the geofence.
                .build());

    }


    /**
     * Builds and returns a GeofencingRequest. Specifies the list of geofences to be monitored.
     * Also specifies how the geofence notifications are initially triggered.
     */
    public GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();

        // The INITIAL_TRIGGER_ENTER flag indicates that geofencing service should trigger a
        // GEOFENCE_TRANSITION_ENTER notification when the geofence is added and if the device
        // is already inside that geofence.
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);

        // Add the geofences to be monitored by geofencing service.
        builder.addGeofences(mGeofenceList);


        // Return a GeofencingRequest.
        return builder.build();
    }



    //==============TEST HARDCODED======
    /* This sample hard codes geofence data. A real app might dynamically create geofences based on
    * the user's location.
            */
    public void populateGeofenceList() {

        for (Map.Entry<String, LatLng> entry : Constants.MY_LANDMARKS.entrySet()) {

            mGeofenceList.add(new Geofence.Builder()
                    // Set the request ID of the geofence. This is a string to identify this
                    // geofence.
                    .setRequestId(entry.getKey())

                    // Set the circular region of this geofence.
                    .setCircularRegion(
                            entry.getValue().latitude,
                            entry.getValue().longitude,
                            Constants.GEOFENCE_RADIUS_IN_METERS
                    )

                    // Set the expiration duration of the geofence. This geofence gets automatically
                    // removed after this period of time.
                    .setExpirationDuration(Constants.GEOFENCE_EXPIRATION_IN_MILLISECONDS)

                    // Set the transition types of interest. Alerts are only generated for these
                    // transition. We track entry and exit transitions in this sample.
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                            Geofence.GEOFENCE_TRANSITION_EXIT)

                    // Create the geofence.
                    .build());
        }
    }


}
