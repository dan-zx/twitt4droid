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
package com.twitt4droid.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;

import com.twitt4droid.R;
import com.twitt4droid.Twitt4droid;
import com.twitt4droid.activity.WebLoginActivity;

/**
 * A log in/log out button that maintains twitter session state.
 * 
 * @author Daniel Pedraza-Arcega
 * @since version 1.0
 */
public class LogInOutButton extends Button {

    private static final String TAG = LogInOutButton.class.getName();
    
    private String loginText;
    private String logoutText;
    private DefaultOnClickListener clickListener;

    /**
     * Create the LogInOutButton.
     * @see android.view.View#View(Context)
     */
    public LogInOutButton(Context context) {
        super(context);
        setListeners();
        setStyle();
    }

    /**
     * Create the LogInOutButton by inflating from XML
     * @see android.view.View#View(Context, AttributeSet)
     */
    public LogInOutButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        setAttributes(attrs);
        setListeners();
        setStyle();
    }

    /**
     * Create the LogInOutButton by inflating from XML and applying a style.
     * @see android.view.View#View(Context, AttributeSet, int)
     */
    public LogInOutButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setAttributes(attrs);
        setListeners();
        setStyle();
    }

    /**
     * The behavior for this button is already programmed so DO NOT CALL THIS
     * DIRECTLY IN YOUR APP.
     */
    @Override
    public void setOnClickListener(View.OnClickListener l) {
        super.setOnClickListener(clickListener);
    }

    /**
     * Sets the default style for this button. Apparently there's no method for
     * setting an xml style we need to do this programmatically.
     * @param context the current context.
     */
    private void setStyle() {
        setButtonLabel();
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        setTypeface(Typeface.DEFAULT_BOLD);
        setCompoundDrawablePadding(15);
        setCompoundDrawablesWithIntrinsicBounds(R.drawable.twitt4droid_ic_twitter_white, 0, 0, 0);
        setBackgroundResource(R.drawable.twitt4droid_button_blue_background);

        if (isInEditMode()) {
            // hardcoding in edit mode as context.getResources().getColorStateList() doesn't seem to work in Eclipse
            setTextColor(Color.parseColor("#F5F8FA"));
        } else {
            setTextColor(getContext().getResources().getColorStateList(R.color.twitt4droid_blue_button));
        }
    }

    /**
     * Sets the custom attributes for this button.
     * @param context the current context.
     * @param attrs the xml attributes.
     */
    private void setAttributes(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs,R.styleable.twitt4droid_login_button);
        loginText = typedArray.getString(R.styleable.twitt4droid_login_button_loginText);
        logoutText = typedArray.getString(R.styleable.twitt4droid_login_button_logoutText);
        typedArray.recycle();
    }

    private void setListeners() {
        clickListener = new DefaultOnClickListener();
        super.setOnClickListener(clickListener);
    }
    /**
     * Sets the text when there is no user logged in this app.
     * <p><b>Related XML Attributes</b> 
     * <ul>
     *  <li>twitt4droid:loginText</li>
     * </ul></p>
     * @param loginText a string.
     */
    public void setLoginText(String loginText) {
        this.loginText = loginText;
    }

    /**
     * Sets the text when there is a user logged in this app.
     * <p><b>Related XML Attributes</b> 
     * <ul>
     *  <li>twitt4droid:logoutText</li>
     * </ul></p>
     * @param logoutText a string.
     */
    public void setLogoutText(String logoutText) {
        this.logoutText = logoutText;
    }

    /**
     * Sets the text when there is no user logged in this app.
     * <p><b>Related XML Attributes</b> 
     * <ul>
     *  <li>twitt4droid:loginText</li>
     * </ul></p>
     * @param id a resource string.
     */
    public void setLoginText(int id) {
        loginText = getResources().getString(id);
    }

    /**
     * Sets the text when there is a user logged in this app.
     * <p><b>Related XML Attributes</b> 
     * <ul>
     *  <li>twitt4droid:logoutText</li>
     * </ul></p>
     * @param id a resource string.
     */
    public void setLogoutText(int id) {
        logoutText = getResources().getString(id);
    }

    /**
     * Sets the listener interface that will be called when the user is logged 
     * out.
     * @param logoutListener the callback interface.
     */
    public void setOnLogoutListener(OnLogoutListener logoutListener) {
        clickListener.setOnLogoutListener(logoutListener);
    }

    /**
     * Sets the correct text when there is a user logged in or not.
     */
    private void setButtonLabel() {
        if (isInEditMode()) {
            setText(R.string.twitt4droid_login_label);
        } else {
            if (Twitt4droid.isUserLoggedIn(getContext())) {
                setText(logoutText == null || logoutText.trim().length() == 0 ? getResources().getString(R.string.twitt4droid_logout_label) : logoutText);
            } else {
                setText(loginText == null || loginText.trim().length() == 0 ? getResources().getString(R.string.twitt4droid_login_label) : loginText);
            }
        }
    }

    /**
     * LogInOutButton listener interface that will be called when the the user is
     * logged out.
     * 
     * @author Daniel Pedraza
     * @since version 1.0
     */
    public static interface OnLogoutListener {
        
        /**
         * Listener method.
         * 
         * @param button the button that called this listener.
         */
        void OnLogout(LogInOutButton button);
    }

    /**
     * LogInOutButton default on click listener.
     * 
     * @author Daniel Pedraza
     * @since version 1.0
     */
    private final class DefaultOnClickListener implements View.OnClickListener {

        private OnLogoutListener logoutListener;

        /**
         * Sets the listener interface that will be called when the user is logged 
         * out.
         * @param logoutListener the callback interface.
         */
        public void setOnLogoutListener(OnLogoutListener logoutListener) {
            this.logoutListener = logoutListener;
        }

        /**
         * Starts the WebLoginActivity class when no user is logged or deletes
         * the user's account information when there is a user logged in this
         * app.
         * @see android.view.View.OnClickListener#onClick(android.view.View)
         */
        @Override
        public void onClick(View v) {
            if (Twitt4droid.isUserLoggedIn(getContext())) {
                Log.d(TAG, "Deleting twitt4droid data...");
                Twitt4droid.resetData(getContext());
                LogInOutButton button = (LogInOutButton)v;
                button.setButtonLabel();
                if (logoutListener != null) logoutListener.OnLogout(button);
            } else {
                Log.d(TAG, "Starting WebLoginActivity...");
                Intent intent = new Intent(getContext(), WebLoginActivity.class);
                ((Activity) getContext()).startActivityForResult(intent, WebLoginActivity.REQUEST_CODE);
                //TODO: setButtonLabel() after completing authentication process
            }
        }
    }
}