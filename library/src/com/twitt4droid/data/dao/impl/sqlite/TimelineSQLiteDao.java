package com.twitt4droid.data.dao.impl.sqlite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.twitt4droid.data.dao.TimelineDao;
import com.twitt4droid.data.source.Twitt4droidDatabaseHelper;

import twitter4j.Status;

import java.util.List;

abstract class TimelineSQLiteDao extends GenericSQLiteDao<Status, Long> implements TimelineDao {

    public TimelineSQLiteDao(Context context) {
        super(context);
        setUpSQLiteOpenHelper();
    }

    protected void setUpSQLiteOpenHelper() {
        setSQLiteOpenHelper(new Twitt4droidDatabaseHelper(getContext()));
    }

    @Override
    protected StatusCursorImpl rowMapper(Cursor cursor) {
        return new StatusCursorImpl(cursor);
    }
    
    @Override
    protected void insertBinder(SQLiteStatement statement, Status entity) {
        int index = 0;
        statement.bindLong(++index, entity.getId());
        statement.bindString(++index, entity.getText());
        statement.bindString(++index, entity.getUser().getScreenName());
        statement.bindString(++index, entity.getUser().getName());
        statement.bindLong(++index, entity.getCreatedAt().getTime());
        statement.bindString(++index, entity.getUser().getProfileImageURL());
    }
    
    @Override
    protected void updateBinder(SQLiteStatement statement, Status entity) {
        int index = 0;
        statement.bindString(++index, entity.getText());
        statement.bindString(++index, entity.getUser().getScreenName());
        statement.bindString(++index, entity.getUser().getName());
        statement.bindLong(++index, entity.getCreatedAt().getTime());
        statement.bindString(++index, entity.getUser().getProfileImageURL());
        statement.bindLong(++index, entity.getId());
    }
    
    @Override
    protected void deleteBinder(SQLiteStatement statement, Status entity) {
        statement.bindLong(1, entity.getId());
    }
    
    @Override
    public TimelineSQLiteTransaction beginTransaction() {
        SQLiteDatabase database = getSQLiteOpenHelper().getWritableDatabase();
        database.beginTransaction();
        return new TimelineSQLiteTransaction(database);
    }
    
    class TimelineSQLiteTransaction extends GenericSQLiteTransaction implements TimelineTransaction {

        public TimelineSQLiteTransaction(SQLiteDatabase database) {
            super(database);
        }
        
        @Override
        public TimelineSQLiteTransaction save(Status entity) {
            return (TimelineSQLiteTransaction) super.save(entity);
        }
        
        @Override
        public TimelineSQLiteTransaction save(List<Status> entities) {
            return (TimelineSQLiteTransaction) super.save(entities);
        }
        
        @Override
        public TimelineSQLiteTransaction update(Status entity) {
            return (TimelineSQLiteTransaction) super.update(entity);
        }

        @Override
        public TimelineSQLiteTransaction update(List<Status> entities) {
            return (TimelineSQLiteTransaction) super.update(entities);
        }
        
        @Override
        public TimelineSQLiteTransaction delete(Status entity) {
            return (TimelineSQLiteTransaction) super.delete(entity);
        }

        @Override
        public TimelineSQLiteTransaction delete(List<Status> entities) {
            return (TimelineSQLiteTransaction) super.delete(entities);
        }

        @Override
        public TimelineSQLiteTransaction deleteAll() {
            return (TimelineSQLiteTransaction) super.deleteAll();
        }
    }
}