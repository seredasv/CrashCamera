package com.sereda.crashcamera.app.adapters;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import com.sereda.crashcamera.app.R;
import com.sereda.crashcamera.app.utils.CropSquareTransformation;
import com.sereda.crashcamera.app.utils.DBHelper;
import com.squareup.picasso.Picasso;

import java.io.File;

public class ElementsAdapter extends SimpleCursorAdapter {
    private int layout;
    private final LayoutInflater inflater;
    private Context context;
    private Cursor cursor;
    private File dir;

    public ElementsAdapter(Context context, int layout, Cursor cursor, String[] from, int[] to, int flags) {
        super(context, layout, cursor, from, to, flags);
        inflater = LayoutInflater.from(context);
        this.layout = layout;
        this.context = context;
        this.cursor = cursor;

        dir = context.getFilesDir();
    }

    @Override
    public void changeCursor(Cursor cursor) {
        super.changeCursor(cursor);
        this.cursor = cursor;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (null == convertView) {
            convertView = inflater.inflate(layout, null);

            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (cursor.getCount() > 0 && cursor.moveToPosition(position)) {
            String fileName = cursor.getString(cursor.getColumnIndex(DBHelper.PHOTO_FILE_NAME));
            String stringUri = cursor.getString(cursor.getColumnIndex(DBHelper.PHOTO_URI));
            Uri uri = Uri.parse(stringUri);

            File file = new File(dir, fileName);
            if (file.exists()) {
                Picasso.with(context).load(uri).placeholder(R.drawable.image_view_empty_photo)
                        .resize(500, 500).into(holder.imageView);
            } else {
                Picasso.with(context).load(R.drawable.image_view_empty_photo).resize(500, 500).into(holder.imageView);
            }
        }

        return convertView;
    }

    private class ViewHolder {
        ImageView imageView;

        public ViewHolder(View view) {
            imageView = (ImageView) view.findViewById(R.id.jazzy_iv_picture);
        }
    }
}
