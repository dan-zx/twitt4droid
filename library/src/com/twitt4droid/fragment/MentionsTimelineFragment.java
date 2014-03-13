package com.twitt4droid.fragment;

import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

public class MentionsTimelineFragment extends TimelineFragment {

    @Override
    protected ResponseList<Status> getTweets(Twitter twitter) throws TwitterException {
        return twitter.getMentionsTimeline();
    }
}