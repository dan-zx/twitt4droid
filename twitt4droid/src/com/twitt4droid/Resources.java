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

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

/**
 * Utility class for loading different application resources.
 * 
 * @author Daniel Pedraza
 * @since version 1.0
 */
public final class Resources {

    private static final String TAG = Resources.class.getSimpleName();

    /**
     * Default constructor. Do NOT try to initialize this class, it is suppose
     * to be an static utility.
     */
    private Resources() {
        throw new IllegalAccessError("This class cannot be instantiated nor extended");
    }

    /**
     * Returns the SharedPreferences used in this library.
     * 
     * @param context the application context.
     * @return a SharedPreferences object.
     */
    public static SharedPreferences getPreferences(Context context) {
        String name = context.getString(R.string.twitt4droid_preference_file_key);
        return context.getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    /**
     * Gets a {@code String} from the meta data specified in the
     * AndroidManifest.xml.
     * 
     * @param context the application context.
     * @param name the android:name value
     * @param defaultValue if the value doesn't exist the defaultValue will be 
     *        used.
     * @return meta data value specified in the AndroidManifest.xml if exists; 
     *         otherwise defaultValue.
     */
    public static String getMetaData(Context context, String name, String defaultValue) {
        try {
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo appi = packageManager.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            String value = appi.metaData.getString(name);
            return value == null || value.trim().length() == 0 ? defaultValue : value;
        } catch (PackageManager.NameNotFoundException ex) {
            Log.w(TAG, "<meta-data android:name=\"" + name + "\" ... \\> not found");
            return defaultValue;
        }
    }

    /**
     * Gets an {@code int} from the meta data specified in the 
     * AndroidManifest.xml.
     * 
     * @param context the application context.
     * @param name the android:name value
     * @param defaultValue if the value doesn't exist the defaultValue will be 
     *        used.
     * @return meta data value specified in the AndroidManifest.xml if exists; 
     *         otherwise defaultValue.
     */
    public static int getMetaData(Context context, String name, int defaultValue) {
        try {
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo appi = packageManager.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            return appi.metaData.getInt(name, defaultValue);
        } catch (PackageManager.NameNotFoundException ex) {
            Log.w(TAG, "<meta-data android:name=\"" + name + "\" ... \\> not found");
            return defaultValue;
        }
    }

    /**
     * Gets a {@code boolean} from the meta data specified in the
     * AndroidManifest.xml.
     * 
     * @param context the application context.
     * @param name the android:name value
     * @param defaultValue if the value doesn't exist the defaultValue will be 
     *        used.
     * @return meta data value specified in the AndroidManifest.xml if exists; 
     *         otherwise defaultValue.
     */
    public static boolean getMetaData(Context context, String name, boolean defaultValue) {
        try {
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo appi = packageManager.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            return appi.metaData.getBoolean(name, defaultValue);
        } catch (PackageManager.NameNotFoundException ex) {
            Log.w(TAG, "<meta-data android:name=\"" + name + "\" ... \\> not found");
            return defaultValue;
        }
    }

    /**
     * Gets a {@code float} from the meta data specified in the
     * AndroidManifest.xml.
     * 
     * @param context the application context.
     * @param name the android:name value
     * @param defaultValue if the value doesn't exist the defaultValue will be 
     *        used.
     * @return meta data value specified in the AndroidManifest.xml if exists; 
     *         otherwise defaultValue.
     */
    public static float getMetaData(Context context, String name, float defaultValue) {
        try {
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo appi = packageManager.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            return appi.metaData.getFloat(name, defaultValue);
        } catch (PackageManager.NameNotFoundException ex) {
            Log.w(TAG, "<meta-data android:name=\"" + name + "\" ... \\> not found");
            return defaultValue;
        }
    }
}