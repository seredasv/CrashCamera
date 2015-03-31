package com.sereda.crashcamera.app.adapters;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.sereda.crashcamera.app.R;
import com.sereda.crashcamera.app.fragments.LeftFragment;
import com.sereda.crashcamera.app.utils.CropSquareTransformation;
import com.sereda.crashcamera.app.utils.DBHelper;
import com.squareup.picasso.Picasso;

import java.io.File;

public class LeftAdapter extends AbstractPreviewAdapter {
    public LeftAdapter(Context context, int layout, Cursor cursor, String[] from, int[] to, int flags) {
        super(context, layout, cursor, from, to, flags);
    }

    @Override
    public ImageView setImageView() {
        return LeftFragment.imageView;
    }
}
