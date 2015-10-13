package com.rm.darya.ui;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import com.rm.darya.R;
import com.rm.darya.events.OnInteractionListener;
import com.rm.darya.util.Prefs;
import com.rm.darya.util.TimeUtil;
import com.rm.darya.util.base.BaseActivity;

import java.util.ArrayList;

import static com.rm.darya.util.Connectivity.isBackgroundUpdateAllowed;
import static com.rm.darya.util.Connectivity.isRoamingAllowed;
import static com.rm.darya.util.Prefs.KEY_AUTO_UPDATE;
import static com.rm.darya.util.Prefs.KEY_UPDATE_WHEN_ROAMING;

public class MainActivity extends BaseActivity implements OnInteractionListener {

    public static final int ID_SETTINGS = 1;

    private ViewPager mContentPager;
    private TabLayout mPagerTabs;

    private ConverterFragment mConverterFragment;
    private SettingsFragment mSettingsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TimeUtil.setAlarm(this);

        mToolbar.setTitle(R.string.app_name);
        setSupportActionBar(mToolbar);

        mPagerTabs = (TabLayout) findViewById(R.id.darya_tab_layout);

        mConverterFragment = new ConverterFragment();
        mSettingsFragment = new SettingsFragment();

        mContentPager = (ViewPager) findViewById(R.id.darya_content_viewpager);
        mContentPager.addOnPageChangeListener(mSettingsFragment);
        mContentPager.addOnPageChangeListener(mConverterFragment);
        mSettingsFragment.setOnProjectionChangeListener(mConverterFragment);

        setupViewPager(mContentPager);
        mPagerTabs.setupWithViewPager(mContentPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getSupportFragmentManager());
        adapter.addFragment(mConverterFragment, "Converter");
        adapter.addFragment(mSettingsFragment, "Settings");
        viewPager.setAdapter(adapter);
    }

    private void setUpdateWhenRoaming(boolean isAllowed) {
        Prefs.put(KEY_UPDATE_WHEN_ROAMING, isAllowed);
    }

    private void setAutoUpdate(boolean isAllowed) {
        Prefs.put(KEY_AUTO_UPDATE, isAllowed);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        MenuItem autoUpdate = menu.findItem(R.id.action_auto_update);
        MenuItem roamingUpdate = menu.findItem(R.id.action_update_roaming);

        autoUpdate.setChecked(isBackgroundUpdateAllowed());
        roamingUpdate.setChecked(isRoamingAllowed());

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        boolean isChecked = !item.isChecked();

        switch (item.getItemId()) {
            case R.id.action_auto_update: {
                item.setChecked(isChecked);
                setAutoUpdate(isChecked);
                return true;
            }
            case R.id.action_update_roaming: {
                item.setChecked(!item.isChecked());
                setUpdateWhenRoaming(isChecked);
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onFragmentEmptyAction(int fragmentId) {
        switch (fragmentId) {
            case ID_SETTINGS: {
                SearchActivity.start(this);
                break;
            }
            default: break;
        }
    }

    @Override
    public <T> void onFragmentActionWithData(T data) {

    }

    static class Adapter extends FragmentPagerAdapter {
        private final ArrayList<Fragment> mFragments = new ArrayList<>();
        private final ArrayList<String> mFragmentTitles = new ArrayList<>();

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }
}
