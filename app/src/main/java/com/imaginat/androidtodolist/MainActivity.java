package com.imaginat.androidtodolist;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
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
import com.google.android.gms.common.api.Status;
import com.imaginat.androidtodolist.customlayouts.ActionListFragment;
import com.imaginat.androidtodolist.customlayouts.AddListFragment;
import com.imaginat.androidtodolist.customlayouts.MainListFragment;
import com.imaginat.androidtodolist.customlayouts.ToDoListOptionsFragment;
import com.imaginat.androidtodolist.data.ToDoListSQLHelper;
import com.imaginat.androidtodolist.google.AddressResultReceiver;
import com.imaginat.androidtodolist.google.GeoCoder;
import com.imaginat.androidtodolist.google.GoogleAPIClientManager;
import com.imaginat.androidtodolist.google.LocationServices;

public class MainActivity extends AppCompatActivity
    implements ActionListFragment.IChangeActionBarTitle,GoogleAPIClientManager.IUseGoogleApiClient,
        com.imaginat.androidtodolist.google.LocationServices.ILocationServiceClient, ToDoListOptionsFragment.IGeoOptions{

    private static final String TAG= MainActivity.class.getName();
    private GoogleApiClient mGoogleApiClient;
    private static final int REQUEST_FINE_LOCATION = 0;
    protected Location mLastLocation;
    final static int REQUEST_LOCATION = 199;
    private LocationServices mLocationServices;
    protected Boolean mRequestingLocationUpdates;
    private AddressResultReceiver mAddressResultReceiver;



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

    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.menu_item_new_list:
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.my_frame, new AddListFragment());
                ft.setTransition(FragmentTransaction.TRANSIT_NONE);
                ft.addToBackStack(null);
                ft.commit();
                return true;
            case 100:
                android.support.v4.app.Fragment f =getSupportFragmentManager().findFragmentById(R.id.my_frame);
                ActionListFragment alf = (ActionListFragment)f;
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

        GoogleAPIClientManager googleAPIClientManager = GoogleAPIClientManager.getInstance(this,this);
        mGoogleApiClient = googleAPIClientManager.getGoogleApiClient();

        mLocationServices = new LocationServices(mGoogleApiClient,this);


        mAddressResultReceiver = new AddressResultReceiver(new Handler());
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
        Log.d(TAG,"MainActivity onConnectedtoGoogleAPIClient");

        loadPermissions(android.Manifest.permission.ACCESS_FINE_LOCATION, REQUEST_FINE_LOCATION);
        mLocationServices.createLocationRequest();
        mRequestingLocationUpdates=true;

        if (mRequestingLocationUpdates) {
            try {
                mLocationServices.startLocationUpdates();
            }catch(SecurityException ex){
                ex.printStackTrace();
            }
        }else{

        }


    }



    @Override
    public void displayDialogBasedOnStatus(Status status) throws IntentSender.SendIntentException{
        status.startResolutionForResult(
                MainActivity.this,
                REQUEST_LOCATION);
    }



    private void loadPermissions(String perm,int requestCode) {
        if (ContextCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, perm)) {
                ActivityCompat.requestPermissions(this, new String[]{perm},requestCode);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void getAddressFromLocation() {
        Log.d(TAG, "inside getAddressFromLocatin");
        Location lastLocation = mLocationServices.getLocation();
        GeoCoder.startIntentService(this,lastLocation,mAddressResultReceiver);
    }
}
