package com.sereda.crashcamera.app.adapters;

import android.content.Context;
import android.database.Cursor;
import android.widget.ImageView;
import com.sereda.crashcamera.app.fragments.LeftFragment;
import com.sereda.crashcamera.app.fragments.RightFragment;

public class RightAdapter extends AbstractPreviewAdapter {
    public RightAdapter(Context context, int layout, Cursor cursor, String[] from, int[] to, int flags) {
        super(context, layout, cursor, from, to, flags);
    }

    @Override
    public ImageView setImageView() {
        return RightFragment.imageView;
    }
}
