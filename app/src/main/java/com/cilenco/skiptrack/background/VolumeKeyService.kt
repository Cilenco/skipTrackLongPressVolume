package com.cilenco.skiptrack.background

import android.app.Service
import android.content.Intent
import android.media.AudioManager
import android.media.session.MediaSessionManager
import android.os.Handler
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import android.view.KeyEvent
import android.widget.Toast

import com.cilenco.skiptrack.R

import net.grandcentrix.tray.AppPreferences
import net.grandcentrix.tray.core.OnTrayPreferenceChangeListener
import net.grandcentrix.tray.core.TrayItem

import android.view.KeyEvent.FLAG_FROM_SYSTEM
import android.view.KeyEvent.FLAG_LONG_PRESS
import android.view.KeyEvent.KEYCODE_MEDIA_NEXT
import android.view.KeyEvent.KEYCODE_MEDIA_PREVIOUS
import android.view.KeyEvent.KEYCODE_VOLUME_UP

import com.cilenco.skiptrack.utils.Constants.PREF_DEBUG
import com.cilenco.skiptrack.utils.Constants.PREF_ENABLED
import com.cilenco.skiptrack.utils.Constants.PREF_NO_MEDIA
import com.cilenco.skiptrack.utils.Constants.PREF_PERMISSION
import com.cilenco.skiptrack.utils.Constants.PREF_SCREEN_ON

class VolumeKeyService : Service(), MediaSessionManager.OnVolumeKeyLongPressListener, OnTrayPreferenceChangeListener {
    private val preferences by lazy { AppPreferences(this) }

    private val mediaSessionManager by lazy { getSystemService(MediaSessionManager::class.java) }
    private val powerManager by lazy { getSystemService(PowerManager::class.java) }
    private val audioManager by lazy { getSystemService(AudioManager::class.java) }

    private var debugEnabled: Boolean = false
    private var prefScreenOn: Boolean = false
    private var prefNoMedia: Boolean = false

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()

        Log.d("cilenco", "VolumeKeyService created")
        preferences.registerOnTrayPreferenceChangeListener(this)
    }

    override fun onTrayPreferenceChanged(items: Collection<TrayItem>) {
        val permission = preferences.getBoolean(PREF_PERMISSION, false)
        val serviceEnabled = preferences.getBoolean(PREF_ENABLED, false)

        debugEnabled = preferences.getBoolean(PREF_DEBUG, false)
        prefScreenOn = preferences.getBoolean(PREF_SCREEN_ON, false)
        prefNoMedia = preferences.getBoolean(PREF_NO_MEDIA, false)

        if (serviceEnabled && permission) {
            Log.d("cilenco", "Registered VolumeKeyListener")
            mediaSessionManager.setOnVolumeKeyLongPressListener(this, Handler())
        } else {
            Log.d("cilenco", "Unregistered VolumeKeyListener")
            mediaSessionManager.setOnVolumeKeyLongPressListener(null, null)
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        preferences.unregisterOnTrayPreferenceChangeListener(this)
        mediaSessionManager.setOnVolumeKeyLongPressListener(null, null)

        Log.d("cilenco", "VolumeKeyService destroyed")
    }

    override fun onVolumeKeyLongPress(keyEvent: KeyEvent) {
        val screenOn = powerManager.isInteractive
        val musicPlaying = audioManager.isMusicActive

        val flags = keyEvent.flags

        //if(keyEvent.getFlags() != FLAG_FROM_SYSTEM) return;
        if (!(flags == FLAG_FROM_SYSTEM || flags == FLAG_LONG_PRESS)) return

        if ((musicPlaying || prefNoMedia) && (!screenOn || prefScreenOn)) {

            if (keyEvent.action == KeyEvent.ACTION_DOWN && keyEvent.repeatCount <= 1) {
                val keyCode = keyEvent.keyCode

                val event = if (keyCode == KEYCODE_VOLUME_UP) KEYCODE_MEDIA_NEXT else KEYCODE_MEDIA_PREVIOUS
                val msgRes = if (keyCode == KEYCODE_VOLUME_UP) R.string.msg_media_next else R.string.msg_media_pre

                val skipEvent = KeyEvent(keyEvent.action, event)
                audioManager.dispatchMediaKeyEvent(skipEvent)

                if (debugEnabled) Toast.makeText(this, getString(msgRes), Toast.LENGTH_SHORT).show()
            }

            return
        }

        // Let the MediaSessionManager deal with the event

        mediaSessionManager.setOnVolumeKeyLongPressListener(null, null)
        mediaSessionManager.dispatchVolumeKeyEvent(keyEvent, audioManager.uiSoundsStreamType, false)
        mediaSessionManager.setOnVolumeKeyLongPressListener(this, Handler())
    }
}
