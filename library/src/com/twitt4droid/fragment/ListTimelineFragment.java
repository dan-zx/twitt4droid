package com.twitt4droid.fragment;

import android.os.Bundle;

import com.twitt4droid.R;
import com.twitt4droid.data.dao.ListTimelineDAO;
import com.twitt4droid.data.dao.impl.DAOFactory;

import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.UserList;

import java.util.List;

public class ListTimelineFragment extends TimelineFragment {

    protected static final String LIST_ARG = "LIST";
    
    private ListTimelineDAO listTimelineDAO;

    public static ListTimelineFragment newInstance(UserList userList, boolean enableDarkTheme) {
        ListTimelineFragment fragment = new ListTimelineFragment();
        Bundle args = new Bundle();
        args.putSerializable(LIST_ARG, userList);
        args.putBoolean(ENABLE_DARK_THEME_ARG, enableDarkTheme);
        fragment.setArguments(args);
        return fragment;
    }

    public static ListTimelineFragment newInstance(UserList userList) {
        return newInstance(userList, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listTimelineDAO = new DAOFactory(getActivity()).getListTimelineDAO();
    }

    @Override
    @Deprecated
    protected List<Status> getTweets(Twitter twitter) throws TwitterException {
        return null;
    }

    @Override
    protected TimelineLoader getNewTimelineLoader() {
        return new ListTimelineLoader();
    }
    
    public String getListTitle() {
        return getList().getName();
    }
    
    protected UserList getList() {
        return (UserList) getArguments().getSerializable(LIST_ARG);
    }

    @Override
    @Deprecated
    public int getResourceTitle() {
        return R.string.twitt4droid_list_timeline_fragment_title;
    }

    @Override
    public int getResourceHoloLightIcon() {
        // TODO Auto-generated method stub
        return R.drawable.twitt4droid_ic_person_holo_light;
    }

    @Override
    public int getResourceHoloDarkIcon() {
        // TODO Auto-generated method stub
        return R.drawable.twitt4droid_ic_person_holo_dark;
    }

    protected class ListTimelineLoader extends TimelineLoader {
        
        @Override
        protected List<twitter4j.Status> loadTweetsInBackground(Void... params) throws TwitterException {
            List<twitter4j.Status> result = null;
            long listId = getList().getId();
            if (isConnectToInternet()) {
                result = getTwitter().getUserListStatuses(listId, new Paging(1));
                listTimelineDAO.deleteAllByListId(listId);
                if (result != null && !result.isEmpty()) listTimelineDAO.save(result, listId);
            } else result = listTimelineDAO.fetchListByListId(listId);
            return result;
        }
    }
}