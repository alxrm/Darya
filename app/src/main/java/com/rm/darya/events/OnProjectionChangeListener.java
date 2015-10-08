package com.rm.darya.events;

import com.rm.darya.model.Currency;

import java.util.ArrayList;

/**
 * Created by alex
 */
public interface OnProjectionChangeListener {
    void onProjectionChanged(ArrayList<Currency> currencies);
}
