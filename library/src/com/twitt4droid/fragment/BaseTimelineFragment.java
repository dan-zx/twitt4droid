package com.twitt4droid.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import twitter4j.TwitterException;

public abstract class BaseTimelineFragment extends Fragment {

    private boolean isUsingDarkTheme;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    protected void onTwitterError(TwitterException ex) { }

    public abstract int getResourceTitle();
    public abstract int getResourceHoloLightIcon();
    public abstract int getResourceHoloDarkIcon();

    protected boolean isUsingDarkTheme() {
        return isUsingDarkTheme;
    }

    public void setUseDarkTheme(boolean useDarkTheme) {
        isUsingDarkTheme = useDarkTheme;
    }
}