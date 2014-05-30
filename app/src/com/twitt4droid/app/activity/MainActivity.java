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
package com.twitt4droid.app.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.twitt4droid.Twitt4droid;
import com.twitt4droid.Twitt4droidAsyncTasks;
import com.twitt4droid.app.R;
import com.twitt4droid.app.fragment.TimelinesFragment;
import com.twitt4droid.task.ImageLoader;

import twitter4j.User;

public class MainActivity extends ActionBarActivity {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        setUpDrawer();
        setUpFragment(new TimelinesFragment());
    }

    private void setUpDrawer() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(
                this,                
                drawerLayout,         
                R.drawable.ic_drawer,
                R.string.drawer_open,
                R.string.drawer_close); 
        drawerLayout.setDrawerListener(drawerToggle);
        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, Gravity.LEFT);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        setUpUserEntry();
    }
    
    private void setUpUserEntry() {
        final RelativeLayout userInfoLayout = (RelativeLayout) drawerLayout.findViewById(R.id.user_info_layout);
        final ImageView userBannerImage =(ImageView) drawerLayout.findViewById(R.id.user_banner_image);
        final ImageView userProfileImage =(ImageView) drawerLayout.findViewById(R.id.user_profile_image);
        final TextView userUsername = (TextView) drawerLayout.findViewById(R.id.user_username);
        final TextView userScreenName =(TextView) drawerLayout.findViewById(R.id.user_screen_name);

        new Twitt4droidAsyncTasks.UserInfoFetcher(this) {

            @Override
            protected void onPostExecute(User result) {
                userInfoLayout.setVisibility(View.VISIBLE);
                userUsername.setText("@" + result.getScreenName());
                userScreenName.setText(result.getName());
                new ImageLoader(getContext())
                    .setLoadingColorId(R.color.twitt4droid_no_image_background)
                    .setImageView(userBannerImage)
                    .execute(result.getProfileBannerURL());
                new ImageLoader(getContext())
                    .setLoadingColorId(R.color.twitt4droid_no_image_background)
                    .setImageView(userProfileImage)
                    .execute(result.getProfileImageURL());
            }
        }.execute(Twitt4droid.getCurrentUserScreenName(this));
    }

    private void setUpFragment(Fragment fragment) {
        getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.content_frame, fragment)
            .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) return true;

        switch (item.getItemId()) {
            case R.id.settings_item:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }
}