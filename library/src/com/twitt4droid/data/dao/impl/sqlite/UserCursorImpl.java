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

import twitter4j.RateLimitStatus;
import twitter4j.Status;
import twitter4j.URLEntity;
import twitter4j.User;

import java.util.Date;

/**
 * Twitter user cursor implementation.
 * 
 * @author Daniel Pedraza-Arcega
 * @since version 1.0
 */
class UserCursorImpl implements User {

    private static final long serialVersionUID = -3838693829821915548L;

    private long id;
    private String name;
    private String screenName;
    private String profileImageURL;
    private String profileBannerURL;
    private String url;
    private String description;
    private String location;

    /**
     * Creates a user with the given Cursor.
     * 
     * @param cursor a cursor
     */
    public UserCursorImpl(Cursor cursor) {
        id = SQLiteUtils.containsColumn(cursor, "id") ? cursor.getLong(cursor.getColumnIndex("id")) : -1;
        name = SQLiteUtils.getString(cursor, "name");
        screenName = SQLiteUtils.getString(cursor, "screen_name");
        profileImageURL = SQLiteUtils.getString(cursor, "profile_image_url");
        profileBannerURL = SQLiteUtils.getString(cursor, "profile_banner_url");
        url = SQLiteUtils.getString(cursor, "url");
        description = SQLiteUtils.getString(cursor, "description");
        location = SQLiteUtils.getString(cursor, "location");
    }
    
    @Override
    public int compareTo(User another) {
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
    public String getBiggerProfileImageURL() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getBiggerProfileImageURLHttps() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Date getCreatedAt() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public URLEntity[] getDescriptionURLEntities() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getFavouritesCount() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getFollowersCount() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getFriendsCount() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public String getLang() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getListedCount() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public String getLocation() {
        return location;
    }

    @Override
    public String getMiniProfileImageURL() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getMiniProfileImageURLHttps() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getOriginalProfileImageURL() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getOriginalProfileImageURLHttps() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getProfileBackgroundColor() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getProfileBackgroundImageURL() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getProfileBackgroundImageUrlHttps() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getProfileBannerIPadRetinaURL() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getProfileBannerIPadURL() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getProfileBannerMobileRetinaURL() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getProfileBannerMobileURL() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getProfileBannerRetinaURL() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getProfileBannerURL() {
        return profileBannerURL;
    }

    @Override
    public String getProfileImageURL() {
        return profileImageURL;
    }

    @Override
    public String getProfileImageURLHttps() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getProfileLinkColor() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getProfileSidebarBorderColor() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getProfileSidebarFillColor() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getProfileTextColor() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getScreenName() {
        return screenName;
    }

    @Override
    public Status getStatus() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getStatusesCount() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public String getTimeZone() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getURL() {
        return url;
    }

    @Override
    public URLEntity getURLEntity() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getUtcOffset() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public boolean isContributorsEnabled() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isFollowRequestSent() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isGeoEnabled() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isProfileBackgroundTiled() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isProfileUseBackgroundImage() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isProtected() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isShowAllInlineMedia() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isTranslator() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isVerified() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isDefaultProfile() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isDefaultProfileImage() {
        // TODO Auto-generated method stub
        return false;
    }
}