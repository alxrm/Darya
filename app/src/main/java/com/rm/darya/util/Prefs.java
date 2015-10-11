package com.rm.darya.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by alex
 */
public final class Prefs {

    public static final String KEY_AUTO_UPDATE = "auto_update";
    public static final String KEY_UPDATE_WHEN_ROAMING = "roaming_update";
    private static final String KEY_LAST_UPDATE_DAY = "last_update";
    private static final String KEY_INITIALIZED = "initialized";

    private static final String PREF_MAIN_NAME = "currency_preferences";
    private static final String TAG = "Prefs";

    private static SharedPreferences sPreferences;
    private static SharedPreferences.Editor sEditor;

    public static void init(Context context) {

        sPreferences        = context.getSharedPreferences(PREF_MAIN_NAME, Context.MODE_PRIVATE);
        sEditor             = sPreferences.edit();
    }

    //region Pref methods
    public static <T> void put(String key, T value) {

        if (value instanceof Integer) {

            sEditor.putInt(key, (Integer) value);
        }

        else if (value instanceof Long) {

            sEditor.putLong(key, (Long) value);
        }

        else if (value instanceof String) {

            sEditor.putString(key, (String) value);
        }

        else if (value instanceof Float) {

            sEditor.putFloat(key, (Float) value);
        }

        else if (value instanceof Boolean) {

            sEditor.putBoolean(key, (Boolean) value);
        }

        Log.d(TAG, "put: key(" + key + ") value(" + value + ")");
        sEditor.commit();
    }

    public static SharedPreferences get() {
        return sPreferences;
    }
    //endregion

    public static long getSavedToday() {

        return sPreferences.getLong(KEY_LAST_UPDATE_DAY, 0);
    }

    public static void saveToday() {

        put(KEY_LAST_UPDATE_DAY, TimeUtil.getToday());
    }

    public static boolean isRatesInitialized() {
        return sPreferences.getBoolean(KEY_INITIALIZED, false);
    }

    public static void setRatesInitialized() {
        sEditor.putBoolean(KEY_INITIALIZED, true);
    }
}