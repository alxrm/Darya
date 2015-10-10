package com.rm.darya.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.rm.darya.R;
import com.rm.darya.model.Currency;
import com.rm.darya.util.CurrenciesUtil;
import com.rm.darya.util.Dimen;

import java.util.ArrayList;

/**
 * Created by alex
 */
public class ChooserListAdapter extends RecyclerView.Adapter<ChooserListAdapter.ViewHolder> {

    public interface OnItemSelectedListener {
        void onItemSelected(boolean isSelected, int position);
    }

    private OnItemSelectedListener mListener;
    private boolean mHasHeaders;
    private ArrayList<Currency> mCurrencies = new ArrayList<>();

    public ChooserListAdapter(ArrayList<Currency> states, boolean hasHeaders) {
        mHasHeaders = hasHeaders;
        updateDataSet(states);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_choosable_currency, parent, false);

        ViewHolder holder = new ViewHolder(itemView);

        if (mListener != null)
            holder.setOnItemSelectedListener(mListener);

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Currency currency = mCurrencies.get(position);
        String name = currency.getName();
        String code = currency.getCode();
        boolean checked = currency.isSelected();

        holder.itemView.setPadding(
                Dimen.dp(mHasHeaders ? 56 : 8),
                Dimen.dp(0),
                Dimen.dp(8),
                Dimen.dp(0)
        );
        holder.mName.setText(name);
        holder.mCode.setText(code);
        holder.mSelectedCheck.setChecked(checked);
        CurrenciesUtil.setIcon(holder.mIcon, currency);
        CurrenciesUtil.setSelectedIcon(checked, holder.mIcon);
    }

    @Override
    public int getItemCount() {
        return mCurrencies.size();
    }

    public void updateDataSet(ArrayList<Currency> choosable) {
        mCurrencies = choosable;
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return mCurrencies.get(position).hashCode();
    }

    public void setOnItemSelectedListener(OnItemSelectedListener listener) {
        mListener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private OnItemSelectedListener mSelectedListener;
        
        private TextView mName;
        private TextView mCode;
        private ImageView mIcon;
        private CheckBox mSelectedCheck;

        public ViewHolder(View itemView) {
            super(itemView);

            mName = (TextView) itemView.findViewById(R.id.chooser_currency_name);
            mCode = (TextView) itemView.findViewById(R.id.chooser_currency_code);
            mIcon = (ImageView) itemView.findViewById(R.id.chooser_currency_icon);
            mSelectedCheck = (CheckBox) itemView.findViewById(R.id.chooser_currency_selected);

            itemView.setOnClickListener(this);
            mSelectedCheck.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (!(v instanceof CheckBox))
                mSelectedCheck.setChecked(!mSelectedCheck.isChecked());

            boolean isChecked = mSelectedCheck.isChecked();

            CurrenciesUtil.setSelectedIcon(isChecked, mIcon);

            if (mSelectedListener != null)
                this.mSelectedListener.onItemSelected(isChecked, getAdapterPosition());
        }

        public void setOnItemSelectedListener(OnItemSelectedListener listener) {
            mSelectedListener = listener;
        }
    }
}
