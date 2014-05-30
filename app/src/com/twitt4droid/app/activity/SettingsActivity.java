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

import android.annotation.TargetApi;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.view.MenuItem;

import com.twitt4droid.app.R;
import com.twitt4droid.app.util.Dialogs;

public class SettingsActivity extends PreferenceActivity {

    private static final String TAG = SettingsActivity.class.getSimpleName();

    private Preference licencesPreference;
    private Preference versionPreference;
    
    @Override
    @SuppressWarnings("deprecation")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) displayHomeAsUp();
        findPreferences();
        setUpLicencesPreference();
        setUpVersionPreference();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void displayHomeAsUp() {
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @SuppressWarnings("deprecation")
    private void findPreferences() {
        licencesPreference = findPreference(getString(R.string.licences_key));
        versionPreference = findPreference(getString(R.string.version_key));
    }

    private void setUpLicencesPreference() {
        licencesPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Dialogs.getLicencesAlertDialog(SettingsActivity.this).show();
                return true;
            }
        });
    }

    private void setUpVersionPreference() {
        try {
            String versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            versionPreference.setSummary(versionName);
        } catch (NameNotFoundException ex) {
            Log.e(TAG, "Couldn't find version name", ex);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }
}