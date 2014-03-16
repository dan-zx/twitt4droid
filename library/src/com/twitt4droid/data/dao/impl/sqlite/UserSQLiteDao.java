package com.twitt4droid.data.dao.impl.sqlite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.twitt4droid.R;
import com.twitt4droid.data.dao.UserDao;
import com.twitt4droid.data.source.Twitt4droidDatabaseHelper;

import twitter4j.User;

import java.util.List;

class UserSQLiteDao extends GenericSQLiteDao<User, Long> implements UserDao {

    public UserSQLiteDao(Context context) {
        super(context);
        setUpSQLiteOpenHelper();
        setUpQueries();
    }

    protected void setUpSQLiteOpenHelper() {
        setSQLiteOpenHelper(new Twitt4droidDatabaseHelper(getContext()));
    }

    protected void setUpQueries() {
        setReadByIdQueryResId(R.string.twitt4droid_user_readById);
        setReadListQueryResId(R.string.twitt4droid_user_readList);
        setSaveQueryResId(R.string.twitt4droid_user_insert);
        setUpdateQueryResId(R.string.twitt4droid_user_update);
        setDeleteQueryResId(R.string.twitt4droid_user_delete);
        setTruncateQueryResId(R.string.twitt4droid_user_truncate);
    }

    @Override
    protected User rowMapper(Cursor cursor) {
        return new UserCursorImpl(cursor);
    }

    @Override
    protected void insertBinder(SQLiteStatement statement, User entity) {
        int index = 0;
        statement.bindLong(++index, entity.getId());
        statement.bindString(++index, entity.getName());
        statement.bindString(++index, entity.getScreenName());
        statement.bindString(++index, entity.getProfileImageURL());
        statement.bindString(++index, entity.getURL());
        statement.bindString(++index, entity.getDescription());
        statement.bindString(++index, entity.getLocation());
    }

    @Override
    protected void updateBinder(SQLiteStatement statement, User entity) {
        int index = 0;
        statement.bindString(++index, entity.getName());
        statement.bindString(++index, entity.getScreenName());
        statement.bindString(++index, entity.getProfileImageURL());
        statement.bindString(++index, entity.getURL());
        statement.bindString(++index, entity.getDescription());
        statement.bindString(++index, entity.getLocation());
        statement.bindLong(++index, entity.getId());
    }

    @Override
    protected void deleteBinder(SQLiteStatement statement, User entity) {
        statement.bindLong(1, entity.getId());
    }

    @Override
    public UsersQLiteTransaction beginTransaction() {
        SQLiteDatabase database = getSQLiteOpenHelper().getWritableDatabase();
        database.beginTransaction();
        return new UsersQLiteTransaction(database);
    }
    
    class UsersQLiteTransaction extends GenericSQLiteTransaction implements UserTransaction {

        public UsersQLiteTransaction(SQLiteDatabase database) {
            super(database);
        }
        
        @Override
        public UsersQLiteTransaction save(User entity) {
            return (UsersQLiteTransaction) super.save(entity);
        }
        
        @Override
        public UsersQLiteTransaction save(List<User> entities) {
            return (UsersQLiteTransaction) super.save(entities);
        }
        
        @Override
        public UsersQLiteTransaction update(User entity) {
            return (UsersQLiteTransaction) super.update(entity);
        }

        @Override
        public UsersQLiteTransaction update(List<User> entities) {
            return (UsersQLiteTransaction) super.update(entities);
        }
        
        @Override
        public UsersQLiteTransaction delete(User entity) {
            return (UsersQLiteTransaction) super.delete(entity);
        }

        @Override
        public UsersQLiteTransaction delete(List<User> entities) {
            return (UsersQLiteTransaction) super.delete(entities);
        }

        @Override
        public UsersQLiteTransaction deleteAll() {
            return (UsersQLiteTransaction) super.deleteAll();
        }
    }
}