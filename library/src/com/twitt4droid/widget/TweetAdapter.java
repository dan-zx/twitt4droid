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
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.twitt4droid.R;
import com.twitt4droid.Resources;
import com.twitt4droid.Twitt4droid;
import com.twitt4droid.activity.UserProfileActivity;
import com.twitt4droid.util.Images.ImageLoader;

import twitter4j.AsyncTwitter;
import twitter4j.Status;
import twitter4j.TwitterAdapter;
import twitter4j.TwitterException;
import twitter4j.TwitterMethod;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter that is responsible for making a View for each Twitter status.
 *  
 * @author Daniel Pedraza-Arcega
 * @since version 1.0
 */
public class TweetAdapter extends BaseAdapter {

    private static final String TAG = TweetAdapter.class.getSimpleName();

    private final Context context;

    private AsyncTwitter twitter;
    private List<Status> data;
    private boolean isUsingDarkTheme;

    /**
     * Standard constructor.
     * 
     * @param context the context.
     */
    public TweetAdapter(Context context) {
        if (!Twitt4droid.isUserLoggedIn(context)) throw new IllegalStateException("User must be logged in in order to use TweetAdapter");
        this.context = context;
        this.data = new ArrayList<Status>();
        this.twitter = Twitt4droid.getAsyncTwitter(context);
        setUpTwitter();
    }

    /** Sets up twitter callbacks. */
    private void setUpTwitter() {
        twitter.addListener(new TwitterAdapter() {

            @Override
            public void createdFavorite(Status status) {
                ((Activity)context).runOnUiThread(new Runnable() {
                    
                    @Override
                    public void run() {
                        Toast.makeText(context.getApplicationContext(), 
                                R.string.twitt4droid_tweet_favorited, 
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                });
            }
            
            @Override
            public void retweetedStatus(Status retweetedStatus) {
                ((Activity) context).runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(context.getApplicationContext(), 
                                R.string.twitt4droid_tweet_retweeted, 
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                });
            }

            @Override
            public void onException(TwitterException te, TwitterMethod method) {
                Log.e(TAG, "Twitter error in" + method, te);
                ((Activity) context).runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(context.getApplicationContext(), 
                                R.string.twitt4droid_error_message, 
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                });
            }
        });
    }

    /** @param data the data to be displayed. */
    public void set(List<Status> data) {
        this.data = data == null ? new ArrayList<Status>() : data;
        notifyDataSetChanged();
    }

    /** {@inheritDoc} */
    @Override
    public int getCount() {
        return data.size();
    }

    /** {@inheritDoc} */
    @Override
    public Status getItem(int position) {
        return data.get(position);
    }

    /** {@inheritDoc} */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /** @param useDarkTheme if this adapter uses or not the dark theme. */
    public void setUseDarkTheme(boolean useDarkTheme) {
        isUsingDarkTheme = useDarkTheme;
    }

    /** {@inheritDoc} */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.twitt4droid_tweet_item, parent);
            holder = new ViewHolder();
            holder.context = context;
            holder.twitter = twitter;
            holder.isUsingDarkTheme = isUsingDarkTheme;
            holder.contentLayout = (RelativeLayout) convertView.findViewById(R.id.content_layout);
            holder.profileImage = (ImageView) convertView.findViewById(R.id.tweet_profile_image);
            holder.clockImage = (ImageView) convertView.findViewById(R.id.clock_image);
            holder.tweetTextView = (TextView) convertView.findViewById(R.id.tweet_content_text);
            holder.usernameTextView = (TextView) convertView.findViewById(R.id.tweet_username_text);
            holder.tweetTimeTextView = (TextView) convertView.findViewById(R.id.tweet_time_text);
            holder.overflowButton = (ImageButton) convertView.findViewById(R.id.tweet_options_button);
            convertView.setTag(holder);
        } else holder = (ViewHolder) convertView.getTag();
        holder.setContent(getItem(position));
        return convertView;
    }

    /**
     * Stores each of the component views inside the tag field of a Layout, so it can immediately
     * access them without the need to look them up repeatedly.
     * 
     * @author Daniel Pedraza-Arcega
     * @since version 1.0
     */
    private static class ViewHolder {

        private Context context;
        private AsyncTwitter twitter;
        private RelativeLayout contentLayout;
        private ImageView profileImage;
        private ImageView clockImage;
        private TextView usernameTextView;
        private TextView tweetTextView;
        private TextView tweetTimeTextView;
        private ImageButton overflowButton;
        private boolean isUsingDarkTheme;

        /**
         * Sets up the content of a Twitter status.
         * 
         * @param status a Twitter status.
         */
        private void setContent(final Status status) {
            setUpDarkThemeIfNeeded();
            usernameTextView.setText(context.getString(R.string.twitt4droid_tweet_username_format, status.getUser().getScreenName(), status.getUser().getName()));
            tweetTextView.setText(status.getText());
            String dateText = context.getString(R.string.twitt4droid_tweet_date_format, 
                    DateFormat.getDateFormat(context.getApplicationContext()).format(status.getCreatedAt()),
                    DateFormat.getTimeFormat(context.getApplicationContext()).format(status.getCreatedAt()));
            tweetTimeTextView.setText(dateText);
            new ImageLoader(context)
                .setImageView(profileImage)
                .setLoadingColorId(R.color.twitt4droid_no_image_background)
                .execute(status.getUser().getProfileImageURL());
            profileImage.setOnClickListener(new View.OnClickListener() {
                
                @Override
                public void onClick(View view) {
                    Intent profileIntent = UserProfileActivity.buildIntent(context, status.getUser().getScreenName(), isUsingDarkTheme);
                    context.startActivity(profileIntent);
                }
            });
            overflowButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    new AlertDialog.Builder(context)
                        .setItems(R.array.twitt4droid_tweet_context_menu, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        String text = context.getString(R.string.twitt4droid_username_format, status.getUser().getScreenName());
                                        new TweetDialog(context).addTextToTweet(text).show();
                                        break;
                                    case 1:
                                        if (Resources.isConnectedToInternet(context)) twitter.retweetStatus(status.getId());
                                        else {
                                            Toast.makeText(context.getApplicationContext(),
                                                    R.string.twitt4droid_is_offline_messege,
                                                    Toast.LENGTH_LONG).show();
                                        }
                                        break;
                                    case 2:
                                        if (Resources.isConnectedToInternet(context)) twitter.createFavorite(status.getId());
                                        else {
                                            Toast.makeText(context.getApplicationContext(),
                                                    R.string.twitt4droid_is_offline_messege,
                                                    Toast.LENGTH_LONG).show();
                                        }
                                        break;
                                }
                            }
                        })
                        .setInverseBackgroundForced(true)
                        .show();
                }
            });
        }

        /** Sets up the dark theme if needed. */
        private void setUpDarkThemeIfNeeded() {
            if (isUsingDarkTheme) {
                contentLayout.setBackgroundResource(R.color.twitt4droid_tweet_background_holo_dark);
                overflowButton.setImageResource(R.drawable.twitt4droid_ic_overflow_holo_dark);
                clockImage.setImageResource(R.drawable.twitt4droid_ic_clock_holo_dark);
            }
        }
    }
}