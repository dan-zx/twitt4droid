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

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.twitt4droid.R;

/**
 * Pull to refresh ListView based on Johan Nilsson's PullToRefreshListView.
 * 
 * @author Johan Nilsson
 * @see <a href="https://github.com/johannilsson/android-pulltorefresh">Pull To Refresh for Android</a>
 * @since version 1.0
 */
public class RefreshableListView extends ListView {

    /**
     * Refresh status enum constants.
     *  
     * @author Daniel Pedraza-Arcega
     * @since version 1.0
     */
    public static enum RefreshStatus {
        TAP_TO_REFRESH, PULL_TO_REFRESH, RELEASE_TO_REFRESH, REFRESHING;
    }

    private OnRefreshListener onRefreshListener;
    private OnScrollRefreshListener onScrollRefreshListener;
    private RelativeLayout headerLayout;
    private TextView refreshingTextView;
    private ImageView arrowImageView;
    private ProgressBar progressBar;
    private RefreshStatus refreshState;
    private RotateAnimation flipAnimation;
    private RotateAnimation reverseFlipAnimation;
    private int currentScrollState;
    private int refreshViewHeight;
    private int refreshOriginalTopPadding;
    private int lastMotionY;
    private boolean bounceHack;

    /**
     * Create the RefreshableListView.
     * @see android.view.View#View(Context)
     */
    public RefreshableListView(Context context) {
        super(context);
        setUpAnimations();
        setUpHeader();
        setUpListeners();
        setVerticalScrollBarEnabled(false); 
    }

