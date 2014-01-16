/*
 * Copyright 2014-present twitt4droid Project
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

import twitter4j.AsyncTwitter;
import twitter4j.AsyncTwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Utility class for loading Twitter configurations.
 * 
 * @author Daniel Pedraza
 * @since version 1.0
 */
public final class Twitt4droid {

    public static final long INVALID_USER_ID = -99;

    private static final String CONSUMER_KEY_META_DATA = "com.twitt4droid.auth.CONSUMER_KEY";
    private static final String CONSUMER_SECRET_META_DATA = "com.twitt4droid.auth.CONSUMER_SECRET";
    private static final String ACCESS_TOKEN_KEY = "OAUTH_TOKEN";
    private static final String ACCESS_TOKEN_SECRET_KEY = "OAUTH_SECRET";
    private static final String SCREEN_NAME_KEY = "SCREEN_NAME";
    private static final String USER_ID_KEY = "USER_ID";
    private static final String IS_LOGGED_IN_KEY = "IS_LOGGED_IN";

    /**
     * Default constructor. Do NOT try to initialize this class, it is suppose
     * to be an static utility.
     */
    private Twitt4droid() {
        throw new IllegalAccessError("This class cannot be instantiated nor extended");
    }

    /**
     * Gets the current async-twitter4j configuration with consumer and access 
     * tokens pre-initialized.
     * 
     * @param context the application context.
     * @return an AsyncTwitter object.
     */
    public static AsyncTwitter getCurrentTwitter(Context context) {
        SharedPreferences preferences = Resources.getPreferences(context);
        Configuration config = new ConfigurationBuilder()
                .setOAuthConsumerKey(Resources.getMetaData(context, CONSUMER_KEY_META_DATA, null))
                .setOAuthConsumerSecret(Resources.getMetaData(context, CONSUMER_SECRET_META_DATA, null))
                .setOAuthAccessToken(preferences.getString(ACCESS_TOKEN_KEY, null))
                .setOAuthAccessTokenSecret(preferences.getString(ACCESS_TOKEN_SECRET_KEY, null))
                .build();

        return new AsyncTwitterFactory(config).getInstance();
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
        return Resources.getPreferences(context).getBoolean(IS_LOGGED_IN_KEY, false);
    }
    
    public static boolean areConsumerTokensAvailable(Context context) {
        return Resources.getMetaData(context, CONSUMER_SECRET_META_DATA, null) != null && 
               Resources.getMetaData(context, CONSUMER_KEY_META_DATA, null) != null;
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
            .putString(ACCESS_TOKEN_KEY, token.getToken())
            .putString(ACCESS_TOKEN_SECRET_KEY, token.getTokenSecret())
            .putString(SCREEN_NAME_KEY, token.getScreenName())
            .putLong(USER_ID_KEY, token.getUserId())
            .putBoolean(IS_LOGGED_IN_KEY, true)
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
            .putString(ACCESS_TOKEN_KEY, null)
            .putString(ACCESS_TOKEN_SECRET_KEY, null)
            .putString(SCREEN_NAME_KEY, null)
            .putLong(USER_ID_KEY, INVALID_USER_ID)
            .putBoolean(IS_LOGGED_IN_KEY, false)
            .commit();
    }

    /**
     * Gets the current twitter user name.
     * 
     * @param context the application context.
     * @return the current twitter user name if exists; otherwise {@code null}.
     */
    public static String getCurrentUserScreenName(Context context) {
        return Resources.getPreferences(context).getString(SCREEN_NAME_KEY, null);
    }

    /**
     * Gets the current twitter user id.
     * 
     * @param context the application context.
     * @return the current twitter user id if exists; otherwise 
     *         {@link #INVALID_USER_ID}.
     */
    public static long getCurrentUserId(Context context) {
        return Resources.getPreferences(context).getLong(USER_ID_KEY, INVALID_USER_ID);
    }
}