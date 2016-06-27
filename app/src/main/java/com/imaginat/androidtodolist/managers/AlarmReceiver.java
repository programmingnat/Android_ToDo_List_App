package com.imaginat.androidtodolist.managers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

/**
 * Created by nat on 5/2/16.
 */
public class AlarmReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {
        //this will update the UI with message
        //MainActivity inst = MainActivity.instance();
        // inst.setAlarmText("Alarm! Wake up! Wake up!");

        //this will sound the alarm tone
        //this will sound the alarm once, if you wish to
        //raise alarm in loop continuously then use MediaPlayer and setLooping(true)
        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alarmUri == null) {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }
        Ringtone ringtone = RingtoneManager.getRingtone(context, alarmUri);
        ringtone.play();

        Log.d("AlarmReceiver","Attempting to startwarkeful seravice");
        //this will send a notification message
        Intent myIntent = new Intent(context, AlarmService.class);
        startWakefulService(context,myIntent);
        //ComponentName comp = new ComponentName("com.imaginat.androidtodolist.businessModels",
        //        "com.imaginat.androidtodolist.businessModels.AlarmService");

        //startWakefulService(context, (intent.setComponent(comp)));
        setResultCode(Activity.RESULT_OK);
        Log.d("AlarmReceiver", "hopefully just called to startwarkeful seravice");
    }
}