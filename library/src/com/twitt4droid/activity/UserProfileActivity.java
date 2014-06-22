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

public class UserProfileActivity extends FragmentActivity {

    private static final String EXTRA_USERNAME = "EXTRA_USERNAME";
    private static final String EXTRA_ENABLE_DARK_THEME = "EXTRA_ENABLE_DARK_THEME";

    public static Intent buildIntent(Context context, String username, boolean isDarkThemeEnabled) {
        Intent intent = new Intent(context, UserProfileActivity.class);
        Bundle b = new Bundle();
        b.putString(EXTRA_USERNAME, username);
        b.putBoolean(EXTRA_ENABLE_DARK_THEME, isDarkThemeEnabled);
        intent.putExtras(b);
        return intent;
    }

    public static Intent buildIntent(Context context, String username) {
        return buildIntent(context, username, false);
    }
    
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

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void displayHomeAsUp() {
        getActionBar().setDisplayHomeAsUpEnabled(true);
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