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

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import com.github.rtyley.android.sherlock.roboguice.fragment.RoboSherlockFragment;

import com.twitt4droid.Twitt4droid;
import com.twitt4droid.app.R;
import com.twitt4droid.app.widget.TweetAdapter;

import roboguice.inject.InjectView;

import twitter4j.Twitter;
import twitter4j.TwitterException;

import java.util.List;

import javax.inject.Inject;

public class StreamFragment extends RoboSherlockFragment {

    private static final String TAG = StreamFragment.class.getSimpleName();
    
    @InjectView(R.id.tweets_list)  private ListView tweetsListView;
    @InjectView(R.id.progress_bar) private ProgressBar progressBar;
    @Inject                        private ConnectivityManager connectivityManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.stream, container, false);
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (isConnected()) updateTweets();
        else showNetworkAlertDialog();
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
                if (isConnected()) updateTweets();
                else showNetworkAlertDialog();
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

    private boolean isConnected() {
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    private void showNetworkAlertDialog() {
        new AlertDialog.Builder(getActivity())
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setTitle(R.string.twitt4droid_nonetwork_title)
            .setMessage(R.string.twitt4droid_nonetwork_messege)
            .setNegativeButton(R.string.twitt4droid_onerror_continue, null)
            .setPositiveButton(R.string.twitt4droid_nonetwork_goto_settings, 
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Settings.ACTION_SETTINGS));
                    }
                })
            .setCancelable(false)
            .show();
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
            tweetsListView.setAdapter(new TweetAdapter(context, R.layout.tweet_list_item, result));
        }
    }
}