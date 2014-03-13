package com.twitt4droid.task;

import java.util.List;

import com.twitt4droid.Twitt4droid;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import android.content.Context;
import android.os.AsyncTask;

public abstract class TweetLoader<Params> extends AsyncTask<Params, Void, List<Status>> {

    private Twitter twitter;
    private TwitterException twitterException;
    
    public TweetLoader(Context context) {
        twitter = Twitt4droid.getTwitter(context);
    }
    
    @Override
    protected List<twitter4j.Status> doInBackground(Params... params) {
        try {
            return loadTweetsInBackground(params);
        } catch (TwitterException ex) {
            twitterException = ex;
            return null;
        }
    }
    
    protected abstract List<twitter4j.Status> loadTweetsInBackground (Params... params) throws TwitterException;

    public Twitter getTwitter() {
        return twitter;
    }
    
    public TwitterException getTwitterException() {
        return twitterException;
    }
}