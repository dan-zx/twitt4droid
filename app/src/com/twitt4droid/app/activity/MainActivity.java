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
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockFragmentActivity;

import com.twitt4droid.app.R;
import com.twitt4droid.app.fragment.CustomHomeTimelineFragment;
import com.twitt4droid.app.fragment.CustomMentionsTimelineFragment;
import com.twitt4droid.app.fragment.CustomQueryableTimelineFragment;
import com.twitt4droid.app.fragment.UserFragment;

import roboguice.inject.InjectView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends RoboSherlockFragmentActivity {

    @InjectView(R.id.view_pager) private ViewPager viewPager;

    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        MyFragmentPagerAdapter adapter = new MyFragmentPagerAdapter(getSupportFragmentManager())
            .addFragment(new CustomHomeTimelineFragment())
            .addFragment(new CustomMentionsTimelineFragment())
            .addFragment(new CustomQueryableTimelineFragment())
            .addFragment(new UserFragment());
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                getSupportActionBar().setSelectedNavigationItem(position);
            }
        });
        MyTabListener listener = new MyTabListener(viewPager);
        getSupportActionBar().addTab(getSupportActionBar()
                .newTab()
                .setContentDescription(R.string.home_tab_title)
                .setIcon(R.drawable.dark_home_icon)
                .setTabListener(listener));
        getSupportActionBar().addTab(getSupportActionBar()
                .newTab()
                .setContentDescription(R.string.mentions_tab_title)
                .setIcon(R.drawable.dark_notifications_icon)
                .setTabListener(listener));
        getSupportActionBar().addTab(getSupportActionBar()
                .newTab()
                .setContentDescription(R.string.query_tab_title)
                .setIcon(R.drawable.twitt4droid_ic_search_holo_light)
                .setTabListener(listener));
        getSupportActionBar().addTab(getSupportActionBar()
                .newTab()
                .setContentDescription(R.string.user_tab_title)
                .setIcon(R.drawable.dark_person_icon)
                .setTabListener(listener));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.main, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_tweet_item:
                startActivity(new Intent(this, TweetingActivity.class));
                return true;
            case R.id.settings_item:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_MENU:
                if (menu != null) menu.performIdentifierAction(R.id.overflow_item, 0);
                return true;
            default: return super.onKeyUp(keyCode, event);
        }
    }

    private static class MyFragmentPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> fragments;

        public MyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
            fragments = new ArrayList<Fragment>();
        }
        
        public MyFragmentPagerAdapter addFragment(Fragment fragment) {
            fragments.add(fragment);
            return this;
        }

        @Override
        public Fragment getItem(int position) {
            if (!fragments.isEmpty() && position >= 0) {
                return fragments.get(position);
            }
            else {
                return null;
            }
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }

    private static class MyTabListener implements ActionBar.TabListener {

        private final ViewPager viewPager;
        
        public MyTabListener(ViewPager viewPager) {
            this.viewPager = viewPager;
        }

        @Override
        public void onTabSelected(Tab tab, FragmentTransaction ft) {
            viewPager.setCurrentItem(tab.getPosition());
        }

        @Override
        public void onTabUnselected(Tab tab, FragmentTransaction ft) { }

        @Override
        public void onTabReselected(Tab tab, FragmentTransaction ft) { }
    }
}