package com.cilenco.skiptrack.ui.fragments

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.support.v7.app.AlertDialog
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceFragmentCompat
import android.support.v7.preference.SwitchPreferenceCompat
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem

import com.cilenco.skiptrack.R
import com.cilenco.skiptrack.ui.activities.LauncherActivity
import com.cilenco.skiptrack.utils.TrayPreferenceStore

import android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_DISABLED
import android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_ENABLED
import android.content.pm.PackageManager.DONT_KILL_APP

import android.net.Uri
import android.os.PowerManager

import com.cilenco.skiptrack.utils.Constants.PREF_ENABLED
import com.cilenco.skiptrack.utils.Constants.PREF_HIDE_ICON

class SettingsFragment : PreferenceFragmentCompat() {

    private val store: TrayPreferenceStore by lazy {
        TrayPreferenceStore(context!!)
    }

    companion object {
        private const val BATTERY_REQUEST_ID = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.preferenceDataStore = store
        addPreferencesFromResource(R.xml.settings)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode != BATTERY_REQUEST_ID) return

        val pm = context!!.getSystemService(PowerManager::class.java)
        val packageName = context!!.packageName

        if (!pm.isIgnoringBatteryOptimizations(packageName)) {
            (findPreference(PREF_ENABLED) as SwitchPreferenceCompat).isChecked = false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        // FIXME inflater.inflate(R.menu.help, menu);
    }

    // There is only one menu item to select
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val builder = AlertDialog.Builder(context!!)

        builder.setPositiveButton(android.R.string.ok, null)
        builder.setMessage(R.string.help_description)
        builder.setTitle(R.string.help_title)
        builder.setCancelable(false)

        builder.show()

        return super.onOptionsItemSelected(item)
    }

    // We only have SwitchPreferences so value is always a boolean
    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        if (PREF_HIDE_ICON == preference.key) {
            val checked = (preference as SwitchPreferenceCompat).isChecked
            val pm = context!!.packageManager

            val componentName = ComponentName(context!!, LauncherActivity::class.java)
            val invisible = if (checked) COMPONENT_ENABLED_STATE_DISABLED else COMPONENT_ENABLED_STATE_ENABLED
            pm.setComponentEnabledSetting(componentName, invisible, DONT_KILL_APP)

        } else if (PREF_ENABLED == preference.key) {
            val serviceEnabled = (preference as SwitchPreferenceCompat).isChecked
            if (serviceEnabled) disableBatteryOptimization()
        }

        return super.onPreferenceTreeClick(preference)
    }

    private fun disableBatteryOptimization() {
        val packageName = context!!.packageName

        val pm = context!!.getSystemService(PowerManager::class.java)
        if (pm.isIgnoringBatteryOptimizations(packageName)) return

        val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
        intent.data = Uri.parse("package:$packageName")

        startActivityForResult(intent, BATTERY_REQUEST_ID)
    }
}