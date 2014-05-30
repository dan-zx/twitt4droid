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
    public List<Status> fetchById(Long id) {
        return getSQLiteTemplate().queryForList(
                getSqlString(R.string.twitt4droid_fetch_all_statuses_by_id_sql), 
                new String[] { Objects.toString(id) }, 
                new SQLiteTemplate.RowMapper<Status>() {

                    @Override
                    public Status mapRow(Cursor cursor, int rowNum) {
                        return new StatusCursorImpl(cursor);
                    }
                });
    }
}
