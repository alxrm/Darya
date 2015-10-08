package com.rm.darya.util;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.text.TextUtils;
import android.widget.ImageView;

import com.rm.darya.R;
import com.rm.darya.model.Currency;
import com.rm.darya.model.Pair;
import com.rm.darya.util.updating.Currencies;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by alex
 */
public final class CurrenciesUtil {

    public static final String FLOAT_PATTERN = "^[0-9]+(\\.?[0-9]*)?$";
    public static final String STATE_PREFIX = "state_";

    private static final DecimalFormatSymbols sDecimalSymbols =
            new DecimalFormatSymbols();
    private static final DecimalFormat sDecimalFormat =
            new DecimalFormat();

    private static final int SELECTED = Color.parseColor("#ff00E5FF");
    private static final int UNSELECTED_ICON = Color.parseColor("#ffefefef");
    private static final int BG_UNSELECTED = R.drawable.unselected_currency_bg;

    static {
        sDecimalSymbols.setDecimalSeparator('.');
        sDecimalSymbols.setGroupingSeparator(',');

        sDecimalFormat.setGroupingSize(3);
        sDecimalFormat.applyPattern("#,##0.##");
        sDecimalFormat.setDecimalFormatSymbols(sDecimalSymbols);
    }

    private CurrenciesUtil() {}

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

    public static void saveCurrencies(ArrayList<Currency> currencies) {

        for (Currency c : currencies)
            Prefs.put(c.getCode(), c.getRate());
    }

    @SuppressWarnings("ConstantConditions")
    public static ArrayList<Currency> getSavedCurrencies() {

        ArrayList<Currency> saved = new ArrayList<>();

        for (int i = 0; i < Currencies.CURRENCIES.size(); i++) {

            String code = Currencies.CURRENCIES.keyAt(i);
            String name = Currencies.CURRENCIES.valueAt(i);

            if (!Prefs.get().getBoolean(STATE_PREFIX + code, false)) continue;

            Currency c = new Currency();
            c.setCode(code);
            c.setName(name);
            c.setRate(Prefs.get().getFloat(code, 0));

            saved.add(c);
        }

        return saved;
    }

    public static ArrayList<Pair<Currency, Boolean>> getAllCurrencies() {
        ArrayList<Pair<Currency, Boolean>> result = new ArrayList<>();

        for (int i = 0; i < Currencies.CURRENCIES.size(); i++) {

            String code = Currencies.CURRENCIES.keyAt(i);
            String name = Currencies.CURRENCIES.valueAt(i);

            boolean selection = Prefs.get().getBoolean(STATE_PREFIX + code, false);

            Currency currency = new Currency();
            currency.setName(name);
            currency.setCode(code);
            currency.setRate(Prefs.get().getFloat(code, 0));

            result.add(Pair.create(currency, selection));
        }

        Collections.sort(result, new Comparator<Pair<Currency, Boolean>>() {
            @Override
            public int compare(Pair<Currency, Boolean> lhs, Pair<Currency, Boolean> rhs) {
                return lhs.getFirst().compareTo(rhs.getFirst());
            }
        });


        return result;
    }

    public static void setIcon(ImageView v, Currency c) {

        int currencyIcon;

        switch (c.getCode()) {
            case Currencies.USD:
                currencyIcon = R.drawable.ic_dollar;
                break;
            case Currencies.EUR:
                currencyIcon = R.drawable.ic_euro;
                break;
            case Currencies.GBP:
                currencyIcon = R.drawable.ic_pound;
                break;
            case Currencies.RUB:
                currencyIcon = R.drawable.ic_ruble;
                break;
            case Currencies.BYR:
                currencyIcon = R.drawable.ic_belarus;
                break;
            case Currencies.UAH:
                currencyIcon = R.drawable.ic_ukraine;
                break;
            default:
                currencyIcon = R.drawable.ic_turkey;
                break;
        }

        v.setImageResource(currencyIcon);
    }

    public static void setSelectedIcon(boolean isSelected, ImageView view) {

        int drawableColor = isSelected ? SELECTED : UNSELECTED_ICON;

        view.setBackgroundResource(BG_UNSELECTED);
        view.getDrawable().mutate().setColorFilter(drawableColor, PorterDuff.Mode.MULTIPLY);
    }

}
