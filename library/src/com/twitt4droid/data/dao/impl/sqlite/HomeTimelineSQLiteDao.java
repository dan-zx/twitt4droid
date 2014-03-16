package com.twitt4droid.data.dao.impl.sqlite;

import android.content.Context;

import com.twitt4droid.R;

class HomeTimelineSQLiteDao extends TimelineSQLiteDao {

    public HomeTimelineSQLiteDao(Context context) {
        super(context);
        setUpQueries();
    }
    
    protected void setUpQueries() {
        setReadByIdQueryResId(R.string.twitt4droid_home_readById);
        setReadListQueryResId(R.string.twitt4droid_home_readList);
        setSaveQueryResId(R.string.twitt4droid_home_insert);
        setUpdateQueryResId(R.string.twitt4droid_home_update);
        setDeleteQueryResId(R.string.twitt4droid_home_delete);
        setTruncateQueryResId(R.string.twitt4droid_home_truncate);
    }
}