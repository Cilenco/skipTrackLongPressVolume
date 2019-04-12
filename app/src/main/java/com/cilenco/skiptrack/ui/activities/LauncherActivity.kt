package com.cilenco.skiptrack.ui.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity

// Must be splitted from MainActivity because that can be hidden
class LauncherActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val settingsIntent = Intent(this, MainActivity::class.java)
        startActivity(settingsIntent) // Start MainActivity

        finish()
    }

}
