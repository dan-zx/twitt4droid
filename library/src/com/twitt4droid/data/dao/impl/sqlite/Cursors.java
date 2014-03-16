package com.twitt4droid.data.dao.impl.sqlite;

import android.database.Cursor;

public final class Cursors {

    public static final int COLUMN_NOT_FOUND = -1;
    
    private Cursors() {
        throw new IllegalAccessError("This class cannot be instantiated nor extended");
    }
    
    public static boolean containsColumn(Cursor cursor, String columnName) {
        return cursor.getColumnIndex(columnName) != COLUMN_NOT_FOUND;
    }
}