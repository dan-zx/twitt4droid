package com.twitt4droid.app.widget;

public class DrawerItem {

    private final int iconRes;
    private final int textRes;

    public DrawerItem(int iconRes, int textRes) {
        this.iconRes = iconRes;
        this.textRes = textRes;
    }

    public int getIconRes() {
        return iconRes;
    }

    public int getTextRes() {
        return textRes;
    }
}