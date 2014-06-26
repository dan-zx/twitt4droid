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

import twitter4j.TwitterException;

import java.util.List;

public class MentionsTimelineFragment extends TimelineFragment {

    public static MentionsTimelineFragment newInstance(boolean enableDarkTheme) {
        MentionsTimelineFragment fragment = new MentionsTimelineFragment();
        Bundle args = new Bundle();
        args.putBoolean(ENABLE_DARK_THEME_ARG, enableDarkTheme);
        fragment.setArguments(args);
        return fragment;
    }

    public static MentionsTimelineFragment newInstance() {
        return newInstance(false);
    }

    @Override
    protected MentionsStatusesLoaderTask initStatusesLoaderTask() {
        return new MentionsStatusesLoaderTask(new DAOFactory(getActivity().getApplicationContext()).getMentionsTimelineDAO());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initStatusesLoaderTask().execute();
    }

    @Override
    public int getResourceTitle() {
        return R.string.twitt4droid_mentions_timeline_fragment_title;
    }

    @Override
    public int getResourceHoloLightIcon() {
        return R.drawable.twitt4droid_ic_notifications_holo_light;
    }

    @Override
    public int getResourceHoloDarkIcon() {
        return R.drawable.twitt4droid_ic_notifications_holo_dark;
    }

    private class MentionsStatusesLoaderTask extends StatusesLoaderTask {

        protected MentionsStatusesLoaderTask(TimelineDAO timelineDao) {
            super(timelineDao);
        }

        @Override
        protected List<twitter4j.Status> loadTweetsInBackground() throws TwitterException {
            TimelineDAO timelineDAO = (TimelineDAO) getDAO();
            List<twitter4j.Status> statuses = null;
            if (isConnectToInternet()) {
                statuses = getTwitter().getMentionsTimeline();
                // TODO: update statuses instead of deleting all previous statuses and save new ones.
                timelineDAO.deleteAll();
                timelineDAO.save(statuses);
            } else statuses = timelineDAO.fetchList();
            return statuses;
        }
    }
}