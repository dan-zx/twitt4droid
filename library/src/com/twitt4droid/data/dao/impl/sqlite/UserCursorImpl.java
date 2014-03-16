package com.twitt4droid.data.dao.impl.sqlite;

import android.database.Cursor;

import twitter4j.RateLimitStatus;
import twitter4j.Status;
import twitter4j.URLEntity;
import twitter4j.User;

import java.net.URL;
import java.util.Date;

class UserCursorImpl implements User {

    private static final long serialVersionUID = -3838693829821915548L;

    private long id;
    private String name;
    private String screenName;
    private String profileImageURL;
    private String url;
    private String description;
    private String location;
    
    public UserCursorImpl(Cursor cursor) {
        id = Cursors.containsColumn(cursor, "id") ? cursor.getLong(cursor.getColumnIndex("id")) : -1;
        name = Cursors.containsColumn(cursor, "name") ? cursor.getString(cursor.getColumnIndex("name")) : null;
        screenName = Cursors.containsColumn(cursor, "screen_name") ? cursor.getString(cursor.getColumnIndex("screen_name")) : null;
        profileImageURL = Cursors.containsColumn(cursor, "profile_image_url") ? cursor.getString(cursor.getColumnIndex("profile_image_url")) : null;
        url = Cursors.containsColumn(cursor, "url") ? cursor.getString(cursor.getColumnIndex("url")) : null;
        description = Cursors.containsColumn(cursor, "description") ? cursor.getString(cursor.getColumnIndex("description")) : null;
        location = Cursors.containsColumn(cursor, "location") ? cursor.getString(cursor.getColumnIndex("location")) : null;
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
    public String getProfileBackgroundImageUrl() {
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
        // TODO Auto-generated method stub
        return null;
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
    public URL getProfileImageUrlHttps() {
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
}