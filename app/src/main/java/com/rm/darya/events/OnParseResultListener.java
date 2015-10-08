package com.rm.darya.events;

import com.rm.darya.model.Currency;

import java.util.ArrayList;

/**
 * Created by alex
 */
public interface OnParseResultListener {
    void onParseSuccessful(ArrayList<Currency> currencies);
    void onError();
}
