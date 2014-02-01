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
package com.twitt4droid.app.fragment;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import com.github.rtyley.android.sherlock.roboguice.fragment.RoboSherlockFragment;

import com.twitt4droid.Twitt4droid;
import com.twitt4droid.app.R;

import roboguice.inject.InjectView;

import twitter4j.ResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StreamFragment extends RoboSherlockFragment {

    private static final String TAG = StreamFragment.class.getSimpleName();
    private static final String[] DATA_BINDING_LABELS = { "user", "tweet" };
    
    @InjectView(R.id.tweets_list)  private ListView tweetsListView;
    @InjectView(R.id.progress_bar) private ProgressBar progressBar;
    
    private List<Map<String, Object>> tweets;
    private SimpleAdapter tweetsAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.stream, container, false);
    }
    
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tweets = new ArrayList<Map<String, Object>>();
        tweetsAdapter = new SimpleAdapter(getActivity(), 
                tweets, R.layout.tweet_list_item, DATA_BINDING_LABELS, 
                new int[] { R.id.username, R.id.tweet_content });
        tweetsListView.setAdapter(tweetsAdapter);
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        updateTweets();
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.stream, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.reload_tweets_item: 
                updateTweets();
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }
    
    private void updateTweets() {
        new TweetLoader(getActivity())
            .setProgressBar(progressBar)
            .setTweets(tweets)
            .setTweetsAdapter(tweetsAdapter)
            .setTweetsListView(tweetsListView)
            .execute();
    }
    
    private static class TweetLoader extends AsyncTask<Void, Void, List<Map<String, Object>>> {

        private SimpleAdapter tweetsAdapter;
        private ProgressBar progressBar;
        private ListView tweetsListView;
        private List<Map<String, Object>> tweets;
        private Twitter twitter;
        
        public TweetLoader(Context context) {
            this.twitter = Twitt4droid.getTwitter(context);
        }
        
        public TweetLoader setTweetsAdapter(SimpleAdapter tweetsAdapter) {
            this.tweetsAdapter = tweetsAdapter;
            return this;
        }
        
        public TweetLoader setTweets(List<Map<String, Object>> tweets) {
            this.tweets = tweets;
            return this;
        }
        
        public TweetLoader setProgressBar(ProgressBar progressBar) {
            this.progressBar = progressBar;
            return this;
        }
        
        public TweetLoader setTweetsListView(ListView tweetsListView) {
            this.tweetsListView = tweetsListView;
            return this;
        }
        
        @Override
        protected void onPreExecute() {
            tweetsListView.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        }
        
        @Override
        protected List<Map<String, Object>> doInBackground(Void... params) {
            List<Map<String, Object>> result = new ArrayList<Map<String, Object>>(20);
            try {
                ResponseList<twitter4j.Status> statuses = twitter.getHomeTimeline();
                for (twitter4j.Status s : statuses) {
                    Map<String, Object> row = new HashMap<String, Object>(2);
                    row.put(DATA_BINDING_LABELS[0], "@" + s.getUser().getScreenName());
                    row.put(DATA_BINDING_LABELS[1], s.getText());
                    result.add(row);
                    Log.d(TAG, row.toString());
                }
            } catch (TwitterException ex) {
                Log.e(TAG, "Twitter error", ex);
            }
            return result;
        }
        
        @Override
        protected void onPostExecute(List<Map<String, Object>> result) {
            tweets.clear();
            tweets.addAll(result);
            progressBar.setVisibility(View.GONE);
            tweetsListView.setVisibility(View.VISIBLE);
            tweetsAdapter.notifyDataSetChanged();
        }
    }
}