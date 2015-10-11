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
        DaryaDatabaseHelper.updateAllRates(app(), currencies);
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

    public static class ExceptedCurrencies {

        public static final String ZWD_CODE = "ZWD";
        public static final String EEK_CODE = "EEK";
        public static final String IRR_CODE = "IRR";
        public static final String VND_CODE = "VND";

        public static final String[] EXCEPTED = {
                ZWD_CODE, EEK_CODE, IRR_CODE, VND_CODE
        };

        public static final float ZWD_RATE = 0.00276319F;
        public static final float EEK_RATE = 0.085249F;
        public static final float IRR_RATE = 0.0000333800F;
        public static final float VND_RATE = 0.0000450500F;
        public static final float EMPTY_RATE = 0;

        private ExceptedCurrencies() {}

        public static float getRate(String code) {
            switch (code) {
                case ZWD_CODE: return ZWD_RATE;
                case EEK_CODE: return EEK_RATE;
                case IRR_CODE: return IRR_RATE;
                case VND_CODE: return VND_RATE;
                default: return EMPTY_RATE;
            }
        }

        public static boolean isExceptedCode(String code) {
            boolean isExcepted = false;
            for (String excCode : EXCEPTED) isExcepted |= excCode.equals(code);
            return isExcepted;
        }
    }
}
