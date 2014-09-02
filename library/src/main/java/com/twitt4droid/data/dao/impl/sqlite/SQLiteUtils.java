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

/**
 * Miscellaneous utility methods for SQLite databases.
 * 
 * @author Daniel Pedraza-Arcega
 * @since version 1.0
 */
class SQLiteUtils {

    /**
     * SQLite time string formats.
     * 
     * @see <a href="http://sqlite.org/lang_datefunc.html">SQLite - Date And Time Functions</a>
     * @author Daniel Pedraza-Arcega
     * @since version 1.0
     */
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

    private static final String TAG = SQLiteUtils.class.getSimpleName();

    /**
     * Default constructor. Do NOT try to initialize this class, it is suppose
     * to be an static utility.
     */
    private SQLiteUtils() {
        throw new IllegalAccessError("This class cannot be instantiated nor extended");
    }

    /**
     * Ends the current transaction from the given database.
     * 
     * @param database a SQLiteDatabase.
     */
    static void endTransaction(SQLiteDatabase database) {
        if (database != null) {
            try {
                database.endTransaction();
            } catch (Exception ex) {
                Log.e(TAG, "Couldn't close database correctly");
            }
        }
    }

    /**
     * Closes the given database.
     * 
     * @param database a SQLiteDatabase.
     */
    static void close(SQLiteDatabase database) {
        if (database != null) {
            try {
                database.close();
            } catch (Exception ex) {
                Log.e(TAG, "Couldn't close database correctly");
            }
        }
    }

    /**
     * Closes the given cursor.
     * 
     * @param cursor a Cursor.
     */
    static void close(Cursor cursor) {
        if (cursor != null) {
            try {
                cursor.close();
            } catch (Exception ex) {
                Log.e(TAG, "Couldn't close cursor correctly");
            }
        }
    }

    /**
     * Closes the given statement.
     * 
     * @param statement a SQLiteStatement.
     */
    static void close(SQLiteStatement statement) {
        if (statement != null) {
            try {
                statement.close();
            } catch (Exception ex) {
                Log.e(TAG, "Couldn't close statement correctly");
            }
        }
    }

