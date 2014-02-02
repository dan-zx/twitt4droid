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
package com.twitt4droid.app.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.util.LruCache;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public final class Images {

    private static final String TAG = Images.class.getSimpleName();
    private static final LruCache<String, Bitmap> CACHE;
    
    static {
        int cacheSize = (int) (Runtime.getRuntime().maxMemory() / 1024 / 8) ;
        CACHE = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getRowBytes() * bitmap.getHeight() / 1024;
            }
        };
    }
    
    private Images() {
        throw new IllegalAccessError("This class cannot be instantiated nor extended");
    }

    public static Bitmap getFromUrl(String url) {
        Bitmap cachedBitmap = getFromCache(url);
        if (cachedBitmap != null) {
            Log.d(TAG, "Image loaded from cache");
            return cachedBitmap;
        }
        
        try {
            Log.d(TAG, "Loading image from " + url + " ...");
            InputStream stream = new URL(url).openConnection().getInputStream();
            Bitmap downloaded = BitmapFactory.decodeStream(stream);
            Images.saveInCache(url, downloaded);
            return downloaded;
        } catch (MalformedURLException ex) {
            Log.e(TAG, "Invalid url " + url, ex);
        } catch (IOException ex) {
            Log.e(TAG, "Couldn't download image from " + url, ex);
        }
        
        return null;
    }

    public static Bitmap getFromCache(String key) {
        return CACHE.get(key);
    }
    
    public static void saveInCache(String key, Bitmap bitmap) {
        CACHE.put(key, bitmap);
    }
}