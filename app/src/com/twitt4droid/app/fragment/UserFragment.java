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
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.rtyley.android.sherlock.roboguice.fragment.RoboSherlockFragment;
import com.twitt4droid.Twitt4droid;
import com.twitt4droid.app.R;
import com.twitt4droid.app.activity.SignInActivity;
import com.twitt4droid.app.util.Strings;
import com.twitt4droid.task.ImageLoadingTask;
import com.twitt4droid.widget.LogInOutButton;

import roboguice.inject.InjectView;
import twitter4j.AsyncTwitter;
import twitter4j.TwitterAdapter;
import twitter4j.TwitterException;
import twitter4j.TwitterMethod;
import twitter4j.User;

import java.io.IOException;
import java.util.List;

public class UserFragment extends RoboSherlockFragment {

    private static final String TAG = UserFragment.class.getSimpleName();

    @InjectView(R.id.profile_image_view)    private ImageView profileImageView;
    @InjectView(R.id.map_image_view)        private ImageView mapImageView;
    @InjectView(R.id.username_text_view)    private TextView usernameTextView;
    @InjectView(R.id.name_text_view)        private TextView nameTextView;
    @InjectView(R.id.location_text_view)    private TextView locationTextView;
    @InjectView(R.id.web_site_text_view)    private TextView webSiteTextView;
    @InjectView(R.id.description_text_view) private TextView descriptionTextView;
    @InjectView(R.id.logout_button)         private LogInOutButton logoutButton;

    private User user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.user, container, false);
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setUpUser();
    }
    
    private void setUpUser() {
        AsyncTwitter twitter = Twitt4droid.getAsyncTwitter(getActivity());
        twitter.addListener(new TwitterAdapter() {
            
            @Override
            public void verifiedCredentials(final User user) {
                Log.d(TAG, "User: " + user);
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
                                R.string.twitt4droid_onerror_message, 
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });
            }
        });
        twitter.verifyCredentials();
    }

    private void setUpLayout() {
        if (user != null) {
            usernameTextView.setText(getString(R.string.screen_name_format, user.getScreenName()));
            usernameTextView.setVisibility(View.VISIBLE);
    
            if (!Strings.isNullOrBlank(user.getProfileImageURL())) {
                new ImageLoadingTask()
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
            
            setUpLocation();
            
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
    
    private void setUpLocation() {
        if (!Strings.isNullOrBlank(user.getLocation())) { 
            try {
                Geocoder geocoder = new Geocoder(getActivity());
                List<Address> addresses = geocoder.getFromLocationName(user.getLocation(), 1);
                if (addresses.isEmpty() || addresses.get(0) == null) {
                    Log.w(TAG,  "Address " + user.getLocation() + " couldn't be found");
                    locationTextView.setText(user.getLocation());
                    locationTextView.setVisibility(View.VISIBLE);
                } else {
                    Log.d(TAG,  "Location " + addresses.get(0) + " found");
                    String url = Uri.parse(getString(R.string.google_static_maps_url))
                        .buildUpon()
                        .appendQueryParameter("sensor", "false")
                        .appendQueryParameter("size", "540x200")
                        .appendQueryParameter("scale", "2")
                        .appendQueryParameter("zoom", "4")
                        .appendQueryParameter("markers", getString(R.string.google_static_maps_latlng_format, addresses.get(0).getLatitude(), addresses.get(0).getLongitude()))
                        .build()
                        .toString();
                    new ImageLoadingTask()
                        .setImageView(mapImageView)
                        .setLoadingResourceImageId(R.drawable.twitt4droid_no_image)
                        .execute(url);
                    mapImageView.setVisibility(View.VISIBLE);
                }
            } catch (IOException ex) {
                Log.w(TAG, "Address " + user.getLocation() + " couldn't be decoded", ex);
                locationTextView.setText(user.getLocation());
                locationTextView.setVisibility(View.VISIBLE);
            }
        }        
    }
}