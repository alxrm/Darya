package com.rm.darya.util.updating;

import android.support.v4.util.ArrayMap;

/**
 * Created by alex
 */
public interface Currencies {

    String USD = "USD";
    String EUR = "EUR";
    String RUB = "RUB";
    String BYR = "BYR";
    String UAH = "UAH";
    String GBP = "GBP";
    String CZK = "CZK";
    String PLN = "PLN";
    String ILS = "ILS";
    String JPY = "JPY";
    String ZWL = "ZWL";

    String USD_NAME = "United States Dollar";
    String EUR_NAME = "Euro";
    String RUB_NAME = "Russian Ruble";
    String BYR_NAME = "Belarusian Ruble";
    String UAH_NAME = "Ukrainian Hryvnia";
    String GBP_NAME = "British Pound Sterling";
    String CZK_NAME = "Czech Republic Koruna";
    String PLN_NAME = "Polish Zloty";
    String ILS_NAME = "Israeli New Shekel";
    String JPY_NAME = "Japanese Yen";
    String ZWL_NAME = "Zimbabwean Dollar";


    ArrayMap<String, String> CURRENCIES = new ArrayMap<String, String>() {{
        put(USD, USD_NAME);
        put(EUR, EUR_NAME);
        put(GBP, GBP_NAME);
        put(UAH, UAH_NAME);
        put(RUB, RUB_NAME);
        put(BYR, BYR_NAME);
        put(CZK, CZK_NAME);
        put(PLN, PLN_NAME);
        put(ILS, ILS_NAME);
        put(JPY, JPY_NAME);
        put(ZWL, ZWL_NAME);
    }};
}
