package com.sereda.crashcamera.app.activities;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;
import com.sereda.crashcamera.app.R;
import com.sereda.crashcamera.app.fragments.CameraFragment;
import com.sereda.crashcamera.app.utils.AbstractFragmentActivity;
import com.sereda.crashcamera.app.utils.DBHelper;


public class CameraActivity extends AbstractFragmentActivity {
    private SharedPreferences sp;
    private int id;

    @Override
    public Fragment createFragment() {
        return new CameraFragment();
    }

    @Override
    public String backStackName() {
        return "CameraFragment";
    }

    @Override
    public int layoutID() {
        return R.layout.activity_fragment;
    }

    @Override
    public int setItemID() {
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        if (sp.contains(DBHelper.ID)) {
            id = sp.getInt(DBHelper.ID, 0);
        } else {
            id = 0;
        }

        Editor editor = sp.edit();
        editor.putInt(DBHelper.ID, id + 1);
        editor.apply();

        return id;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
