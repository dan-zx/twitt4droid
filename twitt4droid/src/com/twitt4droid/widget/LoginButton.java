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
 * @author Daniel Pedraza
 * @since version 1.0
 */
public class LoginButton extends Button {

    private static final String TAG = LoginButton.class.getName();
    
    private String loginText;
    private String logoutText;

    /**
     * Create the LoginButton.
     * @see android.view.View#View(Context)
     */
    public LoginButton(Context context) {
        super(context);
        setStyle(context);
    }

    /**
     * Create the LoginButton by inflating from XML
     * @see android.view.View#View(Context, AttributeSet)
     */
    public LoginButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        setStyle(context);
        setAttributes(context, attrs);
    }

    /**
     * Create the LoginButton by inflating from XML and applying a style.
     * @see android.view.View#View(Context, AttributeSet, int)
     */
    public LoginButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setStyle(context);
        setAttributes(context, attrs);
    }

    /**
     * The behavior for this button is already programmed so DO NOT CALL THIS
     * DIRECTLY IN YOUR APP.
     */
    @Override
    public void setOnClickListener(View.OnClickListener l) {
        super.setOnClickListener(l);
    }

    /**
     * Sets the default style for this button. Apparently there's no method for
     * setting an xml style we need to do this programmatically.
     * @param context the current context.
     */
    private void setStyle(Context context) {
        setButtonLabel();
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        setTypeface(Typeface.DEFAULT_BOLD);
        setCompoundDrawablePadding(15);
        setCompoundDrawablesWithIntrinsicBounds(R.drawable.login_button_icon, 0, 0, 0);
        setBackgroundResource(R.drawable.login_button_background);
        setOnClickListener(new LoginClickListener(context));

        if (isInEditMode()) {
            // hardcoding in edit mode as ontext.getResources().getColorStateList() doesn't seem to work in Eclipse
            setTextColor(Color.parseColor("#292f33"));
        } else {
            setTextColor(context.getResources().getColorStateList(R.color.login_button));
        }
    }

    /**
     * Sets the custom attributes for this button.
     * @param context the current context.
     * @param attrs the xml attributes.
     */
    private void setAttributes(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs,R.styleable.login_button);
        loginText = typedArray.getString(R.styleable.login_button_loginText);
        logoutText = typedArray.getString(R.styleable.login_button_logoutText);
        typedArray.recycle();
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
     * Sets the correct text when there is a user logged in or not.
     */
    private void setButtonLabel() {
        if (!isInEditMode()) {
            if (Twitt4droid.isUserLoggedIn(getContext())) {
                setText(logoutText == null || logoutText.trim().length() == 0 ? getResources().getString(R.string.logout_label) : logoutText);
            } else {
                setText(loginText == null || loginText.trim().length() == 0 ? getResources().getString(R.string.login_label) : loginText);
            }
        } else {
            setText(R.string.login_label);
        }
    }

    /**
     * LoginButton default OnClickListener.
     * 
     * @author Daniel Pedraza
     * @since version 1.0
     */
    private static final class LoginClickListener implements View.OnClickListener {

        private final Context context;

        public LoginClickListener(Context context) {
            this.context = context;
        }

        /**
         * Starts the WebLoginActivity class when no user is logged or deletes
         * the user's account information when there is a user logged in this
         * app.
         * @see android.view.View.OnClickListener#onClick(android.view.View)
         */
        @Override
        public void onClick(View v) {
            if (Twitt4droid.isUserLoggedIn(context)) {
                Log.d(TAG, "Deleting account info...");
                Twitt4droid.deleteAuthenticationInfo(context);
                ((LoginButton)v).setButtonLabel();
            } else {
                Log.d(TAG, "Starting WebLoginActivity...");
                Intent intent = new Intent(context, WebLoginActivity.class);
                ((Activity) context).startActivityForResult(intent, WebLoginActivity.REQUEST_CODE);
                //TODO: setButtonLabel() after completing authentication process
            }
        }
    }
}