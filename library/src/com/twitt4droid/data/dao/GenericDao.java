package com.twitt4droid.data.dao;

import java.util.List;

public interface GenericDao<T, ID> {

    T readById(ID id);
    List<T> readList();
    Transaction<T> beginTransaction();
    
    interface Transaction<T> {

        Transaction<T> save(T entity);
        Transaction<T> save(List<T> entities);
        Transaction<T> update(T entity);
        Transaction<T> update(List<T> entities);
        Transaction<T> delete(T entity);
        Transaction<T> delete(List<T> entities);
        Transaction<T> deleteAll();
        void commit();
        void rollback();
    }
}