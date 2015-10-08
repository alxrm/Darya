package com.rm.darya.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eowise.recyclerview.stickyheaders.StickyHeadersAdapter;
import com.rm.darya.R;
import com.rm.darya.model.Currency;
import com.rm.darya.model.Pair;

import java.util.ArrayList;

/**
 * Created by alex
 */
public class LetterHeaderAdapter implements StickyHeadersAdapter<LetterHeaderAdapter.ViewHolder> {

    private ArrayList<Pair<Currency, Boolean>> items;

    public LetterHeaderAdapter(ArrayList<Pair<Currency, Boolean>> items) {

        this.items = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        View itemView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.header_settings, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder headerViewHolder, int position) {

        Currency currency = items.get(position).getFirst();

        headerViewHolder.letter
                .setText(currency.getName().subSequence(0, 1));
    }

    @Override
    public long getHeaderId(int position) {
        return items.get(position).getFirst().getName().charAt(0);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView letter;

        public ViewHolder(View itemView) {
            super(itemView);
            letter = (TextView) itemView.findViewById(R.id.letter_header);
        }
    }
}

