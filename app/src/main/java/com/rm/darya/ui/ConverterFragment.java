package com.rm.darya.ui;


import android.os.Bundle;
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
import com.rm.darya.util.Connectivity;
import com.rm.darya.util.CurrencyUtils;
import com.rm.darya.util.Prefs;
import com.rm.darya.util.base.BaseFragment;
import com.rm.darya.util.updating.CurrencyUpdateTask;

import java.util.ArrayList;

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

        if (!mCurrencies.isEmpty()) {
            showListIsEmpty(false);
            initializeCurrenciesIfNeeded();
        } else
            showListIsEmpty(true);

    }

    @Override
    public void onResume() {
        super.onResume();
        updateList();
    }

    void updateList() {
        mCurrencies = CurrencyUtils.getSelectedCurrencies();
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

    private void initializeCurrenciesIfNeeded() {

        if (mCurrencies.get(0).getRate() == 0) {

            mLocalHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mRefreshLayout.setRefreshing(true);
                    updateCurrencies();
                }
            }, 100);

        } else if (getSavedToday() < getToday()) {

            mLocalHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Snackbar.make(
                            mRootView,
                            "Последнее обновление: " + getDay(getSavedToday()),
                            Snackbar.LENGTH_LONG)
                            .show();
                }
            }, 300);
        }
    }

    private void updateCurrencies() {

        if (Connectivity.isRoaming() &&
                !Prefs.get().getBoolean(Prefs.KEY_UPDATE_WHEN_ROAMING, false)) {

            Snackbar.make(
                    mRootView,
                    "Обновление в роуминге отключено",
                    Snackbar.LENGTH_LONG
            ).show();

            mRefreshLayout.setRefreshing(false);
            mRefreshLayout.setEnabled(true);

        } else {
            mUpdateTask = new CurrencyUpdateTask(this);
            mUpdateTask.execute();
        }
    }

    @Override
    public void onRefresh() {
        mRefreshLayout.setEnabled(false);
        updateCurrencies();
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
            Snackbar.make(mRootView, "Данные обновлены", Snackbar.LENGTH_LONG).show();

            mRefreshLayout.setRefreshing(false);
            mRefreshLayout.setEnabled(true);
            mCurrencyAdapter.updateDataSet(currencies);
//            mCurrencyListView.setAdapter(mCurrencyAdapter);
        }

        Prefs.saveToday();
    }

    @Override
    public void onError() {

        if (getActivity() == null) return;

        Snackbar.make(mRootView, "Невозможно обновить данные", Snackbar.LENGTH_SHORT)
                .setAction("Заново", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        mLocalHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mRefreshLayout.setRefreshing(true);
                                updateCurrencies();
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

