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

import android.database.Cursor;

import twitter4j.GeoLocation;
import twitter4j.HashtagEntity;
import twitter4j.MediaEntity;
import twitter4j.Place;
import twitter4j.RateLimitStatus;
import twitter4j.Scopes;
import twitter4j.Status;
import twitter4j.SymbolEntity;
import twitter4j.URLEntity;
import twitter4j.UserMentionEntity;

import java.util.Date;

/**
 * Twitter status cursor implementation.
 * 
 * @author Daniel Pedraza-Arcega
 * @since version 1.0
 */
class StatusCursorImpl implements Status {

    private static final long serialVersionUID = -7346562209217982116L;

    private long id;
    private Date createdAt;
    private String text;
    private UserCursorImpl user;

    /**
     * Creates a status with the given Cursor.
     * 
     * @param cursor a cursor
     */
    public StatusCursorImpl(Cursor cursor) {
        id = SQLiteUtils.containsColumn(cursor, "id") ? cursor.getLong(cursor.getColumnIndex("id")) : -1;
        createdAt = SQLiteUtils.getDateFromLong(cursor, "created_at");
        text = SQLiteUtils.getString(cursor, "tweet_content");
        user = new UserCursorImpl(cursor);
    }
    
    @Override
    public int compareTo(Status arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getAccessLevel() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public RateLimitStatus getRateLimitStatus() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public HashtagEntity[] getHashtagEntities() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public MediaEntity[] getMediaEntities() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SymbolEntity[] getSymbolEntities() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public URLEntity[] getURLEntities() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public UserMentionEntity[] getUserMentionEntities() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long[] getContributors() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Date getCreatedAt() {
        return createdAt;
    }

    @Override
    public long getCurrentUserRetweetId() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getFavoriteCount() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public GeoLocation getGeoLocation() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public String getInReplyToScreenName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getInReplyToStatusId() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public long getInReplyToUserId() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public String getLang() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Place getPlace() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getRetweetCount() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Status getRetweetedStatus() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Scopes getScopes() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getSource() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public UserCursorImpl getUser() {
        return user;
    }

    @Override
    public boolean isFavorited() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isPossiblySensitive() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isRetweet() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isRetweeted() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isRetweetedByMe() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isTruncated() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public MediaEntity[] getExtendedMediaEntities() {
        // TODO Auto-generated method stub
        return null;
    }
}