package com.clearcrane.musicplayer.musicservice;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

import com.clearcrane.musicplayer.controller.Controller;
import com.clearcrane.musicplayer.musicmanager.MusicManager;

public class MusicService extends Service implements IMusicService {
    private static final String TAG = "MusicService";
    public static final String ACTION_MUSIC_SERVICE = "com.clearcrane.musicplayer.intent.action.notify_service_state_changed";
    private MediaPlayer mMediaPlayer;

    private MediaPlayer.OnCompletionListener mOnCompletionListener;

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate: ");
        super.onCreate();
        MusicManager.getInstance().setService(this);
        mMediaPlayer = new MediaPlayer();

        mMediaPlayer.setOnCompletionListener(mp -> {
            if (mOnCompletionListener != null) {
                mOnCompletionListener.onCompletion(mp);
            }
        });
        Controller.getInstance().onMusicServiceStarted();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        super.onDestroy();
        Controller.getInstance().onMusicServiceStopped();
    }

    @Override
    public void play(String url) {
        try {
            Log.d(TAG, "play: " + url);
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.reset();
                mMediaPlayer.setDataSource(url);
                mMediaPlayer.prepare();
                mMediaPlayer.start();
            } else {
                mMediaPlayer.reset();
                mMediaPlayer.setDataSource(url);
                mMediaPlayer.prepare();
                mMediaPlayer.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void pause() {
        if (isPlaying()) mMediaPlayer.pause();
    }

    @Override
    public void resume() {
        if (!isPlaying()) mMediaPlayer.start();
    }

    @Override
    public void stop() {
        mMediaPlayer.stop();
    }

    @Override
    public void restart() {
        mMediaPlayer.seekTo(0);
    }

    @Override
    public void setPosition(int position) {
        mMediaPlayer.seekTo(position);
    }

    @Override
    public int getPosition() {
        return mMediaPlayer.getCurrentPosition();
    }

    @Override
    public int getDuration() {
        return mMediaPlayer.getDuration();
    }

    @Override
    public void setOnCompleteListener(MediaPlayer.OnCompletionListener listener) {
        mOnCompletionListener = listener;
    }

    @Override
    public int volumeUp() {
        AudioManager audio = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audio == null) {
            return -1;
        }
        int currentVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
        int maxVolume = audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        if (currentVolume >= maxVolume) {
            return currentVolume;
        }
        audio.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume + 1, 0);

        return audio.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    @Override
    public int volumeDown() {
        AudioManager audio = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audio == null) {
            return -1;
        }
        int currentVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
        int minVolume = 0;
        if (currentVolume <= minVolume) {
            return currentVolume;
        }
        audio.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume - 1, 0);

        return audio.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    @Override
    public int getVolume() {
        AudioManager audio = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audio == null) {
            return -1;
        }
        return audio.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    @Override
    public void setVolume(int volume) {
        AudioManager audio = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audio == null) {
            return;
        }
        audio.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
    }

    @Override
    public boolean isPlaying() {
        return mMediaPlayer != null && mMediaPlayer.isPlaying();
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
