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

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.twitt4droid.R;
import com.twitt4droid.Resources;
import com.twitt4droid.data.dao.TimelineDao;
import com.twitt4droid.task.TweetLoader;
import com.twitt4droid.widget.RefreshableListView;
import com.twitt4droid.widget.TweetAdapter;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import java.util.Collections;
import java.util.List;

public abstract class TimelineFragment extends Fragment {
    
    private static final String TAG = TimelineFragment.class.getSimpleName();
    
    private RefreshableListView tweetListView;
    private ProgressBar progressBar;
    private TimelineDao timelineDao;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.twitt4droid_timeline, container, false);
        setUpLayout(layout);
        return layout;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadTweets();
    }

    protected abstract List<Status> getTweets(Twitter twitter) throws TwitterException;
    protected void onTwitterError(TwitterException ex) {}

    private void setUpLayout(View layout) {
        tweetListView = (RefreshableListView) layout.findViewById(R.id.tweets_list);
        progressBar = (ProgressBar) layout.findViewById(R.id.tweets_progress_bar);
        tweetListView.setOnRefreshListener(new RefreshableListView.OnRefreshListener() {
            @Override
            public void onRefresh(RefreshableListView refreshableListView) {
                if (Resources.isConnectedToInternet(getActivity())) {
                    new TimelineLoader().execute();
                } else {
                    refreshableListView.onRefreshComplete();
                    Toast.makeText(getActivity().getApplicationContext(), 
                            R.string.twitt4droid_is_offline_messege, 
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    protected void loadTweets() {
        if (Resources.isConnectedToInternet(getActivity())) {
            new TimelineLoader().execute();
        } else {
            List<Status> list = timelineDao.readList();
            if (list != null && !list.isEmpty()) {
                tweetListView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                Collections.reverse(list);
                tweetListView.setAdapter(new TweetAdapter(getActivity(), R.layout.twitt4droid_tweet_item, list));
            } 
        }
    }
    
    protected void setTimelineDao(TimelineDao timelineDao) {
        this.timelineDao = timelineDao;
    }

    private class TimelineLoader extends TweetLoader<Void> {
        
        public TimelineLoader() {
            super(getActivity());
        }

        @Override
        protected void onPreExecute() {
            if (getActivity() != null) {
                if(tweetListView.getChildCount() == 0) {
                    tweetListView.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                }
            }
        }
        
        @Override
        protected List<twitter4j.Status> loadTweetsInBackground(Void... params) throws TwitterException {
            return getTweets(getTwitter());
        }
        
        @Override
        protected void onPostExecute(List<twitter4j.Status> result) {
            if (getActivity() != null) {
                tweetListView.onRefreshComplete();
                progressBar.setVisibility(View.GONE);
                tweetListView.setVisibility(View.VISIBLE);
                if (result != null && !result.isEmpty()) {
                    tweetListView.setAdapter(new TweetAdapter(getActivity(), R.layout.twitt4droid_tweet_item, result));
                    timelineDao.beginTransaction()
                        .deleteAll()
                        .save(result)
                        .commit();
                    Log.d(TAG, "Loaded");
                } else if (getTwitterException() != null) {
                    Log.e(TAG, "Error while retrieving tweets", getTwitterException());
                    onTwitterError(getTwitterException());
                } else {
                    Toast.makeText(getActivity().getApplicationContext(),
                            R.string.twitt4droid_no_tweets_found_message,
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}