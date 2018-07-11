package com.clearcrane.musicplayer.ui;

import com.clearcrane.musicplayer.musicmanager.Music;

/**
 * Created by jjy on 2018/5/30.
 * <p>
 * UI抽象接口
 */

public interface UI {
    void notifyProgressChanged(Music music, int progress, int duration, boolean isPlaying);

    void notifyMusicPositionChanged(int oldPos, int newPos, int oldTotal, int newTotal);
}
