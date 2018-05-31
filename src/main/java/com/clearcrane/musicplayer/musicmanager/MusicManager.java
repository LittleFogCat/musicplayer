package com.clearcrane.musicplayer.musicmanager;

import android.os.Handler;
import android.util.Log;

import com.clearcrane.musicplayer.musicservice.IMusicService;

import java.util.ArrayList;
import java.util.List;

import static com.clearcrane.musicplayer.common.utils.Preconditions.checkNotNull;

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
    private List<OnProgressListener> mOnProgressListenerList = new ArrayList<>();

    private MusicManager() {
        mHandler.postDelayed(mProgressTask, 500);
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
        if (mService == null) {
            return;
        }
        mService.play(mPlayList.get(position).url);
    }

    @Override
    public Music getCurrentPlaying() {
        return mPlayList.get(mCurrentMusicIndex);
    }

    @Override
    public void startPlay(int position) {
        if (mService == null) {
            return;
        }
        if (position > mPlayList.size() - 1 || position < 0) {
            return;
        }
        Music play = mPlayList.get(position);
        Log.d(TAG, "startPlay: " + play);
        mService.play(play.url);
    }

    @Override
    public void setProgress(int progress) {
        if (mService == null) {
            return;
        }
        mService.setPosition(progress);
    }

    @Override
    public int getDuration() {
        if (mService == null) {
            return 1;
        }
        return mService.getDuration();
    }

    @Override
    public int getProgress() {
        if (mService == null) {
            return 0;
        }
        return mService.getPosition();
    }

    @Override
    public void forward(int second) {
        if (mService == null) {
            return;
        }
        mService.setPosition(mService.getPosition() + second * 1000);
    }

    @Override
    public void backward(int second) {
        if (mService == null) {
            return;
        }
        mService.setPosition(mService.getPosition() - second * 1000);
    }

    @Override
    public int volumeUp() {
        if (mService == null) {
            return 0;
        }
        return mService.volumeUp();
    }

    @Override
    public int volumeDown() {
        if (mService == null) {
            return 0;
        }
        return mService.volumeDown();
    }

    @Override
    public int getVolume() {
        if (mService == null) {
            return 0;
        }
        return mService.getVolume();
    }

    @Override
    public void setVolume(int volume) {
        if (mService == null) {
            return;
        }
        mService.setVolume(volume);
    }

    @Override
    public void setPlayMode(int mode) {
        // TODO: 2018/5/10
    }

    @Override
    public void setOnEventListener(OnEventListener l) {

    }

    @Override
    public void playNext() {
        if (mService == null) {
            return;
        }
        mCurrentMusicIndex = mCurrentMusicIndex >= mPlayList.size() - 1 ? 0 : mCurrentMusicIndex + 1;
        Music next = mPlayList.get(mCurrentMusicIndex);
        Log.d(TAG, "playNext: " + next);
        mService.play(next.url);
    }

    @Override
    public void playPrevious() {
        Log.d(TAG, "playPrevious: ");
        if (mService == null) {
            return;
        }
        mCurrentMusicIndex = mCurrentMusicIndex <= 0 ? mPlayList.size() - 1 : mCurrentMusicIndex - 1;
        Music previous = mPlayList.get(mCurrentMusicIndex);
        Log.d(TAG, "playPrevious: " + previous);
        mService.play(previous.url);
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
        if (mService == null) {
            return;
        }
        Log.d(TAG, "start: ");
        mService.resume();
    }

    @Override
    public void start(String url) {
        if (mService == null) {
            return;
        }
        Log.d(TAG, "start: " + url);
        mService.play(url);
    }

    @Override
    public void pause() {
        if (mService == null) {
            return;
        }
        Log.d(TAG, "pause: ");
        mService.pause();
    }

    @Override
    public boolean isPlaying() {
        if (mService == null) {
            return false;
        }
        return mService.isPlaying();
    }

    @Override
    public void setOnProgressListener(OnProgressListener listener) {
        mProgressListener = listener;
    }

    @Override
    public void addOnProgressListener(OnProgressListener listener) {
        checkNotNull(listener);
        mOnProgressListenerList.add(listener);
    }

    private void notifyProgressChanged(Music music, int progress, int duration) {
        Log.v(TAG, "notifyProgressChanged: " + music + "\n" + progress + "/" + duration);
        for (OnProgressListener onProgressListener : mOnProgressListenerList) {
            onProgressListener.onProgress(music, progress, duration);
        }
        if (mProgressListener != null) {
            mProgressListener.onProgress(music, progress, duration);
        }
    }

    private Handler mHandler = new Handler();
    private Runnable mProgressTask = new Runnable() {
        @Override
        public void run() {
            if (mPlayList.isEmpty()) {
                Log.v(TAG, "ProgressTask: empty");
                mHandler.postDelayed(this, 3000);
            } else {
                Music music = mPlayList.get(mCurrentMusicIndex);
                notifyProgressChanged(music, mService.getPosition(), mService.getDuration());
                mHandler.postDelayed(this, 1000);
            }
        }
    };


}
