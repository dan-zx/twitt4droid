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

import java.util.List;

import twitter4j.Status;
import android.app.Activity;
import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.twitt4droid.R;
import com.twitt4droid.task.ImageLoadingTask;

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

        public void setContent(Status status) {
            usernameTextView.setText(context.getString(R.string.twitt4droid_tweet_username_format, status.getUser().getScreenName(), status.getUser().getName()));
            tweetTextView.setText(status.getText());
            String dateText = context.getString(R.string.twitt4droid_tweet_date_format, 
                    DateFormat.getDateFormat(context.getApplicationContext()).format(status.getCreatedAt()),
                    DateFormat.getTimeFormat(context.getApplicationContext()).format(status.getCreatedAt()));
            tweetTimeTextView.setText(dateText);
            new ImageLoadingTask()
                .setImageView(profileImage)
                .setLoadingResourceImageId(R.drawable.twitt4droid_no_profile_image)
                .execute(status.getUser().getProfileImageURL());
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