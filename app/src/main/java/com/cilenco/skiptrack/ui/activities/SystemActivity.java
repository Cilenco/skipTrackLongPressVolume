package com.cilenco.skiptrack.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

// Must be splitted from MainActivity because that can be hidden
public class SystemActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent settingsIntent = new Intent(this, MainActivity.class);
        startActivity(settingsIntent); // Start MainActivity

        finish();
    }

}