package com.twitt4droid.fragment;

import android.os.Bundle;

import com.twitt4droid.Twitt4droid;

import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

public class HomeTimelineFragment extends TimelineFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTimelineDao(Twitt4droid.SQLiteDaoFactory(getActivity().getApplicationContext())
                .getHomeTimelineDao());
    }

    @Override
    protected ResponseList<Status> getTweets(Twitter twitter) throws TwitterException {
        return twitter.getHomeTimeline();
    }
}