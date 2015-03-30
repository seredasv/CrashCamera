package com.sereda.crashcamera.app.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    private static DBHelper instance;

    public static final String DATABASE = "crash_camera";
    public static final int VERSION = 1;
    public static final String ID = "_id";

    // photo
    public static final String TABLE_PHOTO = "table_photo";
    public static final String PHOTO_DATE = "date";
    public static final String PHOTO_FILE_NAME = "file_name";
    public static final String PHOTO_ITEM_ID = "item_id";
    public static final String PHOTO_URI = "uri";

    // item
    public static final String TABLE_ITEM = "table_item";
    public static final String ITEM_ID = "item_id";
    public static final String ITEM_NAME = "name";
    public static final String ITEM_DATE = "date";

    private DBHelper(Context context) {
        super(context, DATABASE, null, VERSION);
    }

    public static synchronized DBHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DBHelper(context.getApplicationContext());
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_ITEM + " ("
                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + ITEM_NAME + " TEXT, "
                + ITEM_ID + " INTEGER, "
                + ITEM_DATE + " INTEGER"
                + ")");
        db.execSQL("CREATE TABLE " + TABLE_PHOTO + " ("
                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + PHOTO_DATE + " INTEGER, "
                + PHOTO_FILE_NAME + " TEXT, "
                + PHOTO_ITEM_ID + " INTEGER, "
                + PHOTO_URI + " TEXT"
                + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
