package com.rm.darya.util.view;

import android.graphics.Color;
import android.widget.TextView;

import com.rm.darya.model.Currency;

/**
 * Created by alex
 */
public class TextViewSelector {

    // TODO make it as a separated custom component
    private static final int SELECTED = Color.parseColor("#ff00E5FF");
    private static final int UNSELECTED_ICON = Color.parseColor("#ffefefef");

//    public static void setIcon(ImageView v, Currency c) {
//
//        int currencyIcon;
//
//        switch (c.getCode()) {
//            case Currencies.USD:
//                currencyIcon = R.drawable.ic_dollar;
//                break;
//            case Currencies.EUR:
//                currencyIcon = R.drawable.ic_euro;
//                break;
//            case Currencies.GBP:
//                currencyIcon = R.drawable.ic_pound;
//                break;
//            case Currencies.RUB:
//                currencyIcon = R.drawable.ic_ruble;
//                break;
//            case Currencies.BYR:
//                currencyIcon = R.drawable.ic_belarus;
//                break;
//            case Currencies.UAH:
//                currencyIcon = R.drawable.ic_ukraine;
//                break;
//            default:
//                currencyIcon = R.drawable.ic_turkey;
//                break;
//        }
//
//        v.setImageResource(currencyIcon);
//    }

    public static void setIcon(Currency c, TextView view) {
        String letter;
        String[] nameWords = c.getName().split(" ");
        int wordsCount = nameWords.length;

        if (!wordIsUpperCase(nameWords[wordsCount-1]))
            letter = nameWords[wordsCount-1].substring(0, 1);
        else
            letter = nameWords[1].substring(0, 1);

        view.setText(letter);
    }

    private static boolean wordIsUpperCase(String word) {
        boolean wordIsUpperCase = true;

        for (char character : word.toCharArray())
            wordIsUpperCase &= Character.isUpperCase(character);

        return wordIsUpperCase;
    }

    public static void setSelectedIcon(boolean isSelected, TextView view) {
        int textColor = isSelected ? SELECTED : UNSELECTED_ICON;
        view.setTextColor(textColor);
    }
}
