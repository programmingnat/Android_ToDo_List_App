package com.imaginat.androidtodolist;

import android.app.PendingIntent;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.imaginat.androidtodolist.customlayouts.ActionListFragment;
import com.imaginat.androidtodolist.customlayouts.AddListFragment;
import com.imaginat.androidtodolist.customlayouts.MainListFragment;
import com.imaginat.androidtodolist.customlayouts.ToDoListOptionsFragment;
import com.imaginat.androidtodolist.data.ToDoListSQLHelper;
import com.imaginat.androidtodolist.google.AddressResultReceiver;
import com.imaginat.androidtodolist.google.Constants;
import com.imaginat.androidtodolist.google.CoordinatesResultReceiver;
import com.imaginat.androidtodolist.google.GeoCoder;
import com.imaginat.androidtodolist.google.GeofenceErrorMessages;
import com.imaginat.androidtodolist.google.GeofenceTransitionsIntentService;
import com.imaginat.androidtodolist.google.GoogleAPIClientManager;
import com.imaginat.androidtodolist.google.LocationServices;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements ActionListFragment.IChangeActionBarTitle, GoogleAPIClientManager.IUseGoogleApiClient,
        com.imaginat.androidtodolist.google.LocationServices.ILocationServiceClient, ToDoListOptionsFragment.IGeoOptions,
        ResultCallback<Status>, CoordinatesResultReceiver.ICoordinateReceiver {

    private static final String TAG = MainActivity.class.getName();
    private GoogleApiClient mGoogleApiClient;
    private static final int REQUEST_FINE_LOCATION = 0;
    protected Location mLastLocation;
    final static int REQUEST_LOCATION = 199;
    private LocationServices mLocationServices;
    protected Boolean mRequestingLocationUpdates=true;
    private AddressResultReceiver mAddressResultReceiver;
    private CoordinatesResultReceiver mCoordinatesResultReceiver;

    /**
     * Used when requesting to add or remove geofences.
     */
    private PendingIntent mGeofencePendingIntent;
    private boolean mGeofencesAdded;
    private SharedPreferences mSharedPreferences;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //return super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.lists_of_lists_dropdown, menu);


        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_new_list:
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.my_frame, new AddListFragment());
                ft.setTransition(FragmentTransaction.TRANSIT_NONE);
                ft.addToBackStack(null);
                ft.commit();
                return true;
            case 100:
                android.support.v4.app.Fragment f = getSupportFragmentManager().findFragmentById(R.id.my_frame);
                ActionListFragment alf = (ActionListFragment) f;
                alf.toggleEdit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mSharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES_NAME,
                0);
        mGeofencesAdded = mSharedPreferences.getBoolean(Constants.GEOFENCES_ADDED_KEY, false);

        GoogleAPIClientManager googleAPIClientManager = GoogleAPIClientManager.getInstance(this, this);
        mGoogleApiClient = googleAPIClientManager.getGoogleApiClient();

        mLocationServices = new LocationServices(mGoogleApiClient, this);


        mAddressResultReceiver = new AddressResultReceiver(new Handler());
        mCoordinatesResultReceiver = new CoordinatesResultReceiver(new Handler());

        ToDoListSQLHelper sqlHelper = ToDoListSQLHelper.getInstance(this);
        sqlHelper.getWritableDatabase();

        getSupportActionBar().setTitle("Main");


        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        MainListFragment fragment = new MainListFragment();
        fragment.setIGeoOptions(this);
        fragmentTransaction.add(R.id.my_frame, fragment);
        fragmentTransaction.commit();

        handleIntent(getIntent());


    }

    @Override
    protected void onPause() {
        super.onPause();
        mLocationServices.stopLocationUpdates();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected() && !mRequestingLocationUpdates) {
            mLocationServices.startLocationUpdates();
        }
    }

    @Override
    protected void onStart() {

        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {

        mGoogleApiClient.disconnect();
        super.onStop();
    }


    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Toast.makeText(MainActivity.this, "Searching for " + query, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onUpdateTitle(String title) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(title);

        //actionBar.setBackgroundDrawable(new ColorDrawable(0xff00DDED));
        //actionBar.setDisplayShowTitleEnabled(false);
        //actionBar.setDisplayShowTitleEnabled(true);
    }

    @Override
    public void onConnectedToGoogleAPIClient() {
        Log.d(TAG, "MainActivity onConnectedtoGoogleAPIClient");

        loadPermissions(android.Manifest.permission.ACCESS_FINE_LOCATION, REQUEST_FINE_LOCATION);
        mLocationServices.createLocationRequest();
        mRequestingLocationUpdates = true;

        if (mRequestingLocationUpdates) {
            try {
                mLocationServices.startLocationUpdates();
            } catch (SecurityException ex) {
                ex.printStackTrace();
            }
        } else {

        }


    }


    @Override
    public void displayDialogBasedOnStatus(Status status) throws IntentSender.SendIntentException {
        status.startResolutionForResult(
                MainActivity.this,
                REQUEST_LOCATION);
    }


    private void loadPermissions(String perm, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, perm)) {
                ActivityCompat.requestPermissions(this, new String[]{perm}, requestCode);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void getAddressFromLocation() {
        //Log.d(TAG, "inside getAddressFromLocatin");
        //Location lastLocation = mLocationServices.getLocation();
        //GeoCoder.startIntentService(this,lastLocation,mAddressResultReceiver);

    }

    @Override
    public void setGeoFenceAddress(String street, String city, String state, String zipCode,String alarmTag) {
        Log.d(TAG, "Inside setGeoFenceAddress "+street+" "+city+" "+state+" "+zipCode);
        GeoCoder.getLocationFromAddress(this, street + " " + city + "," + state + " " + zipCode,alarmTag, mCoordinatesResultReceiver);
        //ToDoListOptionsFragment currentFragment =(ToDoListOptionsFragment) MainActivity.this.getSupportFragmentManager().findFragmentById(R.id.my_frame);
        mCoordinatesResultReceiver.setResult(this);


    }

    @Override
    public void removeGeoFence() {
        removeGeofence();
    }


    public void removeGeofence() {
        Log.d(TAG, "Remove geofence");
        ArrayList<String>removeList = new ArrayList<>();
        removeList.add("HOME");
        removeList.add("CRESTWOOD");
        com.google.android.gms.location.LocationServices.GeofencingApi.removeGeofences(mGoogleApiClient,removeList).setResultCallback(this);
        /*com.google.android.gms.location.LocationServices.GeofencingApi.removeGeofences(
                mGoogleApiClient,
                // This is the same pending intent that was used in addGeofences().
                getGeofencePendingIntent()
        ).setResultCallback(this); // Result processed in onResult().*/
    }

    /**
     * Gets a PendingIntent to send with the request to add or remove Geofences. Location Services
     * issues the Intent inside this PendingIntent whenever a geofence transition occurs for the
     * current list of geofences.
     *
     * @return A PendingIntent for the IntentService that handles geofence transitions.
     */
    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // addGeofences() and removeGeofences().
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public void addGeofences() {
        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(this, "NOT CONNECTED", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            com.google.android.gms.location.LocationServices.GeofencingApi.addGeofences(
                    mGoogleApiClient,
                    // The GeofenceRequest object.
                    mLocationServices.getGeofencingRequest(),
                    // A pending intent that that is reused when calling removeGeofences(). This
                    // pending intent is used to generate an intent when a matched geofence
                    // transition is observed.
                    getGeofencePendingIntent()
            ).setResultCallback(this); // Result processed in onResult().
        } catch (SecurityException securityException) {
            // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
            logSecurityException(securityException);
        }
    }


    private void logSecurityException(SecurityException securityException) {
        Log.e(TAG, "Invalid location permission. " +
                "You need to use ACCESS_FINE_LOCATION with geofences", securityException);
    }

    @Override
    public void onResult(@NonNull Status status) {
        if (status.isSuccess()) {
            //Update state and save in shared preferences.
            mGeofencesAdded = !mGeofencesAdded;
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putBoolean(Constants.GEOFENCES_ADDED_KEY, mGeofencesAdded);
            editor.apply();
//
//            // Update the UI. Adding geofences enables the Remove Geofences button, and removing
//            // geofences enables the Add Geofences button.
//            setButtonsEnabledState();


            if (mGeofencesAdded) {
                Log.d(TAG, "geofence added");
                Toast.makeText(MainActivity.this, "GEO FENCE ADDED", Toast.LENGTH_SHORT).show();
            } else {
                Log.d(TAG, "geofence removed");
                Toast.makeText(MainActivity.this, "GEO FENCE REMOVED", Toast.LENGTH_SHORT).show();
            }

        } else {
            // Get the status code for the error and log it using a user-friendly message.
            String errorMessage = GeofenceErrorMessages.getErrorString(this,
                    status.getStatusCode());
            Log.e(TAG, errorMessage);
        }
    }


    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
/*
        MY_LANDMARKS.put("CRESTWOOD TRAIN STATION", new LatLng(40.958997,-73.820564));

        // WARREN.
        MY_LANDMARKS.put("WARREN AVENUE", new LatLng(40.9618839,-73.8154516));

        //EASTCHESTER HIGH SCHOOL
        MY_LANDMARKS.put("WARREN AVENUE", new LatLng(40.961959, -73.817088));

        //LORD & TAYLORS
        MY_LANDMARKS.put("LORD&TAYLORS", new LatLng(40.972252, -73.803934));

        //KENSICO DAM
        MY_LANDMARKS.put("KENSICO DAM",new LatLng(41.073794, -73.766287));
        */
        if(Constants.SUCCESS_RESULT==resultCode) {
            //NOW ADD FENCE
            Location lastLocation = resultData.getParcelable(Constants.RESULT_DATA_KEY);
            String requestID=resultData.getString(Constants.ALARM_TAG);
            mLocationServices.addToGeoFenceList(requestID, lastLocation.getLatitude(), lastLocation.getLongitude());
            addGeofences();
        }


    }
}
