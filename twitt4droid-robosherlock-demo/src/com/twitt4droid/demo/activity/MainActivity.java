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
package com.twitt4droid.demo.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;

import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockFragmentActivity;

import com.twitt4droid.demo.R;
import com.twitt4droid.demo.fragment.ProfileFragment;
import com.twitt4droid.demo.fragment.StreamFragment;

import roboguice.inject.InjectExtra;
import roboguice.inject.InjectView;

import twitter4j.User;

public class MainActivity extends RoboSherlockFragmentActivity {

    public static final String EXTRA_USER = "com.twitt4droid.demo.extra.user";

    @InjectView(R.id.view_pager) private ViewPager viewPager;
    @InjectExtra(EXTRA_USER)     private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        viewPager.setAdapter(new MyFragmentPagerAdapter
                (getSupportFragmentManager(), 
                        new StreamFragment(),
                        new ProfileFragment().setUser(user)));
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
                .setText(R.string.stream_tab_title)
                .setTabListener(listener));
        getSupportActionBar().addTab(getSupportActionBar()
                .newTab()
                .setText(R.string.profile_tab_title)
                .setTabListener(listener));
    }
    
    private static class MyFragmentPagerAdapter extends FragmentPagerAdapter {

        private static final int PAGE_COUNT = 2;

        private Fragment[] fragments;

        public MyFragmentPagerAdapter(FragmentManager fm, Fragment... fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments[position];
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
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