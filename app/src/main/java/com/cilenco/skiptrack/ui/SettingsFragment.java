package com.cilenco.skiptrack.ui;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.widget.Toast;

import com.cilenco.skiptrack.R;
import com.cilenco.skiptrack.services.VolumeKeyService;

import static android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
import static android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_ENABLED;
import static android.content.pm.PackageManager.DONT_KILL_APP;

public class SettingsFragment extends PreferenceFragment {

    private static final String REQUEST = "enabled_notification_listeners";
    private static final int REQUEST_CODE = 0;

    private Intent mStartServiceIntent;
    private String PREF_KEY_HIDE_ICON;
    private String PREF_KEY_ENABLE_SERVICE;
    private String PREF_KEY_ENABLE_SCREEN_ON;
    private String PREF_KEY_ENABLE_MEDIA_NOT_PLAYING;
    private String PREF_KEY_ENABLE_DEBUG;

    private boolean mEnableService, mEnableScreenOn, mEnableMediaNotPlaying, mEnableDebug;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);

        PREF_KEY_HIDE_ICON = getString(R.string.pref_hideIcon);
        PREF_KEY_ENABLE_SERVICE = getString(R.string.pref_service);
        PREF_KEY_ENABLE_SCREEN_ON = getString(R.string.pref_enable_screen_on);
        PREF_KEY_ENABLE_MEDIA_NOT_PLAYING = getString(R.string.pref_enable_media_not_playing);
        PREF_KEY_ENABLE_DEBUG = getString(R.string.pref_enable_debug);

        init();
    }

    @Override // Set for a SwitchPreference so value is always a boolean
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (PREF_KEY_HIDE_ICON.equals(preference.getKey())) {
            boolean checked = ((SwitchPreference) preference).isChecked();
            PackageManager manager = getContext().getPackageManager();
            ComponentName componentName = new ComponentName(getContext(), LauncherActivity.class);
            int invisible = checked ? COMPONENT_ENABLED_STATE_DISABLED : COMPONENT_ENABLED_STATE_ENABLED;
            manager.setComponentEnabledSetting(componentName, invisible, DONT_KILL_APP);
            return true;
        } else if (PREF_KEY_ENABLE_SERVICE.equals(preference.getKey())) {
            mEnableService = ((SwitchPreference) preference).isChecked();
            findPreference(PREF_KEY_ENABLE_SCREEN_ON).setEnabled(mEnableService);
            findPreference(PREF_KEY_ENABLE_MEDIA_NOT_PLAYING).setEnabled(mEnableService);
            if (mEnableService) {
                enableServiceWithNotificationListener();
            }
        } else if (PREF_KEY_ENABLE_SCREEN_ON.equals(preference.getKey())) {
            mEnableScreenOn = ((SwitchPreference) preference).isChecked();
        } else if (PREF_KEY_ENABLE_MEDIA_NOT_PLAYING.equals(preference.getKey())) {
            mEnableMediaNotPlaying = ((SwitchPreference) preference).isChecked();
        } else if (PREF_KEY_ENABLE_DEBUG.equals(preference.getKey())) {
            mEnableDebug = ((SwitchPreference) preference).isChecked();
        }
        startServiceWithCommand();
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    private void init() {
        mEnableService = ((SwitchPreference) findPreference(PREF_KEY_ENABLE_SERVICE)).isChecked();
        SwitchPreference screen = (SwitchPreference) findPreference(PREF_KEY_ENABLE_SCREEN_ON);
        mEnableScreenOn = screen.isChecked();
        screen.setEnabled(mEnableService);
        SwitchPreference media = (SwitchPreference) findPreference(PREF_KEY_ENABLE_MEDIA_NOT_PLAYING);
        mEnableMediaNotPlaying = media.isChecked();
        media.setEnabled(mEnableService);
        startServiceWithCommand();
    }

    private void startServiceWithCommand() {
        final Context context = getContext();
        if (mStartServiceIntent == null) {
            mStartServiceIntent = new Intent(context, VolumeKeyService.class);
            mStartServiceIntent.putExtra("PERMISSION", true);
        }
        mStartServiceIntent.putExtra("ENABLED", mEnableService);
        mStartServiceIntent.putExtra("SCREEN_ON", mEnableScreenOn);
        mStartServiceIntent.putExtra("MEDIA_NOT_PLAYING", mEnableMediaNotPlaying);
        mStartServiceIntent.putExtra("DEBUG",mEnableDebug);
        context.startService(mStartServiceIntent);
        if(mEnableDebug){
            Toast.makeText(getContext(),getString(R.string.msg_start_service),Toast.LENGTH_SHORT).show();
        }
    }

    private void enableServiceWithNotificationListener() {
        ContentResolver resolver = getContext().getContentResolver();
        String listeners = Settings.Secure.getString(resolver, REQUEST);
        if (listeners == null || !listeners.contains(getContext().getPackageName())) {
            if(mEnableDebug){
                Toast.makeText(getContext(),getString(R.string.msg_jump_to_settings),Toast.LENGTH_SHORT).show();
            }
            Intent requestIntent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
            requestIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivityForResult(requestIntent,REQUEST_CODE);
        }
    }

}