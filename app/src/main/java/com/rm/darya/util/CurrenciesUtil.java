package com.rm.darya.util;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.widget.ImageView;

import com.rm.darya.R;
import com.rm.darya.model.Currency;
import com.rm.darya.util.updating.Currencies;

/**
 * Created by alex
 */
public final class CurrenciesUtil {

//    public static final String FLOAT_PATTERN = "^[0-9]+(\\.?[0-9]*)?$";
//
//    private static final DecimalFormatSymbols sDecimalSymbols =
//            new DecimalFormatSymbols();
//    private static final DecimalFormat sDecimalFormat =
//            new DecimalFormat();
//
    private static final int SELECTED = Color.parseColor("#ff00E5FF");
    private static final int UNSELECTED_ICON = Color.parseColor("#ffefefef");
    private static final int BG_UNSELECTED = R.drawable.unselected_currency_bg;

//    static {
//        sDecimalSymbols.setDecimalSeparator('.');
//        sDecimalSymbols.setGroupingSeparator(',');
//
//        sDecimalFormat.setGroupingSize(3);
//        sDecimalFormat.applyPattern("#,##0.##");
//        sDecimalFormat.setDecimalFormatSymbols(sDecimalSymbols);
//    }
//
//    private CurrenciesUtil() {}
//
//    public static String getCalculatedResult(String inputtingData,
//                                             Currency countable, Currency inputting) {
//
//        inputtingData = prepare(inputtingData);
//
//        if (!inputtingData.matches(FLOAT_PATTERN)) return inputtingData;
//
//        float value = Float.parseFloat(inputtingData);
//        float resulting = value * (inputting.getRate() / countable.getRate());
//
//        return sDecimalFormat.format(resulting);
//    }
//
//    public static String prepare(String inp) {
//        return TextUtils.isEmpty(inp) ? inp : inp.replaceAll(",", "");
//    }

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
