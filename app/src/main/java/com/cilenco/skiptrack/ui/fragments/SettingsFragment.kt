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
import com.cilenco.skiptrack.utils.Constants.Companion.PERMISSION_REQUEST
import com.cilenco.skiptrack.utils.Constants.Companion.PERMISSION_REQUEST_ID
import com.cilenco.skiptrack.utils.Constants.Companion.PREF_ENABLED
import com.cilenco.skiptrack.utils.Constants.Companion.PREF_HIDE_ICON

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        val store = TrayPreferenceStore(context!!)
        preferenceManager.preferenceDataStore = store

        addPreferencesFromResource(R.xml.settings)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        // FIXME inflater.inflate(R.menu.help, menu);
    }

    override// There is only one menu item to select
    fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val builder = AlertDialog.Builder(context!!)

        builder.setPositiveButton(android.R.string.ok, null)
        builder.setMessage(R.string.help_description)
        builder.setTitle(R.string.help_title)
        builder.setCancelable(false)

        builder.show()

        return super.onOptionsItemSelected(item)
    }

    override// We only have SwitchPreferences so value is always a boolean
    fun onPreferenceTreeClick(preference: Preference): Boolean {
        if (PREF_HIDE_ICON == preference.key) {
            val checked = (preference as SwitchPreferenceCompat).isChecked
            val pm = context!!.packageManager

            val componentName = ComponentName(context!!, LauncherActivity::class.java)
            val invisible = if (checked) COMPONENT_ENABLED_STATE_DISABLED else COMPONENT_ENABLED_STATE_ENABLED
            pm.setComponentEnabledSetting(componentName, invisible, DONT_KILL_APP)

        } else if (PREF_ENABLED == preference.key) {
            val serviceEnabled = (preference as SwitchPreferenceCompat).isChecked
            if (serviceEnabled) enableNotificationListener()
        }

        return super.onPreferenceTreeClick(preference)
    }

    private fun enableNotificationListener() {
        val resolver = context!!.contentResolver
        val listeners = Settings.Secure.getString(resolver, PERMISSION_REQUEST)

        if (listeners == null || listeners !in (context!!.packageName)) {
            val requestIntent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
            requestIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivityForResult(requestIntent, PERMISSION_REQUEST_ID)
        }
    }
}