package com.cilenco.skiptrack.ui.fragments;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.SwitchPreferenceCompat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.cilenco.skiptrack.R;
import com.cilenco.skiptrack.ui.activities.LauncherActivity;
import com.cilenco.skiptrack.utils.TrayPreferenceStore;

import static android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
import static android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_ENABLED;
import static android.content.pm.PackageManager.DONT_KILL_APP;
import static com.cilenco.skiptrack.utils.Constants.PERMISSION_REQUEST;
import static com.cilenco.skiptrack.utils.Constants.PERMISSION_REQUEST_ID;
import static com.cilenco.skiptrack.utils.Constants.PREF_ENABLED;
import static com.cilenco.skiptrack.utils.Constants.PREF_HIDE_ICON;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        TrayPreferenceStore store = new TrayPreferenceStore(getContext());
        getPreferenceManager().setPreferenceDataStore(store);

        addPreferencesFromResource(R.xml.settings);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        // FIXME inflater.inflate(R.menu.help, menu);
    }

    @Override // There is only one menu item to select
    public boolean onOptionsItemSelected(MenuItem item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setPositiveButton(android.R.string.ok, null);
        builder.setMessage(R.string.help_description);
        builder.setTitle(R.string.help_title);
        builder.setCancelable(false);

        builder.show();

        return super.onOptionsItemSelected(item);
    }

    @Override // We only have SwitchPreferences so value is always a boolean
    public boolean onPreferenceTreeClick(Preference preference) {
        if (PREF_HIDE_ICON.equals(preference.getKey())) {
            boolean checked = ((SwitchPreferenceCompat) preference).isChecked();
            PackageManager pm = getContext().getPackageManager();

            ComponentName componentName = new ComponentName(getContext(), LauncherActivity.class);
            int invisible = checked ? COMPONENT_ENABLED_STATE_DISABLED : COMPONENT_ENABLED_STATE_ENABLED;
            pm.setComponentEnabledSetting(componentName, invisible, DONT_KILL_APP);

        } else if (PREF_ENABLED.equals(preference.getKey())) {
            boolean serviceEnabled = ((SwitchPreferenceCompat) preference).isChecked();
            if (serviceEnabled) enableNotificationListener();
        }

        return super.onPreferenceTreeClick(preference);
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