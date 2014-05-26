package com.twitt4droid.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
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
import com.twitt4droid.data.dao.UserDAO;
import com.twitt4droid.data.dao.impl.DAOFactory;
import com.twitt4droid.task.ImageLoader;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;

public class TweetDialog extends Dialog {

    private static final String TAG = TweetDialog.class.getSimpleName();

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

    public TweetDialog(Context context) {
        super(context);
        init();
    }

    public TweetDialog(Context context, int theme) {
        super(context, theme);
        init();
    }

    public TweetDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init();
    }
    
    public TweetDialog setAsReplayTweet(Status statusToReplay) {
        if (statusToReplay != null) {
            tweetEditText.setText("@" + statusToReplay.getUser().getScreenName());
            onTweetContentChanged(tweetEditText.getText().toString());
        }
        return this;
    }

    private void init() {
        if (!Twitt4droid.isUserLoggedIn(getContext())) throw new IllegalStateException("User must be logged in in order to use TweetDialog");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.twitt4droid_new_tweet);
        inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        findViews();
        initConsts();
        setUpTweetEditText();
        setUpTweetButton();
        new UserDataFetcher().execute();
    }

    private void findViews() {
        userProfileImage = (ImageView) findViewById(R.id.user_profile_image);
        userUsername = (TextView) findViewById(R.id.user_username);
        userScreenName = (TextView) findViewById(R.id.user_screen_name);
        charCounterTextView = (TextView) findViewById(R.id.char_counter_text);
        tweetEditText = (EditText) findViewById(R.id.tweet_content);
        tweetButton = (ImageButton) findViewById(R.id.tweet_button);
    }

    private void initConsts() {
        tweetCharLimit = getContext().getResources().getInteger(R.integer.twitt4droid_tweet_char_limit);
        redColor = getContext().getResources().getColor(R.color.twitt4droid_error_color);
        defaultCharacterCountTextViewTextColor = charCounterTextView.getTextColors().getDefaultColor();
    }

    private void toggleSoftKeyboard() {
        inputMethodManager.toggleSoftInput(
                InputMethodManager.SHOW_FORCED, 
                InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    private void hideSoftKeyboard() {
        inputMethodManager.hideSoftInputFromWindow(
                tweetEditText.getWindowToken(),
                0);
    }

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
    
    private void setUpTweetButton() {
        tweetButton.setEnabled(false);
        tweetButton.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View view) {
                new TweetSender().execute(tweetEditText.getText().toString());
                hideSoftKeyboard();
                dismiss();
            }
        });
    }

    private class UserDataFetcher extends AsyncTask<Void, Void, User> {

        private Twitter twitter;
        private UserDAO userDao;
        private long userId;
        private boolean isConnectedToInternet;
        
        @Override
        protected void onPreExecute() {
            Context appContext = getContext().getApplicationContext();
            isConnectedToInternet = Resources.isConnectedToInternet(appContext);
            twitter = Twitt4droid.getTwitter(appContext);
            userId = Twitt4droid.getCurrentUserId(appContext);
            userDao = new DAOFactory(appContext).getUserDAO();
        }
        
        @Override
        protected User doInBackground(Void... params) {
            try {
                if (!isConnectedToInternet) return userDao.fetchById(userId);
                else return twitter.verifyCredentials();
            } catch (Exception ex) {
                Log.e(TAG, "Error while retrieving user data", ex);
            }

            return null;
        }
        
        @Override
        protected void onPostExecute(User result) {
            if (result != null) {
                userUsername.setText("@" + result.getScreenName());
                userScreenName.setText(result.getName());
                new ImageLoader(getContext())
                    .setImageView(userProfileImage)
                    .setLoadingColorId(R.color.twitt4droid_no_image_background)
                    .execute(result.getProfileImageURL());
            } else {
                Toast.makeText(getContext().getApplicationContext(), 
                        R.string.twitt4droid_error_message, 
                        Toast.LENGTH_LONG).show();
            }
        }
    }
    
    private class TweetSender extends AsyncTask<String, Void, Status> {

        private Twitter twitter;
        
        @Override
        protected void onPreExecute() {
            twitter = Twitt4droid.getTwitter(getContext().getApplicationContext());
            Toast.makeText(getContext().getApplicationContext(),
                    R.string.twitt4droid_sending_tweet,
                    Toast.LENGTH_LONG).show();
        }

        @Override
        protected twitter4j.Status doInBackground(String... params) {
            try {
                return twitter.updateStatus(params[0]);
            } catch (TwitterException ex) {
                Log.e(TAG, "Error while sending tweet [" + params[0] + "]", ex);
            }
            
            return null;
        }
        
        @Override
        protected void onPostExecute(twitter4j.Status result) {
            if (result != null) {
                Toast.makeText(getContext().getApplicationContext(),
                        R.string.twitt4droid_tweet_sent,
                        Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getContext().getApplicationContext(),
                        R.string.twitt4droid_error_message,
                        Toast.LENGTH_LONG).show();
            }
        }
    }
}