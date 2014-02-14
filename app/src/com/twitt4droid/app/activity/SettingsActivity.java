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

import android.os.Bundle;
import android.preference.Preference;

import com.actionbarsherlock.view.MenuItem;

import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockPreferenceActivity;

import com.twitt4droid.app.R;
import com.twitt4droid.app.util.Dialogs;

import roboguice.inject.InjectPreference;

public class SettingsActivity extends RoboSherlockPreferenceActivity {

    @InjectPreference("licences_pref_key") private Preference licencesPreference;
    
    @Override
    @SuppressWarnings("deprecation")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        licencesPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Dialogs.getLicencesAlertDialog(SettingsActivity.this).show();
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}