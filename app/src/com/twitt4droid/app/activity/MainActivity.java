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
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.twitt4droid.app.R;
import com.twitt4droid.app.fragment.CustomHomeTimelineFragment;
import com.twitt4droid.app.fragment.CustomMentionsTimelineFragment;
import com.twitt4droid.app.fragment.CustomQueryableTimelineFragment;
import com.twitt4droid.app.fragment.UserFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ActionBarActivity {

    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        SwipeFragmentPagerAdapter adapter = new SwipeFragmentPagerAdapter()
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
        getSupportActionBar().addTab(getSupportActionBar()
                .newTab()
                .setContentDescription(R.string.home_tab_title)
                .setIcon(R.drawable.ic_home_holo_light)
                .setTabListener(adapter));
        getSupportActionBar().addTab(getSupportActionBar()
                .newTab()
                .setContentDescription(R.string.mentions_tab_title)
                .setIcon(R.drawable.ic_notifications_holo_light)
                .setTabListener(adapter));
        getSupportActionBar().addTab(getSupportActionBar()
                .newTab()
                .setContentDescription(R.string.query_tab_title)
                .setIcon(R.drawable.twitt4droid_ic_search_holo_light)
                .setTabListener(adapter));
        getSupportActionBar().addTab(getSupportActionBar()
                .newTab()
                .setContentDescription(R.string.user_tab_title)
                .setIcon(R.drawable.ic_person_holo_light)
                .setTabListener(adapter));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings_item:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

    private class SwipeFragmentPagerAdapter extends FragmentPagerAdapter implements ActionBar.TabListener {

        private final List<Fragment> fragments;

        public SwipeFragmentPagerAdapter() {
            super(getSupportFragmentManager());
            fragments = new ArrayList<Fragment>();
        }
        
        public SwipeFragmentPagerAdapter addFragment(Fragment fragment) {
            fragments.add(fragment);
            notifyDataSetChanged();
            return this;
        }

        @Override
        public Fragment getItem(int position) {
            if (!fragments.isEmpty() && position >= 0) return fragments.get(position);
            else return null;
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
        
        @Override
        public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
            viewPager.setCurrentItem(tab.getPosition());
        }

        @Override
        public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) { }

        @Override
        public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) { }
    }
}