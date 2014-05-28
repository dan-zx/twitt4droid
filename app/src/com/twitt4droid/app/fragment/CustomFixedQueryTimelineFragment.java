package com.twitt4droid.app.fragment;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.twitt4droid.app.R;
import com.twitt4droid.fragment.FixedQueryTimelineFragment;
import com.twitt4droid.widget.TweetDialog;

import twitter4j.TwitterException;

public class CustomFixedQueryTimelineFragment extends FixedQueryTimelineFragment {

    @Override
    protected void onTwitterError(TwitterException ex) {
        Toast.makeText(getActivity().getApplicationContext(), 
                R.string.twitt4droid_error_message, 
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setQuery("#WorldCup");
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
                new TweetDialog(getActivity()).show();
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }
}