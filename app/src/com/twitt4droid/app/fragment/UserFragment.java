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
package com.twitt4droid.app.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.rtyley.android.sherlock.roboguice.fragment.RoboSherlockFragment;

import com.twitt4droid.Resources;
import com.twitt4droid.Twitt4droid;
import com.twitt4droid.app.R;
import com.twitt4droid.app.activity.SignInActivity;
import com.twitt4droid.data.dao.UserDao;
import com.twitt4droid.task.ImageLoader;
import com.twitt4droid.util.Strings;
import com.twitt4droid.widget.LogInOutButton;

import roboguice.inject.InjectView;

import twitter4j.AsyncTwitter;
import twitter4j.TwitterAdapter;
import twitter4j.TwitterException;
import twitter4j.TwitterMethod;
import twitter4j.User;

public class UserFragment extends RoboSherlockFragment {

    private static final String TAG = UserFragment.class.getSimpleName();

    @InjectView(R.id.profile_image_view)    private ImageView profileImageView;
    @InjectView(R.id.username_text_view)    private TextView usernameTextView;
    @InjectView(R.id.name_text_view)        private TextView nameTextView;
    @InjectView(R.id.location_text_view)    private TextView locationTextView;
    @InjectView(R.id.web_site_text_view)    private TextView webSiteTextView;
    @InjectView(R.id.description_text_view) private TextView descriptionTextView;
    @InjectView(R.id.logout_button)         private LogInOutButton logoutButton;

    private User user;
    private UserDao userDao;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        userDao = Twitt4droid.SQLiteDaoFactory(getActivity().getApplicationContext())
                .getUserDao();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setUpUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.user, container, false);
    }

    private void setUpUser() {
        AsyncTwitter twitter = Twitt4droid.getAsyncTwitter(getActivity());
        twitter.addListener(new TwitterAdapter() {
            
            @Override
            public void verifiedCredentials(final User user) {
                Log.d(TAG, "User: " + user);
                userDao.beginTransaction()
                        .deleteAll()
                        .save(user)
                        .commit();
                getActivity().runOnUiThread(new Runnable() {
                    
                    @Override
                    public void run() {
                        UserFragment.this.user = user;
                        setUpLayout();                        
                    }
                });
            }
            
            @Override
            public void onException(TwitterException te, TwitterMethod method) {
                Log.e(TAG, "Twitter error", te);
                getActivity().runOnUiThread(new Runnable() {
                    
                    @Override
                    public void run() {
                        Toast.makeText(getActivity().getApplicationContext(), 
                                R.string.twitt4droid_error_message, 
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
        
        if (Resources.isConnectedToInternet(getActivity())) {
            twitter.verifyCredentials();
        } else {
            user = userDao.readById(Twitt4droid.getCurrentUserId(getActivity()));
            setUpLayout();
        }
    }

    private void setUpLayout() {
        if (user != null) {
            usernameTextView.setText(getString(R.string.screen_name_format, user.getScreenName()));
            usernameTextView.setVisibility(View.VISIBLE);
    
            if (!Strings.isNullOrBlank(user.getProfileImageURL())) {
                new ImageLoader(getActivity().getApplicationContext())
                    .setImageView(profileImageView)
                    .setLoadingResourceImageId(R.drawable.twitt4droid_no_profile_image)
                    .execute(user.getProfileImageURL());
                profileImageView.setVisibility(View.VISIBLE);
            }
    
            if (!Strings.isNullOrBlank(user.getName())) {
                nameTextView.setText(user.getName());
                nameTextView.setVisibility(View.VISIBLE);
            }
            
            if (!Strings.isNullOrBlank(user.getURL())) {
                webSiteTextView.setText(user.getURL());
                webSiteTextView.setVisibility(View.VISIBLE);
            }
            
            if (!Strings.isNullOrBlank(user.getDescription())) {
                descriptionTextView.setText(user.getDescription());
                descriptionTextView.setVisibility(View.VISIBLE);
            }
            
            if (!Strings.isNullOrBlank(user.getLocation())) {
                locationTextView.setText(user.getLocation());
                locationTextView.setVisibility(View.VISIBLE);
            }
            
            logoutButton.setOnLogoutListener(new LogInOutButton.OnLogoutListener() {
                @Override
                public void OnLogout(LogInOutButton button) {
                    Intent intent = new Intent(getActivity(), SignInActivity.class);
                    getActivity().startActivity(intent);
                    getActivity().finish();
                }
            });
            logoutButton.setVisibility(View.VISIBLE);
        }
    }
}