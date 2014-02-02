/*
 * Copyright 2014 Daniel Pedraza-Arcega
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.twitt4droid.app.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import com.twitt4droid.app.util.Dialogs;
import com.twitt4droid.app.util.Networks;

public class GooglePlayServicesCheckerActivity extends Activity {
    
    private static final String TAG = GooglePlayServicesCheckerActivity.class.getSimpleName();
    private static final int ON_GMS_ERROR_REQUEST_CODE = 9;
    
    @Override
    protected void onResume() {
        super.onResume();
        
        if (Networks.isConnectedToInternet(this)) {
            checkGooglePlayServicesAvailability();
        } else {
            Dialogs.getNetworkAlertDialog(this).show();
        }
    }
    
    private void checkGooglePlayServicesAvailability() {
        int errorCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (errorCode != ConnectionResult.SUCCESS) {
            switch (errorCode) {
                case ConnectionResult.SERVICE_DISABLED:
                    Log.e(TAG, "The installed version of Google Play services has been disabled on this device");
                    break;
                case ConnectionResult.SERVICE_INVALID:
                    Log.e(TAG, "The version of the Google Play services installed on this device is not authentic");
                    break;
                case ConnectionResult.SERVICE_MISSING:
                    Log.e(TAG, "Google Play services is missing on this device");
                    break;
                case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
                    Log.e(TAG, "The installed version of Google Play services is out of date");
                    break;
            }

            GooglePlayServicesUtil.getErrorDialog(errorCode, this, ON_GMS_ERROR_REQUEST_CODE,
                new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        dialog.dismiss();
                        finish();
                    }
                }).show();
        } else {
            startSignInActivity();
        }
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ON_GMS_ERROR_REQUEST_CODE:
                switch (resultCode) {
                    case RESULT_OK:
                        startSignInActivity();
                        break;
                    case RESULT_CANCELED: break;
                }
                break;
        }
    }
    
    private void startSignInActivity() {
        Log.i(TAG, "Google Play services APK is up-to-date, enabled and valid");
        startActivity(new Intent(this, SignInActivity.class));
        finish();
    }
}