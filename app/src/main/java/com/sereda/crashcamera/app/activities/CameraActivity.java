package com.sereda.crashcamera.app.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.sereda.crashcamera.app.R;
import com.sereda.crashcamera.app.fragments.CameraFragment;
import com.sereda.crashcamera.app.utils.DBHelper;


public class CameraActivity extends ActionBarActivity {
    private final String backStackCameraFragment = "CameraFragment";
    private SharedPreferences sp;
    private int id;
    private int backStack = 1;
    private long back_pressed;

    @Override
    public void onBackPressed() {
        int count = getSupportFragmentManager().getBackStackEntryCount();

        if (backStack == count) {
            if (back_pressed + 2000 > System.currentTimeMillis()) {
                Intent startMain = new Intent(Intent.ACTION_MAIN);
                startMain.addCategory(Intent.CATEGORY_HOME);
                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(startMain);
            } else {
                Toast.makeText(getBaseContext(), getResources().getString(R.string.toast_exit),
                        Toast.LENGTH_SHORT).show();
            }
            back_pressed = System.currentTimeMillis();
        } else {
            getSupportFragmentManager().popBackStack();
        }
    }

    private int setItemID() {
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

        setContentView(R.layout.camera_activity_fragment);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.container);

        if (null == fragment) {
            fragment = new CameraFragment();

            Bundle bundle = new Bundle();
            bundle.putInt(DBHelper.ID, setItemID());
            fragment.setArguments(bundle);

            fm.beginTransaction()
                    .add(R.id.container, fragment)
                    .addToBackStack(backStackCameraFragment)
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
