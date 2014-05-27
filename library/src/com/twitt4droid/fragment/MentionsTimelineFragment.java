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
import com.twitt4droid.data.dao.impl.DAOFactory;

import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

public class MentionsTimelineFragment extends TimelineFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTimelineDao(new DAOFactory(getActivity().getApplicationContext()).getMentionsTimelineDAO());
    }
    
    @Override
    protected ResponseList<Status> getTweets(Twitter twitter) throws TwitterException {
        return twitter.getMentionsTimeline();
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
        // TODO Holo dark icon
        return 0;
    }
}