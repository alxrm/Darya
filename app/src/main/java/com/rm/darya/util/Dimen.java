package com.rm.darya.util;

import android.content.Context;

/**
 * Created by alex
 */
public class Dimen {

    private static Context sContext;

    public static void init(Context context){
        Dimen.sContext = context;
    }

    public static int dp(int dp) {
        float scale = sContext.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public static int get(int resourceId) {
        return sContext.getResources().getDimensionPixelSize(resourceId);
    }
}
