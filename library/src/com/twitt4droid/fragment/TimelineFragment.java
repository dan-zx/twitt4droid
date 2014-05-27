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
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.twitt4droid.R;
import com.twitt4droid.Resources;
import com.twitt4droid.data.dao.TimelineDAO;
import com.twitt4droid.task.TweetLoader;
import com.twitt4droid.widget.TweetAdapter;

import twitter4j.Twitter;

import twitter4j.Status;
import twitter4j.TwitterException;

import java.util.Collections;
import java.util.List;

public abstract class TimelineFragment extends BaseTimelineFragment {
    
    private static final String TAG = TimelineFragment.class.getSimpleName();

    private SwipeRefreshLayout swipeLayout;
    private ListView tweetListView;
    private ProgressBar progressBar;
    private TimelineDAO timelineDao;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.twitt4droid_timeline, container, false);
        setUpLayout(layout);
        return layout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadTweets();
    }

    private void setUpLayout(View layout) {
        swipeLayout = (SwipeRefreshLayout) layout.findViewById(R.id.swipe_container);
        tweetListView = (ListView) layout.findViewById(R.id.tweets_list);
        progressBar = (ProgressBar) layout.findViewById(R.id.tweets_progress_bar);
        swipeLayout.setColorScheme(R.color.twitt4droid_primary_color, 
                R.color.twitt4droid_secundary_color_1,
                R.color.twitt4droid_secundary_color_2,
                R.color.twitt4droid_secundary_color_3);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            
            @Override
            public void onRefresh() {
                loadRemoteTweetsIfPossible();
            }
        });
    }

    protected void loadTweets() {
        List<Status> list = timelineDao.fetchAll();
        if (list != null && !list.isEmpty()) {
            swipeLayout.setVisibility(View.VISIBLE);
            tweetListView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            Collections.reverse(list); // TODO: retrieve in reverse order
            tweetListView.setAdapter(new TweetAdapter(getActivity(), R.layout.twitt4droid_tweet_item, list));
        } else {
            loadRemoteTweetsIfPossible();
        }
    }
    
    private void loadRemoteTweetsIfPossible() {
        if (Resources.isConnectedToInternet(getActivity())) {
            new TimelineLoader().execute();
        } else {
            swipeLayout.setRefreshing(false);
            Toast.makeText(getActivity().getApplicationContext(), 
                    R.string.twitt4droid_is_offline_messege, 
                    Toast.LENGTH_SHORT).show();
        }
    }

    protected abstract List<Status> getTweets(Twitter twitter) throws TwitterException;

    protected void setTimelineDao(TimelineDAO timelineDao) {
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
                    swipeLayout.setVisibility(View.GONE);
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
                swipeLayout.setRefreshing(false);
                progressBar.setVisibility(View.GONE);
                swipeLayout.setVisibility(View.VISIBLE);
                tweetListView.setVisibility(View.VISIBLE);
                if (result != null && !result.isEmpty()) {
                    tweetListView.setAdapter(new TweetAdapter(getActivity(), R.layout.twitt4droid_tweet_item, result));
                    timelineDao.deleteAll();
                    timelineDao.save(result);
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