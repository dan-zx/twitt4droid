package com.twitt4droid.data.dao.impl.sqlite;

import android.content.Context;

import com.twitt4droid.R;

class MentionsTimelineSQLiteDao extends TimelineSQLiteDao {

    public MentionsTimelineSQLiteDao(Context context) {
        super(context);
        setUpQueries();
    }
    
    protected void setUpQueries() {
        setReadByIdQueryResId(R.string.twitt4droid_mention_readById);
        setReadListQueryResId(R.string.twitt4droid_mention_readList);
        setSaveQueryResId(R.string.twitt4droid_mention_insert);
        setUpdateQueryResId(R.string.twitt4droid_mention_update);
        setDeleteQueryResId(R.string.twitt4droid_mention_delete);
        setTruncateQueryResId(R.string.twitt4droid_mention_truncate);
    }
}