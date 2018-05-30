package com.clearcrane.musicplayer.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;

import static android.content.Context.AUDIO_SERVICE;

/**
 * Created by jjy on 2018/5/30.
 */

public class SystemUtils {
    public static int getCurrentVolume(Context context) {
        AudioManager audio = (AudioManager) context.getSystemService(AUDIO_SERVICE);
        if (audio == null) {
            return -1;
        }
        return audio.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    public static int getMaxVolume(Context context) {
        AudioManager audio = (AudioManager) context.getSystemService(AUDIO_SERVICE);
        if (audio == null) {
            return -1;
        }
        return audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    }

    public static void setOnVolumeChangeListener(Context context, OnVolumeChangeListener listener) {
        VolumeReceiver volumeReceiver = new VolumeReceiver(listener);
        IntentFilter filter = new IntentFilter("android.media.VOLUME_CHANGED_ACTION");
        context.registerReceiver(volumeReceiver, filter);
    }

    private static class VolumeReceiver extends BroadcastReceiver {
        private OnVolumeChangeListener mListener;

        VolumeReceiver(OnVolumeChangeListener listener) {
            mListener = listener;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            mListener.onVolumeChanged();
        }
    }

    public interface OnVolumeChangeListener {
        void onVolumeChanged();
    }
}
