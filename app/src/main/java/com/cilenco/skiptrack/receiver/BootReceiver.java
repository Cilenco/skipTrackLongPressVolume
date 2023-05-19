package com.cilenco.skiptrack.receiver;

import static com.cilenco.skiptrack.utils.Constants.PREF_ENABLED;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import net.grandcentrix.tray.AppPreferences;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            AppPreferences prefs = new AppPreferences(context);
            boolean serviceEnabled = prefs.getBoolean(PREF_ENABLED, false);
            prefs.put(PREF_ENABLED, serviceEnabled);
        }
    }
}