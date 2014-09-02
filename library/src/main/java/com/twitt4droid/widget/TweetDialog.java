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
package com.twitt4droid.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.twitt4droid.R;
import com.twitt4droid.Resources;
import com.twitt4droid.Twitt4droid;
import com.twitt4droid.util.Images.ImageLoader;
import com.twitt4droid.util.Strings;

import twitter4j.AsyncTwitter;
import twitter4j.Status;
import twitter4j.TwitterAdapter;
import twitter4j.TwitterException;
import twitter4j.TwitterMethod;
import twitter4j.User;

/**
 * A dialog to compose a tweet. Call it like this:
 * <pre>
 * {@code 
 * new TweetDialog(context).show();
 * new TweetDialog(context).addTextToTweet(text).show();
 * }
 * </pre>
 * 
 * @author Daniel Pedraza-Arcega
 * @since version 1.0
 */
public class TweetDialog extends Dialog {

    private static final String TAG = TweetDialog.class.getSimpleName();

    private AsyncTwitter twitter;
    private ImageView userProfileImage;
    private TextView userUsername;
    private TextView userScreenName;
    private TextView charCounterTextView;
    private EditText tweetEditText;
    private ImageButton tweetButton;
    private InputMethodManager inputMethodManager;
    private int tweetCharLimit;
    private int redColor;
    private int defaultCharacterCountTextViewTextColor;

    /**
     * Creates a TweetDialog.
     * 
     * @param context the Context in which the Dialog should run.
     */
    public TweetDialog(Context context) {
        super(context);
        init();
    }

    /**
     * Pre-appends the given text to the tweet.
     * 
     * @param text any text.
     * @return this TweetDialog.
     */
    public TweetDialog addTextToTweet(String text) {
        if (!Strings.isNullOrBlank(text)) {
            tweetEditText.setText(text);
            onTweetContentChanged(tweetEditText.getText().toString());
            tweetEditText.setSelection(tweetEditText.getText().length());
        }

        return this;
    }

    /** Initializes components. */
    private void init() {
        if (!Twitt4droid.isUserLoggedIn(getContext())) throw new IllegalStateException("User must be logged in in order to use TweetDialog");
        twitter = Twitt4droid.getAsyncTwitter(getContext());
        twitter.addListener(new TwitterAdapter() {
            @Override
            public void gotUserDetail(final User user) {
                ((Activity) getContext()).runOnUiThread(new Runnable() {
                    
                    @Override
                    public void run() {
                        setUpUser(user);
                    }
                });
            }

            @Override
            public void updatedStatus(Status status) {
                ((Activity) getContext()).runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(getContext().getApplicationContext(), 
                                R.string.twitt4droid_tweet_sent, 
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                });
            }
            
            @Override
            public void onException(TwitterException te, TwitterMethod method) {
                Log.e(TAG, "Twitter error in " + method, te);
                ((Activity) getContext()).runOnUiThread(new Runnable() {
                    
                    @Override
                    public void run() {
                        Toast.makeText(getContext().getApplicationContext(),
                                R.string.twitt4droid_error_message,
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.twitt4droid_new_tweet);
        inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        findViews();
        initConsts();
        setUpTweetEditText();
        setUpTweetButton();
        setUpUser(Twitt4droid.getCurrentUser(getContext()));
        if (Resources.isConnectedToInternet(getContext())) twitter.showUser(Twitt4droid.getCurrentUser(getContext()).getScreenName());
    }

    /**
     * Sets up the authenticated user.
     * 
     * @param user the authenticated user.
     */
    private void setUpUser(User user) {
        userUsername.setText(getContext().getString(R.string.twitt4droid_username_format, user.getScreenName()));
        userScreenName.setText(user.getName());
        new ImageLoader(getContext())
            .setImageView(userProfileImage)
            .execute(user.getProfileImageURL());
    }

    /** Finds the custom views. */
    private void findViews() {
        userProfileImage = (ImageView) findViewById(R.id.user_profile_image);
        userUsername = (TextView) findViewById(R.id.user_username);
        userScreenName = (TextView) findViewById(R.id.user_screen_name);
        charCounterTextView = (TextView) findViewById(R.id.char_counter_text);
        tweetEditText = (EditText) findViewById(R.id.tweet_content);
        tweetButton = (ImageButton) findViewById(R.id.tweet_button);
    }

    /** Initializes some constants. */
    private void initConsts() {
        tweetCharLimit = getContext().getResources().getInteger(R.integer.twitt4droid_tweet_char_limit);
        redColor = getContext().getResources().getColor(R.color.twitt4droid_error_color);
        defaultCharacterCountTextViewTextColor = charCounterTextView.getTextColors().getDefaultColor();
    }

    /** Toggles the software keyboard. */
    private void toggleSoftKeyboard() {
        inputMethodManager.toggleSoftInput(
                InputMethodManager.SHOW_FORCED, 
                InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    /** Hides the software keyboard. */
    private void hideSoftKeyboard() {
        inputMethodManager.hideSoftInputFromWindow(
                tweetEditText.getWindowToken(),
                0);
    }

    /** Sets up the tweet edit text view. */
    private void setUpTweetEditText() {
        toggleSoftKeyboard();
        tweetEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) { }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                onTweetContentChanged(s.toString());
            }
        });
    }

    /**
     * Action when the tweet content has changed.
     * 
     * @param text new text.
     */
    private void onTweetContentChanged(String text) {
        if (text.trim().length() == 0) tweetButton.setEnabled(false);
        else if (text.trim().length() > tweetCharLimit) {
            if (charCounterTextView != null) charCounterTextView.setTextColor(redColor);
            tweetButton.setEnabled(false);
        } else {
            if (charCounterTextView != null) charCounterTextView.setTextColor(defaultCharacterCountTextViewTextColor);
            tweetButton.setEnabled(true);
        }

        charCounterTextView.setText(String.valueOf(tweetCharLimit-text.length()));
    }

    /** Sets up the tweet button view. */
    private void setUpTweetButton() {
        tweetButton.setEnabled(false);
        tweetButton.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View view) {
                if (Resources.isConnectedToInternet(getContext())) {
                    twitter.updateStatus(tweetEditText.getText().toString());
                    hideSoftKeyboard();
                    dismiss();
                } else {
                    Toast.makeText(getContext().getApplicationContext(),
                            R.string.twitt4droid_is_offline_messege,
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}