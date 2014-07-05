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

import android.annotation.TargetApi;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Build;
import android.util.Log;
import com.twitt4droid.util.Strings;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

class SQLiteUtils {

    static enum TimeString {
        DATE ("yyyy-MM-dd"), 
        DATETIME ("yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm"), 
        TIMESTAMP ("yyyy-MM-dd HH:mm:ss.SSS"), 
        TIME ("HH:mm:ss.SSS", "HH:mm:ss", "HH:mm");

        private final String[] formats;

        TimeString(String... formats) {
            this.formats = formats;
        }

        public String[] getFormats() {
            return formats;
        }
    }

    static final int COLUMN_NOT_FOUND = -1;

    private static final String TAG = SQLiteUtils.class.getSimpleName();

    private SQLiteUtils() {
        throw new IllegalAccessError("This class cannot be instantiated nor extended");
    }

    static void endTransaction(SQLiteDatabase database) {
        if (database != null) {
            try {
                database.endTransaction();
            } catch (Exception ex) {
                Log.e(TAG, "Couldn't close database correctly");
            }
        }
    }

    static void close(SQLiteDatabase database) {
        if (database != null) {
            try {
                database.close();
            } catch (Exception ex) {
                Log.e(TAG, "Couldn't close database correctly");
            }
        }
    }

    static void close(Cursor cursor) {
        if (cursor != null) {
            try {
                cursor.close();
            } catch (Exception ex) {
                Log.e(TAG, "Couldn't close cursor correctly");
            }
        }
    }

    static void close(SQLiteStatement statement) {
        if (statement != null) {
            try {
                statement.close();
            } catch (Exception ex) {
                Log.e(TAG, "Couldn't close statement correctly");
            }
        }
    }

    static void bindAllArgsAsStrings(SQLiteStatement statement, String[] bindArgs) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) nativeBindAllArgsAsStrings(statement, bindArgs);
        else if (bindArgs != null) {
            for (int i = bindArgs.length; i != 0; i--) statement.bindString(i, bindArgs[i-1]);
        }
    }
    
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private static void nativeBindAllArgsAsStrings(SQLiteStatement statement, String[] bindArgs) {
        statement.bindAllArgsAsStrings(bindArgs);
    }
    
    static boolean containsColumn(Cursor cursor, String columnName) {
        return cursor.getColumnIndex(columnName) != COLUMN_NOT_FOUND;
    }

    static Byte getByte(Cursor cursor, String columnName) {
        return containsColumn(cursor, columnName) && !cursor.isNull(cursor.getColumnIndex(columnName))
                ? (byte) cursor.getShort(cursor.getColumnIndex(columnName))
                : null;
    }

    static Short getShort(Cursor cursor, String columnName) {
        return containsColumn(cursor, columnName) && !cursor.isNull(cursor.getColumnIndex(columnName))
                ? cursor.getShort(cursor.getColumnIndex(columnName))
                : null;
    }

    static Integer getInteger(Cursor cursor, String columnName) {
        return containsColumn(cursor, columnName) && !cursor.isNull(cursor.getColumnIndex(columnName))
                ? cursor.getInt(cursor.getColumnIndex(columnName))
                : null;
    }

    static Long getLong(Cursor cursor, String columnName) {
        return containsColumn(cursor, columnName) && !cursor.isNull(cursor.getColumnIndex(columnName))
                ? cursor.getLong(cursor.getColumnIndex(columnName))
                : null;
    }

    static Float getFloat(Cursor cursor, String columnName) {
        return containsColumn(cursor, columnName) && !cursor.isNull(cursor.getColumnIndex(columnName))
                ? cursor.getFloat(cursor.getColumnIndex(columnName))
                : null;
    }

    static Double getDouble(Cursor cursor, String columnName) {
        return containsColumn(cursor, columnName) && !cursor.isNull(cursor.getColumnIndex(columnName))
                ? cursor.getDouble(cursor.getColumnIndex(columnName))
                : null;
    }

    static Boolean getBoolean(Cursor cursor, String columnName) {
        Short value = getShort(cursor, columnName);
        if (value != null) return value == 1 ? true : value == 0 ? false : null;
        return null;
    }

    static Character getCharacter(Cursor cursor, String columnName) {
        String value = getString(cursor, columnName);
        if (value != null && value.length() == 1) return value.charAt(0);
        return null;
    }

    static String getString(Cursor cursor, String columnName) {
        return containsColumn(cursor, columnName) && !cursor.isNull(cursor.getColumnIndex(columnName))
                ? cursor.getString(cursor.getColumnIndex(columnName))
                : null;
    }

    static byte[] getBlob(Cursor cursor, String columnName) {
        return containsColumn(cursor, columnName) && !cursor.isNull(cursor.getColumnIndex(columnName))
                ? cursor.getBlob(cursor.getColumnIndex(columnName))
                : null;
    }

    static Date getDateFromLong(Cursor cursor, String columnName) {
        Long value = getLong(cursor, columnName);
        if (value != null) return new Date(value);
        return null;
    }

    static Date getDateFromUnixTime(Cursor cursor, String columnName) {
        Long value = getLong(cursor, columnName);
        if (value != null) return new Date(value * 1000L);
        return null;
    }

    static Date getDateFromString(Cursor cursor, String columnName, TimeString timeString) {
        String value = getString(cursor, columnName);
        if (!Strings.isNullOrBlank(value)) {
            for (String format : timeString.formats) {
                try {
                    return new SimpleDateFormat(format, Locale.ENGLISH).parse(value);
                } catch (ParseException ex) {
                    // Si no es el formato correcto, se ignora y sigue con el siguiente formato.
                }
            }
        }

        return null;
    }

    static <E extends Enum<E>> E getEnumFromName(Cursor cursor, String columnName, Class<E> enumType) {
        String value = getString(cursor, columnName);
        if (!Strings.isNullOrBlank(value)) return Enum.valueOf(enumType, value);
        return null;
    }

    static <E extends Enum<E>> E getEnumFromOrdinal(Cursor cursor, String columnName, Class<E> enumType) {
        Integer value = getInteger(cursor, columnName);
        if (value != null) return enumType.getEnumConstants()[value];
        return null;
    }
}