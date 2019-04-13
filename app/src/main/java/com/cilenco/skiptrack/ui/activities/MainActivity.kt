package com.cilenco.skiptrack.ui.activities

import android.Manifest
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity

import com.cilenco.skiptrack.R
import com.cilenco.skiptrack.utils.Constants.Companion.PREF_PERMISSION

import net.grandcentrix.tray.AppPreferences

/**
 * Created by Cilenco on 2019/03/28
 * @author Cilenco
 *
 * @description The Main activity of the SkipTrack app. It checks if the permission for Volume Listener is granted.
 * It can self grant the permission using root
 */


class MainActivity : AppCompatActivity(), DialogInterface.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        val permission = checkSelfPermission(PERMISSION)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(PERMISSION), 1)
        } else {
            val prefs = AppPreferences(this)
            prefs.put(PREF_PERMISSION, true)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            val prefs = AppPreferences(this)
            prefs.put(PREF_PERMISSION, true)
        } else {
            val builder = AlertDialog.Builder(this)

            builder.setPositiveButton(android.R.string.ok, this)

            builder.setView(R.layout.dialog_permission)
            builder.setMessage(R.string.permission_description)
            builder.setTitle(R.string.permission_title)
            builder.setCancelable(false)

            builder.show()
        }
    }

    override fun onClick(dialog: DialogInterface, which: Int) {
        finishAndRemoveTask()
    }

    companion object {
        private const val PERMISSION = Manifest.permission.SET_VOLUME_KEY_LONG_PRESS_LISTENER
    }
}
