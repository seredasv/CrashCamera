package com.sereda.crashcamera.app.adapters;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import com.sereda.crashcamera.app.R;
import com.sereda.crashcamera.app.utils.CropSquareTransformation;
import com.sereda.crashcamera.app.utils.DBHelper;
import com.squareup.picasso.Picasso;

import java.io.File;

public abstract class AbstractPreviewAdapter extends SimpleCursorAdapter {
    private static final int IMAGE_SIZE = 100;
    private int layout;
    private Context context;
    private Cursor cursor;
    private File dir;

    public AbstractPreviewAdapter(Context context, int layout, Cursor cursor, String[] from, int[] to, int flags) {
        super(context, layout, cursor, from, to, flags);
        this.layout = layout;
        this.context = context;
        this.cursor = cursor;

        dir = context.getFilesDir();
    }

    public abstract ImageView setImageView();

    public void setBigPicture() {
        if (null != setImageView()) {
            setPicture(setImageView(), cursor);
        }
    }

    @Override
    public void changeCursor(Cursor cursor) {
        super.changeCursor(cursor);
        this.cursor = cursor;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (null == convertView) {
            convertView = inflater.inflate(layout, null);

            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (cursor.getCount() > 0 && cursor.moveToPosition(position)) {
            holder.textView.setText(String.valueOf(position + 1));
            holder.textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (cursor.moveToPosition(position)) {
                        setBigPicture();
                    }
                }
            });

            setPicture(holder.imageView, cursor);
        }

        return convertView;
    }

    private void setPicture(ImageView imageView, Cursor cursor) {
        String fileName = cursor.getString(cursor.getColumnIndex(DBHelper.PHOTO_FILE_NAME));
        String stringUri = cursor.getString(cursor.getColumnIndex(DBHelper.PHOTO_URI));
        Uri uri = Uri.parse(stringUri);

        File file = new File(dir, fileName);
        if (file.exists()) {
            Picasso.with(context).load(uri).transform(new CropSquareTransformation()).resize(IMAGE_SIZE, IMAGE_SIZE)
                    .placeholder(R.drawable.image_view_empty_photo).into(imageView);
        } else {
            Picasso.with(context).load(R.drawable.image_view_empty_photo).resize(IMAGE_SIZE, IMAGE_SIZE).into(imageView);
        }
    }

    private class ViewHolder {
        TextView textView;
        ImageView imageView;

        public ViewHolder(View view) {
            textView = (TextView) view.findViewById(R.id.two_way_view_item_tv_position);
            imageView = (ImageView) view.findViewById(R.id.two_way_view_item_iv_picture);
        }
    }
}
