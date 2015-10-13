package com.rm.darya.util.updating;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.rm.darya.util.TimeUtil;

public class  AlarmStarterReceiver extends BroadcastReceiver {
    public AlarmStarterReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        TimeUtil.setAlarm(context);
    }
}
