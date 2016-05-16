package com.imaginat.androidtodolist.com.imaginat.androidtodolist.google;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;

/**
 * Created by nat on 5/16/16.
 */
public class GoogleAPIClientManager
        implements GoogleApiClient.OnConnectionFailedListener,GoogleApiClient.ConnectionCallbacks{


    public interface IUseGoogleApiClient{
        public void onConnectedToGoogleAPIClient();
    }


    private static final String TAG = GoogleAPIClientManager.class.getSimpleName();
    private static GoogleAPIClientManager mInstance = null;
    private ArrayList<IUseGoogleApiClient> mClientsOfGoogleApi;

    private GoogleApiClient mGoogleApiClient;

    public static GoogleAPIClientManager getInstance(Context context,IUseGoogleApiClient client) {
        if(mInstance==null){
            mInstance = new GoogleAPIClientManager(context.getApplicationContext());
        }
        mInstance.addClient(client);
        return mInstance;
    }

    public GoogleApiClient getGoogleApiClient(){
        return mGoogleApiClient;
    }
    private GoogleAPIClientManager(Context context) {
        mClientsOfGoogleApi = new ArrayList<>();
        buildGoogleApiClient(context);
    }

    private void addClient(IUseGoogleApiClient client){
        mClientsOfGoogleApi.add(client);
    }
    protected synchronized void buildGoogleApiClient(Context context) {
        Log.d(TAG,"Inside buildGoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
            Log.d(TAG,"onConnectionFailed called");
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG,"onConnected called");
        for(IUseGoogleApiClient client:mClientsOfGoogleApi){
            client.onConnectedToGoogleAPIClient();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG,"onSuspended called");
    }
}