    /**
     * Create the RefreshableListView by inflating from XML
     * @see android.view.View#View(Context, AttributeSet)
     */
    public RefreshableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setUpAnimations();
        setUpHeader();
        setUpListeners();
        setVerticalScrollBarEnabled(false); 
    }

    /**
     * Create the RefreshableListView by inflating from XML and applying a style.
     * @see android.view.View#View(Context, AttributeSet, int)
     */
    public RefreshableListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setUpAnimations();
        setUpHeader();
        setUpListeners();
        setVerticalScrollBarEnabled(false); 
    }

    /**
     * Sets up the header layout by inflating 
     * twitt4droid_list_view_refreshing_header.xml.
     */
    private void setUpHeader() {
        // Load all of the animations we need in code rather than through XML
        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        headerLayout = (RelativeLayout) layoutInflater.inflate(R.layout.twitt4droid_list_view_refreshing_header, this, false);
        refreshingTextView = (TextView) headerLayout.findViewById(R.id.refreshing_text);
        arrowImageView = (ImageView) headerLayout.findViewById(R.id.arrow_image);
        progressBar = (ProgressBar) headerLayout.findViewById(R.id.refreshing_progress_bar);
        arrowImageView.setMinimumHeight(50);
        refreshOriginalTopPadding = headerLayout.getPaddingTop();
        refreshState = RefreshStatus.TAP_TO_REFRESH;
        refreshViewHeight = headerLayout.getMeasuredHeight();
        addHeaderView(headerLayout);
        measureView(headerLayout);
    }

    /**
     * Sets up arrow animations.
     */
    private void setUpAnimations() {
        flipAnimation = new RotateAnimation(0f, -180f, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        flipAnimation.setInterpolator(new LinearInterpolator());
        flipAnimation.setDuration(250);
        flipAnimation.setFillAfter(true);
        reverseFlipAnimation = new RotateAnimation(-180f, 0f, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        reverseFlipAnimation.setInterpolator(new LinearInterpolator());
        reverseFlipAnimation.setDuration(250);
        reverseFlipAnimation.setFillAfter(true);
    }

    /**
     * Sets up refresh listeners. 
     */
    private void setUpListeners() {
        headerLayout.setOnClickListener(new OnClickRefreshListener(this));
        onScrollRefreshListener = new OnScrollRefreshListener();
        super.setOnScrollListener(onScrollRefreshListener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setSelection(1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);
        setSelection(1);
    }

    /**
     * Gets the current scroll state.
     * @return {@link android.widget.AbsListView.OnScrollListener#SCROLL_STATE_FLING}, 
     *         {@link android.widget.AbsListView.OnScrollListener#SCROLL_STATE_IDLE}, 
     *         or {@link android.widget.AbsListView.OnScrollListener#SCROLL_STATE_TOUCH_SCROLL}.
     * @see android.widget.AbsListView.OnScrollListener
     */
    private int getCurrentScrollState() {
        return currentScrollState;
    }

    /**
     * Gets the current scroll state.
     * @param currentScrollState can be 
     *        {@link android.widget.AbsListView.OnScrollListener#SCROLL_STATE_FLING}, 
     *        {@link android.widget.AbsListView.OnScrollListener#SCROLL_STATE_IDLE},
     *        or {@link android.widget.AbsListView.OnScrollListener#SCROLL_STATE_TOUCH_SCROLL}.
     * @see android.widget.AbsListView.OnScrollListener
     */
    private void setCurrentScrollState(int currentScrollState) {
        this.currentScrollState = currentScrollState;
    }

    /**
     * Gets the current refresh state.
     * @return {@link RefreshStatus#PULL_TO_REFRESH},
     *         {@link RefreshStatus#REFRESHING},
     *         {@link RefreshStatus#RELEASE_TO_REFRESH} or
     *         {@link RefreshStatus#TAP_TO_REFRESH}.
     * @see RefreshStatus
     */
    private RefreshStatus getCurrentRefreshState() {
        return refreshState;
    }

    /**
     * Checks if bounce hack is enabled.
     * @return if the bounce hack is enabled.
     */
    private boolean isBounceHackActive() {
        return bounceHack;
    }

    /**
     * Sets if bounce hack should be enabled.
     * @param bounceHack if the bounce hack should be enabled.
     */
    private void useBounceHack(boolean bounceHack) {
        this.bounceHack = bounceHack;
    }

    /**
     * Register a callback to be invoked when this ListView should be refreshed.
     * @param onRefreshListener The callback to run.
     */
    public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
        this.onRefreshListener = onRefreshListener;
    }

    /**
     * The behavior for this ListView is already programmed so DO NOT CALL THIS
     * DIRECTLY IN YOUR APP.
     */
    @Override
    public final void setOnScrollListener(OnScrollListener l) {
        super.setOnScrollListener(onScrollRefreshListener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean onTouchEvent(MotionEvent event) {
        final int y = (int) event.getY();
        bounceHack = false;

        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                if (!isVerticalScrollBarEnabled()) {
                    setVerticalScrollBarEnabled(true);
                }
                if (getFirstVisiblePosition() == 0 && refreshState != RefreshStatus.REFRESHING) {
                    if ((headerLayout.getBottom() >= refreshViewHeight || headerLayout.getTop() >= 0) && refreshState == RefreshStatus.RELEASE_TO_REFRESH) {
                        // Initiate the refresh
                        refreshState = RefreshStatus.REFRESHING;
                        prepareForRefresh();
                        if (onRefreshListener != null) {
                            onRefreshListener.onRefresh(this);
                        }
                    } else if (headerLayout.getBottom() < refreshViewHeight || headerLayout.getTop() <= 0) {
                        // Abort refresh and scroll down below the refresh view
                        resetStatus();
                        setSelection(1);
                    }
                }
                break;
            case MotionEvent.ACTION_DOWN:
                lastMotionY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                applyHeaderPadding(event);
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * ???
     * @param ev ???
     */
    private void applyHeaderPadding(MotionEvent ev) {
        int pointerCount = ev.getHistorySize();
        for (int p = 0; p < pointerCount; p++) {
            if (refreshState == RefreshStatus.RELEASE_TO_REFRESH) {
                if (isVerticalFadingEdgeEnabled()) {
                    setVerticalScrollBarEnabled(false);
                }

                int historicalY = (int) ev.getHistoricalY(p);

                // Calculate the padding to apply, we divide by 1.7 to
                // simulate a more resistant effect during pull.
                int topPadding = (int) (((historicalY - lastMotionY) - refreshViewHeight) / 1.7);
                headerLayout.setPadding(headerLayout.getPaddingLeft(), topPadding, headerLayout.getPaddingRight(), headerLayout.getPaddingBottom());
            }
        }
    }

    /**
     * Sets the header padding back to original size.
     */
    private void resetHeaderPadding() {
        headerLayout.setPadding(headerLayout.getPaddingLeft(), refreshOriginalTopPadding, headerLayout.getPaddingRight(), headerLayout.getPaddingBottom());
    }

    /**
     * Resets the header to the original state.
     */
    private void resetStatus() {
        if (refreshState != RefreshStatus.TAP_TO_REFRESH) {
            refreshState = RefreshStatus.TAP_TO_REFRESH;

            resetHeaderPadding();
            // Set refresh view text to the pull label
            refreshingTextView.setText(R.string.twitt4droid_pull_to_refresh_text);
            // Replace refresh drawable with arrow drawable ????
            arrowImageView.setImageResource(R.drawable.twitt4droid_dark_arrow_icon);
            // Clear the full rotation animation
            arrowImageView.clearAnimation();
            // Hide progress bar and arrow.
            arrowImageView.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
        }
    }

    /**
     * ???
     * @param child ???
     */
    private void measureView(View child) {
        ViewGroup.LayoutParams p = child.getLayoutParams();
        if (p == null) {
            p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
        int lpHeight = p.height;
        int childHeightSpec;
        if (lpHeight > 0) {
            childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);
        } else {
            childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }
        child.measure(childWidthSpec, childHeightSpec);
    }

    /**
     * Sets header settings for refreshing state
     */
    private void prepareForRefresh() {
        resetHeaderPadding();

        arrowImageView.setVisibility(View.GONE);
        // We need this hack, otherwise it will keep the previous drawable.
        arrowImageView.setImageDrawable(null);
        progressBar.setVisibility(View.VISIBLE);

        // Set refresh view text to the refreshing label
        refreshingTextView.setText(R.string.twitt4droid_refreshing_text);

        refreshState = RefreshStatus.REFRESHING;
    }

    /**
     * Should release state be enabled.
     * @return if release state should be enabled.
     */
    private boolean shouldRelease() {
        return (headerLayout.getBottom() >= refreshViewHeight + 20 || headerLayout.getTop() >= 0) && refreshState != RefreshStatus.RELEASE_TO_REFRESH;
    }

    /**
     * Should release pull be enabled.
     * @return if pull state should be enabled.
     */
    private boolean shouldPull() {
        return headerLayout.getBottom() < refreshViewHeight + 20 && refreshState != RefreshStatus.PULL_TO_REFRESH;
    }

    /**
     * Sets header settings for releasing state
     */
    private void setReleaseToRefreshState() {
        refreshingTextView.setText(R.string.twitt4droid_release_to_refresh_text);
        arrowImageView.setVisibility(View.VISIBLE);
        arrowImageView.clearAnimation();
        arrowImageView.startAnimation(flipAnimation);
        refreshState = RefreshStatus.RELEASE_TO_REFRESH;
    }

    /**
     * Sets header settings for pull to refresh state
     */
    private void setPullToRefreshState() {
        arrowImageView.setVisibility(View.VISIBLE);
        refreshingTextView.setText(R.string.twitt4droid_pull_to_refresh_text);
        if (refreshState != RefreshStatus.TAP_TO_REFRESH) {
            arrowImageView.clearAnimation();
            arrowImageView.startAnimation(reverseFlipAnimation);
        }
        
        refreshState = RefreshStatus.PULL_TO_REFRESH;
    }

    /**
     * Gets the current callback to be invoked when this list should be refreshed.
     */
    public OnRefreshListener getOnRefreshListener() {
        return onRefreshListener;
    }

    /**
     * Should be called on the client after refreshing
     */
    public void onRefreshComplete() {     
        resetStatus();
        // If refresh view is visible when loading completes, scroll down to
        // the next item.
        if (getFirstVisiblePosition() == 0) {
            invalidateViews();
            setSelection(1);
        }
    }

    /**
     * Interface definition for a callback to be invoked when list should be
     * refreshed.
     * 
     * @author Johan Nilsson
     * @since version 1.0
     */
    public static interface OnRefreshListener {

        /**
         * Called when the ListView should be refreshed.
         * @param refreshableListView the ListView that called this callback.
         */
        void onRefresh(RefreshableListView refreshableListView);
    }

    /**
     * Header default on click listener.
     * 
     * @author Johan Nilsson
     * @since version 1.0
     */
    private static class OnClickRefreshListener implements View.OnClickListener {

        private RefreshableListView refreshableListView;

        public OnClickRefreshListener(RefreshableListView refreshableListView) {
            this.refreshableListView = refreshableListView;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void onClick(View v) {
            if (refreshableListView.getCurrentRefreshState() != RefreshStatus.REFRESHING && refreshableListView.getOnRefreshListener() != null) {
                refreshableListView.prepareForRefresh();
                refreshableListView.getOnRefreshListener().onRefresh(refreshableListView);
            }
        }
    }

    /**
     * RefreshableListView default on scroll listener.
     * 
     * @author Johan Nilsson
     * @since version 1.0
     */
    private static class OnScrollRefreshListener implements AbsListView.OnScrollListener {

        /**
         * {@inheritDoc}
         */
        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            // When the refresh view is completely visible, change the text to say
            // "Release to refresh..." and flip the arrow drawable.
            RefreshableListView listView = (RefreshableListView)view;
            if (listView.getCurrentScrollState() == SCROLL_STATE_TOUCH_SCROLL && listView.getCurrentRefreshState() != RefreshStatus.REFRESHING) {
                if (firstVisibleItem == 0) {
                    if (listView.shouldRelease()) {
                        listView.setReleaseToRefreshState();
                    } else if (listView.shouldPull()) {
                        listView.setPullToRefreshState();
                    }
                } else {
                    listView.resetStatus();
                }
            } else if (listView.getCurrentScrollState() == SCROLL_STATE_FLING && firstVisibleItem == 0 && listView.getCurrentRefreshState() != RefreshStatus.REFRESHING) {
                listView.setSelection(1);
                listView.useBounceHack(true);
            } else if (listView.isBounceHackActive() && listView.getCurrentScrollState() == SCROLL_STATE_FLING) {
                listView.setSelection(1);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            RefreshableListView listView = (RefreshableListView)view;
            listView.setCurrentScrollState(scrollState);
            if (listView.getCurrentScrollState() == SCROLL_STATE_IDLE) {
                listView.useBounceHack(false);
            }
        }
    }
}