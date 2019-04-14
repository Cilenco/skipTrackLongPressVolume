package com.cilenco.skiptrack.utils

import android.content.Context
import android.support.v7.preference.PreferenceDataStore

import net.grandcentrix.tray.AppPreferences

class TrayPreferenceStore(context: Context) : PreferenceDataStore() {
    private val prefs: AppPreferences = AppPreferences(context)

    override fun getBoolean(key: String?, defValue: Boolean): Boolean {
        return prefs.getBoolean(key!!, defValue)
    }

    override fun putBoolean(key: String?, value: Boolean) {
        prefs.put(key!!, value)
    }
}
