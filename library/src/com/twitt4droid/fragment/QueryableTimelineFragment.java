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

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.twitt4droid.R;
import com.twitt4droid.Resources;
import com.twitt4droid.Twitt4droid;
import com.twitt4droid.data.dao.TimelineDao;
import com.twitt4droid.task.TweetLoader;
import com.twitt4droid.util.Strings;
import com.twitt4droid.widget.RefreshableListView;
import com.twitt4droid.widget.TweetAdapter;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.TwitterException;

import java.util.Collections;
import java.util.List;

public abstract class QueryableTimelineFragment extends Fragment {

    private static final String TAG = QueryableTimelineFragment.class.getSimpleName();
    private static final String LAST_QUERY_KEY = "lastQuery";

    private InputMethodManager inputMethodManager;
    private EditText searchEditText;
    private RefreshableListView searchedtweetListView;
    private ProgressBar progressBar;
    private String lastQuery;
    private TimelineDao queryableTimelineDao;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.twitt4droid_queryable_timeline, container, false);
        inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        setUpLayout(layout);
        return layout;
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        queryableTimelineDao = Twitt4droid.SQLiteDaoFactory(getActivity().getApplicationContext())
                .getQueryableTimelineDao();
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        lastQuery = Resources.getPreferences(getActivity()).getString(LAST_QUERY_KEY, Strings.EMPTY);
        if (!Strings.isNullOrBlank(lastQuery)) {
            searchEditText.setText(lastQuery);
            loadLocalTweets();
        }
    }
    
    protected void loadLocalTweets() {
        List<Status> list = queryableTimelineDao.readList();
        if (list != null && !list.isEmpty()) {
            searchedtweetListView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            Collections.reverse(list);
            searchedtweetListView.setAdapter(new TweetAdapter(getActivity(), R.layout.twitt4droid_tweet_item, list));
        }
    }
    
    private void loadRemoteTweetsIfPossible() {
        if (Resources.isConnectedToInternet(getActivity())) {
            new TimelineLoader().execute(lastQuery);
        } else {
            searchedtweetListView.onRefreshComplete();
            Toast.makeText(getActivity().getApplicationContext(), 
                    R.string.twitt4droid_is_offline_messege, 
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!isVisibleToUser) {
            hideSoftKeyboard();
        }
    }
    
    protected void onTwitterError(Exception ex) {}
    
    private void setUpLayout(View layout) {
        searchEditText = (EditText) layout.findViewById(R.id.search_edit_text);
        searchedtweetListView = (RefreshableListView) layout.findViewById(R.id.searched_tweets_list);
        progressBar = (ProgressBar) layout.findViewById(R.id.searched_tweets_progress_bar);
        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_SEARCH:
                        String text = v.getText().toString().trim();
                        if (text.length() > 0) {
                            lastQuery = text;
                            hideSoftKeyboard();
                            loadRemoteTweetsIfPossible();
                        }
                        return true;
                    default: return false;
                }
            }
        });
        searchedtweetListView.setOnRefreshListener(new RefreshableListView.OnRefreshListener() {
            
            @Override
            public void onRefresh(RefreshableListView refreshableListView) {
                loadRemoteTweetsIfPossible();
            }
        });
    }
    
    private void hideSoftKeyboard() {
        if (searchEditText != null && inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(
                    searchEditText.getWindowToken(),
                    0);
        }
    }
    
    private class TimelineLoader extends TweetLoader<String> {

        public TimelineLoader() {
            super(getActivity());
        }

        @Override
        protected void onPreExecute() {
            if (getActivity() != null) {
                if(searchedtweetListView.getChildCount() == 0) {
                    searchedtweetListView.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                }
            }
        }
        
        @Override
        protected List<twitter4j.Status> loadTweetsInBackground(String... params) throws TwitterException {
            QueryResult result = getTwitter().search(new Query(params[0]));
            if (result != null) {
                return result.getTweets();
            }
            
            return null;
        }
        
        @Override
        protected void onPostExecute(List<twitter4j.Status> result) {
            if (getActivity() != null) {
                searchedtweetListView.onRefreshComplete();
                progressBar.setVisibility(View.GONE);
                searchedtweetListView.setVisibility(View.VISIBLE);
                if (result != null && !result.isEmpty()) {
                    searchedtweetListView.setAdapter(new TweetAdapter(getActivity(), R.layout.twitt4droid_tweet_item, result));
                    queryableTimelineDao.beginTransaction()
                        .deleteAll()
                        .save(result)
                        .commit();
                    Resources.getPreferences(getActivity()).edit()
                        .putString(LAST_QUERY_KEY, lastQuery)
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