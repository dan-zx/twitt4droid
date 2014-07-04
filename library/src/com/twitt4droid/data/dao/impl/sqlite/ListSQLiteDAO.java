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

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import com.twitt4droid.R;
import com.twitt4droid.data.dao.ListTimelineDAO;
import com.twitt4droid.util.Objects;

import twitter4j.Status;

import java.util.List;

public class ListSQLiteDAO extends SQLiteTemplate.DAOSupport implements ListTimelineDAO {

    @Override
    public List<Status> fetchListByListId(Long listId) {
        return getSQLiteTemplate().queryForList(
                getSqlString(R.string.twitt4droid_fetch_list_all_statuses_by_list_id_sql), 
                new String[] { Objects.toString(listId) },
                new SQLiteTemplate.RowMapper<Status>() {

                    @Override
                    public Status mapRow(Cursor cursor, int rowNum) {
                        return new StatusCursorImpl(cursor);
                    }
                });
    }

    @Override
    public void save(final List<Status> statuses, final Long listId) {
        getSQLiteTemplate().batchExecute(
                getSqlString(R.string.twitt4droid_insert_list_status_sql), 
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
                        statement.bindLong(++index, listId);
                        statement.bindString(++index, status.getText());
                        statement.bindString(++index, status.getUser().getScreenName());
                        statement.bindString(++index, status.getUser().getName());
                        statement.bindLong(++index, status.getCreatedAt().getTime());
                        statement.bindString(++index, status.getUser().getProfileImageURL());
                    }
                });
    }

    @Override
    public void deleteAllByListId(Long listId) {
        getSQLiteTemplate().execute(
                getSqlString(R.string.twitt4droid_delete_all_list_statuses_by_list_id_sql),
                new String[] { Objects.toString(listId) });
    }
}