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
package com.twitt4droid;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.twitt4droid.data.source.Twitt4droidDatabaseHelper;
import com.twitt4droid.util.Images;

import org.json.JSONException;
import org.json.JSONObject;

import twitter4j.AsyncTwitter;
import twitter4j.AsyncTwitterFactory;
import twitter4j.RateLimitStatus;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.URLEntity;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

import java.util.Date;

/**
 * Utility class for loading Twitter configurations.
 * 
 * @author Daniel Pedraza-Arcega
 * @since version 1.0
 */
public final class Twitt4droid {

    /**
     * Default constructor. Do NOT try to initialize this class, it is suppose
     * to be an static utility.
     */
    private Twitt4droid() {
        throw new IllegalAccessError("This class cannot be instantiated nor extended");
    }

    /**
     * Gets the current Twitter with consumer and access tokens pre-initialized.
     * 
     * @param context the application context.
     * @return an Twitter object.
     */
    public static Twitter getTwitter(Context context) {
        return new TwitterFactory(getCurrentConfig(context)).getInstance();
    }

    /**
     * Gets the current AsyncTwitter with consumer and access tokens pre-initialized.
     * 
     * @param context the application context.
     * @return an AsyncTwitter object.
     */
    public static AsyncTwitter getAsyncTwitter(Context context) {
        return new AsyncTwitterFactory(getCurrentConfig(context)).getInstance();
    }

    /**
     * Gets the current twitter4j configuration with consumer and access tokens pre-initialized. You
     * can use this method to build a Twitter objects.
     * 
     * @param context the application context.
     * @return an Configuration object.
     */
    private static Configuration getCurrentConfig(Context context) {
        SharedPreferences preferences = Resources.getPreferences(context);
        return new ConfigurationBuilder()
                .setOAuthConsumerKey(Resources.getMetaData(context, context.getString(R.string.twitt4droid_consumer_key_metadata), null))
                .setOAuthConsumerSecret(Resources.getMetaData(context, context.getString(R.string.twitt4droid_consumer_secret_metadata), null))
                .setOAuthAccessToken(preferences.getString(context.getString(R.string.twitt4droid_oauth_token_key), null))
                .setOAuthAccessTokenSecret(preferences.getString(context.getString(R.string.twitt4droid_oauth_secret_key), null))
                .build();
    }

    /**
     * Checks if there is any user authentication information stored in this app.
     * 
     * @param context the application context.
     * @return {@code true} if there is any user authentication information stored in this app;
     *         otherwise {@code false}.
     */
    public static boolean isUserLoggedIn(Context context) {
        return Resources.getPreferences(context).getBoolean(
                context.getString(R.string.twitt4droid_user_is_logged_in_key), false);
    }
    
    public static boolean areConsumerTokensAvailable(Context context) {
        return Resources.getMetaData(context, context.getString(R.string.twitt4droid_consumer_key_metadata), null) != null && 
               Resources.getMetaData(context, context.getString(R.string.twitt4droid_consumer_secret_metadata), null) != null;
    }

    /**
     * Saves authentication information from the given AccessToken in {@code SharedPreferences}.
     * 
     * @param context the application context.
     * @param token an AccessToken.
     */
    public static void saveAuthenticationInfo(Context context, AccessToken token) {
        Resources.getPreferences(context)
            .edit()
            .putString(context.getString(R.string.twitt4droid_oauth_token_key), token.getToken())
            .putString(context.getString(R.string.twitt4droid_oauth_secret_key), token.getTokenSecret())
            .putBoolean(context.getString(R.string.twitt4droid_user_is_logged_in_key), true)
            .commit();
    }

    /**
     * Deletes any authentication information from {@code SharedPreferences}.
     * 
     * @param context the application context.
     */
    public static void deleteAuthenticationInfo(Context context) {
        Resources.getPreferences(context)
            .edit()
            .remove(context.getString(R.string.twitt4droid_oauth_token_key))
            .remove(context.getString(R.string.twitt4droid_oauth_secret_key))
            .remove(context.getString(R.string.twitt4droid_user_is_logged_in_key))
            .commit();
    }

    /**
     * Saves a twitter user.
     * 
     * @param user the user to save.
     * @param context the application context.
     */
    public static void saveOrUpdateUser(User user, Context context) {
        Resources.getPreferences(context)
            .edit()
            .putString(context.getString(R.string.twitt4droid_user_key), UserJSONImpl.toJSON(user))
            .commit();
    }

    /**
     * Deletes the current twitter user.
     *
     * @param context the application context.
     */
    public static void deleteCurrentUser(Context context) {
        Resources.getPreferences(context)
            .edit()
            .remove(context.getString(R.string.twitt4droid_user_key))
            .commit();
    }

    /**
     * Gets the current twitter user.
     * 
     * @param context the application context.
     * @return the current twitter user if exists; otherwise {@code null}.
     */
    public static User getCurrentUser(Context context) {
        String json = Resources.getPreferences(context).getString(
                context.getString(R.string.twitt4droid_user_key),
                null);
        return UserJSONImpl.fromJSON(json);
    }

