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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockActivity;

import com.twitt4droid.Resources;
import com.twitt4droid.Twitt4droid;
import com.twitt4droid.app.R;
import com.twitt4droid.app.util.Dialogs;

import roboguice.inject.InjectView;

import twitter4j.AsyncTwitter;
import twitter4j.Status;
import twitter4j.TwitterAdapter;
import twitter4j.TwitterException;
import twitter4j.TwitterMethod;

import javax.inject.Inject;

public class TweetingActivity extends RoboSherlockActivity {
    
    private static final int TWEET_CHAR_LIMIT = 140;
    private static final int RED_COLOR = Color.parseColor("#FF0000");
    private static final String TAG = TweetingActivity.class.getSimpleName();
    
    @InjectView(R.id.new_message_edit_text) private EditText newTweetEditText;
    @Inject                                 private InputMethodManager inputMethodManager; 
    
    private TextView characterCountTextView;
    private MenuItem sendMenuItem;
    private int defaultCharacterCountTextViewTextColor;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.texting);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setUpLayout();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.tweeting, menu);
        sendMenuItem = menu.findItem(R.id.send_tweet_item);
        characterCountTextView = (TextView)menu.findItem(R.id.tweet_char_count_item).getActionView();
        characterCountTextView.setText(String.valueOf(TWEET_CHAR_LIMIT));
        defaultCharacterCountTextViewTextColor = characterCountTextView.getTextColors().getDefaultColor();
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
                    Dialogs.getNetworkAlertDialog(this).show();
                }
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

    private void setUpLayout() {
        newTweetEditText.addTextChangedListener(new TextWatcher() {
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() > TWEET_CHAR_LIMIT) {
                    characterCountTextView.setTextColor(RED_COLOR);
                    sendMenuItem.setEnabled(false);
                } else {
                    characterCountTextView.setTextColor(defaultCharacterCountTextViewTextColor);
                    sendMenuItem.setEnabled(true);
                }
                
                characterCountTextView.setText(String.valueOf(TWEET_CHAR_LIMIT - s.length()));
            }
            
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            
            @Override
            public void afterTextChanged(Editable s) { }
        });
        toggleSoftKeyboard();
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