package com.imaginat.androidtodolist.customlayouts.alarm;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.imaginat.androidtodolist.R;

/**
 * Created by nat on 6/11/16.
 */




public class DialogMapFragment extends DialogFragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Location mCurrentLocation;
    private static final String TAG=DialogMapFragment.class.getSimpleName();
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //View view= super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.map_dialog, container, false);

        Fragment fragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.map);
        SupportMapFragment mapFragment = (SupportMapFragment)fragment;
        mapFragment.getMapAsync(this);

        Button saveButton = (Button)view.findViewById(R.id.closeButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        Button cancelButton =(Button)view.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return view;
    }


    public void setCurrentLocation(Location loc){
        mCurrentLocation=loc;
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Log.d(TAG,"onMapREady");
        googleMap.addMarker(new MarkerOptions().position(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude())).title("marker title"));
                        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                            @Override
                            public void onMapClick(LatLng latLng) {
                                Toast.makeText(getContext(), "onMapClick", Toast.LENGTH_SHORT).show();
                            }
                        });
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()), 12.0f));
    }
}
