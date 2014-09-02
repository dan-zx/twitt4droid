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
import com.twitt4droid.data.dao.UserDAO;
import com.twitt4droid.util.Objects;

import twitter4j.User;

/**
 * SQLite implementation of UserDAO interface.
 *
 * @author Daniel Pedraza-Arcega
 * @since version 1.0
 */
public class UserSQLiteDAO extends SQLiteTemplate.DAOSupport implements UserDAO {

    /** {@inheritDoc} */
    @Override
    public User fetchById(Long id) {
        return getSQLiteTemplate().queryForSingleResult(
                getSqlString(R.string.twitt4droid_fetch_user_by_id_sql), 
                new String[] { Objects.toString(id) }, 
                new SQLiteTemplate.RowMapper<User>() {

                    @Override
                    public User mapRow(Cursor cursor, int rowNum) {
                        return new UserCursorImpl(cursor);
                    }
                });
    }

    /** {@inheritDoc} */
    @Override
    public User fetchByScreenName(String screenName) {
        return getSQLiteTemplate().queryForSingleResult(
                getSqlString(R.string.twitt4droid_fetch_user_by_screen_name_sql), 
                new String[] { screenName }, 
                new SQLiteTemplate.RowMapper<User>() {

                    @Override
                    public User mapRow(Cursor cursor, int rowNum) {
                        return new UserCursorImpl(cursor);
                    }
                });
    }

    /** {@inheritDoc} */
    @Override
    public void save(User user) {
        getSQLiteTemplate().execute(
                getSqlString(R.string.twitt4droid_insert_user_sql), 
                new String[] { Objects.toString(user.getId()), user.getName(), user.getScreenName(), user.getProfileImageURL(), user.getProfileBannerURL(), user.getURL(), user.getDescription(), user.getLocation() });
    }

    /** {@inheritDoc} */
    @Override
    public void delete(User user) {
        getSQLiteTemplate().execute(
                getSqlString(R.string.twitt4droid_delete_user_by_id_sql), 
                new String[] { Objects.toString(user.getId()) });
    }
}