package com.cilenco.skiptrack.ui;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import com.cilenco.skiptrack.R;
import com.cilenco.skiptrack.services.VolumeKeyService;

import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import static android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
import static android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_ENABLED;
import static android.content.pm.PackageManager.DONT_KILL_APP;

import static com.cilenco.skiptrack.utils.Constants.ARG_DEBUG;
import static com.cilenco.skiptrack.utils.Constants.ARG_ENABLED;
import static com.cilenco.skiptrack.utils.Constants.ARG_PERMISSION;
import static com.cilenco.skiptrack.utils.Constants.PERMISSION_REQUEST;
import static com.cilenco.skiptrack.utils.Constants.PERMISSION_REQUEST_ID;

public class SettingsFragment extends PreferenceFragmentCompat {

    private String PREF_KEY_HIDE_ICON;
    private String PREF_KEY_SERVICE_ENABLED;
    private String PREF_KEY_DEBUG_ENABLED;

    private boolean serviceEnabled;
    private boolean debugEnabled;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.settings);

        PREF_KEY_HIDE_ICON = getString(R.string.pref_hide_icon);
        PREF_KEY_SERVICE_ENABLED = getString(R.string.pref_service);
        PREF_KEY_DEBUG_ENABLED = getString(R.string.pref_debug);

        debugEnabled = ((SwitchPreferenceCompat) findPreference(PREF_KEY_DEBUG_ENABLED)).isChecked();
        serviceEnabled = ((SwitchPreferenceCompat) findPreference(PREF_KEY_SERVICE_ENABLED)).isChecked();

        startService();
    }

    @Override // We only have SwitchPreferences so value is always a boolean
    public boolean onPreferenceTreeClick(androidx.preference.Preference preference) {
        if (PREF_KEY_HIDE_ICON.equals(preference.getKey())) {
            boolean checked = ((SwitchPreferenceCompat) preference).isChecked();
            PackageManager pm = getContext().getPackageManager();

            ComponentName componentName = new ComponentName(getContext(), LauncherActivity.class);
            int invisible = checked ? COMPONENT_ENABLED_STATE_DISABLED : COMPONENT_ENABLED_STATE_ENABLED;
            pm.setComponentEnabledSetting(componentName, invisible, DONT_KILL_APP);

        } else if (PREF_KEY_SERVICE_ENABLED.equals(preference.getKey())) {
            serviceEnabled = ((SwitchPreferenceCompat) preference).isChecked();
            if (serviceEnabled) enableNotificationListener();

        } else if (PREF_KEY_DEBUG_ENABLED.equals(preference.getKey())) {
            debugEnabled = ((SwitchPreferenceCompat) preference).isChecked();
        }

        startService();
        return super.onPreferenceTreeClick(preference);
    }

    private void startService() {
        final Context context = getContext();

        Intent mStartServiceIntent = new Intent(context, VolumeKeyService.class);

        mStartServiceIntent.putExtra(ARG_PERMISSION, true);
        mStartServiceIntent.putExtra(ARG_DEBUG, debugEnabled);
        mStartServiceIntent.putExtra(ARG_ENABLED, serviceEnabled);

        context.startService(mStartServiceIntent);

        if(debugEnabled) Toast.makeText(getContext(),getString(R.string.msg_start_service),Toast.LENGTH_SHORT).show();
    }

    private void enableNotificationListener() {
        ContentResolver resolver = getContext().getContentResolver();
        String listeners = Settings.Secure.getString(resolver, PERMISSION_REQUEST);

        if (listeners == null || !listeners.contains(getContext().getPackageName())) {
            Intent requestIntent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
            requestIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivityForResult(requestIntent, PERMISSION_REQUEST_ID);
        }
    }
}