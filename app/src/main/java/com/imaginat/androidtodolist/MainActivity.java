package com.imaginat.androidtodolist;

import android.app.ActivityManager;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
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

import com.google.android.gms.common.api.Status;
import com.imaginat.androidtodolist.businessModels.ToDoListItemManager;
import com.imaginat.androidtodolist.customlayouts.ActionListFragment;
import com.imaginat.androidtodolist.customlayouts.AddListFragment;
import com.imaginat.androidtodolist.customlayouts.AlarmsTriggeredListFragment;
import com.imaginat.androidtodolist.customlayouts.MainListFragment;
import com.imaginat.androidtodolist.customlayouts.ToDoListOptionsFragment;
import com.imaginat.androidtodolist.google.Constants;
import com.imaginat.androidtodolist.google.LocationUpdateService;

import java.util.ArrayList;

//import com.imaginat.androidtodolist.google.GoogleAPIClientManager;

public class MainActivity extends AppCompatActivity
        implements ActionListFragment.IChangeActionBarTitle,
        com.imaginat.androidtodolist.google.LocationServices.ILocationServiceClient,
        ToDoListOptionsFragment.IGeoOptions{

    private static final String TAG = MainActivity.class.getName();

    private static final int REQUEST_FINE_LOCATION = 0;
    private static final int REQUEST_LOCATION = 12;


    //for reference to service
    LocationUpdateService mLocationUpdateService;
    boolean mLocationUpdateServiceBound;
    MyServiceConnection mServiceConnection;

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
            case R.id.testStartService:
                Log.d(TAG, "startService selected");
                Intent startServiceIntent = new Intent(MainActivity.this, LocationUpdateService.class);
                startService(startServiceIntent);
                return true;
            case R.id.testStopService:
                Log.d(TAG, "stopService selected");
                Intent stopServiceIntent = new Intent(MainActivity.this, LocationUpdateService.class);
                stopService(stopServiceIntent);
                return true;
            case R.id.testIsServiceRunning:
                isServiceRunning();
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

        //shared preferences
        mSharedPreferences = getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE);
        //UI Stuff
        getSupportActionBar().setTitle("Main");


        //Settinig up the initial fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        MainListFragment fragment = new MainListFragment();
        fragment.setIGeoOptions(this);
        fragmentTransaction.add(R.id.my_frame, fragment);
        fragmentTransaction.commit();

        //For Search Bar
        handleIntent(getIntent());

        //Permissions for Location services
        loadPermissions(android.Manifest.permission.ACCESS_FINE_LOCATION, REQUEST_FINE_LOCATION);



        //Bind to service (if service is running)
        if (isServiceRunning() == false) {
            //look in shared preference to see if anybody needs it, if it does start it up
            int totalNoOfSharedPreferences = mSharedPreferences.getInt(Constants.GEO_ALARM_COUNT, -1);
            ToDoListItemManager listItemManager = ToDoListItemManager.getInstance(this);
            int totalNoInDatabase = listItemManager.getTotalActiveGeoAlarms();
            Log.d(TAG,"totalInShared: "+totalNoOfSharedPreferences+" totalDatabase"+totalNoInDatabase);
            if (totalNoInDatabase != totalNoOfSharedPreferences) {
                //reset shared preferences
                SharedPreferences.Editor ed = mSharedPreferences.edit();
                ed.putInt(Constants.GEO_ALARM_COUNT, totalNoInDatabase);
                ed.commit();
            }
            int totalNoOfActiveGeoAlarms = totalNoInDatabase;
            if (totalNoOfActiveGeoAlarms > 0) {
                //start up the service
                Intent startUpServiceIntent = new Intent(this, LocationUpdateService.class);
                startService(startUpServiceIntent);
                mServiceConnection=new MyServiceConnection();
                bindService(startUpServiceIntent, mServiceConnection, Context.BIND_AUTO_CREATE);

            }
        } else {
            //service is already up and running, now bind if not already bound
            if (mLocationUpdateServiceBound == false) {
                Intent boundIntent = new Intent(this, LocationUpdateService.class);
                mServiceConnection=new MyServiceConnection();
                bindService(boundIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
            }
        }

    }

    @Override
    protected void onDestroy() {
        if (isServiceRunning()) {
            //look in shared preference to see if anybody needs it, if not, stop iti
            int totalNoOfActiveGeoAlarms = mSharedPreferences.getInt(Constants.GEO_ALARM_COUNT, -1);
            Log.d(TAG,"onDestroy totaNoOfActiveAlarms: "+totalNoOfActiveGeoAlarms);
            if (totalNoOfActiveGeoAlarms < 1) {
                //start up the service
                Intent stopServiceIntent = new Intent(this, LocationUpdateService.class);
                stopService(stopServiceIntent);

            }
            if (mLocationUpdateServiceBound) {
                unbindService(mServiceConnection);
            }

        }
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //mLocationServices.stopLocationUpdates();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //  if (mGoogleApiClient.isConnected() && !mRequestingLocationUpdates) {
        //mLocationServices.startLocationUpdates();
        // }
    }

    @Override
    protected void onStart() {

        // mGoogleApiClient.connect();

        super.onStart();
    }

    @Override
    protected void onStop() {

        // mGoogleApiClient.disconnect();
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

        String possibleSource=intent.getStringExtra(Constants.INTENT_SOURCE);
        if(possibleSource!=null){
            Log.d(TAG,"possibleSource: "+possibleSource);
        }
        if(possibleSource!=null && possibleSource.equals("GeofenceTransitionsIntentService")){
            Log.d(TAG,"INSIDE handleIntent, got started by service ");



            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            AlarmsTriggeredListFragment fragment = new AlarmsTriggeredListFragment();
            ArrayList<String> selectedTags = intent.getStringArrayListExtra(Constants.LIST_OF_TRIGGERED);
            fragment.setSelectedTags(selectedTags);
            //fragment.setIGeoOptions(mIGeoOptions);
            //Log.d(TAG,"List id is "+data);
            //fragment.setListId(data);
            fragmentTransaction.replace(R.id.my_frame, fragment);
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_NONE);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
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





    //===================CODE TO LINK TO SERVICE============================


    private boolean isServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            //Log.d(TAG, "CHECKING " + service.service.getClassName());
            if ("com.imaginat.androidtodolist.google.LocationUpdateService".equalsIgnoreCase(service.service.getClassName())) {
                Toast.makeText(this, "LocationUpdateService is RUNNING", Toast.LENGTH_LONG).show();
                Log.d(TAG, "LocationUpdateService is currently running");
                return true;
            }
        }
        Toast.makeText(this, "LocationUpdateService is NOT RUNNING", Toast.LENGTH_LONG).show();
        Log.d(TAG, "LocationUpdateService is NOT currently running");
        return false;
    }

    //Methods for binding to service
    //these methods let you start the service if it wasn't started onCreate of Main Activity of app
    //it will end the service on destroy if required, but not while app is in use

    @Override
    public LocationUpdateService getServiceReference() {
        if (mLocationUpdateServiceBound) {
            return mLocationUpdateService;
        }
        //check if number of geoFenceAlarms warrants system to start
        int totalNoOfActiveGeoAlarms = mSharedPreferences.getInt(Constants.GEO_ALARM_COUNT, -1);
        if (totalNoOfActiveGeoAlarms > 0 && mLocationUpdateServiceBound == false) {
            //bind it here
            Intent bindingIntent = new Intent(this, LocationUpdateService.class);
            mServiceConnection=new MyServiceConnection();
            bindService(bindingIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
            mLocationUpdateServiceBound = true;
            return mLocationUpdateService;
        }
        return null;
    }

    @Override
    public void requestStartOfLocationUpdateService() {
        //add to count of alarm using it
        int currentTotal = mSharedPreferences.getInt(Constants.GEO_ALARM_COUNT, 0);
        currentTotal++;
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(Constants.GEO_ALARM_COUNT, currentTotal);
        editor.commit();
        if (isServiceRunning() == false) {
            //start the service
            Intent startServiceIntent = new Intent(this, LocationUpdateService.class);
            startService(startServiceIntent);
        }

    }

    @Override
    public void requestStopOfLocationUpdateService() {
        //reduce the number of geoFence kept in shared preferences
        //onDestroy will stop service if count is less than 1
        int currentTotal = mSharedPreferences.getInt(Constants.GEO_ALARM_COUNT, 0);
        currentTotal--;
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(Constants.GEO_ALARM_COUNT, currentTotal);
    }


    private class MyServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LocationUpdateService.MyLocationUpdateServiceBinder myBinder = (LocationUpdateService.MyLocationUpdateServiceBinder) service;
            mLocationUpdateService = myBinder.getService();
            mLocationUpdateServiceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mLocationUpdateServiceBound = false;
        }
    }

}
