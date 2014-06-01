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
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.twitt4droid.Twitt4droid;
import com.twitt4droid.activity.UserProfileActivity;
import com.twitt4droid.app.R;
import com.twitt4droid.app.fragment.TimelinesFragment;
import com.twitt4droid.app.widget.DrawerItemAdapter;
import com.twitt4droid.app.widget.HeaderDrawerItem;
import com.twitt4droid.app.widget.SimpleDrawerItem;

public class MainActivity extends ActionBarActivity {

    private static final String FRAGMENT_TAG = "CURRENT_FRAGMENT";

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private ListView drawerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        setUpDrawerMenu();
    }

    private void setUpDrawerMenu() {
        drawerList = (ListView) drawerLayout.findViewById(R.id.left_drawer);
        DrawerItemAdapter drawerMenuAdapter = new DrawerItemAdapter(this);
        drawerMenuAdapter.add(new HeaderDrawerItem(this));
        drawerMenuAdapter.add(new SimpleDrawerItem(R.drawable.twitt4droid_ic_clock_holo_dark, R.string.drawer_timelines_option));
        drawerMenuAdapter.add(new SimpleDrawerItem(R.drawable.ic_settings, R.string.drawer_settings_option));
        drawerList.setAdapter(drawerMenuAdapter);
        drawerList.setOnItemClickListener(new DrawerItemClickListener());
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
    
    private class DrawerItemClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            switch(position) {
                case 0:
                    Intent intent = new Intent(MainActivity.this, UserProfileActivity.class);
                    Bundle b = new Bundle();
                    b.putString(UserProfileActivity.EXTRA_USER_USERNAME, Twitt4droid.getCurrentUserUsername(MainActivity.this));
                    intent.putExtras(b);
                    startActivity(intent);
                case 1: 
                    setUpFragment(new TimelinesFragment());
                    break;
                case 2: 
                    startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                    break;
            }
            drawerList.setItemChecked(position, true);
            drawerLayout.closeDrawer(drawerList);
        }        
    }
}