/*
 * Copyright 2014 Daniel Pedraza-Arcega
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.twitt4droid.data.dao.impl.sqlite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class SQLiteTemplate {

    private static final String TAG = SQLiteTemplate.class.getSimpleName();

    private final SQLiteOpenHelper databaseHelper;

    SQLiteTemplate(SQLiteOpenHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    <T> T queryForSingleResult(String sql, RowMapper<T> rowMapper) {
        return queryForSingleResult(sql, null, rowMapper);
    }

    <T> T queryForSingleResult(String sql, String[] args, RowMapper<T> rowMapper) {
        SQLiteDatabase database = null;
        Cursor cursor = null;
        T object = null;
        try {
            database = databaseHelper.getReadableDatabase();
            cursor = database.rawQuery(sql, args);
            if (cursor.getCount() == 1 && cursor.moveToNext()) object = rowMapper.mapRow(cursor, 1);
        } catch (Exception ex) {
            Log.e(TAG, "Couldn't complete query [" + sql + "] with args [" + Arrays.deepToString(args) + "]", ex);
        } finally {
            SQLiteUtils.close(cursor);
            SQLiteUtils.close(database);
        }
        return object;
    }

    <T> List<T> queryForList(String sql, RowMapper<T> rowMapper) {
        return queryForList(sql, null, rowMapper);
    }

    <T> List<T> queryForList(String sql, String[] args, RowMapper<T> rowMapper) {
        SQLiteDatabase database = null;
        Cursor cursor = null;
        ArrayList<T> list = null;
        try {
            database = databaseHelper.getReadableDatabase();
            cursor = database.rawQuery(sql, args);
            list = new ArrayList<>(cursor.getCount());
            int rowNum = 0;
            while (cursor.moveToNext()) list.add(rowMapper.mapRow(cursor, ++rowNum));
        } catch (Exception ex) {
            Log.e(TAG, "Couldn't complete query [" + sql + "] with args [" + Arrays.deepToString(args) + "]", ex);
        } finally {
            SQLiteUtils.close(cursor);
            SQLiteUtils.close(database);
        }
        return list;
    }

    void execute(String sql) {
        SQLiteDatabase database = null;
        SQLiteStatement statement = null;
        try {
            database = databaseHelper.getWritableDatabase();
            database.beginTransaction();
            statement = database.compileStatement(sql);
            statement.execute();
            database.setTransactionSuccessful();
        } catch (Exception ex) {
            Log.e(TAG, "Couldn't execute [" + sql + "]", ex);
        } finally {
            SQLiteUtils.close(statement);
            SQLiteUtils.endTransaction(database);
            SQLiteUtils.close(database);
        }
    }

    void execute(String sql, SQLiteStatementBinder statementBinder) {
        SQLiteDatabase database = null;
        SQLiteStatement statement = null;
        try {
            database = databaseHelper.getWritableDatabase();
            database.beginTransaction();
            statement = database.compileStatement(sql);
            statementBinder.bindValues(statement);
            statement.execute();
            database.setTransactionSuccessful();
        } catch (Exception ex) {
            Log.e(TAG, "Couldn't execute [" + sql + "] with statement binder", ex);
        } finally {
            SQLiteUtils.close(statement);
            SQLiteUtils.endTransaction(database);
            SQLiteUtils.close(database);
        }
    }

    void execute(String sql, String[] args) {
        SQLiteDatabase database = null;
        SQLiteStatement statement = null;
        try {
            database = databaseHelper.getWritableDatabase();
            database.beginTransaction();
            statement = database.compileStatement(sql);
            for (int index = args.length; index != 0; index--) {
                statement.bindString(index, args[index - 1]);
            }
            statement.execute();
            database.setTransactionSuccessful();
        } catch (Exception ex) {
            Log.e(TAG, "Couldn't execute [" + sql + "] with args [" + Arrays.deepToString(args) + "]", ex);
        } finally {
            SQLiteUtils.close(statement);
            SQLiteUtils.endTransaction(database);
            SQLiteUtils.close(database);
        }
    }

    void batchExecute(String[] sqls) {
        SQLiteDatabase database = null;
        try {
            database = databaseHelper.getWritableDatabase();
            database.beginTransaction();
            for (String sql : sqls) {
                SQLiteStatement statement = database.compileStatement(sql);
                statement.execute();
                statement.close();
            }
            database.setTransactionSuccessful();
        } catch (Exception ex) {
            Log.e(TAG, "Couldn't execute batch " + Arrays.deepToString(sqls), ex);
        } finally {
            SQLiteUtils.endTransaction(database);
            SQLiteUtils.close(database);
        }
    }

    void batchExecute(String sql, BatchSQLiteStatementBinder statementBinder) {
        SQLiteDatabase database = null;
        SQLiteStatement statement = null;
        try {
            database = databaseHelper.getWritableDatabase();
            database.beginTransaction();
            statement = database.compileStatement(sql);
            for (int i = 0; i < statementBinder.getBatchSize(); i++) {
                statement.clearBindings();
                statementBinder.bindValues(statement, i);
                statement.execute();
            }
            database.setTransactionSuccessful();
        } catch (Exception ex) {
            Log.e(TAG, "Couldn't execute batch [" + sql + "]", ex);
        } finally {
            SQLiteUtils.close(statement);
            SQLiteUtils.endTransaction(database);
            SQLiteUtils.close(database);
        }
    }

    static interface RowMapper<T> {

        T mapRow(Cursor cursor, int rowNum);
    }

    static interface SQLiteStatementBinder {

        void bindValues(SQLiteStatement statement);
    }

    static class SingleColumnRowMapper implements RowMapper<String> {

        @Override
        public String mapRow(Cursor cursor, int rowNum) {
            return cursor.getString(0);
        }
    }

    static class ColumnMapRowMapper implements RowMapper<Map<String, String>> {

        @Override
        public Map<String, String> mapRow(Cursor cursor, int rowNum) {
            int columnCount = cursor.getColumnCount();
            Map<String, String> row = new HashMap<>(columnCount);
            for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                row.put(cursor.getColumnName(columnIndex), cursor.getString(columnIndex));
            }
            return row;
        }

    }

    static interface BatchSQLiteStatementBinder {

        void bindValues(SQLiteStatement statement, int i);
        int getBatchSize();
    }

    static abstract class DAOSupport {

        private SQLiteTemplate sqliteTemplate;
        private Context context;

        protected SQLiteTemplate getSQLiteTemplate() {
            return sqliteTemplate;
        }

        protected Context getContext() {
            return context;
        }

        public void setContext(Context context) {
            this.context = context;
        }

        public void setSQLiteOpenHelper(SQLiteOpenHelper databaseHelper) {
            sqliteTemplate = new SQLiteTemplate(databaseHelper);
        }

        protected String getSqlString(int resId) {
            return context.getString(resId).replaceAll("\\\\'", "'");
        }
    }
}