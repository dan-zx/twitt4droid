package com.twitt4droid.data.dao.impl.sqlite;

import android.content.Context;

import com.twitt4droid.R;

class FixedQueryTimelineSQLiteDao extends TimelineSQLiteDao {

    public FixedQueryTimelineSQLiteDao(Context context) {
        super(context);
        setUpQueries();
    }
    
    protected void setUpQueries() {
        setReadByIdQueryResId(R.string.twitt4droid_fixedQuery_readById);
        setReadListQueryResId(R.string.twitt4droid_fixedQuery_readList);
        setSaveQueryResId(R.string.twitt4droid_fixedQuery_insert);
        setUpdateQueryResId(R.string.twitt4droid_fixedQuery_update);
        setDeleteQueryResId(R.string.twitt4droid_fixedQuery_delete);
        setTruncateQueryResId(R.string.twitt4droid_fixedQuery_truncate);
    }
}