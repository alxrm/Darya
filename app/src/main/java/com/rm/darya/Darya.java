package com.rm.darya;

import android.app.Application;

import com.rm.darya.util.Connectivity;
import com.rm.darya.util.Dimen;
import com.rm.darya.util.Prefs;

/**
 * Created by alex
 */
public class Darya extends Application {

    private static Darya sApp;

    @Override
    public void onCreate() {
        super.onCreate();
        sApp = this;
        Prefs.init(this);
        Dimen.init(this);
        Connectivity.init(this);
    }

    public static Darya app() {
        return sApp;
    }
}
