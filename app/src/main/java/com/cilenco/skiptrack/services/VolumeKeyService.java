package com.cilenco.skiptrack.services;

import android.media.AudioManager;
import android.media.session.MediaSessionManager;
import android.os.Handler;
import android.os.PowerManager;
import android.service.notification.NotificationListenerService;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.cilenco.skiptrack.R;

import net.grandcentrix.tray.AppPreferences;
import net.grandcentrix.tray.core.OnTrayPreferenceChangeListener;
import net.grandcentrix.tray.core.TrayItem;

import java.util.Collection;

import static android.view.KeyEvent.FLAG_FROM_SYSTEM;
import static android.view.KeyEvent.FLAG_LONG_PRESS;
import static android.view.KeyEvent.KEYCODE_MEDIA_NEXT;
import static android.view.KeyEvent.KEYCODE_MEDIA_PREVIOUS;
import static android.view.KeyEvent.KEYCODE_VOLUME_UP;
import static com.cilenco.skiptrack.utils.Constants.PREF_DEBUG;
import static com.cilenco.skiptrack.utils.Constants.PREF_ENABLED;
import static com.cilenco.skiptrack.utils.Constants.PREF_NO_MEDIA;
import static com.cilenco.skiptrack.utils.Constants.PREF_PERMISSION;
import static com.cilenco.skiptrack.utils.Constants.PREF_SCREEN_ON;

public class VolumeKeyService extends NotificationListenerService implements MediaSessionManager.OnVolumeKeyLongPressListener, OnTrayPreferenceChangeListener {
    private AppPreferences preferences;

    private MediaSessionManager mediaSessionManager;
    private PowerManager powerManager;
    private AudioManager audioManager;

    private Handler mHandler;

    private boolean debugEnabled;
    private boolean prefScreenOn;
    private boolean prefNoMedia;

    @Override
    public void onCreate() {
        super.onCreate();

        preferences = new AppPreferences(this);
        preferences.registerOnTrayPreferenceChangeListener(this);

        audioManager = getSystemService(AudioManager.class);
        powerManager = getSystemService(PowerManager.class);

        mediaSessionManager = getSystemService(MediaSessionManager.class);
        mHandler = new Handler();
    }

    @Override
    public void onTrayPreferenceChanged(Collection<TrayItem> items) {
        boolean permission = preferences.getBoolean(PREF_PERMISSION, false);
        boolean serviceEnabled = preferences.getBoolean(PREF_ENABLED, false);

        debugEnabled = preferences.getBoolean(PREF_DEBUG, false);
        prefScreenOn = preferences.getBoolean(PREF_SCREEN_ON, false);
        prefNoMedia = preferences.getBoolean(PREF_NO_MEDIA, false);

        if(serviceEnabled && permission) {
            mediaSessionManager.setOnVolumeKeyLongPressListener(this, mHandler);
            Log.d("cilenco", "Registered VolumeKeyListener");
        } else {
            mediaSessionManager.setOnVolumeKeyLongPressListener(null, null);
            Log.d("cilenco", "Unregistered VolumeKeyListener");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        preferences.unregisterOnTrayPreferenceChangeListener(this);
        mediaSessionManager.setOnVolumeKeyLongPressListener(null, null);
    }

    @Override
    public void onVolumeKeyLongPress(KeyEvent keyEvent) {
        boolean screenOn = powerManager.isInteractive();
        boolean musicPlaying = audioManager.isMusicActive();

        int flags = keyEvent.getFlags();

        //if(keyEvent.getFlags() != FLAG_FROM_SYSTEM) return;
        if(!(flags == FLAG_FROM_SYSTEM || flags == FLAG_LONG_PRESS)) return;

        if((musicPlaying || prefNoMedia) && (!screenOn || prefScreenOn)) {

            if(keyEvent.getAction() == KeyEvent.ACTION_DOWN && keyEvent.getRepeatCount() <= 1) {
                int keyCode = keyEvent.getKeyCode();

                int event = (keyCode == KEYCODE_VOLUME_UP) ? KEYCODE_MEDIA_NEXT : KEYCODE_MEDIA_PREVIOUS;
                int msgRes = (keyCode == KEYCODE_VOLUME_UP) ? R.string.msg_media_next : R.string.msg_media_pre;

                KeyEvent skipEvent = new KeyEvent(keyEvent.getAction(), event);
                audioManager.dispatchMediaKeyEvent(skipEvent);

                if (debugEnabled) Toast.makeText(this, getString(msgRes), Toast.LENGTH_SHORT).show();
            }

            return;
        }

        // Let the MediaSessionManager deal with the event

        mediaSessionManager.setOnVolumeKeyLongPressListener(null, null);
        mediaSessionManager.dispatchVolumeKeyEvent(keyEvent, audioManager.getUiSoundsStreamType(), false);
        mediaSessionManager.setOnVolumeKeyLongPressListener(this, mHandler);
    }
}
