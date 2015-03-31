package com.sereda.crashcamera.app.adapters;

import android.content.Context;
import android.database.Cursor;
import android.widget.ImageView;
import com.sereda.crashcamera.app.fragments.LeftFragment;

public class LeftAdapter extends AbstractPreviewAdapter {
    public LeftAdapter(Context context, int layout, Cursor cursor, String[] from, int[] to, int flags) {
        super(context, layout, cursor, from, to, flags);
    }

    @Override
    public ImageView setImageView() {
        return LeftFragment.imageView;
    }
}
