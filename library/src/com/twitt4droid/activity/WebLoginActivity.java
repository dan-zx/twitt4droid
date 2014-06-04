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
package com.twitt4droid.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.twitt4droid.R;
import com.twitt4droid.Resources;
import com.twitt4droid.Twitt4droid;
import com.twitt4droid.Twitt4droidAsyncTasks;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

/**
 * This Activity provides the web based Twitter login process. Is not meant to 
 * be used directly (DO NOT START IT DIRECTLY). Add this activity to your 
 * AndroidManifest.xml like this:
 * <pre>
 * {@code 
 * <activity android:name="com.twitt4droid.activity.WebLoginActivity"
 *           android:theme="@android:style/Theme.Black.NoTitleBar" />
 * }
 * </pre>
 * 
 * @author Daniel Pedraza-Arcega
 * @since version 1.0
 */
public class WebLoginActivity extends Activity {

    /** The request code for this activity. */
    public static final int REQUEST_CODE = 340;

    /** The name of the Intent-extra used to indicate the twitter user returned. */
    public static final String EXTRA_USER = "com.twitt4droid.extra.user";
     
    private static final String TAG = WebLoginActivity.class.getSimpleName();
    private static final String OAUTH_VERIFIER_CALLBACK_PARAMETER = "oauth_verifier";
    private static final String DENIED_CALLBACK_PARAMETER = "denied";
    private static final String CALLBACK_URL = "oauth://twitt4droid";

    private ProgressBar loadingBar;
    private MenuItem reloadCancelItem;
    private WebView webView;
    private RequestToken requestToken;

