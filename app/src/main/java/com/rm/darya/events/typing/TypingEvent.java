package com.rm.darya.events.typing;

import com.rm.darya.model.Currency;
import com.rm.darya.util.CurrenciesUtil;

import java.util.ArrayList;

/**
 * Created by alex
 */
public class TypingEvent {

    private ArrayList<Currency> mCurrencies;

    public TypingEvent(ArrayList<Currency> currencies) {
        mCurrencies = currencies;
    }

    public void updateList(ArrayList<Currency> currencies) {
        mCurrencies = currencies;
        for (Currency c : mCurrencies) c.setValue("");
    }

    public void onTyping(String data, Currency currency) {
        
        for (int i = 0; i < mCurrencies.size(); i++) {

            String result =
                    CurrenciesUtil.getCalculatedResult(data, mCurrencies.get(i), currency);

            mCurrencies.get(i).setValue(result);
        }
    }
}
