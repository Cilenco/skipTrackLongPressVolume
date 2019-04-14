package com.cilenco.skiptrack.background

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

import net.grandcentrix.tray.AppPreferences

import com.cilenco.skiptrack.utils.Constants.PREF_ENABLED
import com.cilenco.skiptrack.utils.Constants.PREF_PERMISSION

class RestartReceiver: BroadcastReceiver() {

    companion object {
        private val FILTERS = listOf(
            Intent.ACTION_MY_PACKAGE_REPLACED,
            Intent.ACTION_BOOT_COMPLETED
        )
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("cilenco", "RestartReceiver called")
        if (intent?.action !in FILTERS) return

        val preferences = AppPreferences(context!!)

        val permission = preferences.getBoolean(PREF_PERMISSION, false)
        val serviceEnabled = preferences.getBoolean(PREF_ENABLED, false)

        if(serviceEnabled && permission) {
            val startIntent = Intent(context, VolumeKeyService::class.java)
            context.startService(startIntent)
        }
    }

}
