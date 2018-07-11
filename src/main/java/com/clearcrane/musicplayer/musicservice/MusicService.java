package com.clearcrane.musicplayer.musicservice;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

import com.clearcrane.musicplayer.controller.Controller;
import com.clearcrane.musicplayer.musicmanager.MusicManager;

public class MusicService extends Service implements IMusicService {
    private static final String TAG = "MusicService";
    public static final String ACTION_MUSIC_SERVICE = "com.clearcrane.musicplayer.intent.action.notify_service_state_changed";
    private MediaPlayer mMediaPlayer;
    private boolean mPrepared = false;

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
        mMediaPlayer.setOnPreparedListener(mp -> mPrepared = true);
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
                mMediaPlayer.stop();
            }
            mMediaPlayer.reset();
            mPrepared = false;
            mMediaPlayer.setDataSource(url);
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void pause() {
        if (!mPrepared) {
            return;
        }
        if (isPlaying()) mMediaPlayer.pause();
    }

    @Override
    public void resume() {
        if (!mPrepared) {
            Log.i(TAG, "resume: not prepared");
            return;
        }
        Log.d(TAG, "resume");
        if (!isPlaying()) mMediaPlayer.start();
    }

    @Override
    public void stop() {
        if (!mPrepared) {
            return;
        }
        mMediaPlayer.stop();
    }

    @Override
    public void restart() {
        if (!mPrepared) {
            return;
        }
        mMediaPlayer.seekTo(0);
    }

    @Override
    public void setPosition(int position) {
        if (!mPrepared) {
            return;
        }
        mMediaPlayer.seekTo(position);
    }

    @Override
    public int getPosition() {
        if (!mPrepared) {
            return 0;
        }
        return mMediaPlayer.getCurrentPosition();
    }

    @Override
    public int getDuration() {
        if (!mPrepared) {
            return 0;
        }
        return mMediaPlayer.getDuration();
    }

    @Override
    public void setOnCompleteListener(MediaPlayer.OnCompletionListener listener) {
        mOnCompletionListener = listener;
    }

    @Override
    public boolean isPlaying() {
        return mPrepared && mMediaPlayer != null && mMediaPlayer.isPlaying();
    }

    @Override
    public boolean isPrepared() {
        return mPrepared;
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
