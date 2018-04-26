package com.nglinx.pulse.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.common.ConnectionResult;
import com.nglinx.pulse.R;
import com.nglinx.pulse.constants.ApplicationConstants;
import com.nglinx.pulse.constants.ErrorMessages;
import com.nglinx.pulse.session.DataSession;
import com.nglinx.pulse.utils.ApplicationUtils;
import com.nglinx.pulse.utils.ProgressbarUtil;
import com.nglinx.pulse.utils.dialog.AbstractActivity;
import com.nglinx.pulse.utils.dialog.DialogUtils;

import java.util.concurrent.atomic.AtomicInteger;

public class LoginActivity extends AbstractActivity implements View.OnClickListener {

    private static final int RC_SIGN_IN = 0;

    private boolean FLAG = true;

    private EditText et_username, et_password;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        classTag = LoginActivity.class.getName();

        et_username = (EditText) findViewById(R.id.et_username);
        et_password = (EditText) findViewById(R.id.et_password);
        btnLogin = (Button) findViewById(R.id.bt_login);

        // Button click listeners
        btnLogin.setOnClickListener(this);
    }

    private void OnRegisterClick(View v) {
       /* Intent registerIntent = new Intent(getApplicationContext(), UserRegistration.class);
        startActivity(registerIntent);*/
    }

    protected void onStart() {
        super.onStart();
    }

    protected void onStop() {
        super.onStop();
    }

    private void OnLoginButtonClick() {

        Log.v(classTag, "SignIn Function Entered.");

        String username = et_username.getText().toString().trim();
        String password = et_password.getText().toString().trim();

        if ((username == null) || (username.length() == 0) || (password == null) || (password.length() == 0)) {
            DialogUtils.diaplayErrorDialog(LoginActivity.this, ErrorMessages.INVALID_USERNAME_PASSWORF);
        } else {

            //Set the username and passwrd in the Data Session
            DataSession.getInstance().setUsername(username);
            DataSession.getInstance().setPassword(password);

            final ProgressDialog mProgressDialog1 = ProgressbarUtil.startProgressBar(LoginActivity.this);
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
                            ApplicationUtils.clearLoginRelatedInfo(getApplicationContext());
                            ProgressbarUtil.stopProgressBar(mProgressDialog1);
                            DialogUtils.diaplayErrorDialog(LoginActivity.this, (String) resultData.get(ApplicationConstants.ERROR_MSG));
                    }
                }
            });
            startService(intent);
        }
    }

    /*@Override
    protected void onResume() {
        super.onResume();
        et_username.getText().clear();
        et_password.getText().clear();
    }*/

    public void OnForgotPasswordClick(View V) {
        /*Intent intent = new Intent(getApplicationContext(), ForgotPasswordActivty.class);
        startActivity(intent);
        finish();*/
    }

    @Override
    public void onClick(View v) {
        System.out.println("onClick function entered");

        switch (v.getId()) {
            case R.id.bt_login:
                // Signin button clicked
                OnLoginButtonClick();
                break;
        }
    }

}