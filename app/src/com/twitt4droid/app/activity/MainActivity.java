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
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.twitt4droid.Twitt4droid;
import com.twitt4droid.Twitt4droidAsyncTasks;
import com.twitt4droid.activity.UserProfileActivity;
import com.twitt4droid.app.R;
import com.twitt4droid.app.fragment.TimelinesFragment;
import com.twitt4droid.task.ImageLoader;

import twitter4j.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends ActionBarActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
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
        setUpUserEntry();
        setUpListEntry();
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
                if (getTwitterException() != null) {
                    Log.e(TAG, "Twitter error", getTwitterException());
                    Toast.makeText(getContext().getApplicationContext(), 
                            R.string.twitt4droid_error_message, 
                            Toast.LENGTH_LONG)
                            .show();
                } else if (result != null) {
                    userUsername.setText(getString(R.string.twitt4droid_username_format, result.getScreenName()));
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
            }
        }.execute(Twitt4droid.getCurrentUserUsername(this));
        userProfileImage.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, UserProfileActivity.class);
                Bundle b = new Bundle();
                b.putString(UserProfileActivity.EXTRA_USER_USERNAME, Twitt4droid.getCurrentUserUsername(MainActivity.this));
                intent.putExtras(b);
                startActivity(intent);
            }
        });
    }

    private void setUpListEntry() {
        drawerList = (ListView) drawerLayout.findViewById(R.id.drawer_list);
        String[] from = new String[] { "icon", "menu" };
        int[] to = new int[] { R.id.icon_image, R.id.menu_text };
        List<Map<String, Object>> data = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put(from[0], R.drawable.twitt4droid_ic_clock_holo_dark);
        map.put(from[1], getResources().getStringArray(R.array.drawer_options)[0]);
        data.add(map);
        map = new HashMap<>();
        map.put(from[0], R.drawable.ic_settings);
        map.put(from[1], getResources().getStringArray(R.array.drawer_options)[1]);
        data.add(map);
        SimpleAdapter drawerListAdapter = new SimpleAdapter(this, data, R.layout.drawer_item, from, to);
        drawerList.setAdapter(drawerListAdapter);
        drawerList.setOnItemClickListener(new DrawerItemClickListener());
        drawerList.setItemChecked(0, true);
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
                    setUpFragment(new TimelinesFragment());
                    break;
                case 1: 
                    startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                    break;
            }
            drawerList.setItemChecked(position, true);
            drawerLayout.closeDrawers();
        }        
    }
}