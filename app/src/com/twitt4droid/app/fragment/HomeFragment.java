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
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import com.github.rtyley.android.sherlock.roboguice.fragment.RoboSherlockFragment;

import com.twitt4droid.Twitt4droid;
import com.twitt4droid.app.R;
import com.twitt4droid.app.util.Dialogs;
import com.twitt4droid.app.util.Networks;
import com.twitt4droid.app.widget.TweetAdapter;

import roboguice.inject.InjectView;

import twitter4j.Twitter;
import twitter4j.TwitterException;

import java.util.List;

public class HomeFragment extends RoboSherlockFragment {

    private static final String TAG = HomeFragment.class.getSimpleName();
    
    @InjectView(R.id.tweets_list)  private ListView tweetsListView;
    @InjectView(R.id.progress_bar) private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.home, container, false);
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (Networks.isConnectedToInternet(getActivity())) updateTweets();
        else Dialogs.getNetworkAlertDialog(getActivity()).show();
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.home, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.reload_tweets_item: 
                if (Networks.isConnectedToInternet(getActivity())) updateTweets();
                else Dialogs.getNetworkAlertDialog(getActivity()).show();
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }
    
    private void updateTweets() {
        new TweetLoader(getActivity())
            .setProgressBar(progressBar)
            .setTweetsListView(tweetsListView)
            .execute();
    }

    private static class TweetLoader extends AsyncTask<Void, Void, List<twitter4j.Status>> {

        private ProgressBar progressBar;
        private ListView tweetsListView;
        private Twitter twitter;
        private Context context;
        
        public TweetLoader(Context context) {
            this.twitter = Twitt4droid.getTwitter(context);
            this.context = context;
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
        protected List<twitter4j.Status> doInBackground(Void... params) {
            try {
                return twitter.getHomeTimeline();
            } catch (TwitterException ex) {
                Log.e(TAG, "Twitter error", ex);
            }
            return null;
        }
        
        @Override
        protected void onPostExecute(List<twitter4j.Status> result) {
            progressBar.setVisibility(View.GONE);
            tweetsListView.setVisibility(View.VISIBLE);
            if (result != null && !result.isEmpty()) {
                tweetsListView.setAdapter(new TweetAdapter(context, R.layout.tweet_item, result));
            } else {
                Toast.makeText(context.getApplicationContext(), R.string.twitt4droid_onerror_message, Toast.LENGTH_LONG).show();
            }
        }
    }
}