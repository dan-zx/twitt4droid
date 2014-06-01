package com.twitt4droid.app.widget;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.twitt4droid.Twitt4droid;
import com.twitt4droid.Twitt4droidAsyncTasks;
import com.twitt4droid.app.R;
import com.twitt4droid.task.ImageLoader;

import twitter4j.User;

public class HeaderDrawerItem extends DrawerItem {

    private static final String TAG = HeaderDrawerItem.class.getSimpleName();

    private String userScreenName; 
    
    public HeaderDrawerItem(Context context) {
        userScreenName = Twitt4droid.getCurrentUserUsername(context);
    }
    
    @Override
    public View initView(View convertView, final Context context) {
        ViewHolder holder = null;
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.header_drawer_item, null);
            holder = new ViewHolder();
            holder.userProfileBannerImage = (ImageView) convertView.findViewById(R.id.user_profile_banner_image);
            holder.userProfileImage = (ImageView) convertView.findViewById(R.id.user_profile_image);
            holder.userScreenName = (TextView) convertView.findViewById(R.id.user_screen_name);
            holder.userName = (TextView) convertView.findViewById(R.id.user_name);
            convertView.setTag(holder);
        } else holder = (ViewHolder) convertView.getTag();
        setContent(holder, context);
        return convertView;
    }

    private void setContent(final ViewHolder holder, final Context context) {
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
        }.execute(userScreenName);
    }

    private static class ViewHolder {
        private ImageView userProfileBannerImage, userProfileImage;
        private TextView userScreenName, userName;
    }
}