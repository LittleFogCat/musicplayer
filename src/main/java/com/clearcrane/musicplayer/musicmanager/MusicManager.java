package com.clearcrane.musicplayer.musicmanager;

import android.os.Handler;
import android.util.Log;

import com.clearcrane.musicplayer.musicservice.IMusicService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jjy on 2018/5/8.
 * <p>
 * MusicManager的作用是管理音乐播放的状态，比如播放列表等。
 * 管理音乐播放。
 */

public class MusicManager implements IMusicManager {
    private static final String TAG = "MusicManager";
    private static MusicManager sManager;
    private IMusicService mService;
    private List<Music> mPlayList = new ArrayList<>();
    private int mCurrentMusicIndex;
    private OnProgressListener mProgressListener;

    private MusicManager() {
    }

    public static IMusicManager getInstance() {
        if (sManager == null) {
            sManager = new MusicManager();
        }
        return sManager;
    }

    @Override
    public void setService(IMusicService service) {
        if (mService != null) {
            Log.w(TAG, "setService: service is already set", new IllegalStateException("service is already set"));
            return;
        }
        mService = service;
        mService.setOnCompleteListener(mp -> playNext());
    }

    @Override
    public void setPlayList(List<Music> musicList) {
        if (musicList == null) {
            return;
        }
        Log.d(TAG, "setPlayList: " + musicList);
        mPlayList.clear();
        mPlayList.addAll(musicList);
    }

    @Override
    public List<Music> getPlayList() {
        return mPlayList;
    }

    @Override
    public void setCurrentPlaying(int position) {
        mService.play(mPlayList.get(position).url);
    }

    @Override
    public Music getCurrentPlaying() {
        return mPlayList.get(mCurrentMusicIndex);
    }

    @Override
    public void startPlay(int position) {
        if (position > mPlayList.size() - 1 || position < 0) {
            return;
        }
        Music play = mPlayList.get(position);
        Log.d(TAG, "startPlay: " + play);
        mService.play(play.url);
    }

    @Override
    public void setProgress(int progress) {
        mService.setPosition(progress);
    }

    @Override
    public int getDuration() {
        return mService.getDuration();
    }

    @Override
    public int getProgress() {
        return mService.getPosition();
    }

    @Override
    public void forward(int second) {
        mService.setPosition(mService.getPosition() + second * 1000);
    }

    @Override
    public void backward(int second) {
        mService.setPosition(mService.getPosition() - second * 1000);
    }

    @Override
    public int volumeUp() {
        return mService.volumeUp();
    }

    @Override
    public int volumeDown() {
        return mService.volumeDown();
    }

    @Override
    public int getVolume() {
        return mService.getVolume();
    }

    @Override
    public void setVolume(int volume) {
        mService.setVolume(volume);
    }

    @Override
    public void setPlayMode(int mode) {
        // TODO: 2018/5/10
    }

    @Override
    public void playNext() {
        mCurrentMusicIndex = mCurrentMusicIndex >= mPlayList.size() - 1 ? 0 : mCurrentMusicIndex + 1;
        Music next = mPlayList.get(mCurrentMusicIndex);
        Log.d(TAG, "playNext: " + next);
        mService.play(next.url);
    }

    @Override
    public void playPrevious() {
        Log.d(TAG, "playPrevious: ");
        mCurrentMusicIndex = mCurrentMusicIndex <= 0 ? mPlayList.size() - 1 : mCurrentMusicIndex - 1;
        mService.play(mPlayList.get(mCurrentMusicIndex).url);
    }

    @Override
    public void playOrPause() {
        if (isPlaying()) {
            mService.pause();
        } else {
            mService.resume();
        }
    }

    @Override
    public void start() {
        mService.resume();
    }

    @Override
    public void start(String url) {
        mService.play(url);
    }

    @Override
    public void pause() {
        mService.pause();
    }

    @Override
    public boolean isPlaying() {
        return mService.isPlaying();
    }

    @Override
    public void setOnProgressListener(OnProgressListener listener) {
        mProgressListener = listener;
        mHandler.postDelayed(mProgressTask, 500);
    }

    private Handler mHandler = new Handler();
    private Runnable mProgressTask = new Runnable() {
        @Override
        public void run() {
            if (mPlayList.isEmpty()) {
                mProgressListener.onProgress(null, 0, 0);
            } else {
                Music music = mPlayList.get(mCurrentMusicIndex);
                mProgressListener.onProgress(music, mService.getPosition(), mService.getDuration());
            }
            mHandler.postDelayed(this, 1000);
        }
    };


}
