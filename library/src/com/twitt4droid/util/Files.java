package com.twitt4droid.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Environment;

import java.io.File;

public final class Files {

    private static final String ECLAIR_CACHE_DIR_FORMAT = "/Android/data/%s/cache/";

    private Files() {
        throw new IllegalAccessError("This class cannot be instantiated nor extended");
    }

    @SuppressLint("NewApi")
    public static boolean isExternalStorageRemovable() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) 
            return Environment.isExternalStorageRemovable();
        return true;
    }

    @SuppressLint("NewApi")
    public static File getExternalCacheDir(Context context) {
        if (hasExternalCacheDir()) return context.getExternalCacheDir();
        
        // Before Froyo we need to construct the external cache directory
        final String cacheDir = String.format(ECLAIR_CACHE_DIR_FORMAT, context.getPackageName());;
        return new File(Environment.getExternalStorageDirectory().getPath() + cacheDir);
    }

    private static boolean hasExternalCacheDir() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
    }
}
