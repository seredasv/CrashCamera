package com.sereda.crashcamera.app.fragments;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class CameraFragment extends Fragment {
    private int id;
    private Camera camera;
    private SurfaceView surfaceView;
    private SQLiteDatabase db;
    private View view;
    private Camera.ShutterCallback shutterCallback = new Camera.ShutterCallback() {
        @Override
        public void onShutter() {
        }
    };
    private Camera.PictureCallback jpegCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            String fileName = UUID.randomUUID().toString() + ".jpg";

            FileOutputStream fos = null;
            boolean success = true;

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

                    if (7 != picturesQuantity()) {
                        File dir = getActivity().getFilesDir();
                        File file = new File(dir, fileName);

                        cv.put(DBHelper.PHOTO_ITEM_ID, id);
                        cv.put(DBHelper.PHOTO_FILE_NAME, fileName);
                        cv.put(DBHelper.PHOTO_URI, String.valueOf(Uri.fromFile(file)));
                        db.insert(DBHelper.TABLE_PHOTO, null, cv);
                        cv.clear();
                        Toast.makeText(getActivity(), getString(R.string.toast_picture_saved), Toast.LENGTH_SHORT).show();

                        createAdapter(view);
                    } else {
                        Toast.makeText(getActivity(), getString(R.string.toast_7_pictures), Toast.LENGTH_SHORT).show();
                        File dir = getActivity().getFilesDir();
                        File file = new File(dir, fileName);
                        file.delete();
                    }
                }

            } else {
                Toast.makeText(getActivity(), getString(R.string.toast_picture_not_saved), Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void createAdapter(View view) {
        Cursor cursor = db.rawQuery("SELECT * FROM " + DBHelper.TABLE_PHOTO + " WHERE " + DBHelper.PHOTO_ITEM_ID + " = ? ", new String[]{String.valueOf(id)});

        String[] from = new String[]{DBHelper.PHOTO_URI};
        int[] to = new int[]{R.id.two_way_view_item_iv_picture};

        PicturesAdapter adapter = new PicturesAdapter(getActivity(), R.layout.item_list_two_way_view, cursor, from, to, 0);
        TwoWayView listView = (TwoWayView) view.findViewById(R.id.two_way_view_list_picture);
        listView.setAdapter(adapter);
        adapter.changeCursor(cursor);
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
                if (camera != null) {
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

                setCameraDisplayOrientation(getActivity(), 1, camera);
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

        return view;
    }

    private void setCameraDisplayOrientation(Activity activity, int cameraId, android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }

    protected Size getOptimalPreviewSize(List<Size> sizes, int width, int height) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) height / width;

        if (sizes == null) return null;

        Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        for (Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - height) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - height);
            }
        }

//        if (optimalSize == null) {
//            minDiff = Double.MAX_VALUE;
//            for (Size size : sizes) {
//                if (Math.abs(size.height - height) < minDiff) {
//                    optimalSize = size;
//                    minDiff = Math.abs(size.height - height);
//                }
//            }
//        }
        return optimalSize;
    }

    private Size getBestSupportSize(List<Size> sizes, int width, int height) {
        Size bestSize = sizes.get(0);
        int largestArea = bestSize.width * bestSize.height;

        for (Size size : sizes) {
            int area = size.width * size.height;
            if (area > largestArea) {
                bestSize = size;
                largestArea = area;
            }
        }

        return bestSize;
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
