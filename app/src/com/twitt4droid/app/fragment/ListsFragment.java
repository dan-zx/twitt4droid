package com.twitt4droid.app.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.twitt4droid.Resources;
import com.twitt4droid.Twitt4droid;
import com.twitt4droid.app.R;
import com.twitt4droid.fragment.ListTimelineFragment;

import twitter4j.AsyncTwitter;
import twitter4j.ResponseList;
import twitter4j.TwitterAdapter;
import twitter4j.TwitterException;
import twitter4j.TwitterMethod;
import twitter4j.UserList;

import java.util.ArrayList;
import java.util.List;

public class ListsFragment extends Fragment {

    private ViewPager viewPager;
    private PagerTabStrip pagerStrip;
    private SwipeTimelineFragmentPagerAdapter adapter;
    private AsyncTwitter twitter;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setUpTwitter();
    }

    private void setUpTwitter() {
        twitter = Twitt4droid.getAsyncTwitter(getActivity());
        twitter.addListener(new TwitterAdapter() {

            @Override
            public void gotUserLists(final ResponseList<UserList> userLists) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        
                        @Override
                        public void run() {
                            if (userLists != null && !userLists.isEmpty()) {
                                for (UserList userList : userLists) {
                                    ListTimelineFragment fragment = ListTimelineFragment.newInstance(userList);
                                    adapter.addFragment(fragment);
                                }
                            }
                        }
                    });
                }
            }
            
            @Override
            public void onException(TwitterException te, TwitterMethod method) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        
                        @Override
                        public void run() {
                            Toast.makeText(getActivity().getApplicationContext(),
                                    R.string.twitt4droid_error_message,
                                    Toast.LENGTH_SHORT)
                                    .show();
                        }
                    });
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_lists, container, false);
        setUpLayout(layout);
        return layout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (Resources.isConnectedToInternet(getActivity())) twitter.getUserLists(Twitt4droid.getCurrentUser(getActivity()).getScreenName());
        else {
            Toast.makeText(getActivity().getApplicationContext(),
                    R.string.twitt4droid_is_offline_messege,
                    Toast.LENGTH_LONG)
                    .show();
        }
    }

    private void setUpLayout(View layout) {
        viewPager = (ViewPager) layout.findViewById(R.id.view_pager);
        pagerStrip = (PagerTabStrip) layout.findViewById(R.id.pager_strip);
        adapter = new SwipeTimelineFragmentPagerAdapter();
        viewPager.setAdapter(adapter);
        pagerStrip.setDrawFullUnderline(false);
        pagerStrip.setTabIndicatorColor(getResources().getColor(R.color.twitt4droid_primary_color));
    }

    private class SwipeTimelineFragmentPagerAdapter extends FragmentStatePagerAdapter {

        private final List<ListTimelineFragment> fragments;

        private SwipeTimelineFragmentPagerAdapter() {
            super(getChildFragmentManager());
            fragments = new ArrayList<>();
        }

        private void addFragment(ListTimelineFragment fragment) {
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
            if (!fragments.isEmpty() && position >= 0) return fragments.get(position).getListTitle();
            else return null;
        }
    }
}