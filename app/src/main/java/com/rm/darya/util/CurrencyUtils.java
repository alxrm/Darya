package com.rm.darya.util;

import android.text.TextUtils;

import com.rm.darya.model.Currency;
import com.rm.darya.persistence.DaryaDatabaseHelper;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
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

    public static class Parser {
        public static final String FLOAT_PATTERN = "^[0-9]+(\\.?[0-9]*)?$";

        private static final DecimalFormatSymbols sDecimalSymbols =
                new DecimalFormatSymbols();
        private static final DecimalFormat sDecimalFormat =
                new DecimalFormat();

        static {
            sDecimalSymbols.setDecimalSeparator('.');
            sDecimalSymbols.setGroupingSeparator(',');

            sDecimalFormat.setGroupingSize(3);
            sDecimalFormat.applyPattern("#,##0.##");
            sDecimalFormat.setDecimalFormatSymbols(sDecimalSymbols);
        }

        private Parser() {}

        public static String getCalculatedResult(String inputtingData,
                                                 Currency countable, Currency inputting) {

            inputtingData = prepare(inputtingData);

            if (!inputtingData.matches(FLOAT_PATTERN)) return inputtingData;

            float value = Float.parseFloat(inputtingData);
            float resulting = value * (inputting.getRate() / countable.getRate());

            return sDecimalFormat.format(resulting);
        }

        public static String prepare(String inp) {
            return TextUtils.isEmpty(inp) ? inp : inp.replaceAll(",", "");
        }
    }
}
