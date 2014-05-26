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
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.twitt4droid.Resources;
import com.twitt4droid.Twitt4droid;
import com.twitt4droid.app.R;
import com.twitt4droid.app.activity.SignInActivity;
import com.twitt4droid.data.dao.UserDAO;
import com.twitt4droid.data.dao.impl.DAOFactory;
import com.twitt4droid.task.ImageLoader;
import com.twitt4droid.util.Strings;
import com.twitt4droid.widget.LogInOutButton;

import twitter4j.AsyncTwitter;
import twitter4j.TwitterAdapter;
import twitter4j.TwitterException;
import twitter4j.TwitterMethod;
import twitter4j.User;

public class UserFragment extends Fragment {

    private static final String TAG = UserFragment.class.getSimpleName();

    private ImageView profileImageView;
    private TextView usernameTextView;
    private TextView nameTextView;
    private TextView locationTextView;
    private TextView webSiteTextView;
    private TextView descriptionTextView;
    private LogInOutButton logoutButton;
    private User user;
    private UserDAO userDao;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        userDao = new DAOFactory(getActivity().getApplicationContext()).getUserDAO();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setUpUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.user, container, false);
        profileImageView = (ImageView) layout.findViewById(R.id.profile_image_view);
        usernameTextView = (TextView) layout.findViewById(R.id.username_text_view);
        nameTextView = (TextView) layout.findViewById(R.id.name_text_view);
        locationTextView = (TextView) layout.findViewById(R.id.location_text_view);
        webSiteTextView = (TextView) layout.findViewById(R.id.web_site_text_view);
        descriptionTextView = (TextView) layout.findViewById(R.id.description_text_view);
        logoutButton = (LogInOutButton) layout.findViewById(R.id.logout_button);
        return layout;
    }

    private void setUpUser() {
        AsyncTwitter twitter = Twitt4droid.getAsyncTwitter(getActivity());
        twitter.addListener(new TwitterAdapter() {
            
            @Override
            public void verifiedCredentials(final User user) {
                Log.d(TAG, "User: " + user);
                userDao.update(user);
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
            user = userDao.fetchById(Twitt4droid.getCurrentUserId(getActivity()));
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
                    .setLoadingColorId(R.color.twitt4droid_no_image_background)
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