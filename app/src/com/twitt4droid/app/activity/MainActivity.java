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
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.shamanland.fab.FloatingActionButton;

import com.twitt4droid.Twitt4droid;
import com.twitt4droid.activity.UserProfileActivity;
import com.twitt4droid.app.R;
import com.twitt4droid.app.fragment.ListsFragment;
import com.twitt4droid.app.widget.DrawerItem;
import com.twitt4droid.app.widget.DrawerItemAdapter;
import com.twitt4droid.fragment.FixedQueryTimelineFragment;
import com.twitt4droid.fragment.HomeTimelineFragment;
import com.twitt4droid.fragment.MentionsTimelineFragment;
import com.twitt4droid.fragment.QueryableTimelineFragment;
import com.twitt4droid.util.Images.ImageLoader;
import com.twitt4droid.util.Strings;
import com.twitt4droid.widget.TweetDialog;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;

public class MainActivity extends ActionBarActivity {

    private static final String FRAGMENT_TAG = "CURRENT_FRAGMENT";
    private static final String CURRENT_TITLE_KEY = "CURRENT_TITLE";

    private Toolbar toolbar;
    private FloatingActionButton composeTweetButton;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private ListView drawerList;
    private int currentTitleId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpComposeTweetButton();
        setUpDrawer();
        if (savedInstanceState == null) {
            HomeTimelineFragment homeTimelineFragment = HomeTimelineFragment.newInstance(isDarkThemeSelected());
            homeTimelineFragment.setRetainInstance(true);
            setUpFragment(homeTimelineFragment);
            setTitle(R.string.drawer_home_option);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CURRENT_TITLE_KEY, currentTitleId);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        setTitle(savedInstanceState.getInt(CURRENT_TITLE_KEY));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!Twitt4droid.isUserLoggedIn(getApplicationContext())) {
            Intent intent = new Intent(this, SignInActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private boolean isDarkThemeSelected() {
        String theme = PreferenceManager.getDefaultSharedPreferences(this)
            .getString(getString(R.string.change_theme_key), getString(R.string.change_theme_default_value));
        return theme.equals(getString(R.string.dark_theme_entry));
    }

    private void setUpComposeTweetButton() {
        composeTweetButton = (FloatingActionButton) findViewById(R.id.compose_tweet_button);
        composeTweetButton.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                new TweetDialog(MainActivity.this).show();
            }
        });
    }

    private void setUpDrawer() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawerToggle = new ActionBarDrawerToggle(
                this, 
                drawerLayout, 
                toolbar,
                R.string.drawer_open, 
                R.string.drawer_close) {

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle(R.string.app_name);
                supportInvalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getSupportActionBar().setTitle(currentTitleId);
                supportInvalidateOptionsMenu();
            }
        };

        drawerLayout.setDrawerListener(drawerToggle);
        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, Gravity.START);
        drawerLayout.setStatusBarBackground(R.color.primary_dark_color);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        setUpDrawerMenu();
    }

    private void setUpDrawerMenu() {
        drawerList = (ListView) drawerLayout.findViewById(R.id.drawer_options);
        DrawerItemAdapter drawerMenuAdapter = new DrawerItemAdapter(this);
        drawerMenuAdapter.add(new DrawerItem(R.drawable.twitt4droid_ic_home_holo_light, R.string.drawer_home_option));
        drawerMenuAdapter.add(new DrawerItem(R.drawable.twitt4droid_ic_notifications_holo_light, R.string.drawer_mentions_option));
        drawerMenuAdapter.add(new DrawerItem(R.drawable.twitt4droid_ic_list_holo_light, R.string.drawer_lists_option));
        drawerMenuAdapter.add(new DrawerItem(R.drawable.twitt4droid_ic_hashtag_holo_light, R.string.drawer_fixed_search_option));
        drawerMenuAdapter.add(new DrawerItem(R.drawable.twitt4droid_ic_search_holo_light, R.string.drawer_search_option));
        drawerMenuAdapter.add(new DrawerItem(R.drawable.ic_settings, R.string.drawer_settings_option));
        View drawerHeaderView = findViewById(R.id.drawer_header);
        new DrawerHeaderSetUpTask(drawerHeaderView).execute();
        drawerList.setOnItemClickListener(new DrawerItemClickListener());
        drawerList.setAdapter(drawerMenuAdapter);
    }
    
    private void setUpFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment currentFragment = fragmentManager.findFragmentByTag(FRAGMENT_TAG);
        if (currentFragment == null || !currentFragment.getClass().equals(fragment.getClass())) {
            fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment, FRAGMENT_TAG)
                .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) return true;
        return super.onOptionsItemSelected(item);
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

    @Override
    public void setTitle(int titleId) {
        currentTitleId = titleId;
        getSupportActionBar().setTitle(currentTitleId);
    }

    private class DrawerHeaderSetUpTask extends AsyncTask<Void, Void, User> {

        private final View drawerHeaderView;
        private final Twitter twitter;
        private final String screenName;
        
        private DrawerHeaderSetUpTask(View drawerHeaderView) {
            this.drawerHeaderView = drawerHeaderView;
            this.twitter = Twitt4droid.getTwitter(MainActivity.this);
            this.screenName = Twitt4droid.getCurrentUser(MainActivity.this).getScreenName();
        }
        
        @Override
        protected void onPreExecute() {
            setUpUser(Twitt4droid.getCurrentUser(MainActivity.this));
        }
    
        @Override
        protected User doInBackground(Void... params) {
            try {
                return twitter.showUser(screenName);
            } catch (TwitterException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(User result) {
            if (result != null) setUpUser(result);
        }
        
        @SuppressWarnings("deprecation")
        private void setUpUser(User user) {
            ImageView userProfileBannerImage = (ImageView) drawerHeaderView.findViewById(R.id.user_profile_banner_image);
            ImageView userProfileImage = (ImageView) drawerHeaderView.findViewById(R.id.user_profile_image);
            TextView userScreenName = (TextView) drawerHeaderView.findViewById(R.id.user_screen_name);
            TextView userName = (TextView) drawerHeaderView.findViewById(R.id.user_name);
            userScreenName.setText(getString(R.string.twitt4droid_username_format, user.getScreenName()));
            userName.setText(user.getName());
            userProfileBannerImage.setAlpha(0xfe);
            userProfileImage.setAlpha(0xfe);
            userProfileImage.setOnClickListener(new View.OnClickListener() {
                
                @Override
                public void onClick(View v) {
                    Intent profileIntent = UserProfileActivity.buildIntent(MainActivity.this, Twitt4droid.getCurrentUser(MainActivity.this).getScreenName(), isDarkThemeSelected());
                    startActivity(profileIntent);
                }
            });
            if (!Strings.isNullOrBlank(user.getProfileBannerURL())) {
                new ImageLoader(MainActivity.this)
                    .setImageView(userProfileBannerImage)
                    .execute(user.getProfileBannerURL());
            }
            if (!Strings.isNullOrBlank(user.getProfileImageURL())) {
                new ImageLoader(MainActivity.this)
                    .setImageView(userProfileImage)
                    .execute(user.getProfileImageURL());
            }
        }
    }
    
    private class DrawerItemClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            switch(position) {
                case 0: 
                    HomeTimelineFragment homeTimelineFragment = HomeTimelineFragment.newInstance(isDarkThemeSelected());
                    homeTimelineFragment.setRetainInstance(true);
                    setUpFragment(homeTimelineFragment);
                    setTitle(R.string.drawer_home_option);
                    break;
                case 1: 
                    MentionsTimelineFragment mentionsTimelineFragment = MentionsTimelineFragment.newInstance(isDarkThemeSelected());
                    mentionsTimelineFragment.setRetainInstance(true);
                    setUpFragment(mentionsTimelineFragment);
                    setTitle(R.string.drawer_mentions_option);
                    break;
                case 2: 
                    setUpFragment(new ListsFragment());
                    setTitle(R.string.drawer_lists_option);
                    break;
                case 3:
                    FixedQueryTimelineFragment fixedQueryTimelineFragment = FixedQueryTimelineFragment.newInstance(getString(R.string.drawer_fixed_search_option), isDarkThemeSelected());
                    fixedQueryTimelineFragment.setRetainInstance(true);
                    setUpFragment(fixedQueryTimelineFragment);
                    setTitle(R.string.drawer_fixed_search_option);
                    break;
                case 4:
                    QueryableTimelineFragment queryableTimelineFragment = QueryableTimelineFragment.newInstance(isDarkThemeSelected());
                    queryableTimelineFragment.setRetainInstance(true);
                    setUpFragment(queryableTimelineFragment);
                    setTitle(R.string.drawer_search_option);
                    break;
                case 5: 
                    Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
                    startActivity(settingsIntent);
                    break;
            }
            drawerList.setItemChecked(position, true);
            drawerLayout.closeDrawers();
        }
    }
}