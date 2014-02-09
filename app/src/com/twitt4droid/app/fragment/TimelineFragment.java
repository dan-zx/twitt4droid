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

import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;

// *****************************************************
// TODO: TimelineFragment should be added in the library
// *****************************************************
public abstract class TimelineFragment extends RoboSherlockFragment {

    private static final String TAG = TimelineFragment.class.getSimpleName();
    
    @InjectView(R.id.tweet_list)   private ListView tweetListView;
    @InjectView(R.id.progress_bar) private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.timeline, container, false);
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (Networks.isConnectedToInternet(getActivity())) new TweetLoader().execute();
        else Dialogs.getNetworkAlertDialog(getActivity()).show();
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.timeline, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh_tweets_item: 
                if (Networks.isConnectedToInternet(getActivity())) new TweetLoader().execute();
                else Dialogs.getNetworkAlertDialog(getActivity()).show();
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }
    
    protected abstract ResponseList<Status> getTweets(Twitter twitter) throws Exception;

    private class TweetLoader extends AsyncTask<Void, Void, ResponseList<twitter4j.Status>> {

        private Twitter twitter;
        
        public TweetLoader() {
            this.twitter = Twitt4droid.getTwitter(getActivity());
        }

        @Override
        protected void onPreExecute() {
            tweetListView.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        }
        
        @Override
        protected ResponseList<twitter4j.Status> doInBackground(Void... params) {
            try {
                return getTweets(twitter);
            } catch (Exception ex) {
                Log.e(TAG, "Error while retrieving tweets", ex);
            }
            return null;
        }
        
        @Override
        protected void onPostExecute(ResponseList<twitter4j.Status> result) {
            progressBar.setVisibility(View.GONE);
            tweetListView.setVisibility(View.VISIBLE);
            if (result != null && !result.isEmpty()) {
                tweetListView.setAdapter(new TweetAdapter(getActivity(), R.layout.tweet_item, result));
            } else {
                Toast.makeText(getActivity().getApplicationContext(), R.string.twitt4droid_onerror_message, Toast.LENGTH_LONG).show();
            }
        }
    }
}