package com.rm.darya.adapter;

import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.rm.darya.R;
import com.rm.darya.model.Currency;
import com.rm.darya.events.typing.TextWatcherAdapter;
import com.rm.darya.events.typing.TypingEvent;

import java.util.ArrayList;
import java.util.Collections;

import static com.rm.darya.util.CurrencyUtils.Parser.FLOAT_PATTERN;
import static com.rm.darya.util.CurrencyUtils.Parser.prepare;

/**
 * Created by alex
 */
public class CurrencyListAdapter extends RecyclerView.Adapter<CurrencyListAdapter.ViewHolder> {

    private ArrayList<Currency> mCurrencyList;
    private TypingEvent mBroadcast;
    private TextWatcherAdapter mWatcher;

    public CurrencyListAdapter(ArrayList<Currency> currencyList) {

        mBroadcast = new TypingEvent(currencyList);
        updateDataSet(currencyList);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_currency, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        Currency currency = mCurrencyList.get(position);

        holder.mInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (hasFocus) {

                    String inputtingValue = holder.mInput.getText().toString();

                    if (!prepare(inputtingValue).matches(FLOAT_PATTERN)
                            && !TextUtils.isEmpty(inputtingValue)) {
                        holder.mInput.setText("");
                    }

                    mWatcher = new TextWatcherAdapter(holder.getLayoutPosition()) {
                        @Override
                        protected void onTextInput(String s, int pos) {
                            mBroadcast.onTyping(s, mCurrencyList.get(pos));
                            notifyAllBut(pos);
                        }
                    };

                    holder.mInput.addTextChangedListener(mWatcher);

                } else {
                    holder.mInput.removeTextChangedListener(mWatcher);
                    mWatcher = null;
                }
            }
        });

        holder.bind(currency);
    }

    private void notifyAllBut(int pos) {
        if (pos == 0) {
            notifyItemRangeChanged(1, getItemCount());
        } else if (pos == getItemCount()-1) {
            notifyItemRangeChanged(0, getItemCount()-1);
        } else {
            notifyItemRangeChanged(0, pos);
            notifyItemRangeChanged(pos+1, getItemCount()-1);
        }
    }

    @Override
    public int getItemCount() {
        return mCurrencyList.size();
    }

    public void updateDataSet(ArrayList<Currency> currencies) {
        Collections.sort(currencies);
        mCurrencyList = currencies;
        mBroadcast.updateList(currencies);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        EditText mInput;
        TextInputLayout mHint;

        public ViewHolder(View itemView) {
            super(itemView);

            mInput = (EditText) itemView.findViewById(R.id.currency_input);
            mHint = (TextInputLayout) itemView.findViewById(R.id.currency_hint);
        }

        public void bind(Currency c) {
            mInput.setText(c.getValue());
            mHint.setHint(c.getName());
        }
    }
}