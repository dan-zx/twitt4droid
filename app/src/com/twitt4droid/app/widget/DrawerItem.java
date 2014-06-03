package com.twitt4droid.app.widget;

import java.util.HashMap;

public class DrawerItem {

    public static enum Type { HEADER, SIMPLE }

    private final Type type;

    private HashMap<String, Object> data;

    public DrawerItem(Type type) {
        this.type = type;
        this.data = new HashMap<>();
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> type) {
        return (T) data.get(key);
    }

    public boolean isNotNull(String key) {
        return data.get(key) != null;
    }

    public DrawerItem put(String key, Object value) {
        data.put(key, value);
        return this;
    }

    public Type getType() {
        return type;
    }
}