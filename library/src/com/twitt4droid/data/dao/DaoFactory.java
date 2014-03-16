package com.twitt4droid.data.dao;


public interface DaoFactory {

    TimelineDao getHomeTimelineDao();
    TimelineDao getMentionsTimelineDao();
    TimelineDao getUserTimelineDao();
    TimelineDao getFixedQueryTimelineDao();
    TimelineDao getQueryableTimelineDao();
    UserDao getUserDao();
}