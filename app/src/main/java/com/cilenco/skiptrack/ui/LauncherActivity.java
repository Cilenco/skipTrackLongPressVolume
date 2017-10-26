package com.cilenco.skiptrack.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.preference.PreferenceManager;
import android.content.SharedPreferences;

public class LauncherActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        int permission = checkSelfPermission(Manifest.permission.SET_VOLUME_KEY_LONG_PRESS_LISTENER);
        Log.d("mumu", "onCreate -> permission = " + permission);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.SET_VOLUME_KEY_LONG_PRESS_LISTENER}, 1);
        } else {
            startServiceAndFinish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d("mumu", "onRequestPermissionsResult -> " + permissions[0] + ", " + grantResults[0]);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startServiceAndFinish();
        }
    }

    private void startServiceAndFinish() {
        Intent settingsIntent = new Intent(this, SettingsActivity.class);
        startActivity(settingsIntent); // Start SettingsActivity
        finish();
    }
}
