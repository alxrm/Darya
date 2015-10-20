package com.rm.darya.util.updating;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.rm.darya.events.OnParseResultListener;
import com.rm.darya.util.Prefs;
import com.rm.darya.util.TimeUtil;

import static com.rm.darya.util.Connectivity.init;
import static com.rm.darya.util.Connectivity.isAllowed;
import static com.rm.darya.util.Connectivity.isConnected;

/**
 * Created by alex
 */
public class CurrencyUpdateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("CurrencyUpdateReceiver", "onReceive");

        Prefs.init(context);
        init(context);

        if (Prefs.getSavedToday() == TimeUtil.getToday()) return;

        if (!isAllowed())
            Log.d("CurrencyUpdateReceiver", "Update is not allowed");
        else if (isConnected())
            if (TimeUtil.getWeekAfter(Prefs.getLastUpdateAll()) < TimeUtil.getToday()) {
                CurrencyUpdateTask.updateSelected(new OnParseResultListener() {
                    @Override
                    public void onParseSuccessful() {
                        Log.d("CurrencyUpdateReceiver", "onParseSuccessful");
                        Prefs.saveToday();
                    }

                    @Override
                    public void onError() {
                        Log.d("CurrencyUpdateReceiver", "onError");
                    }
                });
            } else {
                CurrencyUpdateTask.updateAll(new OnParseResultListener() {
                    @Override
                    public void onParseSuccessful() {
                        Log.d("CurrencyUpdateReceiver", "onParseSuccessful");
                        Prefs.saveLastUpdateAll();
                        Prefs.saveToday();
                    }

                    @Override
                    public void onError() {
                        Log.d("CurrencyUpdateReceiver", "onError");
                    }
                });
            }
        else
            Log.d("CurrencyUpdateReceiver", "Not connected to the network");
    }
}
