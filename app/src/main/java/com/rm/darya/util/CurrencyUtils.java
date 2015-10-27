package com.rm.darya.util;

import android.text.TextUtils;

import com.rm.darya.model.Currency;
import com.rm.darya.persistence.DaryaDatabaseHelper;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;

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
        return DaryaDatabaseHelper.getSelectedCurrencies(app());
    }

    public static ArrayList<Currency> getAllCurrencies() {
        return DaryaDatabaseHelper.getCurrencies(app());
    }

    public static ArrayList<Currency> findCurrencies(String query) {
        String filtered =  query.replace("\"", "");
        return DaryaDatabaseHelper.findCurrencies(app(), filtered);
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

        static final String ZWD_CODE = "ZWD";
        static final String EEK_CODE = "EEK";
        static final String IRR_CODE = "IRR";
        static final String VND_CODE = "VND";
        static final String USD_CODE = "USD";

        static final String[] EXCEPTED = {
                ZWD_CODE, EEK_CODE, IRR_CODE, VND_CODE, USD_CODE
        };

        private ExceptedCurrencies() {}

        public static boolean isExceptedCode(String code) {
            boolean isExcepted = false;
            for (String excCode : EXCEPTED) isExcepted |= excCode.equals(code);
            return isExcepted;
        }
    }
}
