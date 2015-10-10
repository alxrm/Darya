package com.rm.darya.util.updating;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.rm.darya.events.OnParseResultListener;
import com.rm.darya.util.Connectivity;
import com.rm.darya.util.Prefs;
import com.rm.darya.util.TimeUtil;

/**
 * Created by alex
 */
public class CurrencyUpdateReceiver extends BroadcastReceiver implements OnParseResultListener {

    @Override
    public void onReceive(Context context, Intent intent) {

        Prefs.init(context);
        Connectivity.init(context);

        CurrencyUpdateTask updateTask =
                new CurrencyUpdateTask(this);

        if (Prefs.getSavedToday() == TimeUtil.getToday()) return;

        Log.d("CurrencyUpdateReceiver", "onReceive - Connectivity.isConnected(): "
                + Connectivity.isConnected());

        if (!isAvailable())
            Log.d("CurrencyUpdateReceiver", "Update is not available");
        else if (Connectivity.isConnected()) {

            // FIXME prevent multiple task execution
            Log.d("CurrencyUpdateReceiver", "Update available");
            updateTask.execute();

//            Log.d("CurrencyUpdateReceiver", "onReceive - updateTask.getStatus(): "
//                                + updateTask.getStatus());
//            if (updateTask.getStatus() != AsyncTask.Status.RUNNING) TODO not working
        } else {
            Log.d("CurrencyUpdateReceiver", "Not connected to the network");
        }
    }

    private boolean isAvailable() {

        return !((Connectivity.isRoaming() &&
                !Prefs.get().getBoolean(Prefs.KEY_UPDATE_WHEN_ROAMING, false))
                || !Prefs.get().getBoolean(Prefs.KEY_AUTO_UPDATE, false));
    }

    @Override
    public void onParseSuccessful() {
        Log.d("CurrencyUpdateReceiver", "onParseSuccessful");
        Prefs.saveToday();
    }

    @Override
    public void onError() {
        Log.d("CurrencyUpdateReceiver", "onError");
    }
}
