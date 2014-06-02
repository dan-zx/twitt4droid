package com.twitt4droid.app.widget;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.twitt4droid.Twitt4droidAsyncTasks;
import com.twitt4droid.app.R;
import com.twitt4droid.task.ImageLoader;

import twitter4j.User;

import java.util.ArrayList;
import java.util.List;

public class DrawerItemAdapter extends BaseAdapter {

    private static final String TAG = DrawerItemAdapter.class.getSimpleName();

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
            holder = initViewHolder(convertView);
            convertView.setTag(holder);
        } else holder = (ViewHolder) convertView.getTag();

        DrawerItem item = getItem(position);

        switch (item.getType()) {
            case HEADER: setHeaderItemContent(item, holder); break;
            case SIMPLE: setSimpleItemContent(item, holder); break;
        }

        return convertView;
    }

    private ViewHolder initViewHolder(View convertView) {
        ViewHolder holder = new ViewHolder(); 
        holder.headerItemLayout = (RelativeLayout) convertView.findViewById(R.id.header_item_layout);
        holder.userProfileBannerImage = (ImageView) convertView.findViewById(R.id.user_profile_banner_image);
        holder.userProfileImage = (ImageView) convertView.findViewById(R.id.user_profile_image);
        holder.userScreenName = (TextView) convertView.findViewById(R.id.user_screen_name);
        holder.userName = (TextView) convertView.findViewById(R.id.user_name);
        holder.simpleItemLayout = (RelativeLayout) convertView.findViewById(R.id.simple_item_layout);
        holder.iconImage = (ImageView) convertView.findViewById(R.id.icon_image);
        holder.itemtext = (TextView) convertView.findViewById(R.id.text);
        return holder;
    }
    
    private void setSimpleItemContent(DrawerItem item, ViewHolder holder) {
        holder.simpleItemLayout.setVisibility(View.VISIBLE);
        holder.headerItemLayout.setVisibility(View.GONE);
        holder.iconImage.setImageResource(item.get("ICON_RES", int.class));
        holder.itemtext.setText(item.get("TEXT_RES", int.class));
    }

    private void setHeaderItemContent(DrawerItem item, final ViewHolder holder) {
        holder.headerItemLayout.setVisibility(View.VISIBLE);
        holder.simpleItemLayout.setVisibility(View.GONE);
        new Twitt4droidAsyncTasks.UserInfoFetcher(context) {

            @Override
            protected void onPostExecute(User result) {
                if (getTwitterException() != null) {
                    Log.e(TAG, "Twitter error", getTwitterException());
                    Toast.makeText(getContext().getApplicationContext(), 
                            R.string.twitt4droid_error_message, 
                            Toast.LENGTH_LONG)
                            .show();
                } else if (result != null) {
                    holder.userScreenName.setText(getContext().getString(R.string.twitt4droid_username_format, result.getScreenName()));
                    holder.userName.setText(result.getName());
                    new ImageLoader(getContext())
                        .setLoadingColorId(R.color.twitt4droid_no_image_background)
                        .setImageView(holder.userProfileBannerImage)
                        .execute(result.getProfileBannerURL());
                    new ImageLoader(getContext())
                        .setLoadingColorId(R.color.twitt4droid_no_image_background)
                        .setImageView(holder.userProfileImage)
                        .execute(result.getProfileImageURL());
                }
            }
        }.execute(item.get("SCREEN_NAME", String.class));
    }

    private static class ViewHolder {
        private RelativeLayout headerItemLayout;
        private ImageView userProfileBannerImage;
        private ImageView userProfileImage;
        private TextView userScreenName;
        private TextView userName;

        private RelativeLayout simpleItemLayout;
        private ImageView iconImage;
        private TextView itemtext;
    }
}