    /**
     * Deletes all information stored in databases and image caches.
     * 
     * @param context the application context.
     */
    public static void clearCache(Context context) {
        Twitt4droidDatabaseHelper.destroyDb(context);
        Images.clearCache();
    }

    /**
     * Deletes all information stored by twitt4droid.
     * 
     * @param context the application context.
     */
    public static void resetData(Context context) {
        Resources.getPreferences(context)
            .edit()
            .clear()
            .commit();
        
        Twitt4droidDatabaseHelper.destroyDb(context);
        Images.clearCache();
    }

    /**
     * Twitter user json implementation.
     * 
     * @author Daniel Pedraza-Arcega
     * @since version 1.0
     */
    private static class UserJSONImpl implements User {

        private static final String TAG = UserJSONImpl.class.getSimpleName();
        private static final long serialVersionUID = -3838693829821915548L;

        private long id;
        private String name;
        private String screenName;
        private String profileImageURL;
        private String profileBannerURL;
        private String url;
        private String description;
        private String location;
        
        private UserJSONImpl() { }

        /**
         * Converts any user to a json string.
         * 
         * @param user any user
         * @return a json string.
         */
        private static String toJSON(User user) {
            return new StringBuilder()
                .append("{")
                .append("\"id\": ").append(user.getId()).append(", ")
                .append("\"name\": ").append("\"").append(user.getName()).append("\"").append(", ")
                .append("\"screenName\": ").append("\"").append(user.getScreenName()).append("\"").append(", ")
                .append("\"profileImageURL\": ").append("\"").append(user.getProfileImageURL()).append("\"").append(", ")
                .append("\"profileBannerURL\": ").append("\"").append(user.getProfileBannerURL()).append("\"").append(", ")
                .append("\"url\": ").append("\"").append(user.getURL()).append("\"").append(", ")
                .append("\"description\": ").append("\"").append(user.getDescription()).append("\"").append(", ")
                .append("\"location\": ").append("\"").append(user.getLocation()).append("\"")
                .append("}")
                .toString();
        }

        /**
         * Converts a json string to a user.
         * 
         * @param json a json string
         * @return a user.
         */
        private static User fromJSON(String json) {
            UserJSONImpl user = null;
            try {
                JSONObject obj = new JSONObject(json);
                user = new UserJSONImpl();
                user.id = obj.getLong("id");
                user.name = obj.getString("name");
                user.screenName = obj.getString("screenName");
                user.profileImageURL = obj.getString("profileImageURL");
                user.profileBannerURL = obj.getString("profileBannerURL");
                user.url = obj.getString("url");
                user.description = obj.getString("description");
                user.location = obj.getString("location");
            } catch (JSONException ex) {
                Log.e(TAG, "Error while parsing user json string", ex);
            }

            return user;
        }
        
        @Override
        public int compareTo(User another) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public int getAccessLevel() {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public RateLimitStatus getRateLimitStatus() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String getBiggerProfileImageURL() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String getBiggerProfileImageURLHttps() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Date getCreatedAt() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String getDescription() {
            return description;
        }

        @Override
        public URLEntity[] getDescriptionURLEntities() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public int getFavouritesCount() {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public int getFollowersCount() {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public int getFriendsCount() {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public long getId() {
            return id;
        }

        @Override
        public String getLang() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public int getListedCount() {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public String getLocation() {
            return location;
        }

        @Override
        public String getMiniProfileImageURL() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String getMiniProfileImageURLHttps() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getOriginalProfileImageURL() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String getOriginalProfileImageURLHttps() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String getProfileBackgroundColor() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String getProfileBackgroundImageURL() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String getProfileBackgroundImageUrlHttps() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String getProfileBannerIPadRetinaURL() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String getProfileBannerIPadURL() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String getProfileBannerMobileRetinaURL() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String getProfileBannerMobileURL() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String getProfileBannerRetinaURL() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String getProfileBannerURL() {
            return profileBannerURL;
        }

        @Override
        public String getProfileImageURL() {
            return profileImageURL;
        }

        @Override
        public String getProfileImageURLHttps() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String getProfileLinkColor() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String getProfileSidebarBorderColor() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String getProfileSidebarFillColor() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String getProfileTextColor() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String getScreenName() {
            return screenName;
        }

        @Override
        public Status getStatus() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public int getStatusesCount() {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public String getTimeZone() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String getURL() {
            return url;
        }

        @Override
        public URLEntity getURLEntity() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public int getUtcOffset() {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public boolean isContributorsEnabled() {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean isFollowRequestSent() {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean isGeoEnabled() {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean isProfileBackgroundTiled() {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean isProfileUseBackgroundImage() {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean isProtected() {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean isShowAllInlineMedia() {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean isTranslator() {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean isVerified() {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean isDefaultProfile() {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean isDefaultProfileImage() {
            // TODO Auto-generated method stub
            return false;
        }
    }
}