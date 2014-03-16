package com.twitt4droid.data.dao;

import twitter4j.User;

import java.util.List;

public interface UserDao extends GenericDao<User, Long> {

    UserTransaction beginTransaction();
    
    interface UserTransaction extends Transaction<User> {

        UserTransaction save(User entity);
        UserTransaction save(List<User> entities);
        UserTransaction update(User entity);
        UserTransaction update(List<User> entities);
        UserTransaction delete(User entity);
        UserTransaction delete(List<User> entities);
        UserTransaction deleteAll();
    }
}