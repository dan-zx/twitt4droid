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

import android.content.Intent;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockActivity;

import com.twitt4droid.Resources;
import com.twitt4droid.Twitt4droid;
import com.twitt4droid.activity.WebLoginActivity;
import com.twitt4droid.app.R;
import com.twitt4droid.app.util.Dialogs;

public class SignInActivity extends SherlockActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!Twitt4droid.isUserLoggedIn(this)) {
            setContentView(R.layout.sign_in);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Twitt4droid.isUserLoggedIn(this)) {
            if (Resources.isConnectedToInternet(this)) {
                goToMainActivity(); 
            } else {
                Dialogs.getNetworkAlertDialog(this).show();
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
                        goToMainActivity();
                        break;
                    case RESULT_CANCELED: break;
                }
                break;
        }
    }
    
    private void goToMainActivity() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }
}