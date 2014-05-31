package com.twitt4droid.data.dao;

import twitter4j.Status;

import java.util.List;

public interface UserTimelineDAO extends TimelineDAO {

    List<Status> fetchListByScreenName(String screenName);
}
