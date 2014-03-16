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
import android.os.Environment;
import android.support.v4.util.LruCache;
import android.util.Log;

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
    
    private Images() {
        throw new IllegalAccessError("This class cannot be instantiated nor extended");
    }

    private static void intDiskCacheIfNeeded(Context context) {
        if (DISK_CACHE == null || DISK_CACHE.isClosed()) {
            try {
                long size = 1024 * 1024 * 10;
                String cachePath = 
                        Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || Files.isExternalStorageRemovable()
                        ? Files.getExternalCacheDir(context).getPath() 
                        : context.getCacheDir().getPath();
                File file = new File(cachePath + File.separator + IMAGE_CACHE_DIR);
                DISK_CACHE = DiskLruCache.open(file, 1, 1, size);
            } catch (IOException ex) {
                Log.e(TAG, "Couldn't disk cache");
            }
        }
    }

    public static Bitmap getFromUrl(Context context, String url) {
        String key = buildKeyFor(url);
        Bitmap cachedBitmap = getFromCache(context, key);
        if (cachedBitmap != null) return cachedBitmap;
        
        try {
            Log.d(TAG, "Loading image from " + url + " ...");
            InputStream stream = new URL(url).openConnection().getInputStream();
            Bitmap downloaded = BitmapFactory.decodeStream(stream);
            saveInCache(context, key, downloaded);
            return downloaded;
        } catch (MalformedURLException ex) {
            Log.e(TAG, "Invalid url " + url);
        } catch (IOException ex) {
            Log.e(TAG, "Couldn't download image from " + url);
        }
        
        return null;
    }

    private static String buildKeyFor(String url) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(url.getBytes());
            byte[] data = digest.digest();
            return String.format("%0" + (data.length * 2) + 'x', new BigInteger(1, data));
        } catch (NoSuchAlgorithmException ex) { 
            Log.e(TAG, "Couldn't encode url");
        }
        
        return null;
    }

    public static void clearCache() {
        MEM_CACHE.evictAll();
        if (DISK_CACHE != null) {
            try {
                DISK_CACHE.delete();
            } catch (IOException e) { 
                Log.e(TAG, "Couldn't clear disk cache");
            }
        }
    }

    public static Bitmap getFromCache(Context context, String key) {
        Bitmap bitmap = MEM_CACHE.get(key);
        if (bitmap == null) {
            bitmap = getFromDiskCache(context, key);
            if (bitmap != null) {
                Log.d(TAG, "Image loaded from disk cache");
            }
        } else {
            Log.d(TAG, "Image loaded from memory cache");
        }
        return bitmap;
    }
    
    public static void saveInCache(Context context, String key, Bitmap bitmap) {
        MEM_CACHE.put(key, bitmap);
        saveInDiskCache(context, key, bitmap);
    }

    private static Bitmap getFromDiskCache(Context context, String key) {
        intDiskCacheIfNeeded(context);
        DiskLruCache.Snapshot snapshot = null;
        
        try {
            snapshot = DISK_CACHE.get(key);
            BufferedInputStream in = new BufferedInputStream(snapshot.getInputStream(0));
            return BitmapFactory.decodeStream(in);
        } catch (Exception ex) {
            Log.e(TAG, "Couldn't get image from disk cache");
        } finally {
            if (snapshot != null) {
                try {
                    snapshot.close();
                } catch (Exception ex) {
                    Log.e(TAG, "Couldn't close snapshot");
                }
            }
        }
        
        return null;
    }

    private static void saveInDiskCache(Context context, String key, Bitmap bitmap) {
        intDiskCacheIfNeeded(context);
        DiskLruCache.Editor editor = null;
        OutputStream out = null;
        try {
            editor = DISK_CACHE.edit(key);
            out = new BufferedOutputStream(editor.newOutputStream(0));
            if (bitmap.compress(CompressFormat.JPEG, 100, out)) {
                DISK_CACHE.flush();
                editor.commit();
            } else {
                editor.abort();
            }
        } catch (IOException ex) {
            Log.e(TAG, "Couldn't save image in disk cache");
            if (editor != null) {
                try {
                    editor.abort();
                } catch (IOException ex2) {
                    Log.e(TAG, "Couldn't abort saving");
                }
            }
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException ex) {
                    Log.e(TAG, "Couldn't close stream");
                }
            }
        }
    }
}