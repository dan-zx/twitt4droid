/*
 * Copyright 2014-present twitt4droid Project
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
package com.twitt4droid.demo;

import roboguice.inject.InjectExtra;
import roboguice.inject.InjectView;
import twitter4j.User;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockActivity;

public class MainActivity extends RoboSherlockActivity {

    public static final String EXTRA_USER = "com.twitt4droid.demo.extra.user";
    
    @InjectView(R.id.profile_image_view)    private ImageView profileImageView;
    @InjectView(R.id.username_text_view)    private TextView usernameTextView;
    @InjectView(R.id.name_text_view)        private TextView nameTextView;
    @InjectView(R.id.location_text_view)    private TextView locationTextView;
    @InjectView(R.id.web_site_text_view)    private TextView webSiteTextView;
    @InjectView(R.id.description_text_view) private TextView descriptionTextView;
    @InjectExtra(EXTRA_USER)                private User user;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        usernameTextView.setText("@" + user.getScreenName());
        
        if (!isNullOrBlank(user.getProfileImageURL())) {
            new RemoteImageLoader(profileImageView)
                .execute(user.getProfileImageURL());
        }
            
        if (isNullOrBlank(user.getName())) nameTextView.setVisibility(View.GONE); 
        else nameTextView.setText(user.getName());
        
        if (isNullOrBlank(user.getLocation())) locationTextView.setVisibility(View.GONE); 
        else locationTextView.setText(user.getLocation());
        
        if (isNullOrBlank(user.getURL())) webSiteTextView.setVisibility(View.GONE); 
        else webSiteTextView.setText(user.getURL());
        
        if (isNullOrBlank(user.getDescription())) descriptionTextView.setVisibility(View.GONE); 
        else descriptionTextView.setText(user.getDescription());
    }
    
    private boolean isNullOrBlank(String str) {
        return str == null || str.trim().length() == 0;
    }
}