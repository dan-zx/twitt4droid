package com.twitt4droid.fragment;

import android.os.Bundle;

import com.twitt4droid.R;
import com.twitt4droid.data.dao.ListTimelineDAO;
import com.twitt4droid.data.dao.impl.DAOFactory;

import twitter4j.Paging;
import twitter4j.TwitterException;
import twitter4j.UserList;

import java.util.List;

public class ListTimelineFragment extends TimelineFragment {

    protected static final String LIST_ARG = "LIST";

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
    protected ListStatusesLoaderTask initStatusesLoaderTask() {
        return new ListStatusesLoaderTask(new DAOFactory(getActivity().getApplicationContext()).getListTimelineDAO(), getList().getId());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initStatusesLoaderTask().execute();
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
        return R.drawable.twitt4droid_ic_list_holo_light;
    }

    @Override
    public int getResourceHoloDarkIcon() {
        return R.drawable.twitt4droid_ic_list_holo_dark;
    }

    private class ListStatusesLoaderTask extends StatusesLoaderTask {

        private final long listId;
        
        protected ListStatusesLoaderTask(ListTimelineDAO timelineDao, long listId) {
            super(timelineDao);
            this.listId = listId;
        }

        @Override
        protected List<twitter4j.Status> loadTweetsInBackground() throws TwitterException {
            ListTimelineDAO timelineDAO = (ListTimelineDAO) getDAO();
            List<twitter4j.Status> statuses = null;
            if (isConnectToInternet()) {
                statuses = getTwitter().getUserListStatuses(listId, new Paging(1));
                // TODO: update statuses instead of deleting all previous statuses and save new ones.
                timelineDAO.deleteAllByListId(listId);
                timelineDAO.save(statuses, listId);
            } else statuses = timelineDAO.fetchListByListId(listId);
            return statuses;
        }
        
    }
}