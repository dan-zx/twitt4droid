package com.twitt4droid.data.dao.impl.sqlite;

import android.content.Context;

import com.twitt4droid.R;

class QueryableTimelineSQLiteDao extends TimelineSQLiteDao {

    public QueryableTimelineSQLiteDao(Context context) {
        super(context);
        setUpQueries();
    }
    
    protected void setUpQueries() {
        setReadByIdQueryResId(R.string.twitt4droid_queryable_readById);
        setReadListQueryResId(R.string.twitt4droid_queryable_readList);
        setSaveQueryResId(R.string.twitt4droid_queryable_insert);
        setUpdateQueryResId(R.string.twitt4droid_queryable_update);
        setDeleteQueryResId(R.string.twitt4droid_queryable_delete);
        setTruncateQueryResId(R.string.twitt4droid_queryable_truncate);
    }
}