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
package com.twitt4droid.app.task;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class RemoteImageLoader extends AsyncTask<String, Void, Bitmap> {

    private static final String TAG = RemoteImageLoader.class.getSimpleName();

    private ImageView imageView;
    
    public RemoteImageLoader(ImageView imageView) {
        this.imageView = imageView;
    }
    
    @Override
    protected Bitmap doInBackground(String... param) {
        Log.d(TAG, "Loading image from " + param[0] + " ...");
        try {
            InputStream stream = new URL(param[0]).openConnection().getInputStream();
            return BitmapFactory.decodeStream(stream);
        } catch (MalformedURLException ex) {
            Log.e(TAG, "Invalid url", ex);
        } catch (IOException ex) {
            Log.e(TAG, "Couldn't download image", ex);
        }
        
        return null;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        imageView.setImageBitmap(result);
        imageView = null;
    }
}