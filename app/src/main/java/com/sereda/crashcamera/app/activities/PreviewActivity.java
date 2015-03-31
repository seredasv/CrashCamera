package com.sereda.crashcamera.app.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import com.sereda.crashcamera.app.R;
import com.sereda.crashcamera.app.fragments.LeftFragment;
import com.sereda.crashcamera.app.fragments.RightFragment;
import com.sereda.crashcamera.app.utils.DBHelper;

public class PreviewActivity extends ActionBarActivity {
    private int id;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getIntent().getExtras();
        if (null != bundle) {
            id = bundle.getInt(DBHelper.ID);
        }

        setContentView(R.layout.preview_activity_fragment);

        openLeftFragment();
        openRightFragment();
    }

    private void openLeftFragment() {
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.left_fragment);

        if (null == fragment) {
            fragment = new LeftFragment();

            Bundle bundle = new Bundle();
            bundle.putInt(DBHelper.ID, id);
            fragment.setArguments(bundle);

            fm.beginTransaction()
                    .add(R.id.left_fragment, fragment)
                    .commit();
        } else {
            fragment = new LeftFragment();

            Bundle bundle = new Bundle();
            bundle.putInt(DBHelper.ID, id);
            fragment.setArguments(bundle);

            fm.beginTransaction()
                    .replace(R.id.left_fragment, fragment)
                    .commit();
        }
    }

    private void openRightFragment() {
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.right_fragment);

        if (null == fragment) {
            fragment = new RightFragment();

            Bundle bundle = new Bundle();
            bundle.putInt(DBHelper.ID, id);
            fragment.setArguments(bundle);

            fm.beginTransaction()
                    .add(R.id.right_fragment, fragment)
                    .commit();
        } else {
            fragment = new RightFragment();

            Bundle bundle = new Bundle();
            bundle.putInt(DBHelper.ID, id);
            fragment.setArguments(bundle);

            fm.beginTransaction()
                    .replace(R.id.right_fragment, fragment)
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
