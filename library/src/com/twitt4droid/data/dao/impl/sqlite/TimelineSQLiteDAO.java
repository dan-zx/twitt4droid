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

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import com.twitt4droid.R;
import com.twitt4droid.data.dao.TimelineDAO;

import twitter4j.Status;

import java.util.List;

public class TimelineSQLiteDAO extends SQLiteTemplate.DAOSupport implements TimelineDAO {

    public static enum Table { HOME, MENTION, ANY_USER, FIXED_QUERY, QUERYABLE }

    private final String tableName;

    @SuppressLint("DefaultLocale")
    public TimelineSQLiteDAO(Table which) {
        tableName = which.name().toLowerCase();
    }

    @Override
    public List<Status> fetchList() {
        return getSQLiteTemplate().queryForList(
                String.format(getSqlString(R.string.twitt4droid_fetch_all_statuses_sql), tableName),
                new SQLiteTemplate.RowMapper<Status>() {

                    @Override
                    public Status mapRow(Cursor cursor, int rowNum) {
                        return new StatusCursorImpl(cursor);
                    }
                });
    }

    @Override
    public void save(final List<Status> statuses) {
        getSQLiteTemplate().batchExecute(
                String.format(getSqlString(R.string.twitt4droid_insert_status_sql), tableName), 
                new SQLiteTemplate.BatchSQLiteStatementBinder() {

                    @Override
                    public int getBatchSize() {
                        return statuses.size();
                    }

                    @Override
                    public void bindValues(SQLiteStatement statement, int i) {
                        Status status = statuses.get(i);
                        int index = 0;
                        statement.bindLong(++index, status.getId());
                        statement.bindString(++index, status.getText());
                        statement.bindString(++index, status.getUser().getScreenName());
                        statement.bindString(++index, status.getUser().getName());
                        statement.bindLong(++index, status.getCreatedAt().getTime());
                        statement.bindString(++index, status.getUser().getProfileImageURL());
                    }
                });
    }

    @Override
    public void deleteAll() {
        getSQLiteTemplate().execute(
                String.format(getSqlString(R.string.twitt4droid_delete_all_statuses_sql), tableName));
    }
}