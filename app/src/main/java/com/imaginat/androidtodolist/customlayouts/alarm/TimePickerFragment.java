package com.imaginat.androidtodolist.customlayouts.alarm;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TimePicker;

import com.imaginat.androidtodolist.R;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by nat on 5/2/16.
 */
public class TimePickerFragment extends DialogFragment{

    public static final String EXTRA_TIME =
            "com.imaginat.androidtodolist.time";

    private static final String ARG_TIME = "time";
    private TimePicker mTimePicker;


    public static TimePickerFragment newInstance(Date date){
        Bundle args = new Bundle();
        args.putSerializable(ARG_TIME, date);

        TimePickerFragment fragment = new TimePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Date date = (Date)getArguments().getSerializable(ARG_TIME);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);


        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_time, null);
        mTimePicker = (TimePicker)v.findViewById(R.id.dialog_time_picker);

        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= 23){
            // Do something for lollipop and above versions
            mTimePicker.setHour(hour);
            mTimePicker.setMinute(min);
        } else{
            // do something for phones running an SDK before lollipop
            mTimePicker.setCurrentHour(hour);
            mTimePicker.setCurrentMinute(min);
        }

        mTimePicker.setIs24HourView(false);

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle("TIME PICKER")
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int currentapiVersion = android.os.Build.VERSION.SDK_INT;
                                int hour=0,min=0;
                                if (currentapiVersion >= 23){
                                     hour = mTimePicker.getHour();
                                     min = mTimePicker.getMinute();

                                }else{
                                    hour = mTimePicker.getCurrentHour();
                                    min = mTimePicker.getCurrentMinute();
                                }

                                //ignore year,month, day. we just want time
                                Date date = new GregorianCalendar(2016,1,1,hour,min).getTime();
                                sendResult(Activity.RESULT_OK,date);
                            }
                        }
                )
                .create();
    }

    private void sendResult(int resultCode,Date date){
        if(getTargetFragment()==null){
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(EXTRA_TIME,date);
        getTargetFragment().onActivityResult(getTargetRequestCode(),resultCode,intent);
    }
}
