package com.twitt4droid.data.dao.impl.sqlite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.twitt4droid.data.dao.GenericDao;

import java.util.ArrayList;
import java.util.List;

abstract class GenericSQLiteDao<T, ID> implements GenericDao<T, ID> {

    private static final String TAG = GenericSQLiteDao.class.getSimpleName();

    private final Context context;

    private SQLiteOpenHelper databaseHelper;
    private int readByIdQueryResId;
    private int readListQueryResId;
    private int saveQueryResId;
    private int updateQueryResId;
    private int deleteQueryResId;
    private int truncateQueryResId;
    
    public GenericSQLiteDao(Context context) {
        this.context = context;
    }
    
    @Override
    public T readById(ID id) {
        SQLiteDatabase database = databaseHelper.getReadableDatabase();
        String sql = context.getString(readByIdQueryResId);
        Log.d(TAG, "<-" + sql + " ?=" + id);
        Cursor cursor = database.rawQuery(sql, new String[] { id.toString() });
        cursor.moveToNext();
        T entity = rowMapper(cursor);
        cursor.close();
        database.close();
        return entity;
    }

    @Override
    public ArrayList<T> readList() {
        SQLiteDatabase database = databaseHelper.getReadableDatabase();
        String sql = context.getString(readListQueryResId);
        Log.d(TAG, "<-" + sql);
        Cursor cursor = database.rawQuery(sql, null);
        ArrayList<T> entities = new ArrayList<T>(cursor.getCount());
        while (cursor.moveToNext()) {
            T entity = rowMapper(cursor);
            entities.add(entity);
        }
        cursor.close();
        database.close();
        return entities;
    }

    public Context getContext() {
        return context;
    }

    public SQLiteOpenHelper getSQLiteOpenHelper() {
        return databaseHelper;
    }

    public void setSQLiteOpenHelper(SQLiteOpenHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public void setReadByIdQueryResId(int readByIdQueryResId) {
        this.readByIdQueryResId = readByIdQueryResId;
    }
    
    public void setReadListQueryResId(int readListQueryResId) {
        this.readListQueryResId = readListQueryResId;
    }

    public void setSaveQueryResId(int saveQueryResId) {
        this.saveQueryResId = saveQueryResId;
    }

    public void setUpdateQueryResId(int updateQueryResId) {
        this.updateQueryResId = updateQueryResId;
    }

    public void setDeleteQueryResId(int deleteQueryResId) {
        this.deleteQueryResId = deleteQueryResId;
    }
    
    public void setTruncateQueryResId(int truncateQueryResId) {
        this.truncateQueryResId = truncateQueryResId;
    }

    protected abstract T rowMapper (Cursor cursor);
    protected abstract void insertBinder(SQLiteStatement statement, T entity);
    protected abstract void updateBinder(SQLiteStatement statement, T entity);
    protected abstract void deleteBinder(SQLiteStatement statement, T entity);

    @Override
    public GenericSQLiteTransaction beginTransaction() {
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        database.beginTransaction();
        return new GenericSQLiteTransaction(database);
    }

    class GenericSQLiteTransaction implements Transaction<T> {

        private final SQLiteDatabase database;
      
        public GenericSQLiteTransaction(SQLiteDatabase database) {
            this.database = database;
        }

        @Override
        public GenericSQLiteTransaction save(T entity) {
            String sql = context.getString(saveQueryResId);
            SQLiteStatement statement = database.compileStatement(sql);
            Log.d(TAG, "->" + sql + " ?=" + entity);
            insertBinder(statement, entity);
            statement.executeInsert();
            statement.close();
            return this;
        }
        
        @Override
        public GenericSQLiteTransaction save(List<T> entities) {
            String sql = context.getString(saveQueryResId);
            SQLiteStatement statement = database.compileStatement(sql);
            for (T entity : entities) {
                insertBinder(statement, entity);
                Log.d(TAG, "->" + sql + " ?=" + entity);
                statement.executeInsert();
                statement.clearBindings();
            }
            statement.close();
            return this;
        }

        @Override
        public GenericSQLiteTransaction update(T entity) {
            String sql = context.getString(updateQueryResId);
            SQLiteStatement statement = database.compileStatement(sql);
            updateBinder(statement, entity);
            Log.d(TAG, "->" + sql + " ?=" + entity);
            statement.execute();
            statement.close();
            return this;
        }

        @Override
        public GenericSQLiteTransaction update(List<T> entities) {
            String sql = context.getString(updateQueryResId);
            SQLiteStatement statement = database.compileStatement(sql);
            for (T entity : entities) {
                updateBinder(statement, entity);
                Log.d(TAG, "->" + sql + " ?=" + entity);
                statement.execute();
                statement.clearBindings();
            }
            statement.close();
            return this;
        }

        @Override
        public GenericSQLiteTransaction delete(T entity) {
            String sql = context.getString(deleteQueryResId);
            SQLiteStatement statement = database.compileStatement(sql);
            deleteBinder(statement, entity);
            Log.d(TAG, "->" + sql + " ?=" + entity);
            statement.execute();
            statement.close();
            return this;
        }

        @Override
        public GenericSQLiteTransaction delete(List<T> entities) {
            String sql = context.getString(deleteQueryResId);
            SQLiteStatement statement = database.compileStatement(sql);
            for (T entity : entities) {
                deleteBinder(statement, entity);
                Log.d(TAG, "->" + sql + " ?=" + entity);
                statement.execute();
                statement.clearBindings();
            }
            statement.close();
            return this;
        }

        @Override
        public GenericSQLiteTransaction deleteAll() {
            String sql = context.getString(truncateQueryResId);
            SQLiteStatement statement = database.compileStatement(sql);
            Log.d(TAG, "->" + sql);
            statement.execute();
            statement.close();
            return this;
        }

        @Override
        public void commit() {
            database.setTransactionSuccessful();
            database.endTransaction();
            database.close();
        }

        @Override
        public void rollback() {
            database.endTransaction();
            database.close();
        }
        
        public SQLiteDatabase getSQLiteDatabase() {
            return database;
        }
    }
}