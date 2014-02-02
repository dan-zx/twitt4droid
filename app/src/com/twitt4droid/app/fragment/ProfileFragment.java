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
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.rtyley.android.sherlock.roboguice.fragment.RoboSherlockFragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.twitt4droid.app.R;
import com.twitt4droid.app.activity.SignInActivity;
import com.twitt4droid.app.task.ImageLoadingTask;
import com.twitt4droid.app.util.Strings;
import com.twitt4droid.widget.LogInOutButton;

import roboguice.inject.InjectFragment;
import roboguice.inject.InjectView;

import twitter4j.User;

import java.io.IOException;
import java.util.List;

public class ProfileFragment extends RoboSherlockFragment {

    private static final String TAG = ProfileFragment.class.getSimpleName();

    @InjectView(R.id.profile_image_view)    private ImageView profileImageView;
    @InjectView(R.id.username_text_view)    private TextView usernameTextView;
    @InjectView(R.id.name_text_view)        private TextView nameTextView;
    @InjectView(R.id.location_text_view)    private TextView locationTextView;
    @InjectView(R.id.web_site_text_view)    private TextView webSiteTextView;
    @InjectView(R.id.description_text_view) private TextView descriptionTextView;
    @InjectView(R.id.logout_button)         private LogInOutButton logoutButton;
    @InjectFragment(R.id.map)               private SupportMapFragment mapFragment;
    
    private GoogleMap map;
    private User user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.profile, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpLayout();
    }

    private void setUpLayout() {
        usernameTextView.setText(getString(R.string.screen_name_format, user.getScreenName()));

        if (!Strings.isNullOrBlank(user.getProfileImageURL())) 
            new ImageLoadingTask()
                .setImageView(profileImageView)
                .setLoadingResourceImageId(R.drawable.twitt4droid_no_profile_image)
                .execute(user.getProfileImageURL());

        if (Strings.isNullOrBlank(user.getName())) nameTextView.setVisibility(View.GONE); 
        else nameTextView.setText(user.getName());
        
        if (Strings.isNullOrBlank(user.getURL())) webSiteTextView.setVisibility(View.GONE); 
        else webSiteTextView.setText(user.getURL());
        
        if (Strings.isNullOrBlank(user.getDescription())) descriptionTextView.setVisibility(View.GONE); 
        else descriptionTextView.setText(user.getDescription());
        
        setUpLocation();
        
        logoutButton.setOnLogoutListener(new LogInOutButton.OnLogoutListener() {
            @Override
            public void OnLogout(LogInOutButton button) {
                Intent intent = new Intent(getActivity(), SignInActivity.class);
                getActivity().startActivity(intent);
                getActivity().finish();
            }
        });
    }
    
    private void setUpLocation() {
        if (Strings.isNullOrBlank(user.getLocation())) {
            mapFragment.getView().setVisibility(View.GONE);
            locationTextView.setVisibility(View.GONE); 
        } else if (map == null) {
            map = mapFragment.getMap();
            if (map != null) {
                try {
                    Geocoder geocoder = new Geocoder(getActivity());
                    List<Address> addresses = geocoder.getFromLocationName(user.getLocation(), 1);
                    if (addresses.isEmpty() || addresses.get(0) == null) {
                        Log.d(TAG,  "Address " + user.getLocation() + " couldn't be found");
                        mapFragment.getView().setVisibility(View.GONE);
                        locationTextView.setText(user.getLocation());
                    } else {
                        locationTextView.setVisibility(View.GONE);
                        LatLng position = new LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude());
                        Marker marker = map.addMarker(new MarkerOptions()
                            .position(position)
                            .title(user.getLocation())
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                        marker.showInfoWindow();
                        map.moveCamera(CameraUpdateFactory.newLatLng(position));
                        Log.d(TAG,  "Location " + position + " found");
                    }
                } catch (IOException ex) {
                    Log.w(TAG, "Address " + user.getLocation() + " couldn't be decoded", ex);
                    mapFragment.getView().setVisibility(View.GONE);
                    locationTextView.setText(user.getLocation());
                }
            }
        }
    }
    
    public ProfileFragment setUser(User user) {
        this.user = user;
        return this;
    }
}