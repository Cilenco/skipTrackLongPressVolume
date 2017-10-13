package com.cilenco.skiptrack.ui;

import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;

import com.cilenco.skiptrack.R;

import static android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
import static android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_ENABLED;
import static android.content.pm.PackageManager.DONT_KILL_APP;

public class SettingsFragment extends PreferenceFragment implements OnPreferenceChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);

        Preference pref = findPreference(getString(R.string.pref_hideIcon));
        pref.setOnPreferenceChangeListener(this);
    }

    @Override // Set for a SwitchPreference so value is always a boolean
    public boolean onPreferenceChange(Preference preference, Object value) {
        boolean checked = (boolean) value;

        PackageManager manager = getContext().getPackageManager();
        ComponentName componentName = new ComponentName(getContext(), LauncherActivity.class);

        int invisible = checked ? COMPONENT_ENABLED_STATE_DISABLED : COMPONENT_ENABLED_STATE_ENABLED;
        manager.setComponentEnabledSetting(componentName, invisible, DONT_KILL_APP);

        return true;
    }
}