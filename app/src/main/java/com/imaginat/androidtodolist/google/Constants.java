package com.imaginat.androidtodolist.google;

/**
 * Created by nat on 5/16/16.
 */

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;

/**
 * Constant values reused in this sample.
 */
public final class Constants {
    public static final String PREFERENCES="ToDoListPreferences";

    public static final String GEO_ALARM_COUNT="numberOfGeoAlarmsActivated";
    public static final int SUCCESS_RESULT = 0;

    public static final int FAILURE_RESULT = 1;

    public static final String PACKAGE_NAME =
            "com.imaginat.androidtodolist";

    public static final String SHARED_PREFERENCES_NAME = PACKAGE_NAME + ".SHARED_PREFERENCES_NAME";


    public static final String GEOFENCES_ADDED_KEY = PACKAGE_NAME + ".GEOFENCES_ADDED_KEY";
    public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";

    public static final String RESULT_DATA_KEY = PACKAGE_NAME + ".RESULT_DATA_KEY";

    public static final String LOCATION_DATA_EXTRA = PACKAGE_NAME + ".LOCATION_DATA_EXTRA";
    public static final long GEOFENCE_EXPIRATION_IN_HOURS = 12;
    public static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS =
            GEOFENCE_EXPIRATION_IN_HOURS * 60 * 60 * 1000;
    public static final float GEOFENCE_RADIUS_IN_METERS = 200; // 1 mile, 1.6 km
    public static final String ALARM_TAG=PACKAGE_NAME+"ALARM_TAG";
    public static final String REMINDER_ID=PACKAGE_NAME+"REMINDER_ID";
    public static final String LIST_ID=PACKAGE_NAME+"LIST_ID";
    public static final String THE_TEXT=PACKAGE_NAME+"TEXT";


    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    /**
     * Map for storing information about airports in the San Francisco bay area.
     */
    public static final HashMap<String, LatLng> MY_LANDMARKS = new HashMap<String, LatLng>();
    static {
        //CRESTWOOD
        MY_LANDMARKS.put("CRESTWOOD TRAIN STATION", new LatLng(40.958997,-73.820564));

        // WARREN.
        MY_LANDMARKS.put("WARREN AVENUE", new LatLng(40.9618839,-73.8154516));

        //EASTCHESTER HIGH SCHOOL
        MY_LANDMARKS.put("EHS", new LatLng(40.961959, -73.817088));

        //LORD & TAYLORS
        MY_LANDMARKS.put("LORD&TAYLORS", new LatLng(40.972252, -73.803934));

        //KENSICO DAM
        MY_LANDMARKS.put("KENSICO DAM",new LatLng(41.073794, -73.766287));
    }
}