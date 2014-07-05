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
import java.util.List;

/**
 * Simplifies the use of SQLite databases and helps to avoid common errors.
 * 
 * @author Daniel Pedraza-Arcega
 * @since version 1.0
 */
class SQLiteTemplate {

    private static final String TAG = SQLiteTemplate.class.getSimpleName();

    private final SQLiteOpenHelper databaseHelper;

    /**
     * Creates a SQLiteTemplate.
     * 
     * @param databaseHelper a SQLiteOpenHelper.
     */
    SQLiteTemplate(SQLiteOpenHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    /**
     * Creates a query from given SQL to create a raw query, mapping a single row to a Domain object
     * via a RowMapper.
     * 
     * @param <T> the Object type to be returned.
     * @param sql SQL query to execute.
     * @param rowMapper object that will map one object per row.
     * @return the object mapped.
     */
    <T> T queryForSingleResult(String sql, RowMapper<T> rowMapper) {
        return queryForSingleResult(sql, null, rowMapper);
    }

    /**
     * Creates a query from given SQL to create a raw query, mapping a single row to a Domain object
     * via a RowMapper. The parameters are binded to the query using an string array.
     * 
     * @param <T> the Object type to be returned.
     * @param sql SQL query to execute.
     * @param args arguments to bind to the query.
     * @param rowMapper object that will map one object per row.
     * @return the object mapped.
     */
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

    /**
     * Creates a query from given SQL to create a prepared statement, mapping 
     * each row to a Domain object via a RowMapper
     * 
     * @param <T> the List type to be returned.
     * @param sql SQL query to execute.
     * @param rowMapper object that will map one object per row. 
     * @return the result List, containing mapped objects.
     */
    <T> List<T> queryForList(String sql, RowMapper<T> rowMapper) {
        return queryForList(sql, null, rowMapper);
    }

    /**
     * Creates a query from given SQL to create a row query, mapping 
     * each row to a Domain object via a RowMapper. The parameters are binded to
     * the query using an Object array.
     * 
     * @param <T> the List type to be returned.
     * @param sql SQL query to execute.
     * @param args parameters to bind to the query.
     * @param rowMapper object that will map one object per row. 
     * @return the result List, containing mapped objects.
     */
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

    /**
     * Issue a single SQL update operation (such as an insert, update or delete statement)
     * 
     * @param sql a SQL command.
     */
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

    /**
     * Issue a single SQL update operation (such as an insert, update or delete statement) with a
     * binder object.
     * 
     * @param sql SQL to execute .
     * @param statementBinder the SQLiteStatementBinder to set values to a SQLiteStatement.
     */
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

    /**
     * Issue a single SQL update operation (such as an insert, update or delete statement) and an
     * array of arguments to bind to the update.
     * 
     * @param sql SQL to execute.
     * @param args parameters to bind to the query.
     */
    void execute(String sql, String[] args) {
        SQLiteDatabase database = null;
        SQLiteStatement statement = null;
        try {
            database = databaseHelper.getWritableDatabase();
            database.beginTransaction();
            statement = database.compileStatement(sql);
            SQLiteUtils.bindAllArgsAsStrings(statement, args);
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

    /**
     * Submits a batch of commands to the database for execution..
     * 
     * @param sqls SQLs to execute.
     */
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

    /**
     * Submits a batch of commands to the database for execution.
     * 
     * @param sql SQL to execute.
     * @param args arguments to bind to the query.
     */
    void batchExecute(String sql, String[][] argsPerRow) {
        SQLiteDatabase database = null;
        SQLiteStatement statement = null;
        try {
            database = databaseHelper.getWritableDatabase();
            database.beginTransaction();
            statement = database.compileStatement(sql);
            for (String[] args : argsPerRow) {
                statement.clearBindings();
                SQLiteUtils.bindAllArgsAsStrings(statement, args);
                statement.execute();
            }
            database.setTransactionSuccessful();
        } catch (Exception ex) {
            Log.e(TAG, "Couldn't execute batch " + sql, ex);
        } finally {
            SQLiteUtils.close(statement);
            SQLiteUtils.endTransaction(database);
            SQLiteUtils.close(database);
        }
    }

    /**
     * Submits a batch of commands to the database for execution.
     * 
     * @param sql SQL to execute.
     * @param statementBinder the BatchSQLiteStatementBinder to set values to a SQLiteStatement.
     */
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

    /**
     * An interface for mapping rows of a Cursor on a per-row basis. Implementations of this
     * interface perform the actual work of mapping each row to a result object.
     * 
     * @author Daniel Pedraza-Arcega
     * @since version 1.0
     */
    static interface RowMapper<T> {

        /**
         * Implementations must implement this method to map each row of data in the ResultSet. This
         * method should not call next() on the ResultSet; it is only supposed to map values of the
         * current row.
         * 
         * @param <T> the object type.
         * @param cursor the Cursor to map (pre-initialized for the current row).
         * @param rowNum the number of the current row.
         * @return the result object for the current row.
         */
        T mapRow(Cursor cursor, int rowNum);
    }

    /**
     * This interface sets values on a SQLiteStatement provided by the SQLiteTemplate class, for
     * each of a number of updates in a batch using the same SQL. Implementations are responsible
     * for setting any necessary parameters. SQL with placeholders will already have been supplied.
     * 
     * @author Daniel Pedraza-Arcega
     * @since version 1.0
     */
    static interface SQLiteStatementBinder {

        /**
         * Binds parameter values on the given SQLiteStatement.
         *
         * @param statement the SQLiteStatement to invoke setter methods on.
         */
        void bindValues(SQLiteStatement statement);
    }

    /**
     * This interface sets values on a SQLiteStatement provided by the SQLiteTemplate class, for
     * each of a number of updates in a batch using the same SQL. Implementations are responsible
     * for setting any necessary parameters. SQL with placeholders will already have been supplied.
     * 
     * @author Daniel Pedraza-Arcega
     * @since version 1.0
     */
    static interface BatchSQLiteStatementBinder {

        /**
         * Sets parameter values on the given SQLiteStatement.
         *
         * @param statement the SQLiteStatement to invoke setter methods on.
         * @param i index of the statement we're issuing in the batch, starting from 0.
         */
        void bindValues(SQLiteStatement statement, int i);

        /**
         * Returns the size of the batch.
         *
         * @return the number of statements in the batch.
         */
        int getBatchSize();
    }

    /**
     * Generic base class for DAOs, defining template methods for DAO initialization.
     * 
     * @author Daniel Pedraza-Arcega
     * @since version 1.0
     */
    static abstract class DAOSupport {

        private SQLiteTemplate sqliteTemplate;
        private Context context;

        /** @return the current SQLiteTemplate. */
        protected SQLiteTemplate getSQLiteTemplate() {
            return sqliteTemplate;
        }

        /** @return the current context. */
        protected Context getContext() {
            return context;
        }

        /** @param context the current context. */
        public void setContext(Context context) {
            this.context = context;
        }

        /** @param databaseHelper the SQLiteOpenHelper. */
        public void setSQLiteOpenHelper(SQLiteOpenHelper databaseHelper) {
            sqliteTemplate = new SQLiteTemplate(databaseHelper);
        }

        /** @return a SQL command from String resources. */
        protected String getSqlString(int resId) {
            return context.getString(resId).replaceAll("\\\\'", "'");
        }
    }
}