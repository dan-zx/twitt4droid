package com.twitt4droid.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.twitt4droid.R;
import com.twitt4droid.fragment.UserTimelineFragment;

public class UserProfileActivity extends FragmentActivity {

    public static final String EXTRA_USER_USERNAME = "com.twitt4droid.extra.user";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.twitt4droid_generic_activity);
        setTitle(R.string.twitt4droid_user_profile_activity_title);
        if (!getIntent().getExtras().containsKey(EXTRA_USER_USERNAME)) throw new IllegalArgumentException("EXTRA_USER_USERNAME expected");
        String username = getIntent().getExtras().getString(EXTRA_USER_USERNAME);
        getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.content_frame, new UserTimelineFragment().setUsername(username))
            .commit();
    }
}