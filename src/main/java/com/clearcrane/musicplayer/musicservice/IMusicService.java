package com.clearcrane.musicplayer.musicservice;

import android.media.MediaPlayer;

/**
 * Created by jjy on 2018/5/8.
 * <p>
 * MusicService的作用是控制音乐的播放，很纯粹
 */

public interface IMusicService {
    void play(String url);

    void pause();

    void resume();

    void stop();

    void restart();

    void setPosition(int position);

    int getPosition();

    int getDuration();

    void setOnCompleteListener(MediaPlayer.OnCompletionListener listener);

    boolean isPlaying();

    boolean isPrepared();
}
