package com.imaginat.androidtodolist;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.imaginat.androidtodolist.customlayouts.IChangeToolbar;
import com.imaginat.androidtodolist.customlayouts.alarm.ToDoListOptionsFragment;
import com.imaginat.androidtodolist.customlayouts.alarm.AlarmsTriggeredListFragment;
import com.imaginat.androidtodolist.customlayouts.list.ActionListFragment;
import com.imaginat.androidtodolist.customlayouts.list.AddListFragment;
import com.imaginat.androidtodolist.customlayouts.list.MainListFragment;
import com.imaginat.androidtodolist.google.Constants;
import com.imaginat.androidtodolist.google.LocationUpdateService;
import com.imaginat.androidtodolist.google.location.LocationServices;
import com.imaginat.androidtodolist.managers.ToDoListItemManager;
import com.imaginat.androidtodolist.nfc.NFC_List_Transfer_Manager;

import java.util.ArrayList;

//import com.imaginat.androidtodolist.google.GoogleAPIClientManager;

public class MainActivity extends AppCompatActivity
        implements IChangeToolbar,
        LocationServices.ILocationServiceClient,
        ToDoListOptionsFragment.IGeoOptions,NFC_List_Transfer_Manager.INFCTransferManager
{


    private static final String TAG = MainActivity.class.getName();
    private static final int REQUEST_FINE_LOCATION = 0;
    private static final int REQUEST_LOCATION = 12;
    private int TOOLBAR_ICON_INSTRUCTIONS=-1;


    //NFC related variables
    NFC_List_Transfer_Manager nfcManager =  NFC_List_Transfer_Manager.getInstance(this);


    //GEO & Google related variables
    LocationUpdateService mLocationUpdateService;
    boolean mLocationUpdateServiceBound;
    MyServiceConnection mServiceConnection;

    //Save options
    private SharedPreferences mSharedPreferences;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.lists_of_lists_dropdown, menu);


        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));


        //load with current lists
        ToDoListItemManager toDoListItemManager = ToDoListItemManager.getInstance(this);
        ArrayList<String>titles = toDoListItemManager.getListTitles();
        MenuItem menuItem = menu.findItem(R.id.testPrepNFCTransfer);
        SubMenu subMenu = menuItem.getSubMenu();
        for(String title:titles) {
            subMenu.addSubMenu(Menu.NONE, 5001, Menu.NONE, title);
        }
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
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
            case R.id.editListInfo:
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.my_frame, new AddListFragment());
                ft.setTransition(FragmentTransaction.TRANSIT_NONE);
                ft.addToBackStack(null);
                ft.commit();
                return true;
            case R.id.deleteList:

                Fragment foundFragment = getSupportFragmentManager().findFragmentById(R.id.my_frame);
                if (foundFragment instanceof MainListFragment){
                    Toast.makeText(MainActivity.this,"ATTEMPTING TO DELETE LIST",Toast.LENGTH_SHORT).show();
                    MainListFragment mainListFragment=(MainListFragment)foundFragment;
                    mainListFragment.deleteList();
                }else{
                    Toast.makeText(MainActivity.this,"COULD NOT FIND LIST",Toast.LENGTH_SHORT).show();
                }

                return true;
            case R.id.shareListNFC:
                Toast.makeText(MainActivity.this,"SHARE VIA NFC",Toast.LENGTH_SHORT).show();
                return true;
            case R.id.testPrepNFCTransfer:

                return true;
            case R.id.goTo_DrivePage:
                Intent n = new Intent(this,BackupToDrive.class);
                startActivity(n);
                return true;
            case GlobalConstants.HIDE_COMPLETED_ITEMS:
                Toast.makeText(MainActivity.this,"HIDE COMPLETED ITEMS",Toast.LENGTH_SHORT).show();
                ToDoListItemManager itemManagerToSetHide = ToDoListItemManager.getInstance(MainActivity.this);
                if(item.isChecked()){
                    itemManagerToSetHide.setHideCompleted(false);
                    item.setChecked(false);
                }else{
                    itemManagerToSetHide.setHideCompleted(true);
                    item.setChecked(true);
                }
                Fragment theFragment = getSupportFragmentManager().findFragmentById(R.id.my_frame);
                if (theFragment instanceof ActionListFragment){
                    Toast.makeText(MainActivity.this,"ATTEMPTING Updatepage",Toast.LENGTH_SHORT).show();
                    ActionListFragment actionListFragment=(ActionListFragment)theFragment;
                    actionListFragment.reloadPage();
                }else{
                    Toast.makeText(MainActivity.this,"COULD NOT FIND Fragment",Toast.LENGTH_SHORT).show();
                }
                return true;
            case 5001:
                String s = item.getTitle().toString();
                ToDoListItemManager itemManager = ToDoListItemManager.getInstance(this);
                ArrayList<String>reminders = itemManager.getRemindersByListTitle(s);
                nfcManager.populateMessagesToSend(s,reminders);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        updateIcons(menu);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //toolbar related
        Toolbar toolbar = (Toolbar)findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        //shared preferences
        mSharedPreferences = getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE);

        //UI Stuff
        getSupportActionBar().setTitle("Main");

        if(savedInstanceState==null) {

            //Setting up the initial fragment
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            MainListFragment fragment = new MainListFragment();
            fragment.setIGeoOptions(this);
            fragmentTransaction.add(R.id.my_frame, fragment);
            fragmentTransaction.commit();
            getSupportFragmentManager().addOnBackStackChangedListener(getListener());
        }

        //For Search Bar and NFC
        if (getIntent().getAction().equals(NfcAdapter.ACTION_NDEF_DISCOVERED)) {
            Log.d(TAG,"onCreate INTENT nfc received");
            nfcManager.handleNfcIntent(getIntent(),this);
        }else {
            Log.d(TAG,"onCreate non nfc intent receiaved");
            handleIntent(getIntent());
            Intent dummyIntent = new Intent();
            dummyIntent.setAction("do nothing");
            setIntent(dummyIntent);
        }


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

        //NFC check if available
        if(nfcManager.isNFCAvail()){
            nfcManager.init(this);
        }else{
            Toast.makeText(this, "NFC not available on this device",
                    Toast.LENGTH_SHORT).show();
        }





    }

    private FragmentManager.OnBackStackChangedListener getListener()
    {
        FragmentManager.OnBackStackChangedListener result = new FragmentManager.OnBackStackChangedListener()
        {
            public void onBackStackChanged()
            {
                FragmentManager manager = getSupportFragmentManager();

                if (manager != null)
                {
                    Fragment currFrag = (Fragment)manager.
                            findFragmentById(R.id.my_frame);
                    Toast.makeText(MainActivity.this,"Calling this,this is paapening",Toast.LENGTH_SHORT).show();
                    currFrag.onResume();
                }
            }
        };

        return result;
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
        //For Search Bar and NFC
        if (getIntent().getAction().equals(NfcAdapter.ACTION_NDEF_DISCOVERED)) {
            Log.d(TAG,"onResume INTENT nfc received");
            nfcManager.handleNfcIntent(getIntent(),this);
        }else {
            Log.d(TAG,"onResume non nfc intent receiaved");
            handleIntent(getIntent());

        }

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
        super.onNewIntent(intent);
        setIntent(intent);
    }

    private void handleIntent(Intent intent) {

        Log.d(TAG,"Entered handleIntent with "+intent.getAction());
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {


            String query = intent.getStringExtra(SearchManager.QUERY);
            Toast.makeText(MainActivity.this, "Searching for " + query, Toast.LENGTH_SHORT).show();
            //ToDoListItemManager listItemManager = ToDoListItemManager.getInstance(MainActivity.this);
            //ArrayList<ToDoListItem>result = listItemManager.findRemindersBasedOnQuery(query);


            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            ActionListFragment fragment = new ActionListFragment();
            fragment.setQuery(query);


            fragmentTransaction.replace(R.id.my_frame, fragment);
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_NONE);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();


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
        if (intent.getAction().equals(NfcAdapter.ACTION_NDEF_DISCOVERED)) {
            nfcManager.handleNfcIntent(getIntent(),this);

            Intent i = new Intent();
            i.setAction("do nothing");
            setIntent(i);

        }
    }



    public void updateIcons(Menu menu){
        Log.d(TAG,"Inside updateIcons");
        if(TOOLBAR_ICON_INSTRUCTIONS==100) {
            MenuItem menuItem = menu.findItem(R.id.search);
            menuItem.setVisible(false);
            menuItem=menu.findItem(R.id.deleteList);
            menuItem.setVisible(true);
            menuItem=menu.findItem(R.id.shareListNFC);
            menuItem.setVisible(true);
            menuItem=menu.findItem(R.id.editListInfo);
            menuItem.setVisible(true);

        }else if(TOOLBAR_ICON_INSTRUCTIONS==200){
            MenuItem menuItem = menu.findItem(R.id.search);
            menuItem.setVisible(true);
            menuItem=menu.findItem(R.id.deleteList);
            menuItem.setVisible(false);
            menuItem=menu.findItem(R.id.shareListNFC);
            menuItem.setVisible(false);
            menuItem=menu.findItem(R.id.editListInfo);
            menuItem.setVisible(false);
        }
    }

    @Override
    public void onUpdateTitle(String title) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(title);

    }



    public void swapIcons(int instructions){
        Log.d(TAG,"inside swapIcons");
        TOOLBAR_ICON_INSTRUCTIONS=instructions;
        invalidateOptionsMenu();
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



    //===============Helper method for non Android classes that need reference to activity
    @Override
    public Activity getActivityReference() {
        return this;
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





    //==========================================================================
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
