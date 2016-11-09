package com.bosch.myfootprint;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.estimote.sdk.SystemRequirementsChecker;
import com.flybits.core.api.Flybits;
import com.flybits.core.api.context.plugins.activity.ActivityProvider;
import com.flybits.core.api.context.plugins.battery.BatteryLifeProvider;
import com.flybits.core.api.context.plugins.carrier.CarrierProvider;
import com.flybits.core.api.context.plugins.language.LanguageProvider;
import com.flybits.core.api.context.plugins.location.LocationProvider;
import com.flybits.core.api.context.plugins.network.NetworkProvider;
import com.flybits.core.api.exceptions.FeatureNotSupportedException;
import com.flybits.core.api.interfaces.IRequestCallback;
import com.flybits.core.api.interfaces.IRequestLoggedIn;
import com.flybits.core.api.models.User;
import com.flybits.core.api.utils.filters.LoginOptions;

public class MainActivity extends AppCompatActivity {
    private final int MY_PERMISSIONS_REQUEST_READ_LOCATION = 10;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressDialog = new ProgressDialog(this);

        setProgressBar(getString(R.string.progressLogin), false);
        SystemRequirementsChecker.checkWithDefaultDialogs(this);
        //Check that Location permissions have been granted. This is only needed for Location/Beacon Context Plugin.
        checkForLoginPermissions();
    }
    private void checkIfUserIsAlreadyLoggedIn(final boolean isLocationActivated) {

        //Confirm that the user is currently connected to the Internet.
        if (ConnectivityUtils.isOnline(this)) {

            Flybits.include(this).isUserLoggedIn(true, new IRequestLoggedIn() {
                @Override
                public void onLoggedIn(User user) {
                    logUserIn();
                    stopProgressBar();
                }

                @Override
                public void onNotLoggedIn() {

                    LoginOptions options    = new LoginOptions.Builder(MainActivity.this)
                            .loginAnonymously()
                            .setRememberMeToken()
                            .setDeviceOSVersion()
                            .build();

                    Flybits.include(MainActivity.this).login(options, new IRequestCallback<User>() {
                        @Override
                        public void onSuccess(User user) {
                            activateContext(isLocationActivated);
                            logUserIn();
                        }

                        @Override
                        public void onException(Exception e) {
                            Toast.makeText(MainActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailed(String s) {

                        }

                        @Override
                        public void onCompleted() {
                            stopProgressBar();
                        }
                    });
                }
            });

        } else {
            //No Internet is currently available hence display an error.
            showAlertDialog(R.string.errorTitleNoInternet, R.string.errorMessageNoInternet, R.string.errorBtnPositive);
        }
    }

    private void setProgressBar(String text, boolean isCancelable) {
        progressDialog.setCancelable(isCancelable);
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

            public void onCancel(DialogInterface dialog) {

                if (progressDialog.isShowing())
                    progressDialog.dismiss();
            }
        });
        progressDialog.show();
        progressDialog.setMessage(text);
    }

    private void stopProgressBar() {
        try {
            if (progressDialog.isShowing())
                progressDialog.dismiss();
        } catch (Exception e) {}
    }

    private void logUserIn(){
//        Intent homeActivityIntent = new Intent(MainActivity.this, HomeActivity.class);
//        startActivity(homeActivityIntent);
//        finish();
    }

    private void activateContext(boolean isLocationActivated){

        try {
            ActivityProvider provider = new ActivityProvider(MainActivity.this, 60000);
            Flybits.include(MainActivity.this).activateContext(null, provider);

            BatteryLifeProvider provider2 = new BatteryLifeProvider(MainActivity.this, 60000);
            Flybits.include(MainActivity.this).activateContext(null, provider2);

            CarrierProvider provider3 = new CarrierProvider(MainActivity.this, 60000);
            Flybits.include(MainActivity.this).activateContext(null, provider3);

            LanguageProvider provider4 = new LanguageProvider(MainActivity.this, 60000);
            Flybits.include(MainActivity.this).activateContext(null, provider4);

            NetworkProvider provider5 = new NetworkProvider(MainActivity.this, 60000);
            Flybits.include(MainActivity.this).activateContext(null, provider5);

            //Only Allow Location/Beacon Providers to be activated if the user has allowed the application to use their location
            if (isLocationActivated){
                LocationProvider provider6 = new LocationProvider(MainActivity.this, 60000);
                Flybits.include(MainActivity.this).activateContext(null, provider6);

//                BeaconProvider provider7 = new BeaconProvider(MainActivity.this, 60000);
//                Flybits.include(MainActivity.this).activateContext(null, provider7);
            }

        }catch (FeatureNotSupportedException exception){

        }
    }

    private void checkForLoginPermissions() {

        if (Build.VERSION.SDK_INT < 23) {

            //Device SDK is less 6.0 therefore permissions do not need to be asked for specifically.
            checkIfUserIsAlreadyLoggedIn(true);
        } else {
            if (ContextCompat.checkSelfPermission(MainActivity.this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                //Request the location permission as the User has not yet granted it to the application.
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_READ_LOCATION);

            } else if (ContextCompat.checkSelfPermission(MainActivity.this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {

                //User has already granted the location permission in a previous session.
                checkIfUserIsAlreadyLoggedIn(true);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_LOCATION: {
                //Confirm the user has given permission for location to the application.
                boolean isLocationActivated = (grantResults.length > 0  && grantResults[0] == PackageManager.PERMISSION_GRANTED);
                checkIfUserIsAlreadyLoggedIn(isLocationActivated);
                return;
            }
        }
    }

    private void showAlertDialog(int title, int message, int buttonPositive){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(buttonPositive, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MainActivity.this.finish();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
