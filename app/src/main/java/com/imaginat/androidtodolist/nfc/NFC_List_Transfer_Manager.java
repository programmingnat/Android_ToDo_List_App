package com.imaginat.androidtodolist.nfc;

import android.app.Activity;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Build;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import com.imaginat.androidtodolist.managers.ListManager;
import com.imaginat.androidtodolist.managers.ToDoListItemManager;

import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * Created by nat on 6/28/16.
 */
public class NFC_List_Transfer_Manager  implements
        NfcAdapter.OnNdefPushCompleteCallback,
        NfcAdapter.CreateNdefMessageCallback{

    public interface INFCTransferManager{
        public Activity getActivityReference();
    }
    private static final String TAG=NFC_List_Transfer_Manager.class.getSimpleName();
    private static NFC_List_Transfer_Manager instance;

    private NfcAdapter mNfcAdapter;
    private ArrayList<String> messagesToSendArray = new ArrayList<>();
    private ArrayList<String> messagesReceivedArray = new ArrayList<>();

    private INFCTransferManager mCallingActivity;

    private NFC_List_Transfer_Manager(INFCTransferManager itm){
        mCallingActivity=itm;
    }
    public static NFC_List_Transfer_Manager getInstance(INFCTransferManager itm){
        if(instance==null){
            instance = new NFC_List_Transfer_Manager(itm);
        }
        return instance;
    }

    public boolean init(Activity activity){
        if (isNFCAvail()) {
            //This will refer back to createNdefMessage for what it will send
            mNfcAdapter.setNdefPushMessageCallback(this, activity);

            //This will be called if the message is sent successfully
            mNfcAdapter.setOnNdefPushCompleteCallback(this, activity);
            return true;

        }else{
            return false;
        }
    }
    public void populateMessagesToSend(String listTitle,ArrayList<String>reminders){

        messagesToSendArray.clear();
        messagesToSendArray.add(listTitle);
        for(String word:reminders){
            messagesToSendArray.add(word);
        }
        Log.d(TAG,"reminders size: "+reminders.size());

        Log.d(TAG,"messagesToSendArray size: "+messagesToSendArray.size());
    }

    public boolean isNFCAvail(){
        mNfcAdapter = NfcAdapter.getDefaultAdapter(mCallingActivity.getActivityReference());
        if(mNfcAdapter != null) {
            return true;

        }
        else {
            return false;
        }
    }

    public void addToNFCSendList(String data){
        messagesToSendArray.add(data);
    }
    public int getMessageCount(){
        return messagesToSendArray.size();
    }
    public void clearMessageQueue(){
        messagesToSendArray.clear();
    }


    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        Activity activity = mCallingActivity.getActivityReference();
        //if list has nothing in it, return null
        Log.d(TAG,"createNdefMessage");

        //This will be called when another NFC capable device is detected.
        if (messagesToSendArray.size() == 0) {
            Log.d(TAG,"createNdefMessage: leaving early");
            return null;
        }
        Log.d(TAG,"About to send messages, there are "+messagesToSendArray.size()+" in the queue");
        //We'll write the createRecords() method in just a moment
        NdefRecord[] recordsToAttach = createRecords(activity);
        //When creating an NdefMessage we need to provide an NdefRecord[]
        return new NdefMessage(recordsToAttach);
    }

    @Override
    public void onNdefPushComplete(NfcEvent event) {
        messagesToSendArray.clear();
    }

    public NdefRecord[] createRecords(Activity activity) {
        Log.d(TAG,"inside createRecords");
        NdefRecord[] records = new NdefRecord[messagesToSendArray.size() + 1];
        //To Create Messages Manually if API is less than
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            for (int i = 0; i < messagesToSendArray.size(); i++){
                byte[] payload = messagesToSendArray.get(i).
                        getBytes(Charset.forName("UTF-8"));
                NdefRecord record = new NdefRecord(
                        NdefRecord.TNF_WELL_KNOWN,      //Our 3-bit Type name format
                        NdefRecord.RTD_TEXT,            //Description of our payload
                        new byte[0],                    //The optional id for our Record
                        payload);                       //Our payload for the Record

                records[i] = record;
            }
        }
        //Api is high enough that we can use createMime, which is preferred.
        else {
            for (int i = 0; i < messagesToSendArray.size(); i++){
                byte[] payload = messagesToSendArray.get(i).
                        getBytes(Charset.forName("UTF-8"));

                NdefRecord record = NdefRecord.createMime("text/plain",payload);
                records[i] = record;
            }
        }
        records[messagesToSendArray.size()] = NdefRecord.createApplicationRecord(activity.getPackageName());
        return records;
    }

    public void handleNfcIntent(Intent NfcIntent,Activity activity) {
        Log.d(TAG,"handleNFcIntent");
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(NfcIntent.getAction())) {
            Parcelable[] receivedArray =
                    NfcIntent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

            if(receivedArray != null) {
                messagesReceivedArray.clear();
                NdefMessage receivedMessage = (NdefMessage) receivedArray[0];
                NdefRecord[] attachedRecords = receivedMessage.getRecords();

                for (NdefRecord record:attachedRecords) {
                    String string = new String(record.getPayload());
                    //Make sure we don't pass along our AAR (Android Applicatoin Record)
                    if (string.equals(activity.getPackageName())) { continue; }
                    messagesReceivedArray.add(string);

                }
                Toast.makeText(activity, "Received " + messagesReceivedArray.size() +
                        " Messages", Toast.LENGTH_LONG).show();

                //updateTextViews();
                ListManager listManager = ListManager.getInstance(activity);
                ToDoListItemManager toDoListItemManager = ToDoListItemManager.getInstance(activity);
                int total=messagesReceivedArray.size();
                String listID=null;
                for(int i=0;i<total;i++){
                    String s = messagesReceivedArray.get(i);
                    Log.d(TAG,s);
                    if(i==0){
                        listID=listManager.createNewList(s,0);
                        continue;
                    }
                    toDoListItemManager.createNewReminder(listID,s);

                }
            }
            else {
                Toast.makeText(activity, "Received Blank Parcel", Toast.LENGTH_LONG).show();
            }

        }
        Intent dummyIntent = new Intent();
        dummyIntent.setAction("stand in intent");
        activity.setIntent(dummyIntent);
    }
}
