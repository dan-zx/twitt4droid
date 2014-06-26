package com.twitt4droid.app.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.twitt4droid.app.R;

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
        ViewHolder holder = null;
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.drawer_item, null);
            holder = new ViewHolder(); 
            holder.itemtext = (TextView) convertView.findViewById(R.id.item_text);
            convertView.setTag(holder);
        } else holder = (ViewHolder) convertView.getTag();

        holder.setContent(getItem(position));
        return convertView;
    }
    
    private static class ViewHolder {

        private TextView itemtext;

        private void setContent(DrawerItem item) {
            itemtext.setCompoundDrawablesWithIntrinsicBounds(item.getIconRes(), 0, 0, 0);
            itemtext.setText(item.getTextRes());
        }
    }
}