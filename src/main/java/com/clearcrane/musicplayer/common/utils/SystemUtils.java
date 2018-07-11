package com.clearcrane.musicplayer.common.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;

import static android.content.Context.AUDIO_SERVICE;

/**
 * Created by jjy on 2018/5/30.
 * <p>
 * 系统相关工具类
 */

public class SystemUtils {
    private static SystemUtils inst;
    private AudioManager mAudioManager;
    private int mMaxVolume;

    public static SystemUtils getInstance() {
        synchronized (SystemUtils.class) {
            if (inst == null) {
                inst = new SystemUtils();
            }
        }
        return inst;
    }

    public void init(Context context) {
        mAudioManager = (AudioManager) context.getSystemService(AUDIO_SERVICE);
    }

    public void volumeUp() {
        if (mAudioManager == null) {
            return;
        }
        int curr = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        if (curr + 1 <= mMaxVolume) {
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, curr + 1, 0);
        }
    }

    public void volumeDown() {
        if (mAudioManager == null) {
            return;
        }
        int curr = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        if (curr - 1 >= 0) {
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, curr - 1, 0);
        }
    }

    public static void setVolume(Context context, int volume) {
        AudioManager audio = (AudioManager) context.getSystemService(AUDIO_SERVICE);
        if (audio == null) {
            return;
        }
        audio.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
    }

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

    /**
     * 发送重启广播给Controller
     */
    public static void reboot(Context context) {
        Intent intent = new Intent("com.cleartv.controller.intent.SHELL");
        intent.putExtra("command", "reboot");
        context.sendBroadcast(intent);
    }
}
