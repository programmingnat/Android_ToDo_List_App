package com.imaginat.androidtodolist.google;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import com.google.android.gms.vision.barcode.Barcode;

import java.util.List;

/**
 * Created by nat on 5/17/16.
 */
public class GeoCoder {


    public static void startIntentService(Context c,Location location,AddressResultReceiver resultReceiver) {
        Intent intent = new Intent(c, FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, resultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, location);
        c.startService(intent);
    }

    public static Location getLocationFromAddress(Context c,String strAddress){

        Geocoder coder = new Geocoder(c);
        List<Address> address;
        Barcode.GeoPoint p1 = null;
        Location l=null;
        try {
            address = coder.getFromLocationName(strAddress,5);
            if (address==null) {
                return null;
            }
            Address location=address.get(0);

            l = new Location("");
            l.setLatitude(location.getLatitude());
            l.setLongitude(location.getLongitude());



        }catch(Exception ex){
            ex.printStackTrace();
        }
        return l;
    }
}
