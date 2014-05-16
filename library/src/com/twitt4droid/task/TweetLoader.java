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