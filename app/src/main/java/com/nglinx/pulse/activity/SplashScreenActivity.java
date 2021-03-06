package com.nglinx.pulse.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.view.Window;

import com.nglinx.pulse.R;
import com.nglinx.pulse.constants.ApplicationConstants;
import com.nglinx.pulse.constants.ErrorMessages;
import com.nglinx.pulse.session.SharedPrefUtility;
import com.nglinx.pulse.utils.ApplicationUtils;
import com.nglinx.pulse.utils.ProgressbarUtil;
import com.nglinx.pulse.utils.dialog.DialogUtils;
import com.nglinx.pulse.utils.internet.InternetUtils;
import io.fabric.sdk.android.Fabric;
import com.crashlytics.android.Crashlytics;

import java.util.Timer;
import java.util.TimerTask;

public class SplashScreenActivity extends Activity {

    boolean isTaskCompleted = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

        // Remove the Title Bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // Get the view from splash_screen.xml
        setContentView(R.layout.splash_screen);

        // Create a Timer
        Timer splashTimer = new Timer();

        // Task to do when the timer ends
        TimerTask showSplash = new TimerTask() {
            @Override
            public void run() {
                // Close SplashScreenActivity.class
                isTaskCompleted = true;
            }
        };

        // Start the timer
        splashTimer.schedule(showSplash, ApplicationConstants.SPLASH_SCREEN_DELAY);
        startLogin();
    }


    private void startLogin() {

        //Start after the Splash screen loading gets Completed.
        while (!isTaskCompleted) {
            try {
                Thread.sleep(ApplicationConstants.SPLASH_SCREEN_DELAY);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //If Internet not present, return error.
        if (!InternetUtils.isConnectingToInternet(getApplicationContext())) {
            DialogUtils.diaplayErrorDialog(SplashScreenActivity.this, ErrorMessages.NO_INTERNET_CONNECTION);
        } else {

            //If the user is not logged in previously, direct him to Sensor Screen screen.
            if (!SharedPrefUtility.isOneTimeLoggedIn(getApplicationContext())) {
                ApplicationUtils.clearLoginRelatedInfo(getApplicationContext());
                // Start MainActivity.class
                Intent myIntent = new Intent(SplashScreenActivity.this,
                        LoginActivity.class);
                startActivity(myIntent);
                finish();
            } else {
                ApplicationUtils.setLoginRelatedInfoToDataSession(getApplicationContext());

                final ProgressDialog mProgressDialog1 = ProgressbarUtil.startProgressBar(SplashScreenActivity.this, "Logging In...");
                Intent intent = new Intent(Intent.ACTION_SYNC, null, this, AuthenticateIntentService.class);
                intent.putExtra("receiver", new ResultReceiver(new Handler()) {
                    @Override
                    protected void onReceiveResult(int resultCode, Bundle resultData) {
                        switch (resultCode) {
                            case ApplicationConstants.STATUS_FINISHED:
                                ProgressbarUtil.stopProgressBar(mProgressDialog1);
                                Intent intent = new Intent(getApplicationContext(), SensorsActivity.class);
                                startActivity(intent);
                                finish();
                                break;
                            case ApplicationConstants.STATUS_ERROR:
                                ProgressbarUtil.stopProgressBar(mProgressDialog1);
                                ApplicationUtils.clearLoginRelatedInfo(getApplicationContext());
                                DialogUtils.diaplayDialogAndStartIntentOnOk(SplashScreenActivity.this, "Error", (String) resultData.get(ApplicationConstants.ERROR_MSG), LoginActivity.class);
                        }
                    }
                });
                startService(intent);
            }
        }
    }
}