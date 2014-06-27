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
import com.twitt4droid.Twitt4droid;
import com.twitt4droid.data.dao.GenericDAO;
import com.twitt4droid.widget.TweetAdapter;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import java.util.List;

public abstract class TimelineFragment extends Fragment {
    
    protected static final String ENABLE_DARK_THEME_ARG = "ENABLE_DARK_THEME";

    private static final String TAG = TimelineFragment.class.getSimpleName();

    private SwipeRefreshLayout swipeLayout;
    private ListView tweetListView;
    private TweetAdapter listAdapter;
    private ProgressBar progressBar;

    public abstract int getResourceTitle();
    public abstract int getResourceHoloLightIcon();
    public abstract int getResourceHoloDarkIcon();

    protected abstract StatusesLoaderTask initStatusesLoaderTask();

    protected boolean isDarkThemeEnabled() {
        return getArguments().getBoolean(ENABLE_DARK_THEME_ARG, false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.twitt4droid_timeline, container, false);
        setUpLayout(layout);
        return layout;
    }

    protected void setUpLayout(View layout) {
        swipeLayout = (SwipeRefreshLayout) layout.findViewById(R.id.swipe_container);
        tweetListView = (ListView) layout.findViewById(R.id.tweets_list);
        progressBar = (ProgressBar) layout.findViewById(R.id.tweets_progress_bar);
        listAdapter = new TweetAdapter(getActivity());
        listAdapter.setUseDarkTheme(isDarkThemeEnabled());
        tweetListView.setAdapter(listAdapter);
        swipeLayout.setColorScheme(R.color.twitt4droid_primary_color, 
                R.color.twitt4droid_secundary_color_1,
                R.color.twitt4droid_secundary_color_2,
                R.color.twitt4droid_secundary_color_3);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            
            @Override
            public void onRefresh() {
                reloadTweetsIfPossible();
            }
        });
    }

    protected void reloadTweetsIfPossible() {
        if (Resources.isConnectedToInternet(getActivity())) initStatusesLoaderTask().execute();
        else {
            swipeLayout.setRefreshing(false);
            Toast.makeText(getActivity().getApplicationContext(), 
                    R.string.twitt4droid_is_offline_messege, 
                    Toast.LENGTH_SHORT).show();
        }
    }

    protected abstract class StatusesLoaderTask extends AsyncTask<Void, Void, List<Status>> {

        private final boolean isConnectToInternet;
        private final GenericDAO<?, ?> dao;
        private final Twitter twitter;

        private TwitterException error;

        protected StatusesLoaderTask(GenericDAO<?, ?> dao) {
            this.dao = dao;
            isConnectToInternet = Resources.isConnectedToInternet(getActivity());
            twitter = Twitt4droid.getTwitter(getActivity());
        }

        protected boolean isConnectToInternet() {
            return isConnectToInternet;
        }

        protected GenericDAO<?, ?> getDAO() {
            return dao;
        }

        protected Twitter getTwitter() {
            return twitter;
        }

        protected abstract List<twitter4j.Status> loadTweetsInBackground() throws TwitterException;

        @Override
        protected List<twitter4j.Status> doInBackground(Void... params) {
            try {
                return loadTweetsInBackground();
            } catch (TwitterException ex) {
                error = ex;
                return null;
            }
        }

        @Override
        public void onPostExecute(List<twitter4j.Status> data) {
            if (getActivity() != null) {
                if (error != null) {
                    Log.e(TAG, "Twitter error", error);
                    Toast.makeText(getActivity().getApplicationContext(), 
                            R.string.twitt4droid_error_message, 
                            Toast.LENGTH_LONG)
                            .show();
                    error = null;
                } else {
                    swipeLayout.setRefreshing(false);
                    progressBar.setVisibility(View.GONE);
                    swipeLayout.setVisibility(View.VISIBLE);
                    tweetListView.setVisibility(View.VISIBLE);
                    if (data != null && !data.isEmpty()) listAdapter.set(data);
                    else {
                        Toast.makeText(getActivity().getApplicationContext(),
                                R.string.twitt4droid_no_tweets_found_message,
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                }
            }
        }
    }
}