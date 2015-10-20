package com.rm.darya.ui;


import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.rm.darya.R;
import com.rm.darya.adapter.CurrencyListAdapter;
import com.rm.darya.events.OnParseResultListener;
import com.rm.darya.events.OnProjectionChangeListener;
import com.rm.darya.model.Currency;
import com.rm.darya.util.CurrencyUtils;
import com.rm.darya.util.Prefs;
import com.rm.darya.util.base.BaseFragment;
import com.rm.darya.util.updating.CurrencyUpdateTask;

import java.util.ArrayList;

import static com.rm.darya.util.Connectivity.isRoaming;
import static com.rm.darya.util.Connectivity.isRoamingAllowed;
import static com.rm.darya.util.Prefs.getSavedToday;
import static com.rm.darya.util.TimeUtil.getDay;
import static com.rm.darya.util.TimeUtil.getToday;

public class ConverterFragment extends BaseFragment
        implements
        SwipeRefreshLayout.OnRefreshListener,
        OnParseResultListener, OnProjectionChangeListener {

    private SwipeRefreshLayout mRefreshLayout;
    private RecyclerView mCurrencyListView;

    private CurrencyListAdapter mCurrencyAdapter;
    private LinearLayoutManager mLayoutManager;
    private ArrayList<Currency> mCurrencies = new ArrayList<>();
    private CurrencyUpdateTask mUpdateTask;
    private RelativeLayout mEmptyView;
    private CoordinatorLayout mCoordinator;

    public ConverterFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_converter, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mCoordinator = (CoordinatorLayout) findViewById(R.id.converter_coordinator);
        mEmptyView = (RelativeLayout) findViewById(R.id.empty_view);
        mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.converter_refresh_layout);
        mCurrencyListView = (RecyclerView) findViewById(R.id.currency_list);
        mCurrencyListView.setVisibility(View.GONE);

        mCurrencies = CurrencyUtils.getSelectedCurrencies();
        mCurrencyAdapter = new CurrencyListAdapter(mCurrencies);
        mLayoutManager = new LinearLayoutManager(getActivity());

        mCurrencyListView.setLayoutManager(mLayoutManager);
        mCurrencyListView.setAdapter(mCurrencyAdapter);
        mRefreshLayout.setOnRefreshListener(this);

        showListIsEmpty(mCurrencies.isEmpty());
        showLastUpdateIfNeeded();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("ConverterFragment", "onResume");
        updateList();
    }

    void updateList() {
        mCurrencies = CurrencyUtils.getSelectedCurrencies();
        showListIsEmpty(mCurrencies.isEmpty());
        if (mCurrencyAdapter != null) {
            mCurrencyAdapter.updateDataSet(mCurrencies);
            mCurrencyListView.setAdapter(mCurrencyAdapter);
        }
    }

    private void showListIsEmpty(boolean isEmpty) {
        mEmptyView.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        mCurrencyListView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        mRefreshLayout.setEnabled(!isEmpty);
    }

    private void showLastUpdateIfNeeded() {
        if (getSavedToday() < getToday()) {
            mLocalHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Snackbar.make(
                            mCoordinator,
                            "Last update: " + getDay(getSavedToday()),
                            Snackbar.LENGTH_LONG)
                            .show();
                }
            }, 300);
        }
    }

    private void updateCurrencies(boolean selected) {
        Log.d("ConverterFragment", "updateCurrencies");
        if (isRoaming() && !isRoamingAllowed()) {

            Snackbar.make(
                    mCoordinator,
                    "Update in roaming turned off",
                    Snackbar.LENGTH_LONG
            ).show();

            mRefreshLayout.setRefreshing(false);
            mRefreshLayout.setEnabled(true);

        } else {
            if (selected)
                CurrencyUpdateTask.updateSelected(this);
            else
                CurrencyUpdateTask.updateAll(this);
        }
    }

    @Override
    public void onRefresh() {
        Log.d("ConverterFragment", "onRefresh");
        mRefreshLayout.setEnabled(false);
        updateCurrencies(true);
    }

    @Override
    public void onProjectionChanged() {
        Log.d("ConverterFragment", "onProjectionChanged");
        mCurrencies = CurrencyUtils.getSelectedCurrencies();
        mCurrencyAdapter.updateDataSet(mCurrencies);
        showListIsEmpty(mCurrencies.isEmpty());
    }

    //region Update task callbacks
    @Override
    public void onParseSuccessful() {
        ArrayList<Currency> currencies = CurrencyUtils.getSelectedCurrencies();

        if (getActivity() != null) {
            Snackbar.make(mCoordinator, "Rates updated", Snackbar.LENGTH_LONG).show();

            mRefreshLayout.setRefreshing(false);
            mRefreshLayout.setEnabled(!currencies.isEmpty());
            mCurrencyAdapter.updateDataSet(currencies);
        }

        Prefs.saveToday();
    }

    @Override
    public void onError() {

        if (getActivity() == null) return;

        Snackbar.make(mCoordinator, "Update error", Snackbar.LENGTH_SHORT)
                .setAction("RETRY", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        mLocalHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mRefreshLayout.setRefreshing(true);
                                updateCurrencies(true);
                            }
                        }, 100);
                    }
                })
                .show();

        mRefreshLayout.setRefreshing(false);
        mRefreshLayout.setEnabled(true);
    }
    //endregion
}

