package com.twitt4droid.activity;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;

import com.twitt4droid.R;
import com.twitt4droid.fragment.UserTimelineFragment;

public class UserProfileActivity extends FragmentActivity {

    public static final String EXTRA_USER_USERNAME = "com.twitt4droid.extra.user";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.twitt4droid_generic_activity);
        setTitle(R.string.twitt4droid_user_profile_activity_title);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) displayHomeAsUp();
        if (!getIntent().getExtras().containsKey(EXTRA_USER_USERNAME)) throw new IllegalArgumentException("EXTRA_USER_USERNAME expected");
        String username = getIntent().getExtras().getString(EXTRA_USER_USERNAME);
        getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.content_frame, new UserTimelineFragment().setUsername(username))
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