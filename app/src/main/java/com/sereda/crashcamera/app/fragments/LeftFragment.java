package com.sereda.crashcamera.app.fragments;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Toast;
import com.sereda.crashcamera.app.R;
import com.sereda.crashcamera.app.adapters.LeftAdapter;
import com.sereda.crashcamera.app.utils.DBHelper;
import com.squareup.picasso.Picasso;
import org.lucasr.twowayview.TwoWayView;

import java.io.File;

public class LeftFragment extends Fragment {
    private static final int IMAGE_RESIZE = 8;
    public static ImageView imageView;
    private int id;
    private SQLiteDatabase db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getArguments();
        if (null != bundle) {
            id = bundle.getInt(DBHelper.ID);
        }

        db = DBHelper.getInstance(getActivity()).getWritableDatabase();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_left, container, false);

        imageView = (ImageView) view.findViewById(R.id.iv_main_image);

        createAdapter(view);
        showLastItemInImageView(imageView);

        return view;
    }

    private void showLastItemInImageView(ImageView imageView) {
        Cursor cursor = db.rawQuery("SELECT * FROM " + DBHelper.TABLE_PHOTO + " WHERE " + DBHelper.PHOTO_ITEM_ID
                + " = ? ", new String[]{String.valueOf(id)});
        if (null != cursor) {
            if (cursor.moveToLast()) {
                String fileName = cursor.getString(cursor.getColumnIndex(DBHelper.PHOTO_FILE_NAME));
                String stringUri = cursor.getString(cursor.getColumnIndex(DBHelper.PHOTO_URI));

                Uri uri = Uri.parse(stringUri);
                File dir = getActivity().getFilesDir();
                File file = new File(dir, fileName);

                if (file.exists()) {
                    BitmapFactory.Options options = getOptions(uri);
                    Picasso.with(getActivity()).load(uri).placeholder(R.drawable.image_view_empty_photo)
                            .resize(getImageWidth(options) / IMAGE_RESIZE, getImageHeight(options) / IMAGE_RESIZE)
                            .into(imageView);
                } else {
                    Picasso.with(getActivity()).load(R.drawable.image_view_empty_photo).into(imageView);
                }
            }
            cursor.close();
        }
    }

    private BitmapFactory.Options getOptions(Uri uri) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(new File(uri.getPath()).getAbsolutePath(), options);

        return options;
    }

    private int getImageHeight(BitmapFactory.Options options) {
        return options.outHeight;
    }

    private int getImageWidth(BitmapFactory.Options options) {
        return options.outWidth;
    }

    private void createAdapter(View view) {
        final Cursor cursor = db.rawQuery("SELECT * FROM " + DBHelper.TABLE_PHOTO + " WHERE " + DBHelper.PHOTO_ITEM_ID
                + " = ? ", new String[]{String.valueOf(id)});

        String[] from = new String[]{DBHelper.PHOTO_URI};
        int[] to = new int[]{R.id.two_way_view_item_iv_picture};

        LeftAdapter adapter = new LeftAdapter(getActivity(), R.layout.item_list_two_way_view, cursor, from, to, 0);
        TwoWayView listView = (TwoWayView) view.findViewById(R.id.two_way_view_list_picture);
        listView.setAdapter(adapter);
        adapter.changeCursor(cursor);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getActivity(), "click: " + position, Toast.LENGTH_SHORT).show();
                if (cursor.moveToPosition(position)) {
                    Toast.makeText(getActivity(), cursor.getString(cursor.getColumnIndex(DBHelper.ID)), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
