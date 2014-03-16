package com.twitt4droid.data.source;

import android.util.Log;

import com.twitt4droid.util.Strings;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

public class SQLFileParser {
    
    private static final int END_OF_STREAM = -1;
    private static final String TAG = SQLFileParser.class.getSimpleName();
    private static final String STATEMENT_DELIMITER = ";";
    private static final Pattern COMMENT_PATTERN = Pattern.compile("(?:/\\*[^;]*?\\*/)|(?:--[^;]*?$)", Pattern.DOTALL | Pattern.MULTILINE);
    
    public static String[] getSqlStatements(InputStream stream) {
        
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new InputStreamReader(stream));
            int r;
            StringBuilder sb = new StringBuilder();
            while ((r = reader.read()) != END_OF_STREAM) {
                char character = (char) r;
                sb.append(character);
            }

            return COMMENT_PATTERN.matcher(sb)
                    .replaceAll(Strings.EMPTY)
                    .split(STATEMENT_DELIMITER);

        } catch (IOException ex) {
            Log.e(TAG, "Unable to parse SQL Statements", ex);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ex) {
                    Log.e(TAG, "Unable to close stream", ex);
                }
            }
        }

        return null;
    }
}