    /** {@inheritDoc} */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Twitt4droid.areConsumerTokensAvailable(getApplicationContext())) {
            if (Resources.isConnectedToInternet(this)) {
                if (Twitt4droid.isUserLoggedIn(getApplicationContext())) {
                    new Twitt4droidAsyncTasks.VerifyCredentialsTask(this) {

                        @Override
                        protected void onPostExecute(User user) {
                            if (getTwitterException() != null) handleError(getTwitterException());
                            else handleUserValidation(user);
                        }
                    }.execute();
                } else {
                    setContentView(R.layout.twitt4droid_web_browser);
                    setUpView();
                    new Twitt4droidAsyncTasks.GetOAuthRequestTokenTask(this) {

                        @Override
                        protected void onPostExecute(RequestToken token) {
                            requestToken = token;
                            if (getTwitterException() != null) handleError(getTwitterException());
                            else webView.loadUrl(token.getAuthenticationURL());
                        }
                    }.execute(CALLBACK_URL);
                }
            } else {
                Log.w(TAG, "No Internet connection detected");
                showNetworkAlertDialog();
            }
        } else {
            Log.e(TAG, "Twitter consumer key and/or consumer secret are not defined correctly");
            setResult(RESULT_CANCELED, getIntent());
            finish();
        }
    }

    /** Shows a network error alert dialog. */
    private void showNetworkAlertDialog() {
        new AlertDialog.Builder(this)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setTitle(R.string.twitt4droid_is_offline_title)
            .setMessage(R.string.twitt4droid_is_offline_messege)
            .setNegativeButton(android.R.string.cancel, 
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i(TAG, "User canceled authentication process due to network failure");
                        setResult(RESULT_CANCELED, getIntent());
                        finish();
                    }
            })
            .setPositiveButton(R.string.twitt4droid_goto_settings, 
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Settings.ACTION_SETTINGS));
                        finish();
                    }
                })
            .setCancelable(false)
            .show();
    }

    /** Sets up views. */
    @SuppressWarnings("deprecation")
    private void setUpView() {
        loadingBar = (ProgressBar) findViewById(R.id.loading_bar);
        webView = (WebView) findViewById(R.id.web_view);
        CookieSyncManager.createInstance(this);
        CookieManager.getInstance().removeAllCookie();
        webView.getSettings().setSaveFormData(false);
        webView.getSettings().setSavePassword(false);
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int progress) {
                if (progress == 100) {
                    loadingBar.setVisibility(View.INVISIBLE);
                    loadingBar.setProgress(0);
                    if (reloadCancelItem != null) {
                        reloadCancelItem.setTitle(R.string.twitt4droid_refresh_menu_title);
                        reloadCancelItem.setIcon(R.drawable.twitt4droid_ic_refresh_holo_dark);
                    }
                } else loadingBar.setVisibility(View.VISIBLE);
                loadingBar.setProgress(progress);
            }
        });
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith(CALLBACK_URL)) {
                    Uri uri = Uri.parse(url);
                    if (uri.getQueryParameter(DENIED_CALLBACK_PARAMETER) != null) {
                        Log.i(TAG, "Authentication process was denied");
                        setResult(RESULT_CANCELED, getIntent());
                        finish();
                        return true;
                    }
                    if (uri.getQueryParameter(OAUTH_VERIFIER_CALLBACK_PARAMETER) != null) {
                        String oauthVerifier = uri.getQueryParameter(OAUTH_VERIFIER_CALLBACK_PARAMETER);
                        new Twitt4droidAsyncTasks.AsyncTwitterFetcher<String, Object[]>(WebLoginActivity.this) {

                            @Override
                            protected Object[] doInBackground(String... params) {
                                try {
                                    Object[] results = new Object[2];
                                    Twitter twitter = getTwitter();
                                    results[0] = twitter.getOAuthAccessToken(requestToken, params[0]);
                                    results[1] = twitter.verifyCredentials();
                                    return results;
                                } catch (TwitterException ex) {
                                    setTwitterException(ex);
                                }
                                
                                return null;
                            }

                            @Override
                            protected void onPostExecute(Object[] tokenAndUser) {
                                if (getTwitterException() != null) handleError(getTwitterException());
                                else {
                                    AccessToken token = (AccessToken) tokenAndUser[0];
                                    User user = (User) tokenAndUser[1];
                                    Twitt4droid.saveAuthenticationInfo(getApplicationContext(), token);
                                    handleUserValidation(user);
                                }
                            }
                        }.execute(oauthVerifier);
                        return true;
                    }
                }

                return super.shouldOverrideUrlLoading(view, url);
            }
        });
    }

    /**
     * Properly handles twitter errors.
     * 
     * @param ex a {@link TwitterException}.
     */
    private void handleError(TwitterException ex) {
        Log.e(TAG, "Twitter error", ex);
        showErrorAlertDialog();
    }

    /**
     * Handles user validation.
     * 
     * @param user the validated user.
     */
    private void handleUserValidation(User user) {
        Log.i(TAG, "@" + user.getScreenName() + " was successfully authenticated");
        Twitt4droid.saveOrUpdateUser(user, getApplicationContext());
        Intent data = getIntent();
        data.putExtra(EXTRA_USER, user);
        setResult(RESULT_OK, data);
        finish();
    }

    /** Shows a generic alert dialog. */
    private void showErrorAlertDialog() {
        new AlertDialog.Builder(WebLoginActivity.this)
            .setTitle(R.string.twitt4droid_error_title)
            .setMessage(R.string.twitt4droid_error_message)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setNegativeButton(R.string.twitt4droid_return,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i(TAG, "User canceled authentication process due to an error");
                        setResult(RESULT_CANCELED, getIntent());
                        finish();
                    }
                })
            .setPositiveButton(R.string.twitt4droid_continue, null)
            .setCancelable(false)
            .show();
    }

    /** {@inheritDoc}*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.twitt4droid_web_browser, menu);
        reloadCancelItem = menu.findItem(R.id.reload_cancel_item);
        if (loadingBar.getProgress() == loadingBar.getMax() || loadingBar.getProgress() == 0) {
            reloadCancelItem.setTitle(R.string.twitt4droid_refresh_menu_title);
            reloadCancelItem.setIcon(R.drawable.twitt4droid_ic_refresh_holo_dark);
        }
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.reload_cancel_item) {
            if (item.getTitle().toString().equals(getString(R.string.twitt4droid_cancel_menu_title))) {
                webView.stopLoading();
                item.setTitle(R.string.twitt4droid_refresh_menu_title);
                item.setIcon(R.drawable.twitt4droid_ic_refresh_holo_dark);
            } else {
                webView.reload();
                item.setTitle(R.string.twitt4droid_cancel_menu_title);
                item.setIcon(R.drawable.twitt4droid_ic_cancel_holo_dark);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /** {@inheritDoc} */
    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) webView.goBack();
        else super.onBackPressed();
    }
}