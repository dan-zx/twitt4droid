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
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.twitt4droid.R;
import com.twitt4droid.Twitt4droidAsyncTasks;
import com.twitt4droid.data.dao.TimelineDAO;
import com.twitt4droid.data.dao.UserTimelineDAO;
import com.twitt4droid.data.dao.impl.DAOFactory;
import com.twitt4droid.task.ImageLoader;
import com.twitt4droid.util.Strings;

import java.util.List;

import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;

public class UserTimelineFragment extends TimelineFragment {

    protected static final String USERNAME_ARG = "USERNAME";

    private static final String TAG = UserTimelineFragment.class.getSimpleName();

    public static UserTimelineFragment newInstance(String username, boolean enableDarkTheme) {
        UserTimelineFragment fragment = new UserTimelineFragment();
        Bundle args = new Bundle();
        args.putString(USERNAME_ARG, username);
        args.putBoolean(ENABLE_DARK_THEME_ARG, enableDarkTheme);
        fragment.setArguments(args);
        return fragment;
    }
    
    public static UserTimelineFragment newInstance(String username) {
        return newInstance(username, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTimelineDao(new DAOFactory(getActivity().getApplicationContext()).getUserTimelineDAO());
        setLayoutResource(R.layout.twitt4droid_user_timeline);
    }

    @Override
    protected void setUpLayout(View layout) {
        super.setUpLayout(layout);
        final RelativeLayout userInfoLayout = (RelativeLayout) layout.findViewById(R.id.user_info_layout);
        final ImageView userBannerImage =(ImageView) layout.findViewById(R.id.user_banner_image);
        final ImageView userProfileImage =(ImageView) layout.findViewById(R.id.user_profile_image);
        final TextView userUsername = (TextView) layout.findViewById(R.id.user_username);
        final TextView userScreenName =(TextView) layout.findViewById(R.id.user_screen_name);

        new Twitt4droidAsyncTasks.UserInfoFetcher(getActivity()) {

            @Override
            protected void onPostExecute(User result) {
                userInfoLayout.setVisibility(View.VISIBLE);
                if (getTwitterException() != null) {
                    Log.e(TAG, "Twitter error", getTwitterException());
                    Toast.makeText(getContext().getApplicationContext(), 
                            R.string.twitt4droid_error_message, 
                            Toast.LENGTH_LONG)
                            .show();
                } else if (getActivity() != null && result != null) {
                    userUsername.setText(getString(R.string.twitt4droid_username_format, result.getScreenName()));
                    userScreenName.setText(result.getName());
                    if (!Strings.isNullOrBlank(result.getProfileBannerURL())) {
                        new ImageLoader(getContext())
                            .setLoadingColorId(R.color.twitt4droid_no_image_background)
                            .setImageView(userBannerImage)
                            .execute(result.getProfileBannerURL());
                    }
                    new ImageLoader(getContext())
                        .setLoadingColorId(R.color.twitt4droid_no_image_background)
                        .setImageView(userProfileImage)
                        .execute(result.getProfileImageURL());
                }
            }
        }.execute(getUsername());
    }
    
    @Override
    protected ResponseList<Status> getTweets(Twitter twitter) throws TwitterException {
        return twitter.getUserTimeline(getUsername());
    }

    @Override
    protected List<Status> getSavedTweets(TimelineDAO timelineDao) {
        return ((UserTimelineDAO)timelineDao).fetchListByScreenName(getUsername());
    }

    @Override
    public int getResourceTitle() {
        return R.string.twitt4droid_user_timeline_fragment_title;
    }

    @Override
    public int getResourceHoloLightIcon() {
        return R.drawable.twitt4droid_ic_person_holo_light;
    }

    @Override
    public int getResourceHoloDarkIcon() {
        return R.drawable.twitt4droid_ic_person_holo_dark;
    }
    
    public String getUsername() {
        return getArguments().getString(USERNAME_ARG);
    }
}