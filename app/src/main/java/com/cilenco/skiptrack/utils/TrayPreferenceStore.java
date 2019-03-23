package com.cilenco.skiptrack.utils;

import android.content.Context;
import android.support.v7.preference.PreferenceDataStore;

import net.grandcentrix.tray.AppPreferences;

public class TrayPreferenceStore extends PreferenceDataStore {
    private final AppPreferences prefs;

    public TrayPreferenceStore(Context context) {
        prefs = new AppPreferences(context);
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        return prefs.getBoolean(key, defValue);
    }

    @Override
    public void putBoolean(String key, boolean value) {
        prefs.put(key, value);
    }
}
