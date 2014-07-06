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

import com.twitt4droid.R;
import com.twitt4droid.data.dao.TimelineDAO;
import com.twitt4droid.data.dao.impl.DAOFactory;

import twitter4j.Query;
import twitter4j.TwitterException;

import java.util.List;

/**
 * Shows the 20 most recent statuses that match a given query. 
 * 
 * @author Daniel Pedraza-Arcega
 * @since version 1.0
 */
public class FixedQueryTimelineFragment extends TimelineFragment {

    protected static final String QUERY_ARG = "QUERY";

    /**
     * Creates a FixedQueryTimelineFragment.
     * 
     * @param query a search query.
     * @param enableDarkTheme if the dark theme is enabled.
     * @return a new FixedQueryTimelineFragment.
     */
    public static FixedQueryTimelineFragment newInstance(String query, boolean enableDarkTheme) {
        FixedQueryTimelineFragment fragment = new FixedQueryTimelineFragment();
        Bundle args = new Bundle();
        args.putString(QUERY_ARG, query);
        args.putBoolean(ENABLE_DARK_THEME_ARG, enableDarkTheme);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Creates a FixedQueryTimelineFragment.
     * 
     * @param query a search query.
     * @return a new FixedQueryTimelineFragment.
     */
    public static FixedQueryTimelineFragment newInstance(String query) {
        return newInstance(query, false);
    }

    /** {@inheritDoc} */
    @Override
    protected QueryStatusesLoaderTask initStatusesLoaderTask() {
        return new QueryStatusesLoaderTask(new DAOFactory(getActivity().getApplicationContext()).getFixedQueryTimelineDAO(), getQuery());
    }

    /** {@inheritDoc} */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initStatusesLoaderTask().execute();
    }

    /** 
     * {@inheritDoc}
     * 
     * @deprecated use {@link #getQuery()} instead. 
     */
    @Override
    @Deprecated
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

    /** @return the search query. */
    public String getQuery() {
        return getArguments().getString(QUERY_ARG);
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