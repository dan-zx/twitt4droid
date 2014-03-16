package com.twitt4droid.data.dao;

import twitter4j.Status;

import java.util.List;

public interface TimelineDao extends GenericDao<Status, Long> {

    TimelineTransaction beginTransaction();
    
    interface TimelineTransaction extends Transaction<Status> {

        TimelineTransaction save(Status entity);
        TimelineTransaction save(List<Status> entities);
        TimelineTransaction update(Status entity);
        TimelineTransaction update(List<Status> entities);
        TimelineTransaction delete(Status entity);
        TimelineTransaction delete(List<Status> entities);
        TimelineTransaction deleteAll();
    }
}