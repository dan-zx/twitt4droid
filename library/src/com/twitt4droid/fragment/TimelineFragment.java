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
package com.twitt4droid.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.twitt4droid.R;
import com.twitt4droid.Resources;
import com.twitt4droid.Twitt4droid;
import com.twitt4droid.widget.RefreshableListView;
import com.twitt4droid.widget.TweetAdapter;

import twitter4j.Status;
import twitter4j.Twitter;

import java.util.List;

public abstract class TimelineFragment extends Fragment {

private static final String TAG = TimelineFragment.class.getSimpleName();
    
    private RefreshableListView tweetListView;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.twitt4droid_timeline, container, false);
        tweetListView = (RefreshableListView) layout.findViewById(R.id.tweets_list);
        progressBar = (ProgressBar) layout.findViewById(R.id.tweets_progress_bar);
        tweetListView.setOnRefreshListener(new RefreshableListView.OnRefreshListener() {
            @Override
            public void onRefresh(RefreshableListView refreshableListView) {
                new TweetUpdater().execute();
            }
        });
        return layout;
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (Resources.isConnectedToInternet(getActivity())) {
            new TweetLoader().execute();
        } else {
            onNetworkDown();
        }
    }
    
    protected abstract List<Status> updateTweets(Twitter twitter) throws Exception;
    protected void onTwitterError(Exception ex) {}
    protected void onNetworkDown() {}
    
    private class TweetLoader extends AsyncTask<Void, Void, List<Status>> {

        protected Twitter twitter;
        protected Exception twitterError;

        public TweetLoader() {
            this.twitter = Twitt4droid.getTwitter(getActivity());
        }

        @Override
        protected void onPreExecute() {
            tweetListView.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        }
        
        @Override
        protected List<twitter4j.Status> doInBackground(Void... params) {
            try {
                return updateTweets(twitter);
            } catch (Exception ex) {
                Log.e(TAG, "Error while retrieving tweets", ex);
                twitterError = ex;
            }
            return null;
        }
        
        @Override
        protected void onPostExecute(List<twitter4j.Status> result) {
            tweetListView.onRefreshComplete();
            progressBar.setVisibility(View.GONE);
            tweetListView.setVisibility(View.VISIBLE);
            if (result != null && !result.isEmpty()) {
                tweetListView.setAdapter(new TweetAdapter(getActivity(), R.layout.twitt4droid_tweet_item, result));
                Log.d(TAG, "Loaded");
            } else {
                onTwitterError(twitterError);
            }
        }
    }

    private class TweetUpdater extends TweetLoader {
        
        @Override
        protected void onPreExecute() { }
        
        @Override
        protected List<twitter4j.Status> doInBackground(Void... params) {
            return super.doInBackground(params);
        }

        @Override
        protected void onPostExecute(List<twitter4j.Status> result) {
            tweetListView.onRefreshComplete();
            if (result != null && !result.isEmpty()) {
                tweetListView.setAdapter(new TweetAdapter(getActivity(), R.layout.twitt4droid_tweet_item, result));
                Log.d(TAG, "Updated");
            } else {
                onTwitterError(twitterError);
            }
        }
    }
}