package com.imaginat.androidtodolist.customlayouts;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.imaginat.androidtodolist.R;
import com.imaginat.androidtodolist.businessModels.AlarmReceiver;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by nat on 5/1/16.
 */
public class ToDoListOptionsFragment extends Fragment   {



    public interface IGeoOptions{
        public void getAddressFromLocation();
        public void setGeoFenceAddress(String street,String city,String state, String zipCode);
        public void removeGeoFence();


    }
    private static final String TAG =ToDoListOptionsFragment.class.getSimpleName();
    private static final String DIALOG_DATE="DialogDate";
    private static final String DIALOG_TIME="DialogTime";
    private static final int REQUEST_DATE=0;
    private static final int REQUEST_TIME=1;

    AlarmManager alarmManager;
    private PendingIntent pendingIntent;
   // private TimePicker alarmTimePicker;
    private TextView alarmTextView;
    private TextView displayAlarmDateTextView,displayAlarmTimeTextView;
    //private Date mAlarmDate,mAlarmTime;
    private Calendar mAlarmCalendar;
    private Button  mCoordinatesToAddressButton;
    private Button mRemoveFenceButton;
    private IGeoOptions mIGeoOptions;





    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.todos_more_options, container, false);



        mAlarmCalendar = Calendar.getInstance();
        Button selectDateButton = (Button) view.findViewById(R.id.selectDate_button);
        selectDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(new Date(System.currentTimeMillis()));
                //DatePickerFragment dialog = new DatePickerFragment();
                dialog.setTargetFragment(ToDoListOptionsFragment.this, REQUEST_DATE);
                dialog.show(manager, DIALOG_DATE);
            }
        });

        Button selectTimeButton = (Button) view.findViewById(R.id.selectTime_button);
        selectTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                TimePickerFragment dialog = TimePickerFragment.newInstance(new Date(System.currentTimeMillis()));
                //TimePickerFragment dialog = new TimePickerFragment();
                dialog.setTargetFragment(ToDoListOptionsFragment.this, REQUEST_TIME);
                dialog.show(manager, DIALOG_TIME);

            }
        });
        displayAlarmTimeTextView = (TextView) view.findViewById(R.id.displayTime_textView);
        displayAlarmDateTextView = (TextView) view.findViewById(R.id.displayDate_textView);
        // alarmTimePicker = (TimePicker) view.findViewById(R.id.alarmTimePicker);
        alarmTextView = (TextView) view.findViewById(R.id.alarmText);
        Switch alarmToggle = (Switch) view.findViewById(R.id.alarmToggle);
        alarmManager = (AlarmManager) getActivity().getSystemService(getContext().ALARM_SERVICE);

        alarmToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                if (isChecked) {
                    Log.d("MyActivity", "Alarm On");

                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.HOUR_OF_DAY, mAlarmCalendar.get(Calendar.HOUR_OF_DAY));
                    calendar.set(Calendar.MINUTE, mAlarmCalendar.get(Calendar.MINUTE));
                    Intent myIntent = new Intent(getContext(), AlarmReceiver.class);
                    //Intent myIntent = new Intent(getContext(), AlarmService.class);
                    pendingIntent = PendingIntent.getBroadcast(getContext(), 0, myIntent, 0);
                    //pendingIntent = PendingIntent.getService(getContext(), 0, myIntent, 0);
                    alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);


                    Date copiedDate = new Date(calendar.getTimeInMillis());

                    DateFormat df = new SimpleDateFormat("MM.dd:yy:HH:mm:ss");
                    Log.d(TAG, "pendingItent sent for " + df.format(copiedDate));
                } else {
                    alarmManager.cancel(pendingIntent);
                    Log.d("MyActivity", "Alarm Off");
                }
            }
        });

        mCoordinatesToAddressButton =(Button)view.findViewById(R.id.getAddressFromCoordinates);
        mCoordinatesToAddressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mIGeoOptions.getAddressFromLocation();
                mIGeoOptions.setGeoFenceAddress("Crestwood Train Station", "Tuckahoe", "NY", "10707");//71 Warren Ave","Tuckahoe","NY","10707");
            }
        });

        mRemoveFenceButton =(Button)view.findViewById(R.id.removeFencesButton);
        mRemoveFenceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIGeoOptions.removeGeoFence();
            }
        });
        return view;
    }

    public void setIGeoOptions(IGeoOptions IGeoOptions) {
        mIGeoOptions = IGeoOptions;
    }

    public void setAlarmText(String alarmText) {
        alarmTextView.setText(alarmText);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != Activity.RESULT_OK){
            return;
        }

        if(requestCode == REQUEST_DATE){
            Date date = (Date)data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            Calendar result = Calendar.getInstance();
            result.setTime(date);
           // mAlarmDate=date;
            SimpleDateFormat sf = new SimpleDateFormat("MM.dd.yy");
            displayAlarmDateTextView.setText(sf.format(date));
            mAlarmCalendar.set(Calendar.MONTH, result.get(Calendar.MONTH));
            mAlarmCalendar.set(Calendar.DAY_OF_MONTH,result.get(Calendar.DAY_OF_MONTH));
            mAlarmCalendar.set(Calendar.YEAR,result.get(Calendar.YEAR));
        }else if(requestCode==REQUEST_TIME){
            Date date = (Date)data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
            Calendar result = Calendar.getInstance();
            result.setTime(date);
            //mAlarmTime=date;
            SimpleDateFormat sf = new SimpleDateFormat("hh:mm:ss a");
            displayAlarmTimeTextView.setText(sf.format(date));
            mAlarmCalendar.set(Calendar.HOUR_OF_DAY,result.get(Calendar.HOUR_OF_DAY));
            mAlarmCalendar.set(Calendar.MINUTE,result.get(Calendar.MINUTE));

        }

    }


}
