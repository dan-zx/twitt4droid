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
package com.twitt4droid.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;

import com.twitt4droid.R;
import com.twitt4droid.fragment.UserTimelineFragment;

/**
 * Shows the user profile of the given user. 
 * Use the following factory method to create an intent to call this activity:
 * <pre>
 * {@code  
 * Intent intent = UserProfileActivity.buildIntent(context, "2010MisterChip");
 * Intent intent = UserProfileActivity.buildIntent(context, "2010MisterChip", true); // dark themed
 * }
 * </pre> 
 * @author Daniel Pedraza-Arcega
 * @since version 1.0
 */
public class UserProfileActivity extends FragmentActivity {

    private static final String EXTRA_USERNAME = "EXTRA_USERNAME";
    private static final String EXTRA_ENABLE_DARK_THEME = "EXTRA_ENABLE_DARK_THEME";

    /**
     * Builds the intent to call this activity.
     * 
     * @param context the application context.
     * @param username a twitter username.
     * @param isDarkThemeEnabled if is going to use the dark theme.
     * @return an Intent.
     */
    public static Intent buildIntent(Context context, String username, boolean isDarkThemeEnabled) {
        Intent intent = new Intent(context, UserProfileActivity.class);
        Bundle b = new Bundle();
        b.putString(EXTRA_USERNAME, username);
        b.putBoolean(EXTRA_ENABLE_DARK_THEME, isDarkThemeEnabled);
        intent.putExtras(b);
        return intent;
    }

    /**
     * Builds the intent to call this activity.
     * 
     * @param context the application context.
     * @param username a twitter username.
     * @return an Intent.
     */
    public static Intent buildIntent(Context context, String username) {
        return buildIntent(context, username, false);
    }

    /** {@inheritDoc} */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.twitt4droid_generic_activity);
        setTitle(R.string.twitt4droid_user_profile_activity_title);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) displayHomeAsUp();
        if (!getIntent().getExtras().containsKey(EXTRA_USERNAME)) throw new IllegalArgumentException("EXTRA_USER_USERNAME expected");
        String username = getIntent().getExtras().getString(EXTRA_USERNAME);
        boolean isDarkThemeEnabled = getIntent().getExtras().getBoolean(EXTRA_ENABLE_DARK_THEME);
        getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.content_frame, UserTimelineFragment.newInstance(username, isDarkThemeEnabled))
            .commit();
    }

    /** Set whether home should be displayed as an "up" affordance. */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void displayHomeAsUp() {
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /** {@inheritDoc} */
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