    /**
     * Binds all of the arguments in one single call. 
     * 
     * @param statement a SQLiteStatement.
     * @param bindArgs the arguments.
     */
    static void bindAllArgsAsStrings(SQLiteStatement statement, String[] bindArgs) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) nativeBindAllArgsAsStrings(statement, bindArgs);
        else if (bindArgs != null) {
            for (int i = bindArgs.length; i != 0; i--) statement.bindString(i, bindArgs[i-1]);
        }
    }

    /**
     * Calls the native bindAllArgsAsStrings method in the given SQLiteStatement.
     * 
     * @param statement a SQLiteStatement
     * @param bindArgs the arguments.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private static void nativeBindAllArgsAsStrings(SQLiteStatement statement, String[] bindArgs) {
        statement.bindAllArgsAsStrings(bindArgs);
    }

    /**
     * Checks if the column with the given name exists.
     *  
     * @param cursor a Cursor.
     * @param columnName the column name.
     * @return {@code true} if exists; otherwise {@code false}.
     */
    static boolean containsColumn(Cursor cursor, String columnName) {
        return cursor.getColumnIndex(columnName) != -1;
    }

    /**
     * Gets the byte value of the given column.
     * 
     * @param cursor a Cursor.
     * @param columnName the column name.
     * @return the column value if exists; otherwise {@code null}.
     */
    static Byte getByte(Cursor cursor, String columnName) {
        return containsColumn(cursor, columnName) && !cursor.isNull(cursor.getColumnIndex(columnName))
                ? (byte) cursor.getShort(cursor.getColumnIndex(columnName))
                : null;
    }

    /**
     * Gets the short value of the given column.
     * 
     * @param cursor a Cursor.
     * @param columnName the column name.
     * @return the column value if exists; otherwise {@code null}.
     */
    static Short getShort(Cursor cursor, String columnName) {
        return containsColumn(cursor, columnName) && !cursor.isNull(cursor.getColumnIndex(columnName))
                ? cursor.getShort(cursor.getColumnIndex(columnName))
                : null;
    }

    /**
     * Gets the integer value of the given column.
     * 
     * @param cursor a Cursor.
     * @param columnName the column name.
     * @return the column value if exists; otherwise {@code null}.
     */
    static Integer getInteger(Cursor cursor, String columnName) {
        return containsColumn(cursor, columnName) && !cursor.isNull(cursor.getColumnIndex(columnName))
                ? cursor.getInt(cursor.getColumnIndex(columnName))
                : null;
    }

    /**
     * Gets the long value of the given column.
     * 
     * @param cursor a Cursor.
     * @param columnName the column name.
     * @return the column value if exists; otherwise {@code null}.
     */
    static Long getLong(Cursor cursor, String columnName) {
        return containsColumn(cursor, columnName) && !cursor.isNull(cursor.getColumnIndex(columnName))
                ? cursor.getLong(cursor.getColumnIndex(columnName))
                : null;
    }

    /**
     * Gets the float value of the given column.
     * 
     * @param cursor a Cursor.
     * @param columnName the column name.
     * @return the column value if exists; otherwise {@code null}.
     */
    static Float getFloat(Cursor cursor, String columnName) {
        return containsColumn(cursor, columnName) && !cursor.isNull(cursor.getColumnIndex(columnName))
                ? cursor.getFloat(cursor.getColumnIndex(columnName))
                : null;
    }

    /**
     * Gets the double value of the given column.
     * 
     * @param cursor a Cursor.
     * @param columnName the column name.
     * @return the column value if exists; otherwise {@code null}.
     */
    static Double getDouble(Cursor cursor, String columnName) {
        return containsColumn(cursor, columnName) && !cursor.isNull(cursor.getColumnIndex(columnName))
                ? cursor.getDouble(cursor.getColumnIndex(columnName))
                : null;
    }

    /**
     * Gets the boolean value of the given column.
     * 
     * @param cursor a Cursor.
     * @param columnName the column name.
     * @return the column value if exists; otherwise {@code null}.
     */
    static Boolean getBoolean(Cursor cursor, String columnName) {
        Short value = getShort(cursor, columnName);
        if (value != null) return value == 1 ? true : value == 0 ? false : null;
        return null;
    }

    /**
     * Gets the character value of the given column.
     * 
     * @param cursor a Cursor.
     * @param columnName the column name.
     * @return the column value if exists; otherwise {@code null}.
     */
    static Character getCharacter(Cursor cursor, String columnName) {
        String value = getString(cursor, columnName);
        if (value != null && value.length() == 1) return value.charAt(0);
        return null;
    }

    /**
     * Gets the string value of the given column.
     * 
     * @param cursor a Cursor.
     * @param columnName the column name.
     * @return the column value if exists; otherwise {@code null}.
     */
    static String getString(Cursor cursor, String columnName) {
        return containsColumn(cursor, columnName) && !cursor.isNull(cursor.getColumnIndex(columnName))
                ? cursor.getString(cursor.getColumnIndex(columnName))
                : null;
    }

    /**
     * Gets the blob value of the given column.
     * 
     * @param cursor a Cursor.
     * @param columnName the column name.
     * @return the column value if exists; otherwise {@code null}.
     */
    static byte[] getBlob(Cursor cursor, String columnName) {
        return containsColumn(cursor, columnName) && !cursor.isNull(cursor.getColumnIndex(columnName))
                ? cursor.getBlob(cursor.getColumnIndex(columnName))
                : null;
    }

    /**
     * Gets the date value of the given column when the column is long type. 
     * 
     * @param cursor a Cursor.
     * @param columnName the column name.
     * @return the column value if exists; otherwise {@code null}.
     */
    static Date getDateFromLong(Cursor cursor, String columnName) {
        Long value = getLong(cursor, columnName);
        if (value != null) return new Date(value);
        return null;
    }

    /**
     * Gets the date value of the given column when the column is unix time. 
     * 
     * @param cursor a Cursor.
     * @param columnName the column name.
     * @return the column value if exists; otherwise {@code null}.
     */
    static Date getDateFromUnixTime(Cursor cursor, String columnName) {
        Long value = getLong(cursor, columnName);
        if (value != null) return new Date(value * 1000L);
        return null;
    }

    /**
     * Gets the date value of the given column when the column is string type. 
     * 
     * @param cursor a Cursor.
     * @param columnName the column name.
     * @param timeString the format of the date.
     * @return the column value if exists; otherwise {@code null}.
     */
    static Date getDateFromString(Cursor cursor, String columnName, TimeString timeString) {
        String value = getString(cursor, columnName);
        if (!Strings.isNullOrBlank(value)) {
            for (String format : timeString.formats) {
                try {
                    return new SimpleDateFormat(format, Locale.ENGLISH).parse(value);
                } catch (ParseException ex) {
                    // Ignore and proceed with the next format.
                }
            }
        }

        return null;
    }

    /**
     * Gets the enum value of the given column when the column is string type.
     * 
     * @param cursor a Cursor.
     * @param columnName the column name.
     * @param enumType the enum class.
     * @return the column value if exists; otherwise {@code null}.
     */
    static <E extends Enum<E>> E getEnumFromName(Cursor cursor, String columnName, Class<E> enumType) {
        String value = getString(cursor, columnName);
        if (!Strings.isNullOrBlank(value)) return Enum.valueOf(enumType, value);
        return null;
    }

    /**
     * Gets the enum value of the given column when the column is numeric type.
     * 
     * @param cursor a Cursor.
     * @param columnName the column name.
     * @param enumType the enum class.
     * @return the column value if exists; otherwise {@code null}.
     */
    static <E extends Enum<E>> E getEnumFromOrdinal(Cursor cursor, String columnName, Class<E> enumType) {
        Integer value = getInteger(cursor, columnName);
        if (value != null) return enumType.getEnumConstants()[value];
        return null;
    }
}