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

import com.twitt4droid.R;
import com.twitt4droid.data.dao.UserTimelineDAO;
import com.twitt4droid.util.Objects;

import twitter4j.Status;

import java.util.List;

public class UserTimelineSQLiteDAO extends TimelineSQLiteDAO implements UserTimelineDAO {

    public UserTimelineSQLiteDAO() {
        super(Table.ANY_USER);
    }

    @Override
    public List<Status> fetchListByScreenName(String screenName) {
        return getSQLiteTemplate().queryForList(
                getSqlString(R.string.twitt4droid_fetch_statuses_by_screen_name_sql), 
                new String[] { Objects.toString(screenName) }, 
                new SQLiteTemplate.RowMapper<Status>() {

                    @Override
                    public Status mapRow(Cursor cursor, int rowNum) {
                        return new StatusCursorImpl(cursor);
                    }
                });
    }
}
