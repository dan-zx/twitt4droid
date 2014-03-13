package com.twitt4droid.app.fragment;

import android.widget.Toast;

import com.twitt4droid.app.R;
import com.twitt4droid.fragment.QueryableTimelineFragment;

public class CustomQueryableTimelineFragment extends QueryableTimelineFragment {

    @Override
    protected void onTwitterError(Exception ex) {
        Toast.makeText(getActivity().getApplicationContext(), 
                R.string.twitt4droid_error_message, 
                Toast.LENGTH_LONG).show();
    }
}