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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.twitt4droid.R;
import com.twitt4droid.Resources;
import com.twitt4droid.data.dao.TimelineDAO;
import com.twitt4droid.data.dao.impl.DAOFactory;
import com.twitt4droid.util.Strings;

import twitter4j.Query;
import twitter4j.TwitterException;

import java.util.List;

/**
 * Contains a search box for searching statuses in Twitter. 
 * 
 * @author Daniel Pedraza-Arcega
 * @since version 1.0
 */
public class QueryableTimelineFragment extends TimelineFragment {

    protected static final String LAST_QUERY_KEY = "lastQuery";

    private InputMethodManager inputMethodManager;
    private EditText searchEditText;
    private String lastQuery;

    /**
     * Creates a QueryableTimelineFragment.
     * 
     * @param enableDarkTheme if the dark theme is enabled.
     * @return a new QueryableTimelineFragment.
     */
    public static QueryableTimelineFragment newInstance(boolean enableDarkTheme) {
        QueryableTimelineFragment fragment = new QueryableTimelineFragment();
        Bundle args = new Bundle();
        args.putBoolean(ENABLE_DARK_THEME_ARG, enableDarkTheme);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Creates a QueryableTimelineFragment.
     * 
     * @return a new QueryableTimelineFragment.
     */
    public static QueryableTimelineFragment newInstance() {
        return newInstance(false);
    }

    /** {@inheritDoc} */
    @Override
    protected QueryStatusesLoaderTask initStatusesLoaderTask() {
        return new QueryStatusesLoaderTask(new DAOFactory(getActivity().getApplicationContext()).getQueryableTimelineDAO(), lastQuery);
    }

    /** {@inheritDoc} */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.twitt4droid_queryable_timeline, container, false);
        setUpLayout(layout);
        return layout;
    }

    /** {@inheritDoc} */
    @Override
    protected void setUpLayout(View layout) {
        super.setUpLayout(layout);
        searchEditText = (EditText) layout.findViewById(R.id.search_edit_text);
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
                    default:
                        return false;
                }
            }
        });
    }

    /** Hides the software keyboard. */
    private void hideSoftKeyboard() {
        if (searchEditText != null && inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    /** {@inheritDoc} */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        lastQuery = Resources.getPreferences(getActivity()).getString(LAST_QUERY_KEY, Strings.EMPTY);
        if (!Strings.isNullOrBlank(lastQuery)) {
            searchEditText.setText(lastQuery);
            initStatusesLoaderTask().execute();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Resources.getPreferences(getActivity())
            .edit()
            .putString(LAST_QUERY_KEY, lastQuery)
            .commit();
    }

    /** {@inheritDoc} */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!isVisibleToUser) hideSoftKeyboard();
    }

    /** {@inheritDoc} */
    @Override
    public int getResourceTitle() {
        return R.string.twitt4droid_queryable_timeline_fragment_title;
    }

    /** {@inheritDoc} */
    @Override
    public int getResourceHoloLightIcon() {
        return R.drawable.twitt4droid_ic_search_holo_light;
    }

    /** {@inheritDoc} */
    @Override
    public int getResourceHoloDarkIcon() {
        return R.drawable.twitt4droid_ic_search_holo_dark;
    }

    /**
     * Loads twitter statuses asynchronously.
     * 
     * @author Daniel Pedraza-Arcega
     * @since version 1.0
     */
    private class QueryStatusesLoaderTask extends StatusesLoaderTask {

        private final String query;

        /**
         * Creates a QueryStatusesLoaderTask.
         * 
         * @param timelineDao a TimelineDAO.
         * @param query the search query.
         */
        protected QueryStatusesLoaderTask(TimelineDAO timelineDao, String query) {
            super(timelineDao);
            this.query = query;
        }

        /** {@inheritDoc} */
        @Override
        protected List<twitter4j.Status> loadTweetsInBackground() throws TwitterException {
            TimelineDAO timelineDAO = (TimelineDAO) getDAO();
            List<twitter4j.Status> statuses = null;
            if (isConnectedToInternet()) {
                statuses = getTwitter().search(new Query(query)).getTweets();
                // TODO: update statuses instead of deleting all previous statuses and save new ones.
                timelineDAO.deleteAll();
                timelineDAO.save(statuses);
            } else statuses = timelineDAO.fetchList();
            return statuses;
        }
    }
}