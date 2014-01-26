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
package com.twitt4droid.activity;

import twitter4j.AsyncTwitter;
import twitter4j.TwitterAdapter;
import twitter4j.TwitterException;
import twitter4j.TwitterMethod;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.twitt4droid.R;
import com.twitt4droid.Twitt4droid;

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
 * @author Daniel Pedraza
 * @since version 1.0
 */
public class WebLoginActivity extends Activity {

    /**
     * The request code for this activity.
     */
    public static final int REQUEST_CODE = 340;

    /**
     * The name of the Intent-extra used to indicate the twitter user returned.
     */
    public static final String EXTRA_USER = "com.twitt4droid.extra.user";
     
    private static final String TAG = WebLoginActivity.class.getSimpleName();
    private static final String URL_OAUTH_VERIFIER = "oauth_verifier";
    private static final String CALLBACK_URL = "oauth://twitt4droid";

    private AsyncTwitter twitter;
    private ProgressBar loadingBar;
    private MenuItem reloadCancelItem;
    private WebView webView;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        if (Twitt4droid.areConsumerTokensAvailable(getApplicationContext())) {
            setUpTwitter();
            if (Twitt4droid.isUserLoggedIn(getApplicationContext())) {
                twitter.verifyCredentials();
            } else {
                setContentView(R.layout.web_browser);
                setUpWebView();
                twitter.getOAuthRequestTokenAsync(CALLBACK_URL);
            }
        } else {
            Log.e(TAG, "Twitter consumer key and/or consumer secret are not defined correctly");
            finish();
        }
    }

    /**
     * Sets up the web view
     */
    private void setUpWebView() {
        webView = (WebView) findViewById(R.id.web_view);
        loadingBar = (ProgressBar) findViewById(R.id.loading_bar);
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int progress) {
                if (progress == 100) {
                    loadingBar.setVisibility(View.INVISIBLE);
                    loadingBar.setProgress(0);
                    if (reloadCancelItem != null) {
                        reloadCancelItem.setTitle(R.string.reload_menu_title);
                        reloadCancelItem.setIcon(R.drawable.reload_icon);
                    }
                } else {
                    loadingBar.setVisibility(View.VISIBLE);
                }
                loadingBar.setProgress(progress);
            }
        });
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d(TAG, url);
                if (url.startsWith(CALLBACK_URL)) {
                    String oauthVerifier = Uri.parse(url).getQueryParameter(URL_OAUTH_VERIFIER);
                    twitter.getOAuthAccessTokenAsync(oauthVerifier);
                    twitter.verifyCredentials();
                    return true;
                }

                return super.shouldOverrideUrlLoading(view, url);
            }
        });
    }

    /**
     * Sets up Twitter async listeners.
     */
    private void setUpTwitter() {
        twitter = Twitt4droid.getCurrentTwitter(getApplicationContext());
        twitter.addListener(new TwitterAdapter() {
            @Override
            public void verifiedCredentials(User user) {
                Log.d(TAG, "User=" + user);
                Intent data = getIntent();
                data.putExtra(EXTRA_USER, user);
                setResult(RESULT_OK, data);
                twitter.addListener(null);
                finish();
            }

            @Override
            public void gotOAuthRequestToken(RequestToken token) {
                Log.d(TAG, "RequestToken=" + token);
                // FIXME: webView.loadUrl() cannot be called from other threads in higher API levels 
                webView.loadUrl(token.getAuthenticationURL());
            }

            @Override
            public void gotOAuthAccessToken(AccessToken token) {
                Log.d(TAG, "AccessToken=" + token);
                Twitt4droid.saveAuthenticationInfo(getApplicationContext(), token);
            }

            @Override
            public void onException(TwitterException te, TwitterMethod method) {
                Log.e(TAG, "Twitter error", te);
                // TODO: Report Twitter errors
                // TODO: Catch cancel button event
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.web_browser, menu);
        reloadCancelItem = menu.findItem(R.id.reload_cancel_item);
        if (loadingBar.getProgress() == loadingBar.getMax() || loadingBar.getProgress() == 0) {
            reloadCancelItem.setTitle(R.string.reload_menu_title);
            reloadCancelItem.setIcon(R.drawable.reload_icon);
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.reload_cancel_item) {
            if (item.getTitle().toString().equals(getString(R.string.cancel_menu_title))) {
                webView.stopLoading();
                item.setTitle(R.string.reload_menu_title);
                item.setIcon(R.drawable.reload_icon);
            } else {
                webView.reload();
                item.setTitle(R.string.cancel_menu_title);
                item.setIcon(R.drawable.cancel_icon);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}