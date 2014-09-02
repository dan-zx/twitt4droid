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
package com.twitt4droid.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Environment;

import java.io.File;

/**
 * Files class contains miscellaneous file utility methods.
 *
 * @author Daniel Pedraza-Arcega
 * @since version 1.0
 */
public final class Files {

    private static final String ECLAIR_CACHE_DIR_FORMAT = "/Android/data/%s/cache/";

    /**
     * Default constructor. Do NOT try to initialize this class, it is suppose
     * to be an static utility.
     */
    private Files() {
        throw new IllegalAccessError("This class cannot be instantiated nor extended");
    }

    /** 
     * @return Environment.isExternalStorageRemovable() when GINGERBREAD+; otherwise {@code true}. 
     */
    @SuppressLint("NewApi")
    public static boolean isExternalStorageRemovable() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) return Environment.isExternalStorageRemovable();
        return true;
    }

    /**
     * @param context the application context.
     * @return the external cache file.
     */
    @SuppressLint("NewApi")
    public static File getExternalCacheDir(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) return context.getExternalCacheDir();
        
        // Before Froyo we need to construct the external cache directory
        final String cacheDir = String.format(ECLAIR_CACHE_DIR_FORMAT, context.getPackageName());
        return new File(Environment.getExternalStorageDirectory().getPath() + cacheDir);
    }
}