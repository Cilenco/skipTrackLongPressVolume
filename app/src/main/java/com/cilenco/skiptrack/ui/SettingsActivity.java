package com.cilenco.skiptrack.ui;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;

import com.cilenco.skiptrack.R;

public class SettingsActivity extends Activity {
    private static final String REQUEST = "enabled_notification_listeners";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        ContentResolver resolver = getContentResolver();
        String listeners = Settings.Secure.getString(resolver, REQUEST);

        if (listeners == null || !listeners.contains(getPackageName())) {
            Intent requestIntent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
            requestIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(requestIntent);
        }
    }
}