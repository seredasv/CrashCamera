package com.sereda.crashcamera.app.fragments;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.*;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.sereda.crashcamera.app.R;
import com.sereda.crashcamera.app.adapters.LeftAdapter;
import com.sereda.crashcamera.app.adapters.RightAdapter;
import com.sereda.crashcamera.app.utils.DBHelper;
import com.squareup.picasso.Picasso;
import org.lucasr.twowayview.TwoWayView;

import java.io.File;

public class RightFragment extends Fragment {
    public static ImageView imageView;
    private static final int IMAGE_RESIZE = 8;
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
        View view = inflater.inflate(R.layout.fragment_right, container, false);

        imageView = (ImageView) view.findViewById(R.id.iv_main_image);
        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.ll_mask_damages);

        createAdapter(view);
        showLastItemInImageView(imageView);

        registerForContextMenu(linearLayout);

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
        Cursor cursor = db.rawQuery("SELECT * FROM " + DBHelper.TABLE_PHOTO + " WHERE " + DBHelper.PHOTO_ITEM_ID
                + " = ? ", new String[]{String.valueOf(id)});

        String[] from = new String[]{DBHelper.PHOTO_URI};
        int[] to = new int[]{R.id.two_way_view_item_iv_picture};

        RightAdapter adapter = new RightAdapter(getActivity(), R.layout.item_list_two_way_view, cursor, from, to, 0);
        TwoWayView listView = (TwoWayView) view.findViewById(R.id.two_way_view_list_picture);
        listView.setAdapter(adapter);
        adapter.changeCursor(cursor);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = this.getActivity().getMenuInflater();
        inflater.inflate(R.menu.menu_context, menu);
    }

    private void showToast(String string) {
        Toast.makeText(getActivity(), string, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.context_menu_cancel:
                showToast("Cancel pressed");
                break;
            case R.id.context_menu_scratch:
                showToast("Scratch pressed");
                break;
            case R.id.context_menu_dent:
                showToast("Dent pressed");
                break;
        }
        return super.onContextItemSelected(item);
    }
}
