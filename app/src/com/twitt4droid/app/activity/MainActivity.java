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
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.twitt4droid.app.R;
import com.twitt4droid.app.fragment.CustomFixedQueryTimelineFragment;
import com.twitt4droid.app.fragment.CustomHomeTimelineFragment;
import com.twitt4droid.app.fragment.CustomMentionsTimelineFragment;
import com.twitt4droid.app.fragment.CustomQueryableTimelineFragment;
import com.twitt4droid.app.fragment.CustomUserTimelineFragment;
import com.twitt4droid.fragment.BaseTimelineFragment;
import com.twitt4droid.fragment.FixedQueryTimelineFragment;
import com.twitt4droid.fragment.UserTimelineFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ActionBarActivity {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private FrameLayout contentLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        findViews();
        setUpDrawer();
        setUpTimelines();
    }

    private void findViews() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        contentLayout = (FrameLayout) findViewById(R.id.content_frame);
    }

    private void setUpDrawer() {
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
    }

    private void setUpTimelines() {
        SwipeTimelineFragmentPagerAdapter adapter = new SwipeTimelineFragmentPagerAdapter();
        adapter.addFragment(new CustomHomeTimelineFragment());
        adapter.addFragment(new CustomMentionsTimelineFragment());
        adapter.addFragment(new CustomUserTimelineFragment().setUsername("dan_zx"));
        adapter.addFragment(new CustomFixedQueryTimelineFragment().setQuery("#WorldCup"));
        adapter.addFragment(new CustomQueryableTimelineFragment());
        ViewPager viewPager = new ViewPager(this);
        viewPager.setAdapter(adapter);
        viewPager.setId(R.id.view_pager);
        PagerTabStrip pagerTabStrip = new PagerTabStrip(this);
        pagerTabStrip.setId(R.id.pager_strip);
        pagerTabStrip.setDrawFullUnderline(false);
        pagerTabStrip.setTabIndicatorColor(getResources().getColor(R.color.twitt4droid_primary_color));
        ViewPager.LayoutParams layoutParams = new ViewPager.LayoutParams();
        layoutParams.height = ViewPager.LayoutParams.WRAP_CONTENT;
        layoutParams.width = ViewPager.LayoutParams.MATCH_PARENT;
        layoutParams.gravity = Gravity.TOP;
        viewPager.addView(pagerTabStrip, layoutParams);
        contentLayout.addView(viewPager);
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
    
    private class SwipeTimelineFragmentPagerAdapter extends FragmentStatePagerAdapter {

        private final List<BaseTimelineFragment> fragments;

        private SwipeTimelineFragmentPagerAdapter() {
            super(getSupportFragmentManager());
            fragments = new ArrayList<>();
        }

        private void addFragment(BaseTimelineFragment fragment) {
            fragments.add(fragment);
            notifyDataSetChanged();
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
        public CharSequence getPageTitle(int position) {
            if (!fragments.isEmpty() && position >= 0) {
                BaseTimelineFragment fragment = fragments.get(position);
                if (fragment instanceof FixedQueryTimelineFragment) return ((FixedQueryTimelineFragment)fragment).getQuery();
                if (fragment instanceof UserTimelineFragment) return "@" + ((UserTimelineFragment)fragment).getUsername();
                return getString(fragment.getResourceTitle());
            }
            else return null;
        }
    }
}