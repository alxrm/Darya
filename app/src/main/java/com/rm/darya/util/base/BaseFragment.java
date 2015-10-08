package com.rm.darya.util.base;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * Created by alex
 */
public class BaseFragment extends Fragment implements ViewPager.OnPageChangeListener {

    protected View mRootView;
    protected Handler mLocalHandler;
    protected BaseActivity mParent;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mParent = (BaseActivity) getActivity();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRootView = view;
        mLocalHandler = new Handler();
    }

    public View findViewById(int layoutId){
        return mRootView.findViewById(layoutId);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
