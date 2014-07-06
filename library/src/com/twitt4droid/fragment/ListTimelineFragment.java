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
import com.twitt4droid.data.dao.ListTimelineDAO;
import com.twitt4droid.data.dao.impl.DAOFactory;

import twitter4j.Paging;
import twitter4j.TwitterException;
import twitter4j.UserList;

import java.util.List;

/**
 * Shows the 20 most recent statuses from the given list. 
 * 
 * @author Daniel Pedraza-Arcega
 * @since version 1.0
 */
public class ListTimelineFragment extends TimelineFragment {

    protected static final String LIST_ARG = "LIST";

    /**
     * Creates a ListTimelineFragment.
     * 
     * @param userList a Twitter list.
     * @param enableDarkTheme if the dark theme is enabled.
     * @return a new ListTimelineFragment.
     */
    public static ListTimelineFragment newInstance(UserList userList, boolean enableDarkTheme) {
        ListTimelineFragment fragment = new ListTimelineFragment();
        Bundle args = new Bundle();
        args.putSerializable(LIST_ARG, userList);
        args.putBoolean(ENABLE_DARK_THEME_ARG, enableDarkTheme);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Creates a ListTimelineFragment.
     * 
     * @param userList a Twitter list.
     * @return a new ListTimelineFragment.
     */
    public static ListTimelineFragment newInstance(UserList userList) {
        return newInstance(userList, false);
    }

    /** {@inheritDoc} */
    @Override
    protected ListStatusesLoaderTask initStatusesLoaderTask() {
        return new ListStatusesLoaderTask(new DAOFactory(getActivity().getApplicationContext()).getListTimelineDAO(), getList().getId());
    }

    /** {@inheritDoc} */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initStatusesLoaderTask().execute();
    }

    /** @return the Twitter list name. */
    public String getListTitle() {
        return getList().getName();
    }

    /** @return the Twitter list. */
    protected UserList getList() {
        return (UserList) getArguments().getSerializable(LIST_ARG);
    }

    /** 
     * {@inheritDoc}
     * 
     *  @deprecated use {@link #getListTitle()}
     */
    @Override
    @Deprecated
    public int getResourceTitle() {
        return R.string.twitt4droid_list_timeline_fragment_title;
    }

    /** {@inheritDoc} */
    @Override
    public int getResourceHoloLightIcon() {
        return R.drawable.twitt4droid_ic_list_holo_light;
    }

    /** {@inheritDoc} */
    @Override
    public int getResourceHoloDarkIcon() {
        return R.drawable.twitt4droid_ic_list_holo_dark;
    }

    /**
     * Loads twitter statuses asynchronously.
     * 
     * @author Daniel Pedraza-Arcega
     * @since version 1.0
     */
    private class ListStatusesLoaderTask extends StatusesLoaderTask {

        private final long listId;

        /**
         * Creates a ListStatusesLoaderTask.
         * 
         * @param timelineDao a TimelineDAO.
         * @param listId the list id.
         */
        protected ListStatusesLoaderTask(ListTimelineDAO timelineDao, long listId) {
            super(timelineDao);
            this.listId = listId;
        }

        /** {@inheritDoc} */
        @Override
        protected List<twitter4j.Status> loadTweetsInBackground() throws TwitterException {
            ListTimelineDAO timelineDAO = (ListTimelineDAO) getDAO();
            List<twitter4j.Status> statuses = null;
            if (isConnectedToInternet()) {
                statuses = getTwitter().getUserListStatuses(listId, new Paging(1));
                // TODO: update statuses instead of deleting all previous statuses and save new ones.
                timelineDAO.deleteAllByListId(listId);
                timelineDAO.save(statuses, listId);
            } else statuses = timelineDAO.fetchListByListId(listId);
            return statuses;
        }
    }
}