package com.twitt4droid.app.widget;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

public class DrawerItemAdapter extends BaseAdapter {

    private final Context context;

    private List<DrawerItem> data;

    public DrawerItemAdapter(Context context) {
        this.context = context;
        this.data = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public DrawerItem getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void add(DrawerItem item) {
        data.add(item);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getItem(position).initView(convertView, context);
    }
}