package com.rm.darya.util;

import com.rm.darya.model.Currency;
import com.rm.darya.persistence.DaryaDatabaseHelper;

import java.util.ArrayList;
import java.util.Collections;

import static com.rm.darya.Darya.app;

/**
 * Created by alex
 */
public class CurrencyUtils {

    public static void pushRates(ArrayList<Currency> currencies) {
        for (Currency c : currencies)
            DaryaDatabaseHelper.updateCurrencyRate(app(), c);
    }

    public static void selectCurrency(Currency currency) {
        DaryaDatabaseHelper.updateCurrencySelection(app(), currency);
    }

    public static ArrayList<Currency> getSelectedCurrencies() {
        ArrayList<Currency> selected = DaryaDatabaseHelper.getCurrencies(app(), true);
        Collections.sort(selected);
        return ListUtils.filter(selected, new ListUtils.Predicate<Currency>() {
            @Override
            public boolean apply(Currency type) {
                return type.isSelected();
            }
        });
    }

    public static ArrayList<Currency> getAllCurrencies() {
        ArrayList<Currency> all = DaryaDatabaseHelper.getCurrencies(app(), false);
        Collections.sort(all);
        return all;
    }

    public static ArrayList<Currency> findCurrencies(final String query) {
        return ListUtils.filter(getAllCurrencies(), new ListUtils.Predicate<Currency>() {
            @Override
            public boolean apply(Currency type) {
                return type.filter(query);
            }
        });
    }
}
