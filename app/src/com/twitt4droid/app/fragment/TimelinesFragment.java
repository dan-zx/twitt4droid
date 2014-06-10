package com.twitt4droid.app.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.twitt4droid.Twitt4droid;
import com.twitt4droid.Twitt4droidAsyncTasks;
import com.twitt4droid.app.R;
import com.twitt4droid.fragment.BaseTimelineFragment;
import com.twitt4droid.fragment.FixedQueryTimelineFragment;
import com.twitt4droid.fragment.HomeTimelineFragment;
import com.twitt4droid.fragment.ListTimelineFragment;
import com.twitt4droid.fragment.MentionsTimelineFragment;
import com.twitt4droid.widget.TweetDialog;

import twitter4j.ResponseList;
import twitter4j.UserList;

import java.util.ArrayList;
import java.util.List;

public class TimelinesFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_timelines, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_timelines, container, false);
        setUpLayout(layout);
        return layout;
    }

    private void setUpLayout(View layout) {
        ViewPager viewPager = (ViewPager) layout.findViewById(R.id.view_pager);
        PagerTabStrip pagerStrip = (PagerTabStrip) layout.findViewById(R.id.pager_strip);
        final SwipeTimelineFragmentPagerAdapter adapter = new SwipeTimelineFragmentPagerAdapter();
        adapter.addFragment(new HomeTimelineFragment());
        adapter.addFragment(new MentionsTimelineFragment());
        viewPager.setAdapter(adapter);
        pagerStrip.setDrawFullUnderline(false);
        pagerStrip.setTabIndicatorColor(getResources().getColor(R.color.twitt4droid_primary_color));

        new Twitt4droidAsyncTasks.UserListsFetcher(getActivity()) {

            @Override
            protected void onPostExecute(ResponseList<UserList> result) {
                if (getTwitterException() == null) {
                    if (result != null) {
                        for (UserList list : result) {
                            ListTimelineFragment fragment = ListTimelineFragment.newInstance(list);
                            adapter.addFragment(fragment);
                        }
                    }
                }
            }
        }
        .execute(Twitt4droid.getCurrentUser(getActivity()).getScreenName());
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_tweet_item:
                new TweetDialog(getActivity()).show();
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

    private class SwipeTimelineFragmentPagerAdapter extends FragmentStatePagerAdapter {

        private final List<BaseTimelineFragment> fragments;

        private SwipeTimelineFragmentPagerAdapter() {
            super(getFragmentManager());
            fragments = new ArrayList<>();
        }

        private void addFragment(BaseTimelineFragment fragment) {
            fragments.add(fragment);
            notifyDataSetChanged();
        }

        @Override
        public Fragment getItem(int position) {
            if (!fragments.isEmpty() && position >= 0) return fragments.get(position);
            else return null;
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
        
        @Override
        public CharSequence getPageTitle(int position) {
            if (!fragments.isEmpty() && position >= 0) {
                BaseTimelineFragment fragment = fragments.get(position);
                if (fragment instanceof FixedQueryTimelineFragment) return ((FixedQueryTimelineFragment)fragment).getQuery();
                if (fragment instanceof ListTimelineFragment) return ((ListTimelineFragment)fragment).getListTitle();
                return getString(fragment.getResourceTitle());
            }
            else return null;
        }
    }
}