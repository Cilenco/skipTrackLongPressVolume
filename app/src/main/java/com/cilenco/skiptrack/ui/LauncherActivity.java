package com.cilenco.skiptrack.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class LauncherActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent settingsIntent = new Intent(this, SettingsActivity.class);
        startActivity(settingsIntent); // Start SettingsActivity

        finish();
    }
}