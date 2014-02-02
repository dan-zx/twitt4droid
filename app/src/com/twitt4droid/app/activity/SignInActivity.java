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

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;

import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockActivity;

import com.twitt4droid.Twitt4droid;
import com.twitt4droid.activity.WebLoginActivity;
import com.twitt4droid.app.R;

import twitter4j.AsyncTwitter;
import twitter4j.TwitterAdapter;
import twitter4j.User;

import java.io.Serializable;

import javax.inject.Inject;

public class SignInActivity extends RoboSherlockActivity {

    @Inject private ConnectivityManager connectivityManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!Twitt4droid.isUserLoggedIn(getApplicationContext())) {
            setContentView(R.layout.sign_in);
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        if (Twitt4droid.isUserLoggedIn(getApplicationContext())) {
            if (isConnected()) {
                final ProgressDialog progressDialog = getLoadingDialog();
                AsyncTwitter twitter = Twitt4droid.getAsyncTwitter(getApplicationContext());
                twitter.addListener(new TwitterAdapter() {
                    @Override
                    public void verifiedCredentials(User user) {
                        progressDialog.dismiss();
                        goToMainActivity(user);
                    }
                });
                twitter.verifyCredentials();
            } else {
                showNetworkAlertDialog();
            }
        }
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case WebLoginActivity.REQUEST_CODE:
                switch (resultCode) {
                    case RESULT_OK:
                        goToMainActivity(data.getExtras().getSerializable(WebLoginActivity.EXTRA_USER));
                        break;
                    case RESULT_CANCELED: break;
                }
                break;
        }
    }
    
    private void goToMainActivity(Serializable user) {
        Intent i = new Intent(this, MainActivity.class);
        i.putExtra(MainActivity.EXTRA_USER, user);
        startActivity(i);
        finish();
    }
    
    private boolean isConnected() {
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    private ProgressDialog getLoadingDialog() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        progressDialog.show();
        return progressDialog;
    }

    private void showNetworkAlertDialog() {
        new AlertDialog.Builder(this)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setTitle(R.string.twitt4droid_nonetwork_title)
            .setMessage(R.string.twitt4droid_nonetwork_messege)
            .setPositiveButton(R.string.twitt4droid_nonetwork_goto_settings, 
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Settings.ACTION_SETTINGS));
                    }
                })
            .setCancelable(false)
            .show();
    }
}