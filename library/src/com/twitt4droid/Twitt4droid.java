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

import com.twitt4droid.data.source.Twitt4droidDatabaseHelper;
import com.twitt4droid.util.Images;

import twitter4j.AsyncTwitter;
import twitter4j.AsyncTwitterFactory;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

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
     * Gets the current AsyncTwitter with consumer and access tokens 
     * pre-initialized.
     * 
     * @param context the application context.
     * @return an AsyncTwitter object.
     */
    public static AsyncTwitter getAsyncTwitter(Context context) {
        return new AsyncTwitterFactory(getCurrentConfig(context)).getInstance();
    }

    /**
     * Gets the current twitter4j configuration with consumer and access tokens
     * pre-initialized. You can use this method to build a Twitter objects.
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
     * Checks if there is any user authentication information stored in this
     * app.
     * 
     * @param context the application context.
     * @return {@code true} if there is any user authentication information
     *         stored in this app; otherwise {@code false}.
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
     * Saves authentication information from the given AccessToken in
     * {@code SharedPreferences}.
     * 
     * @param context the application context.
     * @param token an AccessToken.
     */
    public static void saveAuthenticationInfo(Context context, AccessToken token) {
        Resources.getPreferences(context)
            .edit()
            .putString(context.getString(R.string.twitt4droid_oauth_token_key), token.getToken())
            .putString(context.getString(R.string.twitt4droid_oauth_secret_key), token.getTokenSecret())
            .putString(context.getString(R.string.twitt4droid_user_screen_name_key), token.getScreenName())
            .putLong(context.getString(R.string.twitt4droid_user_id_key), token.getUserId())
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
            .putString(context.getString(R.string.twitt4droid_oauth_token_key), null)
            .putString(context.getString(R.string.twitt4droid_oauth_secret_key), null)
            .putString(context.getString(R.string.twitt4droid_user_screen_name_key), null)
            .putLong(context.getString(R.string.twitt4droid_user_id_key), Long.MIN_VALUE)
            .putBoolean(context.getString(R.string.twitt4droid_user_is_logged_in_key), false)
            .commit();
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
     * Gets the current twitter user name.
     * 
     * @param context the application context.
     * @return the current twitter user name if exists; otherwise {@code null}.
     */
    public static String getCurrentUserScreenName(Context context) {
        return Resources.getPreferences(context).getString(context.getString(R.string.twitt4droid_user_screen_name_key), null);
    }

    /**
     * Gets the current twitter user id.
     * 
     * @param context the application context.
     * @return the current twitter user id if exists; otherwise 
     *         {@link Long#MIN_VALUE}.
     */
    public static long getCurrentUserId(Context context) {
        return Resources.getPreferences(context).getLong(
                context.getString(R.string.twitt4droid_user_id_key), 
                Long.MIN_VALUE);
    }
}