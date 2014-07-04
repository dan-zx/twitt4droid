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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.widget.ImageView;

import com.jakewharton.disklrucache.DiskLruCache;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Images class contains miscellaneous image utility methods.
 *
 * @author Daniel Pedraza-Arcega
 * @since version 1.0
 */
public final class Images {

    private static final String TAG = Images.class.getSimpleName();
    private static final String IMAGE_CACHE_DIR = "images";
    private static final LruCache<String, Bitmap> MEM_CACHE;
    private static DiskLruCache DISK_CACHE;
    
    static {
        int cacheSize = (int) (Runtime.getRuntime().maxMemory() / 1024 / 8) ;
        MEM_CACHE = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getRowBytes() * bitmap.getHeight() / 1024;
            }
        };
    }

    /**
     * Default constructor. Do NOT try to initialize this class, it is suppose
     * to be an static utility.
     */
    private Images() {
        throw new IllegalAccessError("This class cannot be instantiated nor extended");
    }

    /**
     * Initializes the disk cache when is closed or null.
     * 
     * @param context the application context.
     */
    private static void intDiskCacheIfNeeded(Context context) {
        if (DISK_CACHE == null || DISK_CACHE.isClosed()) {
            try {
                long size = 1024 * 1024 * 10;
                String cachePath = 
                        Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || Files.isExternalStorageRemovable()
                        ? Files.getExternalCacheDir(context).getPath() 
                        : context.getCacheDir().getPath();
                File file = new File(cachePath + File.separator + IMAGE_CACHE_DIR);
                DISK_CACHE = DiskLruCache.open(file, 1, 1, size); // Froyo sometimes fails to initialize
            } catch (IOException ex) {
                Log.e(TAG, "Couldn't init disk cache", ex);
            }
        }
    }

    /**
     * Gets a bitmap, if exists, from the given url and stores in both memory cache and disk cache.
     * 
     * @param context the application context.
     * @param url an url.
     * @return a bitmap or {@code null}.
     */
    public static Bitmap getFromUrl(Context context, String url) {
        String key = buildKey(url);
        Bitmap cachedBitmap = getFromCache(context, key);
        if (cachedBitmap != null) return cachedBitmap;
        
        try {
            InputStream stream = new URL(url).openConnection().getInputStream();
            Bitmap downloaded = BitmapFactory.decodeStream(stream);
            saveInCache(context, key, downloaded);
            return downloaded;
        } catch (MalformedURLException ex) {
            Log.e(TAG, "Invalid url " + url, ex);
        } catch (IOException ex) {
            // Useless for now
        }
        
        return null;
    }

    /**
     * Builds a key for given url.
     * 
     * @param url an url.
     * @return a key.
     */
    private static String buildKey(String url) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(url.getBytes());
            byte[] data = digest.digest();
            return String.format("%0" + (data.length * 2) + 'x', new BigInteger(1, data));
        } catch (NoSuchAlgorithmException ex) { 
            Log.e(TAG, "Couldn't encode url", ex);
        }
        
        return null;
    }

    /** Clears both the memory cache and disk cache. */
    public static void clearCache() {
        MEM_CACHE.evictAll();
        if (DISK_CACHE != null) {
            try {
                DISK_CACHE.delete();
            } catch (IOException ex) { 
                Log.e(TAG, "Couldn't clear disk cache", ex);
            }
        }
    }

    /**
     * Returns the bitmap to which the specified key is mapped, or null if both the memory cache and
     * disk cache contains no mapping for the key.
     * 
     * @param context the application context.
     * @param key the key whose associated value is to be returned.
     * @return a bitmap or {@code null}.
     */
    public static Bitmap getFromCache(Context context, String key) {
        Bitmap bitmap = MEM_CACHE.get(key);
        if (bitmap == null) {
            bitmap = getFromDiskCache(context, key);
            if (bitmap != null) MEM_CACHE.put(key, bitmap);
        }
        return bitmap;
    }

    /**
     * Associates the specified bitmap with the specified key in both memory cache and disk cache.
     * 
     * @param context the application context.
     * @param key the key with which the specified value is to be associated
     * @param bitmap the bitmap.
     */
    public static void saveInCache(Context context, String key, Bitmap bitmap) {
        if (!Strings.isNullOrBlank(key) && bitmap != null) {
            MEM_CACHE.put(key, bitmap);
            saveInDiskCache(context, key, bitmap);
        }
    }

    /**
     * Returns the bitmap to which the specified key is mapped, or null if the disk cache contains
     * no mapping for the key.
     * 
     * @param context the application context.
     * @param key the key whose associated value is to be returned.
     * @return a bitmap or {@code null}.
     */
    private static Bitmap getFromDiskCache(Context context, String key) {
        intDiskCacheIfNeeded(context);
        DiskLruCache.Snapshot snapshot = null;
        
        try {
            snapshot = DISK_CACHE.get(key);
        } catch (IOException ex) {
            Log.e(TAG, "Couldn't get image from disk cache", ex);
        }
        
        if (snapshot != null) {
            BufferedInputStream in = new BufferedInputStream(snapshot.getInputStream(0));
            Bitmap bitmap = BitmapFactory.decodeStream(in);
            snapshot.close();
            return bitmap;
        }
        
        return null;
    }

    /**
     * Associates the specified bitmap with the specified key in the disk cache.
     * 
     * @param context the application context.
     * @param key the key with which the specified value is to be associated
     * @param bitmap the bitmap.
     */
    private static void saveInDiskCache(Context context, String key, Bitmap bitmap) {
        intDiskCacheIfNeeded(context);
        DiskLruCache.Editor editor = null;
        OutputStream out = null;
        try {
            editor = DISK_CACHE.edit(key);
            if (editor != null) { // Froyo fix
                out = new BufferedOutputStream(editor.newOutputStream(0));
                if (bitmap.compress(CompressFormat.JPEG, 100, out)) {
                    DISK_CACHE.flush();
                    editor.commit();
                } else editor.abort();
            }
        } catch (IOException ex) {
            Log.e(TAG, "Couldn't save image in disk cache", ex);
            if (editor != null) {
                try {
                    editor.abort();
                } catch (IOException ex2) {
                    Log.e(TAG, "Couldn't abort saving", ex2);
                }
            }
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException ex) {
                    Log.e(TAG, "Couldn't close stream", ex);
                }
            }
        }
    }

    /**
     * Loades an bitmap from any url asynchronously and the sets the bitmap in the given ImageView. 
     * 
     * @author Daniel Pedraza-Arcega
     * @since version 1.0
     */
    public static class ImageLoader extends AsyncTask<String, Void, Bitmap> {

        private ImageView imageView;
        private Integer loadingResourceImageId;
        private Integer loadingColorId;
        private Context context;

        /**
         * Creates an ImageLoader.
         * 
         * @param context the application context.
         */
        public ImageLoader(Context context) {
            this.context = context;
        }

        /**
         * Sets the ImageView to use.
         * 
         * @param imageView an ImageView.
         * @return this ImageLoader.
         */
        public ImageLoader setImageView(ImageView imageView) {
            this.imageView = imageView;
            return this;
        }

        /**
         * Sets the drawable resource to use when the ImageView is loading.
         * 
         * @param loadingResourceImageId an ImageView.
         * @return this ImageLoader.
         */
        public ImageLoader setLoadingResourceImageId(int loadingResourceImageId) {
            this.loadingResourceImageId = loadingResourceImageId;
            return this;
        }

        /**
         * Sets the color resource to use when the ImageView is loading.
         * 
         * @param loadingColorId an ImageView.
         * @return this ImageLoader.
         */
        public ImageLoader setLoadingColorId(int loadingColorId) {
            this.loadingColorId = loadingColorId;
            return this;
        }

        /** {@inheritDoc} */
        @Override
        protected void onPreExecute() {
            if (loadingResourceImageId != null) imageView.setImageResource(loadingResourceImageId);
            if (loadingColorId != null) imageView.setBackgroundColor(context.getResources().getColor(loadingColorId));
        }

        /** {@inheritDoc} */
        @Override
        protected Bitmap doInBackground(String... param) {
            return getFromUrl(context, param[0]);
        }

        /** {@inheritDoc} */
        @Override
        protected void onPostExecute(Bitmap result) {
            if (result != null) imageView.setImageBitmap(result);
            imageView = null;
            loadingResourceImageId = null;
            loadingColorId = null;
            context = null;
        }
    }
}