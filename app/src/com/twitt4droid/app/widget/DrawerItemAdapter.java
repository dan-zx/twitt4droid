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
            convertView = layoutInflater.inflate(R.layout.drawer_item, parent);
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