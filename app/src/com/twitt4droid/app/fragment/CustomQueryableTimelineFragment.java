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
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.twitt4droid.app.R;
import com.twitt4droid.app.activity.TweetingActivity;
import com.twitt4droid.fragment.QueryableTimelineFragment;

public class CustomQueryableTimelineFragment extends QueryableTimelineFragment {

    @Override
    protected void onTwitterError(Exception ex) {
        Toast.makeText(getActivity().getApplicationContext(), 
                R.string.twitt4droid_error_message, 
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.timeline, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_tweet_item:
                startActivity(new Intent(getActivity(), TweetingActivity.class));
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }
}