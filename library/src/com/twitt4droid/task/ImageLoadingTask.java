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
package com.twitt4droid.task;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.twitt4droid.util.Images;

public class ImageLoadingTask extends AsyncTask<String, Void, Bitmap> {

    private ImageView imageView;
    private Integer loadingResourceImageId;
    
    public ImageLoadingTask setImageView(ImageView imageView) {
        this.imageView = imageView;
        return this;
    }
    
    public ImageLoadingTask setLoadingResourceImageId(int loadingResourceImageId) {
        this.loadingResourceImageId = loadingResourceImageId;
        return this;
    }

    @Override
    protected void onPreExecute() {
        if (loadingResourceImageId != null) imageView.setImageResource(loadingResourceImageId);
    }

    @Override
    protected Bitmap doInBackground(String... param) {
        return Images.getFromUrl(param[0]);
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        if (result != null) imageView.setImageBitmap(result);
        imageView = null;
    }
}