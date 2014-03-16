package com.twitt4droid.data.dao.impl.sqlite;

import android.content.Context;

import com.twitt4droid.R;

class UserTimelineSQLiteDao extends TimelineSQLiteDao {

    public UserTimelineSQLiteDao(Context context) {
        super(context);
        setUpQueries();
    }
    
    protected void setUpQueries() {
        setReadByIdQueryResId(R.string.twitt4droid_mine_readById);
        setReadListQueryResId(R.string.twitt4droid_mine_readList);
        setSaveQueryResId(R.string.twitt4droid_mine_insert);
        setUpdateQueryResId(R.string.twitt4droid_mine_update);
        setDeleteQueryResId(R.string.twitt4droid_mine_delete);
        setTruncateQueryResId(R.string.twitt4droid_mine_truncate);
    }
}