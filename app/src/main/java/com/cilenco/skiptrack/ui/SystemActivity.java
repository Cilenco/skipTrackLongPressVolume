package com.cilenco.skiptrack.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

// Must be splitted from Launcher activity because that can be hidden
public class SystemActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent settingsIntent = new Intent(this, SettingsActivity.class);
        startActivity(settingsIntent); // Start SettingsActivity

        finish();
    }

}