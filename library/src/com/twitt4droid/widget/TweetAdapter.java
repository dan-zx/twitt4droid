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
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.twitt4droid.R;
import com.twitt4droid.Twitt4droid;
import com.twitt4droid.task.ImageLoader;

import twitter4j.AsyncTwitter;
import twitter4j.Status;
import twitter4j.TwitterAdapter;

import java.util.List;

public class TweetAdapter extends ArrayAdapter<Status> {

    public TweetAdapter(Context context, int resource) {
        super(context, resource);
    }
    
    public TweetAdapter(Context context, int resource, List<Status> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        Status rowItem = getItem(position);
        LayoutInflater mInflater = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.twitt4droid_tweet_item, null);
            holder = new ViewHolder()
                    .setContext(getContext())
                    .setProfileImage((ImageView) convertView.findViewById(R.id.tweet_profile_image))
                    .setTweetTextView((TextView) convertView.findViewById(R.id.tweet_content_text))
                    .setUsernameTextView((TextView) convertView.findViewById(R.id.tweet_username_text))
                    .setTweetTimeTextView((TextView) convertView.findViewById(R.id.tweet_time_text))
                    .setOverflowButton((ImageButton) convertView.findViewById(R.id.tweet_options_button));
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.setContent(rowItem);
        return convertView;
    }
    
    private static class ViewHolder {
        
        private Context context;
        private ImageView profileImage;
        private TextView usernameTextView;
        private TextView tweetTextView;
        private TextView tweetTimeTextView;
        private ImageButton overflowButton;

        public ViewHolder setContext(Context context) {
            this.context = context;
            return this;
        }

        public void setContent(final Status status) {
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
            overflowButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    new AlertDialog.Builder(context)
                        .setItems(R.array.twitt4droid_tweet_context_menu, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final Context appContext = context.getApplicationContext();
                                AsyncTwitter asyncTwitter = Twitt4droid.getAsyncTwitter(appContext);
                                asyncTwitter.addListener(new TwitterAdapter() {
                                    @Override
                                    public void retweetedStatus(Status retweetedStatus) {
                                        Toast.makeText(
                                                appContext, 
                                                R.string.twitt4droid_tweet_retweeted, 
                                                Toast.LENGTH_SHORT)
                                                .show();
                                    }
                                    
                                    @Override
                                    public void createdFavorite(Status status) {
                                        Toast.makeText(
                                                appContext, 
                                                R.string.twitt4droid_tweet_favorited, 
                                                Toast.LENGTH_SHORT)
                                                .show();
                                    }
                                });
                                switch (which) {
                                    case 0:
                                        new TweetDialog(context)
                                            .setAsReplayTweet(status)
                                            .show();
                                        break;
                                    case 1:
                                        asyncTwitter.retweetStatus(status.getId());
                                        break;
                                    case 2:
                                        asyncTwitter.createFavorite(status.getId());
                                        break;
                                }
                            }
                        })
                        .setInverseBackgroundForced(true)
                        .show();
                }
            });
        }

        public ViewHolder setProfileImage(ImageView profileImage) {
            this.profileImage = profileImage;
            return this;
        }

        public ViewHolder setUsernameTextView(TextView usernameTextView) {
            this.usernameTextView = usernameTextView;
            return this;
        }
        
        public ViewHolder setTweetTextView(TextView tweetTextView) {
            this.tweetTextView = tweetTextView;
            return this;
        }
        
        public ViewHolder setTweetTimeTextView(TextView tweetTimeTextView) {
            this.tweetTimeTextView = tweetTimeTextView;
            return this;
        }
        
        public ViewHolder setOverflowButton(ImageButton overflowButton) {
            this.overflowButton = overflowButton;
            return this;
        }
    }
}