package com.rm.darya.ui;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

import com.eowise.recyclerview.stickyheaders.StickyHeadersBuilder;
import com.eowise.recyclerview.stickyheaders.StickyHeadersItemDecoration;
import com.rm.darya.R;
import com.rm.darya.adapter.ChooserListAdapter;
import com.rm.darya.adapter.LetterHeaderAdapter;
import com.rm.darya.events.HidingScrollListener;
import com.rm.darya.events.OnInteractionListener;
import com.rm.darya.events.OnProjectionChangeListener;
import com.rm.darya.model.Currency;
import com.rm.darya.util.CurrencyUtils;
import com.rm.darya.util.KeyBoardUtil;
import com.rm.darya.util.base.BaseFragment;

import java.util.ArrayList;

import static android.support.v4.view.ViewPager.SCROLL_STATE_IDLE;
import static android.support.v4.view.ViewPager.SCROLL_STATE_SETTLING;
import static com.rm.darya.ui.MainActivity.ID_SETTINGS;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends BaseFragment
        implements ChooserListAdapter.OnItemSelectedListener {

    private ArrayList<Currency> mChoosable;
    private ChooserListAdapter mCurrencyAdapter;
    private OnProjectionChangeListener mProjectionListener;
    private FloatingActionButton mSearchFab;
    private OnInteractionListener mListener;
    private StickyHeadersItemDecoration mLetterStickyHeader;
    private int mState = SCROLL_STATE_IDLE;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = (OnInteractionListener) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mChoosable = CurrencyUtils.getAllCurrencies();
        mCurrencyAdapter = new ChooserListAdapter(mChoosable, true);
        mCurrencyAdapter.setHasStableIds(true);
        mCurrencyAdapter.setOnItemSelectedListener(this);
        mSearchFab = (FloatingActionButton) findViewById(R.id.search_fab);
        mSearchFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onFragmentEmptyAction(ID_SETTINGS);
            }
        });

        RecyclerView chooserCurrencies = (RecyclerView) findViewById(R.id.chooser_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());

        chooserCurrencies.setLayoutManager(linearLayoutManager);
        mLetterStickyHeader = new StickyHeadersBuilder()
                .setAdapter(mCurrencyAdapter)
                .setRecyclerView(chooserCurrencies)
                .setStickyHeadersAdapter(
                        new LetterHeaderAdapter(mChoosable), true)
                .build();

        chooserCurrencies.addItemDecoration(mLetterStickyHeader);
        chooserCurrencies.setAdapter(mCurrencyAdapter);
        chooserCurrencies.addOnScrollListener(new HidingScrollListener() {
            @Override
            public void onHide() {
                setFabVisible(false);
            }

            @Override
            public void onShow() {
                setFabVisible(true);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mChoosable = CurrencyUtils.getAllCurrencies();
        if (mCurrencyAdapter != null) {
            mCurrencyAdapter.updateDataSet(mChoosable);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (state == SCROLL_STATE_IDLE && mState == SCROLL_STATE_SETTLING)
            KeyBoardUtil.hide(getActivity());
        mState = state;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        super.onPageScrolled(position, positionOffset, positionOffsetPixels);
        if (position == 1 && mSearchFab.getAlpha() < 1) {
            setFabVisible(true);
        }
        else if (position != 1 && positionOffset != 0.0F && mSearchFab.getAlpha() == 1) {
            setFabVisible(false);
        }
    }

    private void setFabVisible(final boolean fabVisible) {
        mSearchFab.animate()
                .scaleX(fabVisible ? 1.0F : 0F)
                .scaleY(fabVisible ? 1.0F : 0F)
                .alpha(fabVisible ? 1.0F : 0F)
                .setInterpolator(new DecelerateInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        mSearchFab.setClickable(fabVisible);
                    }
                })
                .setDuration(200)
                .start();
    }

    public void setOnProjectionChangeListener(OnProjectionChangeListener listener) {
        mProjectionListener = listener;
    }

    @Override
    public void onItemSelected(boolean isSelected, int position) {
        Currency currency = mChoosable.get(position);
        currency.setSelected(isSelected);
        CurrencyUtils.selectCurrency(currency);

        mProjectionListener.onProjectionChanged();
    }
}
