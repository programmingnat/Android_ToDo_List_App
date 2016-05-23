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
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.imaginat.androidtodolist.R;
import com.imaginat.androidtodolist.businessModels.AlarmReceiver;
import com.imaginat.androidtodolist.businessModels.ToDoListItem;
import com.imaginat.androidtodolist.businessModels.ToDoListItemManager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by nat on 5/1/16.
 */
public class ToDoListOptionsFragment extends Fragment   {


    private ToDoListItem mToDoListItem;


    public interface IGeoOptions{
        public void getAddressFromLocation();
        public void setGeoFenceAddress(String street,String city,String state, String zipCode,String alarmTag);
        public void removeGeoFence();


    }
    private static final String TAG =ToDoListOptionsFragment.class.getSimpleName();
    private static final String DIALOG_DATE="DialogDate";
    private static final String DIALOG_TIME="DialogTime";
    private static final int REQUEST_DATE=0;
    private static final int REQUEST_TIME=1;

    private String mListID,mItemID;

    public String getListID() {
        return mListID;
    }

    public void setListID(String listID) {
        mListID = listID;
    }

    public String getItemID() {
        return mItemID;
    }

    public void setItemID(String itemID) {
        mItemID = itemID;
    }

    AlarmManager alarmManager;
    private PendingIntent pendingIntent;
   // private TimePicker alarmTimePicker;
    private EditText mEditTextOfListItem;
    private TextView alarmTextView;
    private TextView displayAlarmDateTextView,displayAlarmTimeTextView;
    //private Date mAlarmDate,mAlarmTime;
    private Calendar mAlarmCalendar;
    private Button  mCoordinatesToAddressButton;
    private Button mRemoveFenceButton;
    private IGeoOptions mIGeoOptions;
    private EditText mStreetAddress_EditText,mCity_EditText,mState_EditText,mZip_EditText;





    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.todos_more_options, container, false);

        //I. GET REFERENCE TO VIEWS
        mEditTextOfListItem = (EditText)view.findViewById(R.id.theItemText_EditText);
        displayAlarmTimeTextView = (TextView) view.findViewById(R.id.displayTime_textView);
        displayAlarmDateTextView = (TextView) view.findViewById(R.id.displayDate_textView);

        mStreetAddress_EditText = (EditText)view.findViewById(R.id.streetAddress_editText);
        mCity_EditText = (EditText)view.findViewById(R.id.city_editText);
        mState_EditText=(EditText)view.findViewById(R.id.state_editText);
        mZip_EditText=(EditText)view.findViewById(R.id.zipCode_editText);

        // alarmTimePicker = (TimePicker) view.findViewById(R.id.alarmTimePicker);
        alarmTextView = (TextView) view.findViewById(R.id.alarmText);
        Switch alarmToggle = (Switch) view.findViewById(R.id.alarmToggle);
        alarmManager = (AlarmManager) getActivity().getSystemService(getContext().ALARM_SERVICE);

        //II. GET INFO BASED ON TO THE listID,itemID
        ToDoListItemManager itemManager = ToDoListItemManager.getInstance(getContext());
        mToDoListItem = itemManager.getSingleListItem(mListID,mItemID);
        Log.d(TAG,mToDoListItem.getText());

        mAlarmCalendar = Calendar.getInstance();

        //III. POPULATE PAGE CONTROLS WITH INFO (from database)
        //A.set the text
        mEditTextOfListItem.setText(mToDoListItem.getText());

        //B. set calendar alarm if set
        SimpleDateFormat sf;
        if(mToDoListItem.isCalendarAlarm()){
            //get the date
            sf = new SimpleDateFormat("MM.dd.yy H:m");
            String theDate = mToDoListItem.getAlarmMonth()+"."+mToDoListItem.getAlarmDay()+"."+(mToDoListItem.getAlarmYear()-2000);
            //get the time
            String theTime=mToDoListItem.getAlarmHour()+":"+mToDoListItem.getAlarmMin();
            Date date=null;

            try {
                date = sf.parse(theDate+" "+theTime );
            }catch(Exception ex){
                ex.printStackTrace();
            }
            setAlarmDate(date);
            setAlarmCalendarTime(date);
            if(mToDoListItem.isCalendarAlarmActive()) {
                alarmToggle.setChecked(true);
            }
        }
        //3. set geofence alarm if set
        //if(toDoListItem.isGeoFenceAlarm()){
           /* sf = new SimpleDateFormat("hh:mm:ss a");
            String theTime = "1:34:30 pm";
            try{
                date = sf.parse(theTime);
            }catch(Exception ex){

            }
            setAlarmCalendarTime(date);*/
       // }


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


        alarmToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                //SETTING THE CALENDAR ALARM
                ToDoListItemManager listItemManager = ToDoListItemManager.getInstance(getContext());

                //I. CREATING THE INTENT (using a custom tag). Intent can be used to start or cancel alarm
                Intent myIntent = new Intent(getContext(), AlarmReceiver.class);
                pendingIntent = PendingIntent.getBroadcast(getContext(), ToDoListOptionsFragment.this.createAlarmTag(),
                        myIntent, 0);

                //II. IF THE switch is set to ON, update info, save it, and
                if (isChecked) {
                    alarmManager.set(AlarmManager.RTC, mAlarmCalendar.getTimeInMillis(), pendingIntent);
                    //now save current setting in database
                    DateFormat df = new SimpleDateFormat("MM.dd:yy:HH:mm:ss");
                    ToDoListItemManager itemManager = ToDoListItemManager.getInstance(getContext());
                    itemManager.saveCalendarAlarm(getCalendarAlarmID(),mItemID,1+mAlarmCalendar.get(Calendar.MONTH),mAlarmCalendar.get(Calendar.DATE),mAlarmCalendar.get(Calendar.YEAR),
                            mAlarmCalendar.get(Calendar.HOUR_OF_DAY),mAlarmCalendar.get(Calendar.MINUTE),true);
                } else {
                    //cancel the pending intent here  (with alarmManager and in database)
                    alarmManager.cancel(pendingIntent);
                    ToDoListItemManager itemManager = ToDoListItemManager.getInstance(getContext());
                    itemManager.toggleCalendarAlarm(getCalendarAlarmID(),0);
                }
            }
        });


        mCoordinatesToAddressButton =(Button)view.findViewById(R.id.getAddressFromCoordinates);
        mCoordinatesToAddressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mIGeoOptions.getAddressFromLocation();
                String streetAddress = mStreetAddress_EditText.getText().toString();
                String cityAddress = mCity_EditText.getText().toString();
                String stateAddress = mState_EditText.getText().toString();
                String zipAddress = mZip_EditText.getText().toString();
                //mIGeoOptions.setGeoFenceAddress(streetAddress, cityAddress, stateAddress, zipAddress);
                mIGeoOptions.setGeoFenceAddress(streetAddress, cityAddress, stateAddress, zipAddress,ToDoListOptionsFragment.this.getCalendarAlarmID());//71 Warren Ave","Tuckahoe","NY","10707");
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
            setAlarmDate(date);

        }else if(requestCode==REQUEST_TIME){
            Date date = (Date)data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
            setAlarmCalendarTime(date);

        }



    }

    //=====================CALENDAR ALARM RELATED HELPERS========================================
    private void setAlarmDate(Date date){
        Calendar result = Calendar.getInstance();
        result.setTime(date);
        // mAlarmDate=date;
        SimpleDateFormat sf = new SimpleDateFormat("MM.dd.yy");
        displayAlarmDateTextView.setText(sf.format(date));
        mAlarmCalendar.set(Calendar.MONTH, result.get(Calendar.MONTH));
        mAlarmCalendar.set(Calendar.DAY_OF_MONTH,result.get(Calendar.DAY_OF_MONTH));
        mAlarmCalendar.set(Calendar.YEAR,result.get(Calendar.YEAR));
    }
    private void setAlarmCalendarTime(Date date){

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        SimpleDateFormat sf = new SimpleDateFormat("hh:mm:ss a");
        displayAlarmTimeTextView.setText(sf.format(date));
        mAlarmCalendar.set(Calendar.HOUR_OF_DAY,calendar.get(Calendar.HOUR_OF_DAY));
        mAlarmCalendar.set(Calendar.MINUTE,calendar.get(Calendar.MINUTE));
    }

    private String getCalendarAlarmID(){
        String substring = mToDoListItem.getText().substring(0,5);
        return substring+"_L"+mListID+"I"+mItemID;
    }

    private int createAlarmTag(){
        String result = getCalendarAlarmID();
        int strlen = result.length();
        int hash = 7;
        for (int i = 0; i < strlen; i++) {
            hash = hash*31 + result.charAt(i);
        }
        return hash;
    }
    //=========================GEO FENCE RELATED====================================================
}
