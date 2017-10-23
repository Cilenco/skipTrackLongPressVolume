package com.cilenco.skiptrack.services;

import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.session.MediaSessionManager;
import android.media.session.MediaSessionManager.OnVolumeKeyLongPressListener;
import android.os.Handler;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.service.notification.NotificationListenerService;
import android.view.KeyEvent;

import static android.view.KeyEvent.FLAG_FROM_SYSTEM;
import static android.view.KeyEvent.FLAG_LONG_PRESS;
import static android.view.KeyEvent.KEYCODE_MEDIA_NEXT;
import static android.view.KeyEvent.KEYCODE_MEDIA_PREVIOUS;
import static android.view.KeyEvent.KEYCODE_VOLUME_UP;

public class VolumeKeyService extends NotificationListenerService implements OnVolumeKeyLongPressListener {
    private MediaSessionManager mediaSessionManager;

    private PowerManager powerManager;
    private AudioManager audioManager;

    private boolean serviceEnabled;

    @Override
    public void onCreate() {
        super.onCreate();

        audioManager = getSystemService(AudioManager.class);
        powerManager = getSystemService(PowerManager.class);

        mediaSessionManager = getSystemService(MediaSessionManager.class);
        mediaSessionManager.setOnVolumeKeyLongPressListener(this, new Handler());

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        serviceEnabled = prefs.getBoolean("", true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mediaSessionManager.setOnVolumeKeyLongPressListener(null, null);
    }

    @Override
    public void onVolumeKeyLongPress(KeyEvent keyEvent) {
        boolean isScreenOn = powerManager.isInteractive();
        boolean isMusicPlaying = audioManager.isMusicActive();

        int flags = keyEvent.getFlags();

        if(!(flags == FLAG_FROM_SYSTEM || flags == FLAG_LONG_PRESS)) return;
        if(!isMusicPlaying || isScreenOn || !serviceEnabled) return;

        if(keyEvent.getKeyCode() == KEYCODE_VOLUME_UP) {
            KeyEvent event = new KeyEvent(keyEvent.getAction(), KEYCODE_MEDIA_NEXT);
            audioManager.dispatchMediaKeyEvent(event);
        } else {
            KeyEvent event = new KeyEvent(keyEvent.getAction(), KEYCODE_MEDIA_PREVIOUS);
            audioManager.dispatchMediaKeyEvent(event);
        }
    }
}