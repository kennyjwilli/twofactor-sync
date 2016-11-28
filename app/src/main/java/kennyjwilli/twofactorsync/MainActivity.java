package kennyjwilli.twofactorsync;

import android.content.Intent;
import android.content.IntentSender;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataChangeSet;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final int RESOLVE_CONNECTION_REQUEST_CODE = 1;
    private GoogleApiClient apiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        apiClient = new GoogleApiClient.Builder(getApplicationContext())
                .addApi(Drive.API)
                .addScope(Drive.SCOPE_APPFOLDER)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();


        Button loginBtn = (Button) findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("login drive...");
                //apiClient.connect();
                System.out.println("made drive client");
            }
        });

        Button testBtn = (Button) findViewById(R.id.testBtn);
        testBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("upload...");
                //https://github.com/googledrive/android-demos/blob/master/app/src/main/java/com/google/android/gms/drive/sample/demo/CreateFileInAppFolderActivity.java
                Drive.DriveApi.newDriveContents(apiClient).setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {
                    @Override
                    public void onResult(@NonNull DriveApi.DriveContentsResult result) {
                        if (!result.getStatus().isSuccess()) {
                            showMessage("Error while trying to create new file contents");
                            return;
                        }
                        MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                .setTitle("appconfig.txt")
                                .setMimeType("text/plain")
                                .build();
                        Drive.DriveApi.getAppFolder(apiClient)
                                .createFile(apiClient, changeSet, result.getDriveContents())
                                .setResultCallback(new ResultCallback<DriveFolder.DriveFileResult>() {
                                    @Override
                                    public void onResult(@NonNull DriveFolder.DriveFileResult result) {
                                        if (!result.getStatus().isSuccess()) {
                                            showMessage("Error while trying to create the file");
                                            return;
                                        }
                                        showMessage("Created a file in App Folder: "
                                                + result.getDriveFile().getDriveId());
                                    }
                                });
                    }
                });
                // https://developers.google.com/drive/android/appfolder
                DriveFolder appFolder = Drive.DriveApi.getAppFolder(apiClient);
//                Drive.DriveApi.newDriveContents(apiClient).setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {
//                    @Override
//                    public void onResult(@NonNull DriveApi.DriveContentsResult result) {
//                        if (!result.getStatus().isSuccess()) {
//                            Log.i(Util.TAG, "Failed to make contents");
//                            return;
//                        }
//
//                    }
//                });
            }
        });
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        switch (requestCode) {
            case RESOLVE_CONNECTION_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    apiClient.connect();
                }
                break;
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(Util.TAG, "API client connected.");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(Util.TAG, "GoogleApiClient connection suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        // Called whenever the API client fails to connect.
        Log.i(Util.TAG, "GoogleApiClient connection failed: " + result.toString());
        if (result.hasResolution()) {
            try {
                result.startResolutionForResult(this, RESOLVE_CONNECTION_REQUEST_CODE);
            } catch (IntentSender.SendIntentException e) {
                // Unable to resolve, message user appropriately
            }
        } else {
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this, 0).show();
        }
        // The failure has a resolution. Resolve it.
        // Called typically when the app is not yet authorized, and an
        // authorization
        // dialog is displayed to the user.
//        try {
//            result.startResolutionForResult(this, REQUEST_CODE_RESOLUTION);
//        } catch (IntentSender.SendIntentException e) {
//            Log.e(Util.TAG, "Exception while starting resolution activity", e);
//        }
    }

    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
