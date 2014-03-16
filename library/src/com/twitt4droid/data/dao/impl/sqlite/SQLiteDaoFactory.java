package com.twitt4droid.data.dao.impl.sqlite;

import android.content.Context;

import com.twitt4droid.data.dao.DaoFactory;
import com.twitt4droid.data.dao.TimelineDao;
import com.twitt4droid.data.dao.UserDao;

public final class SQLiteDaoFactory implements DaoFactory {

    private final Context context;

    public SQLiteDaoFactory(Context context) {
        this.context = context;
    }

    @Override
    public HomeTimelineSQLiteDao getHomeTimelineDao() {
        return new HomeTimelineSQLiteDao(context);
    }
    
    @Override
    public MentionsTimelineSQLiteDao getMentionsTimelineDao() {
        return new MentionsTimelineSQLiteDao(context);
    }
    
    @Override
    public UserTimelineSQLiteDao getUserTimelineDao() {
        return new UserTimelineSQLiteDao(context);
    }

    @Override
    public TimelineDao getFixedQueryTimelineDao() {
        return new FixedQueryTimelineSQLiteDao(context);
    }

    @Override
    public TimelineDao getQueryableTimelineDao() {
        return new QueryableTimelineSQLiteDao(context);
    }
    
    @Override
    public UserDao getUserDao() {
        return new UserSQLiteDao(context);
    }
}