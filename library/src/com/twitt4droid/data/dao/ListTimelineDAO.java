package com.twitt4droid.data.dao;

import java.util.List;

import twitter4j.Status;

public interface ListTimelineDAO extends GenericDAO<Status, Long> {

    List<Status> fetchListByListId(Long listId);
    void save(List<Status> statuses, Long listId);
    void deleteAllByListId(Long listId);
}