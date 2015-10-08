package com.rm.darya.util.base;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.rm.darya.R;

/**
 * Created by alex
 */
public class BaseActivity extends AppCompatActivity {

    protected Toolbar mToolbar;

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);

        mToolbar = (Toolbar) findViewById(R.id.darya_toolbar);
        setSupportActionBar(mToolbar);
    }
}
