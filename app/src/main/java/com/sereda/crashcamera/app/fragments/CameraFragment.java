package com.sereda.crashcamera.app.fragments;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.*;
import android.widget.Button;
import android.widget.Toast;
import com.sereda.crashcamera.app.R;
import com.sereda.crashcamera.app.adapters.PicturesAdapter;
import com.sereda.crashcamera.app.utils.DBHelper;
import org.lucasr.twowayview.TwoWayView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CameraFragment extends Fragment {
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadExecutor();
    public static boolean isUpdated = false;
    public static int updatedID;
    private static int QUANTITY_OF_PERMITTED_PHOTO = 7;
    private int id;
    private Camera camera;
    private SurfaceView surfaceView;
    private SQLiteDatabase db;
    private View view;
    private File file;
    private PicturesAdapter adapter;
    private Runnable runnable;
    private Camera.ShutterCallback shutterCallback = new Camera.ShutterCallback() {
        @Override
        public void onShutter() {
        }
    };
    private Camera.PictureCallback jpegCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            String fileName = UUID.randomUUID().toString() + ".jpg";

            boolean success = true;
            if (isUpdated) {
                QUANTITY_OF_PERMITTED_PHOTO = 8;
            } else {
                QUANTITY_OF_PERMITTED_PHOTO++;
            }

            if (QUANTITY_OF_PERMITTED_PHOTO != picturesQuantity()) {
                FileOutputStream fos = null;

                try {
                    fos = getActivity().openFileOutput(fileName, Context.MODE_PRIVATE);
                    fos.write(data);
                } catch (Exception e) {
                    e.printStackTrace();
                    success = false;
                } finally {
                    try {
                        if (null != fos) {
                            fos.close();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        success = false;
                    }
                }
            } else {
                Toast.makeText(getActivity(), getString(R.string.toast_7_pictures), Toast.LENGTH_SHORT).show();
                success = false;
            }

            if (success) {
                if (null != db && db.isOpen()) {
                    ContentValues cv = new ContentValues();
                    cv.put(DBHelper.ITEM_ID, id);
                    Cursor cursor = db.rawQuery("SELECT * FROM " + DBHelper.TABLE_ITEM + " WHERE " + DBHelper.ID
                            + " = ? ", new String[]{String.valueOf(id)});
                    if (null != cursor) {
                        if (cursor.moveToFirst()) {
                            db.update(DBHelper.TABLE_ITEM, cv, DBHelper.ID + " = ? ", new String[]{String.valueOf(id)});
                        }
                        cursor.close();
                    }
                    db.insert(DBHelper.TABLE_ITEM, null, cv);
                    cv.clear();

                    if (!isUpdated) {
                        File dir = getActivity().getFilesDir();
                        file = new File(dir, fileName);

                        rotateImage(file.toString());

                        cv.put(DBHelper.PHOTO_ITEM_ID, id);
                        cv.put(DBHelper.PHOTO_FILE_NAME, fileName);
                        cv.put(DBHelper.PHOTO_URI, String.valueOf(Uri.fromFile(file)));
                        db.insert(DBHelper.TABLE_PHOTO, null, cv);
                        cv.clear();
                        Toast.makeText(getActivity(), getString(R.string.toast_picture_saved), Toast.LENGTH_SHORT).show();

                        updateAdapter();
                    } else {
                        File dir = getActivity().getFilesDir();
                        file = new File(dir, fileName);

                        rotateImage(file.toString());

                        cv.put(DBHelper.PHOTO_ITEM_ID, id);
                        cv.put(DBHelper.PHOTO_FILE_NAME, fileName);
                        cv.put(DBHelper.PHOTO_URI, String.valueOf(Uri.fromFile(file)));
                        db.update(DBHelper.TABLE_PHOTO, cv, DBHelper.ID + " = ?", new String[]{String.valueOf(updatedID)});
                        cv.clear();
                        Toast.makeText(getActivity(), getString(R.string.toast_picture_saved), Toast.LENGTH_SHORT).show();

                        isUpdated = false;

                        updateAdapter();
                    }
                }

            } else {
                Toast.makeText(getActivity(), getString(R.string.toast_picture_not_saved), Toast.LENGTH_SHORT).show();
            }
        }
    };

    public static int getCameraPhotoOrientation(Context context, Uri imageUri, String imagePath) {
        int rotate = 0;
        try {
            context.getContentResolver().notifyChange(imageUri, null);
            File imageFile = new File(imagePath);
            ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
            switch (orientation) {
                case ExifInterface.ORIENTATION_NORMAL:
                    rotate = 180;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 0;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 270;
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rotate;
    }

    private void createAdapter(View view) {
        Cursor cursor = db.rawQuery("SELECT * FROM " + DBHelper.TABLE_PHOTO + " WHERE " + DBHelper.PHOTO_ITEM_ID
                + " = ? ORDER BY " + DBHelper.ID + " ASC", new String[]{String.valueOf(id)});

        String[] from = new String[]{DBHelper.PHOTO_URI};
        int[] to = new int[]{R.id.two_way_view_item_iv_picture};

        adapter = new PicturesAdapter(getActivity(), R.layout.item_list_two_way_view, cursor, from, to, 0);
        TwoWayView listView = (TwoWayView) view.findViewById(R.id.two_way_view_list_picture);
        listView.setAdapter(adapter);

    }

    private void updateAdapter() {
        Log.e("mylog", "update Adapter");
        if (db != null && db.isOpen()) {
            Cursor cursor = db.rawQuery("SELECT * FROM " + DBHelper.TABLE_PHOTO + " WHERE " + DBHelper.PHOTO_ITEM_ID
                    + " = ? ORDER BY " + DBHelper.ID + " ASC", new String[]{String.valueOf(id)});
            adapter.changeCursor(cursor);
        }
    }

    private int picturesQuantity() {
        SQLiteStatement sqLiteStatement = db.compileStatement("SELECT COUNT(*) FROM " + DBHelper.TABLE_PHOTO
                + " WHERE " + DBHelper.ITEM_ID + " = " + id + ";");

        return (int) sqLiteStatement.simpleQueryForLong();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            id = bundle.getInt(DBHelper.ID);
        }

        db = DBHelper.getInstance(getActivity()).getWritableDatabase();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_camera, container, false);

        Button buttonMakePicture = (Button) view.findViewById(R.id.button_make_picture);
        buttonMakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != camera) {
                    // TODO fix bug with crash (if app is open and screen is turn-off and turn-on again)
                    camera.takePicture(shutterCallback, null, jpegCallback);
                }
            }
        });
        Button buttonOpenPreview = (Button) view.findViewById(R.id.button_open_elements);
        buttonOpenPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (picturesQuantity() > 0) {
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    Fragment fragment = new ElementsFragment();

                    Bundle bundle = new Bundle();
                    bundle.putInt(DBHelper.ID, id);
                    fragment.setArguments(bundle);

                    fm.beginTransaction()
                            .replace(R.id.container, fragment)
                            .addToBackStack("ElementsFragment")
                            .commit();
                }
            }
        });

        surfaceView = (SurfaceView) view.findViewById(R.id.surfaceView);
        SurfaceHolder holder = surfaceView.getHolder();
        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (null != camera) {
                        camera.setPreviewDisplay(holder);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                if (null == camera) {
                    return;
                }

                setCameraDisplayOrientation(getActivity(), CameraInfo.CAMERA_FACING_FRONT, camera);
                Parameters parameters = camera.getParameters();
                Size size = getOptimalPreviewSize(parameters.getSupportedPreviewSizes(), width, height);
                if (null != size) {
                    parameters.setPreviewSize(size.width, size.height);
                    parameters.setPictureSize(size.width, size.height);
                }
                camera.setParameters(parameters);

                try {
                    camera.startPreview();
                } catch (Exception e) {
                    e.printStackTrace();
                    camera.release();
                    camera = null;
                }
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                if (null != camera) {
                    camera.stopPreview();
                }
            }
        });

        createAdapter(view);

        return view;
    }

    public void rotateImage(final String fileName) {
        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(fileName, bounds);

        BitmapFactory.Options opts = new BitmapFactory.Options();
        Bitmap bm = BitmapFactory.decodeFile(fileName, opts);

        int rotationAngle = getCameraPhotoOrientation(getActivity(), Uri.fromFile(file), file.toString());

        Matrix matrix = new Matrix();
        matrix.postRotate(rotationAngle, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
        Bitmap rotatedBitmap = Bitmap.createBitmap(bm, 0, 0, bounds.outWidth, bounds.outHeight, matrix, true);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        try {
            if (null != fos) {
                fos.flush();
                fos.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void setCameraDisplayOrientation(Activity activity, int cameraId, Camera camera) {
        CameraInfo info = new CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;
        } else {
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }

    protected Size getOptimalPreviewSize(List<Size> sizes, int width, int height) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) height / width;

        if (null == sizes) {
            return null;
        }

        Size optimalSize = null;

        for (Size size : sizes) {
            if (size.height != width) continue;
            double ratio = (double) size.width / size.height;
            if (ratio <= targetRatio + ASPECT_TOLERANCE && ratio >= targetRatio - ASPECT_TOLERANCE) {
                optimalSize = size;
            }
        }

        return optimalSize;
    }

    @Override
    public void onPause() {
        super.onPause();

        if (null != camera) {
            camera.release();
            camera = null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        camera = Camera.open();
    }
}
