package com.rm.darya.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by alex
 */
public class Connectivity {

    private static ConnectivityManager sConnectivityManager;

    public static void init(Context c) {
        sConnectivityManager = (ConnectivityManager)
                c.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public static boolean isRoaming() {

        NetworkInfo activeMobileNetInfo =
                sConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        return activeMobileNetInfo != null && activeMobileNetInfo.isRoaming();
    }

    public static boolean isMobileWebConnected() {

        NetworkInfo activeMobileNetInfo =
                sConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        return activeMobileNetInfo != null && activeMobileNetInfo.isConnectedOrConnecting();
    }

    public static boolean isWifiConnected() {

        NetworkInfo activeWifiNetInfo =
                sConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        return activeWifiNetInfo != null && activeWifiNetInfo.isConnectedOrConnecting();
    }

    public static boolean isConnected() {
        return isWifiConnected() | isMobileWebConnected();
    }

    public static boolean isAllowed() {
        return (isRoaming() && isRoamingAllowed()) || isBackgroundUpdateAllowed();
    }

    public static boolean isRoamingAllowed() {
        return Prefs.get().getBoolean(Prefs.KEY_UPDATE_WHEN_ROAMING, false);
    }

    public static boolean isBackgroundUpdateAllowed() {
        return Prefs.get().getBoolean(Prefs.KEY_AUTO_UPDATE, false);
    }
}
