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

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.webkit.WebView;

import com.twitt4droid.app.R;

public final class Dialogs {

    private Dialogs() {
        throw new IllegalAccessError("This class cannot be instantiated nor extended");
    }

    public static AlertDialog getLicencesAlertDialog(Context context) {
        WebView webView = new WebView(context);
        webView.loadUrl(context.getString(R.string.licenses_file));
        return new AlertDialog.Builder(context)
            .setTitle(R.string.licenses_dialog_tile)
            .setView(webView)
            .setCancelable(true)
            .create();
    }

    public static AlertDialog getNetworkAlertDialog(final Context context) {
        return new AlertDialog.Builder(context)
            .setIcon(R.drawable.dark_warning_icon)
            .setTitle(R.string.twitt4droid_is_offline_title)
            .setMessage(R.string.twitt4droid_is_offline_messege)
            .setNegativeButton(R.string.twitt4droid_continue, null)
            .setPositiveButton(R.string.twitt4droid_goto_settings, 
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        context.startActivity(new Intent(Settings.ACTION_SETTINGS));
                    }
                })
            .setCancelable(false)
            .create();
    }
}