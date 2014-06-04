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
import android.app.AlertDialog;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.Toast;

import com.twitt4droid.Twitt4droid;
import com.twitt4droid.app.R;

public class SettingsActivity extends PreferenceActivity {

    private static final String TAG = SettingsActivity.class.getSimpleName();

    private Preference licencesPreference;
    private Preference clearCachePreference;
    private Preference versionPreference;
    
    @Override
    @SuppressWarnings("deprecation")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) displayHomeAsUp();
        findPreferences();
        setUpLicencesPreference();
        setUpClearCachePreference();
        setUpVersionPreference();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void displayHomeAsUp() {
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @SuppressWarnings("deprecation")
    private void findPreferences() {
        licencesPreference = findPreference(getString(R.string.licences_key));
        clearCachePreference = findPreference(getString(R.string.clear_cache_key));
        versionPreference = findPreference(getString(R.string.app_version_key));
    }

    private void setUpLicencesPreference() {
        licencesPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            
            @Override
            public boolean onPreferenceClick(Preference preference) {
                WebView webView = new WebView(SettingsActivity.this);
                webView.loadUrl(getString(R.string.licenses_file));
                new AlertDialog.Builder(SettingsActivity.this)
                    .setTitle(R.string.licenses_dialog_tile)
                    .setView(webView)
                    .setCancelable(true)
                    .show();
                return true;
            }
        });
    }

    private void setUpClearCachePreference() {
        clearCachePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {
                Twitt4droid.clearCache(getApplicationContext());
                Toast.makeText(getApplicationContext(), 
                        R.string.cache_cleared_message, 
                        Toast.LENGTH_SHORT).show();
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