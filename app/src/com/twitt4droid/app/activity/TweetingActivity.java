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

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.twitt4droid.Resources;
import com.twitt4droid.Twitt4droid;
import com.twitt4droid.app.R;
import com.twitt4droid.util.Strings;

import twitter4j.AsyncTwitter;
import twitter4j.Status;
import twitter4j.TwitterAdapter;
import twitter4j.TwitterException;
import twitter4j.TwitterMethod;

public class TweetingActivity extends ActionBarActivity {
    
    private static final int TWEET_CHAR_LIMIT = 140;
    private static final int RED_COLOR = Color.parseColor("#FF0000");
    private static final String TWEET_BEFORE_RESTART = "TWEET_BEFORE_RESTART";
    private static final String TAG = TweetingActivity.class.getSimpleName();
    
    private EditText newTweetEditText;
    private InputMethodManager inputMethodManager; 
    private TextView characterCountTextView;
    private MenuItem sendMenuItem;
    private int defaultCharacterCountTextViewTextColor;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.texting);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        setUpLayout(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(TWEET_BEFORE_RESTART, newTweetEditText.getText().toString());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tweeting, menu);
        sendMenuItem = menu.findItem(R.id.send_tweet_item);
        characterCountTextView = (TextView) MenuItemCompat.getActionView(menu.findItem(R.id.tweet_char_count_item));
        characterCountTextView.setText(String.valueOf(TWEET_CHAR_LIMIT));
        defaultCharacterCountTextViewTextColor = characterCountTextView.getTextColors().getDefaultColor();
        onTweetTextChanged(newTweetEditText.getText().toString());
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        hideSoftKeyboard();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.send_tweet_item:
                if (Resources.isConnectedToInternet(this)) {
                    if (newTweetEditText.getText().toString().trim().length() > 0 && 
                            newTweetEditText.getText().toString().trim().length() <= TWEET_CHAR_LIMIT) {
                        sendTweet();
                        finish();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), 
                            R.string.twitt4droid_is_offline_messege, 
                            Toast.LENGTH_SHORT).show();
                }
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

    private void setUpLayout(Bundle savedInstanceState) {
        newTweetEditText = (EditText) findViewById(R.id.new_message_edit_text);
        newTweetEditText.addTextChangedListener(new TextWatcher() {
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                onTweetTextChanged(s.toString());
            }
            
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            
            @Override
            public void afterTextChanged(Editable s) { }
        });

        if (savedInstanceState != null) {
            String tweet = savedInstanceState.getString(TWEET_BEFORE_RESTART);
            if (tweet == null) tweet = Strings.EMPTY;
            newTweetEditText.setText(tweet);
        }

        toggleSoftKeyboard();
    }

    private void onTweetTextChanged(String text) {
        if (text.trim().length() > TWEET_CHAR_LIMIT) {
            if (characterCountTextView != null) characterCountTextView.setTextColor(RED_COLOR);
            if (sendMenuItem != null) sendMenuItem.setEnabled(false);
        } else {
            if (characterCountTextView != null) characterCountTextView.setTextColor(defaultCharacterCountTextViewTextColor);
            if (sendMenuItem != null) sendMenuItem.setEnabled(true);
        }
        
        if (characterCountTextView != null) {
            characterCountTextView.setText(String.valueOf(TWEET_CHAR_LIMIT - text.length()));
        }
    }

    private void sendTweet() {
        AsyncTwitter twitter = Twitt4droid.getAsyncTwitter(this);
        twitter.addListener(new TwitterAdapter() {
            @Override
            public void updatedStatus(Status status) {
                Log.d(TAG, "Tweet [" + status.getText() + "] sent");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                R.string.tweet_sent,
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                });
            }

            @Override
            public void onException(TwitterException te, TwitterMethod method) {
                Log.e(TAG, "Error while sending tweet", te);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                R.string.twitt4droid_error_message,
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
        Toast.makeText(getApplicationContext(),
                R.string.sending_tweet,
                Toast.LENGTH_SHORT)
                .show();
        twitter.updateStatus(newTweetEditText.getText().toString().trim());
    }
    
    private void toggleSoftKeyboard() {
        inputMethodManager.toggleSoftInput(
                InputMethodManager.SHOW_FORCED, 
                InputMethodManager.HIDE_IMPLICIT_ONLY);
    }
    
    private void hideSoftKeyboard() {
        inputMethodManager.hideSoftInputFromWindow(
                newTweetEditText.getWindowToken(),
                0);
    }
}