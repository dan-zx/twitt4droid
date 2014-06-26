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

public class FixedQueryTimelineFragment extends TimelineFragment {

    protected static final String QUERY_ARG = "QUERY";

    public static FixedQueryTimelineFragment newInstance(String query, boolean enableDarkTheme) {
        FixedQueryTimelineFragment fragment = new FixedQueryTimelineFragment();
        Bundle args = new Bundle();
        args.putString(QUERY_ARG, query);
        args.putBoolean(ENABLE_DARK_THEME_ARG, enableDarkTheme);
        fragment.setArguments(args);
        return fragment;
    }

    public static FixedQueryTimelineFragment newInstance(String query) {
        return newInstance(query, false);
    }

    @Override
    protected QueryStatusesLoaderTask initStatusesLoaderTask() {
        return new QueryStatusesLoaderTask(new DAOFactory(getActivity().getApplicationContext()).getFixedQueryTimelineDAO(), getQuery());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initStatusesLoaderTask().execute();
    }

    @Override
    @Deprecated
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

    public String getQuery() {
        return getArguments().getString(QUERY_ARG);
    }

    private class QueryStatusesLoaderTask extends StatusesLoaderTask {

        private final String query;
        
        protected QueryStatusesLoaderTask(TimelineDAO timelineDao, String query) {
            super(timelineDao);
            this.query = query;
        }

        @Override
        protected List<twitter4j.Status> loadTweetsInBackground() throws TwitterException {
            TimelineDAO timelineDAO = (TimelineDAO) getDAO();
            List<twitter4j.Status> statuses = null;
            if (isConnectToInternet()) {
                statuses = getTwitter().search(new Query(query)).getTweets();
                // TODO: update statuses instead of deleting all previous statuses and save new ones.
                timelineDAO.deleteAll();
                timelineDAO.save(statuses);
            } else statuses = timelineDAO.fetchList();
            return statuses;
        }
    }
}