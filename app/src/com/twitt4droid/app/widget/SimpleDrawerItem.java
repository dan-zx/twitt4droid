package com.twitt4droid.app.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.twitt4droid.app.R;

public class SimpleDrawerItem extends DrawerItem {

    private final int iconRes;
    private final int textRes;

    public SimpleDrawerItem(int iconRes, int textRes) {
        this.iconRes = iconRes;
        this.textRes = textRes;
    }

    @Override
    public View initView(View convertView, Context context) {
        ViewHolder holder = null;
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.simple_drawer_item, null);
            holder = new ViewHolder();
            holder.iconImage = (ImageView) convertView.findViewById(R.id.icon_image);
            holder.text = (TextView) convertView.findViewById(R.id.text);
            convertView.setTag(holder);
        } else holder = (ViewHolder) convertView.getTag();
        holder.iconImage.setImageResource(iconRes);
        holder.text.setText(textRes);
        return convertView;
    }

    private static class ViewHolder {
        private ImageView iconImage;
        private TextView text;
    }
}
