package com.rm.darya.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.rm.darya.adapter.ChooserListAdapter;
import com.rm.darya.R;
import com.rm.darya.model.Currency;
import com.rm.darya.model.Pair;
import com.rm.darya.util.Prefs;
import com.rm.darya.util.base.BaseActivity;
import com.rm.darya.util.KeyBoardUtil;
import com.rm.darya.util.ListUtils;
import com.rm.darya.util.view.SearchViewHacker;

import java.util.ArrayList;

import static com.rm.darya.util.CurrenciesUtil.STATE_PREFIX;
import static com.rm.darya.util.CurrenciesUtil.getAllCurrencies;

public class SearchActivity extends BaseActivity implements ChooserListAdapter.OnItemSelectedListener {

    private SearchView mSearchView;
    private RecyclerView mSearchResults;
    private View mNoResultsView;
    private ChooserListAdapter mResultsAdapter;
    private ArrayList<Pair<Currency, Boolean>> mChoosable;

    public static void start(Context context) {
        Intent starter = new Intent(context, SearchActivity.class);
        context.startActivity(starter);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(false);

        mSearchResults = (RecyclerView) findViewById(R.id.search_results);
        mNoResultsView = findViewById(R.id.no_results_view);

        mChoosable = getAllCurrencies();
        mResultsAdapter = new ChooserListAdapter(mChoosable, false);
        mResultsAdapter.setOnItemSelectedListener(this);
        mSearchResults.setLayoutManager(new LinearLayoutManager(this));
        mSearchResults.setAdapter(mResultsAdapter);
        showListIsEmpty(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem searchMenuItem = menu.getItem(0);

        mSearchView = (SearchView) searchMenuItem.getActionView();
        mSearchView.setIconified(false);
        mSearchView.setQueryHint("Search");

        int searchPlateId = mSearchView
                .getContext()
                .getResources()
                .getIdentifier("android:id/search_plate", null, null);

        View searchPlate = mSearchView.findViewById(searchPlateId);

        if (searchPlate != null) {

            int searchTextId = searchPlate
                    .getContext()
                    .getResources()
                    .getIdentifier("android:id/search_src_text", null, null);

            TextView searchText = (TextView) searchPlate.findViewById(searchTextId);

            if (searchText != null) {

                searchText.setTextColor(Color.WHITE);
                searchText.setHintTextColor(Color.WHITE);
//                searchText.setHint("Search");
            }
        }

        SearchViewHacker.setCloseIcon(mSearchView, R.drawable.bar_clear_search);

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String s) {

                KeyBoardUtil.hide(mSearchView, SearchActivity.this);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {

                if (query.equals("")) {
                    SearchViewHacker.disableCloseButton(mSearchView);
                    mChoosable = getAllCurrencies();
                } else {
                    SearchViewHacker.setCloseIcon(mSearchView, R.drawable.bar_clear_search);
                    mChoosable = findCurrencies(query);
                }

                mResultsAdapter.updateDataSet(mChoosable);
                showListIsEmpty(mChoosable.isEmpty());
                return false;
            }
        });

        mSearchView.requestFocusFromTouch();
        SearchViewHacker.disableCloseButton(mSearchView);
        InputMethodManager inputMethodManager =
                (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        return true;
    }

    private ArrayList<Pair<Currency, Boolean>> findCurrencies(final String query) {
        return ListUtils.filter(getAllCurrencies(),
                new ListUtils.Predicate<Pair<Currency, Boolean>>() {
                    @Override
                    public boolean apply(Pair<Currency, Boolean> type) {
                        return type.getFirst().filter(query);
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {

            case android.R.id.home:
                KeyBoardUtil.hide(mSearchView, this);
                onBackPressed();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showListIsEmpty(boolean isEmpty) {
        mNoResultsView.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        mSearchResults.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onItemSelected(boolean isSelected, int position) {
        if (position < mChoosable.size())
            Prefs.put(STATE_PREFIX + mChoosable.get(position).getFirst().getCode(), isSelected);
    }
}
