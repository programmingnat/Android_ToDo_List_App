package com.imaginat.androidtodolist;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;
import com.imaginat.androidtodolist.google.ApiClientAsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Iterator;

public class BackupToDrive extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    protected static final int REQUEST_CODE_RESOLUTION = 1;
    protected static final int RESOLVE_CONNECTION_REQUEST_CODE=2;
    protected static final int COMPLETE_AUTHORIZATION_REQUEST_CODE=3;
    private DriveFile driveFileID=null;

    GoogleApiClient mGoogleApiClient;
    boolean isClientConnected=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup_to_drive);


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Drive.API)
                .addScope(Drive.SCOPE_FILE)
                .addScope(Drive.SCOPE_APPFOLDER)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();


        Button createButton = (Button)findViewById(R.id.createFileOnDrive);
        if(createButton!=null) {
            createButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isClientConnected) {
                        Drive.DriveApi.newDriveContents(mGoogleApiClient)
                                .setResultCallback(BackupToDrive.this.driveContentsCallback);
                    }else{
                        Toast.makeText(BackupToDrive.this,"client is not connected",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        Button findFileButton = (Button)findViewById(R.id.FindFileOnDrive);
        if(findFileButton!=null) {
            findFileButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isClientConnected) {
                        Query query = new Query.Builder()
                                .addFilter(Filters.eq(SearchableField.MIME_TYPE, "text/plain"))
                                .addFilter(Filters.eq(SearchableField.TITLE,"backupRemindeMe.txt"))
                                .build();
                        Drive.DriveApi.query(mGoogleApiClient, query)
                                .setResultCallback(metadataCallback);
                    } else {
                        Toast.makeText(BackupToDrive.this, "client is not connected", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        Button updateFileButton = (Button)findViewById(R.id.UpdateFileODrive);
        if(updateFileButton!=null) {
            updateFileButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isClientConnected) {
                        // Drive.DriveApi.newDriveContents(mGoogleApiClient)
                        //       .setResultCallback(driveContentsCallback);
                        EditContentsAsyncTask updateFile = new EditContentsAsyncTask(BackupToDrive.this);
                        updateFile.execute(driveFileID);
                    } else {
                        Toast.makeText(BackupToDrive.this, "client is not connected", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        Button readFileButton = (Button)findViewById(R.id.ReadFileODrive);
        if(readFileButton!=null) {
            readFileButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isClientConnected) {
                        // Drive.DriveApi.newDriveContents(mGoogleApiClient)
                        //       .setResultCallback(driveContentsCallback);
                        RetrieveDriveFileContentsAsyncTask retrieve = new RetrieveDriveFileContentsAsyncTask(BackupToDrive.this);
                        retrieve.execute(driveFileID.getDriveId());
                    } else {
                        Toast.makeText(BackupToDrive.this, "client is not connected", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }


    }


    final private ResultCallback<DriveApi.MetadataBufferResult> metadataCallback = new
            ResultCallback<DriveApi.MetadataBufferResult>() {
                @Override
                public void onResult(DriveApi.MetadataBufferResult result) {
                    if (!result.getStatus().isSuccess()) {
                        showMessage("Problem while retrieving results");
                        return;
                    }
                    MetadataBuffer mdBuffer=result.getMetadataBuffer();

                    try {
                        Iterator<Metadata> iterator = mdBuffer.iterator();
                        Metadata m=null;
                        int counter=0;
                        while(iterator.hasNext()==true){
                            m=iterator.next();
                            Log.d(TAG,counter+" FOUND: "+m.getOriginalFilename()+" "+m.getDriveId());
                            BackupToDrive.this.driveFileID=m.getDriveId().asDriveFile();
                            counter++;
                        }

                        mdBuffer.release();
//                        while(m!=null){
//                           Log.d(TAG,"FOUND: "+m.getOriginalFilename()+" "+m.getDriveId());
//                           m = iterator.next();
//                        }
                    }catch(Exception ex){
                        ex.printStackTrace();
                    }
                }
            };
    // [START drive_contents_callback]
    final private ResultCallback<DriveApi.DriveContentsResult> driveContentsCallback =
            new ResultCallback<DriveApi.DriveContentsResult>() {
                @Override
                public void onResult(DriveApi.DriveContentsResult result) {
                    if (!result.getStatus().isSuccess()) {
                        showMessage("Error while trying to create new file contents");
                        return;
                    }

                    MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                            .setTitle("backupRemindeMe.txt")
                            .setMimeType("text/plain")
                            .build();
                    Drive.DriveApi.getRootFolder(mGoogleApiClient)
                            .createFile(mGoogleApiClient, changeSet, result.getDriveContents())
                            .setResultCallback(fileCallback);
                }
            };
    // [END drive_contents_callback]

    final private ResultCallback<DriveFolder.DriveFileResult> fileCallback = new
            ResultCallback<DriveFolder.DriveFileResult>() {
                @Override
                public void onResult(DriveFolder.DriveFileResult result) {
                    if (!result.getStatus().isSuccess()) {
                        showMessage("Error while trying to create the file");
                        return;
                    }
                    showMessage("Created a file in App Folder: "
                            + result.getDriveFile().getDriveId());
                    Log.d(TAG,"Drive id: "+result.getDriveFile().getDriveId());
                }
            };
    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG,"on connected");
        isClientConnected=true;
        Toast.makeText(this,"CONNECTION MADE",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG,"connection suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG,"Connection failed");
        Toast.makeText(this,"CONNECTION NOT MADE",Toast.LENGTH_SHORT).show();
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(this, RESOLVE_CONNECTION_REQUEST_CODE);
            } catch (IntentSender.SendIntentException e) {
                // Unable to resolve, message user appropriately
            }
        } else {
            GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), this, 0).show();
        }
    }

    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        switch (requestCode) {

            case COMPLETE_AUTHORIZATION_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    // App is authorized, you can go back to sending the API request
                    Log.d(TAG,"USER ACCOUNT AUTHORIZED");
                } else {
                    // User denied access, show him the account chooser again
                    Log.d(TAG,"USER ACCOUNT DENIED");
                }
                break;
        }
    }



    public class EditContentsAsyncTask extends ApiClientAsyncTask<DriveFile, Void, Boolean> {

        public EditContentsAsyncTask(Context context) {
            super(context);
        }

        @Override
        protected Boolean doInBackgroundConnected(DriveFile... args) {
            DriveFile file = args[0];
            try {
                DriveApi.DriveContentsResult driveContentsResult = file.open(
                        getGoogleApiClient(), DriveFile.MODE_WRITE_ONLY, null).await();
                if (!driveContentsResult.getStatus().isSuccess()) {
                    return false;
                }
                DriveContents driveContents = driveContentsResult.getDriveContents();
                OutputStream outputStream = driveContents.getOutputStream();
                outputStream.write("Hello world. quick brown fox jumped over the lazy dog".getBytes());
                com.google.android.gms.common.api.Status status =
                        driveContents.commit(getGoogleApiClient(), null).await();
                return status.getStatus().isSuccess();
            } catch (IOException e) {
                Log.e(TAG, "IOException while appending to the output stream", e);
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (!result) {
                showMessage("Error while editing contents");
                Log.d(TAG,"Error edited contents");
                return;
            }
            showMessage("Successfully edited contents");
            Log.d(TAG,"Successfully edited contents");
        }
    }

    final private class RetrieveDriveFileContentsAsyncTask
            extends ApiClientAsyncTask<DriveId, Boolean, String> {

        public RetrieveDriveFileContentsAsyncTask(Context context) {
            super(context);
        }

        @Override
        protected String doInBackgroundConnected(DriveId... params) {
            String contents = null;
            Log.d(TAG,"inside doInBackground with "+params[0]);
            DriveFile file = params[0].asDriveFile();
            DriveApi.DriveContentsResult driveContentsResult =
                    file.open(getGoogleApiClient(), DriveFile.MODE_READ_ONLY, null).await();
            if (!driveContentsResult.getStatus().isSuccess()) {
                return null;
            }
            DriveContents driveContents = driveContentsResult.getDriveContents();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(driveContents.getInputStream()));
            StringBuilder builder = new StringBuilder();
            String line;
            try {
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
                contents = builder.toString();
            } catch (IOException e) {
                Log.e(TAG, "IOException while reading");
            }

            driveContents.discard(getGoogleApiClient());
            return contents;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result == null) {
                showMessage("Error while reading from the file");
                return;
            }
            showMessage("File contents: " + result);
        }


    }

    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }



}
