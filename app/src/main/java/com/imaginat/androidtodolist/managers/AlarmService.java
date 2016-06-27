package com.imaginat.androidtodolist.managers;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.imaginat.androidtodolist.MainActivity;
import com.imaginat.androidtodolist.R;
import com.imaginat.androidtodolist.google.Constants;

/**
 * Created by nat on 5/2/16.
 */
public class AlarmService extends IntentService {
    private NotificationManager alarmNotificationManager;

    public AlarmService() {
        super("AlarmService");
    }

    @Override
    public void onHandleIntent(Intent intent) {
        sendNotification("Wake Up! Wake Up!");
    }

    private void sendNotification(String msg) {
        Log.d("AlarmService", "Preparing to send notification...: " + msg);
        alarmNotificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);

        Intent alarmIntent = new Intent(this, MainActivity.class);
        alarmIntent.putExtra(Constants.INTENT_SOURCE,"AlarmService");

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                alarmIntent, 0);


        NotificationCompat.Builder alarmNotficationBuilder =
                new NotificationCompat.Builder(this);
        alarmNotficationBuilder.setContentTitle("Alarm Test");
        alarmNotficationBuilder.setSmallIcon(R.drawable.alarm_clock_white);
        alarmNotficationBuilder.setContentText(msg);



        alarmNotficationBuilder.setContentIntent(contentIntent);
        alarmNotificationManager.notify(1, alarmNotficationBuilder.build());
        Log.d("AlarmService", "Notification sent.");
    }
}
