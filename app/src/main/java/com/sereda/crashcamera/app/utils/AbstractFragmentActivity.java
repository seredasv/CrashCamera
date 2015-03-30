package com.sereda.crashcamera.app.utils;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import com.sereda.crashcamera.app.R;

public abstract class AbstractFragmentActivity extends FragmentActivity {
    public abstract Fragment createFragment();
    public abstract String backStackName();
    public abstract int layoutID();
    public abstract int setItemID();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layoutID());

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.container);

        if (null == fragment) {
            fragment = createFragment();

            Bundle bundle = new Bundle();
            bundle.putInt(DBHelper.ID, setItemID());
            fragment.setArguments(bundle);

            fm.beginTransaction()
                    .add(R.id.container, fragment)
                    .addToBackStack(backStackName())
                    .commit();
        }
    }
}
