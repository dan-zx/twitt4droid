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
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.twitt4droid.R;
import com.twitt4droid.Resources;
import com.twitt4droid.Twitt4droidAsyncTasks;
import com.twitt4droid.data.dao.TimelineDAO;
import com.twitt4droid.data.dao.impl.DAOFactory;
import com.twitt4droid.util.Strings;
import com.twitt4droid.widget.TweetAdapter;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.TwitterException;

import java.util.List;

public class QueryableTimelineFragment extends BaseTimelineFragment {

    private static final String TAG = QueryableTimelineFragment.class.getSimpleName();
    private static final String LAST_QUERY_KEY = "lastQuery";

    private SwipeRefreshLayout swipeLayout;
    private InputMethodManager inputMethodManager;
    private EditText searchEditText;
    private ListView searchedtweetListView;
    private TweetAdapter listAdapter;
    private ProgressBar progressBar;
    private String lastQuery;
    private TimelineDAO queryableTimelineDao;

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
        queryableTimelineDao = new DAOFactory(getActivity().getApplicationContext()).getQueryableTimelineDAO();
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        lastQuery = Resources.getPreferences(getActivity()).getString(LAST_QUERY_KEY, Strings.EMPTY);
        if (!Strings.isNullOrBlank(lastQuery)) {
            searchEditText.setText(lastQuery);
            new TimelineLoader().execute(lastQuery);
        }
    }
    
    private void reloadTweetsIfPossible() {
        if (Resources.isConnectedToInternet(getActivity())) new TimelineLoader().execute(lastQuery);
        else {
            swipeLayout.setRefreshing(false);
            Toast.makeText(getActivity().getApplicationContext(), 
                    R.string.twitt4droid_is_offline_messege, 
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!isVisibleToUser) hideSoftKeyboard();
    }
    
    private void setUpLayout(View layout) {
        swipeLayout = (SwipeRefreshLayout) layout.findViewById(R.id.swipe_container);
        searchEditText = (EditText) layout.findViewById(R.id.search_edit_text);
        searchedtweetListView = (ListView) layout.findViewById(R.id.searched_tweets_list);
        progressBar = (ProgressBar) layout.findViewById(R.id.searched_tweets_progress_bar);
        listAdapter = new TweetAdapter(getActivity());
        listAdapter.setUseDarkTheme(isUsingDarkTheme());
        searchedtweetListView.setAdapter(listAdapter);
        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_SEARCH:
                        String text = v.getText().toString().trim();
                        if (text.length() > 0) {
                            lastQuery = text;
                            hideSoftKeyboard();
                            reloadTweetsIfPossible();
                        }
                        return true;
                    default: return false;
                }
            }
        });
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
    
    private void hideSoftKeyboard() {
        if (searchEditText != null && inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(
                    searchEditText.getWindowToken(),
                    0);
        }
    }

    @Override
    public int getResourceTitle() {
        return R.string.twitt4droid_queryable_timeline_fragment_title;
    }

    @Override
    public int getResourceHoloLightIcon() {
        return R.drawable.twitt4droid_ic_search_holo_light;
    }

    @Override
    public int getResourceHoloDarkIcon() {
        return R.drawable.twitt4droid_ic_search_holo_dark;
    }

    private class TimelineLoader extends Twitt4droidAsyncTasks.TweetFetcher<String> {

        private boolean isConnectToInternet;

        public TimelineLoader() {
            super(getActivity());
        }

        @Override
        protected void onPreExecute() {
            if (getActivity() != null) {
                isConnectToInternet = Resources.isConnectedToInternet(getActivity());
                if(searchedtweetListView.getChildCount() == 0) {
                    swipeLayout.setVisibility(View.GONE);
                    searchedtweetListView.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                }
            }
        }
        
        @Override
        protected List<twitter4j.Status> loadTweetsInBackground(String... params) throws TwitterException {
            List<twitter4j.Status> result = null;
            if (isConnectToInternet) {
                QueryResult queryResult = getTwitter().search(new Query(params[0]));
                if (queryResult != null) {
                    result = queryResult.getTweets();
                    queryableTimelineDao.deleteAll();
                    if (result != null && !result.isEmpty()) queryableTimelineDao.save(result);
                }
            } else result = queryableTimelineDao.fetchList();
            return result;
        }
        
        @Override
        protected void onPostExecute(List<twitter4j.Status> result) {
            if (getActivity() != null) {
                swipeLayout.setRefreshing(false);
                progressBar.setVisibility(View.GONE);
                swipeLayout.setVisibility(View.VISIBLE);
                searchedtweetListView.setVisibility(View.VISIBLE);
                if (getTwitterException() != null) {
                    Log.e(TAG, "Error while retrieving tweets", getTwitterException());
                    onTwitterError(getTwitterException());
                } else if (result != null && !result.isEmpty()) {
                    listAdapter.set(result);
                    Resources.getPreferences(getActivity()).edit()
                        .putString(LAST_QUERY_KEY, lastQuery)
                        .commit();
                    Log.d(TAG, "Loaded");
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), 
                            R.string.twitt4droid_no_tweets_found_message, 
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}