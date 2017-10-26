package com.cilenco.skiptrack.services;

import android.media.AudioManager;
import android.media.session.MediaSessionManager;
import android.media.session.MediaSessionManager.OnVolumeKeyLongPressListener;
import android.os.Handler;
import android.os.PowerManager;
import android.service.notification.NotificationListenerService;
import android.util.Log;
import android.view.KeyEvent;
import android.content.Intent;
import android.widget.Toast;

import com.cilenco.skiptrack.R;

import static android.view.KeyEvent.FLAG_LONG_PRESS;
import static android.view.KeyEvent.FLAG_FROM_SYSTEM;
import static android.view.KeyEvent.KEYCODE_MEDIA_NEXT;
import static android.view.KeyEvent.KEYCODE_MEDIA_PREVIOUS;
import static android.view.KeyEvent.KEYCODE_VOLUME_UP;

public class VolumeKeyService extends NotificationListenerService implements OnVolumeKeyLongPressListener {
    private static final int FLAG_FROM_ADB = 0;
    private MediaSessionManager mediaSessionManager;

    private PowerManager powerManager;
    private AudioManager audioManager;
    private Handler mHandler;
    private boolean mServiceEnabled;
    private boolean mScreenOnEnable;
    private boolean mMediaNotPlayingEnable;
    private boolean mDebug;

    @Override
    public void onCreate() {
        super.onCreate();

        audioManager = getSystemService(AudioManager.class);
        powerManager = getSystemService(PowerManager.class);

        mediaSessionManager = getSystemService(MediaSessionManager.class);
        mHandler = new Handler();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mServiceEnabled = intent.getBooleanExtra("ENABLED", false);
        mScreenOnEnable = intent.getBooleanExtra("SCREEN_ON", false);
        mMediaNotPlayingEnable = intent.getBooleanExtra("MEDIA_NOT_PLAYING", false);
        mDebug = intent.getBooleanExtra("DEBUG", false);
        boolean permission = intent.getBooleanExtra("PERMISSION", false);
        Log.d("mumu", "onStartCommand -> permission = " + permission
                + "mServiceEnabled = " + mServiceEnabled
                + "mScreenOnEnable = " + mScreenOnEnable
                + "mMediaNotPlayingEnable = " + mMediaNotPlayingEnable);
        if (mServiceEnabled && permission) {
            mediaSessionManager.setOnVolumeKeyLongPressListener(this, mHandler);
        } else {
            mediaSessionManager.setOnVolumeKeyLongPressListener(null, null);
        }

        return super.onStartCommand(intent, flags, startId);
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

        Log.d("mumu", keyEvent.getKeyCode() + ", " + keyEvent.getFlags() + ", " + keyEvent.isLongPress());

        if (keyEvent.getFlags() != FLAG_FROM_ADB
                && (keyEvent.getFlags() & FLAG_LONG_PRESS) == 0
                && (keyEvent.getFlags() & FLAG_FROM_SYSTEM) == 0) {
            return;
        }

        if ((isMusicPlaying || mMediaNotPlayingEnable) && (!isScreenOn || mScreenOnEnable)) {
            //TODO: skip track
            if (keyEvent.getKeyCode() == KEYCODE_VOLUME_UP) {
                KeyEvent event = new KeyEvent(keyEvent.getAction(), KEYCODE_MEDIA_PREVIOUS);
                audioManager.dispatchMediaKeyEvent(event);
                if (mDebug && keyEvent.getAction() == KeyEvent.ACTION_DOWN && keyEvent.getRepeatCount() == 0) {
                    Toast.makeText(getContext(), getString(R.string.msg_media_pre), Toast.LENGTH_SHORT).show();
                }
            } else {
                KeyEvent event = new KeyEvent(keyEvent.getAction(), KEYCODE_MEDIA_NEXT);
                audioManager.dispatchMediaKeyEvent(event);
                if (mDebug && keyEvent.getAction() == KeyEvent.ACTION_DOWN && keyEvent.getRepeatCount() == 0) {
                    Toast.makeText(getContext(), getString(R.string.msg_media_next), Toast.LENGTH_SHORT).show();
                }
            }
            return;
        }
        //TODO: let MediaSessionManager deal with it
        if (mDebug && keyEvent.getRepeatCount() == 0 && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
            Toast.makeText(getContext(), getString(R.string.msg_do_nothing), Toast.LENGTH_SHORT).show();
        }
        mediaSessionManager.setOnVolumeKeyLongPressListener(null, null);
        mediaSessionManager.dispatchVolumeKeyEvent(keyEvent, audioManager.getUiSoundsStreamType(), false);
        mediaSessionManager.setOnVolumeKeyLongPressListener(this, mHandler);
    }
}
