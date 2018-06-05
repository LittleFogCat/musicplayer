package com.clearcrane.musicplayer.musicmanager;

import com.clearcrane.musicplayer.musicservice.IMusicService;

import java.util.List;

/**
 * Created by jjy on 2018/5/8.
 * <p>
 * MusicManager的作用是管理音乐播放的状态，比如播放列表等，并且和外界相连
 */
@SuppressWarnings("unused")
public interface IMusicManager {
    void setService(IMusicService service);

    /**
     * 设置播放列表
     */
    void setPlayList(List<Music> musicList);

    List<Music> getPlayList();

    /**
     * 设置当前播放的音乐
     *
     * @param position 当前播放的音乐的序号
     */
    void setCurrentPlaying(int position);

    /**
     * 开始播放
     */
    void start();

    /**
     * 开始播放指定曲目
     */
    void start(String url);

    /**
     * 暂停播放
     */
    void pause();

    /**
     * 播放/暂停
     */
    void playOrPause();

    /**
     * @return 返回是否正在播放
     */
    boolean isPlaying();

    /**
     * 获取当前播放的音乐
     */
    Music getCurrentPlaying();

    /**
     * 开始播放
     *
     * @param position 音乐列表的序号
     */
    void startPlay(int position);

    /**
     * 设置播放进度
     *
     * @param progress 单位毫秒
     */
    void setProgress(int progress);

    /**
     * 获取当前播放进度
     *
     * @return 单位：毫秒
     */
    int getProgress();

    int getDuration();

    /**
     * 快进
     *
     * @param second 快进秒数
     */
    void forward(int second);

    void backward(int second);

    void playNext();

    void playPrevious();

    @Deprecated
    void setOnProgressListener(OnProgressListener listener);

    void addOnProgressListener(OnProgressListener listener);

    interface OnProgressListener {
        void onProgress(Music music, int progress, int duration);
    }

    /**
     * 设置播放模式
     * <p>
     * int ORDERED = 0;
     * int SHUFFLE = 1;
     * int SINGLE = 2;
     *
     * @param mode 模式
     */
    void setPlayMode(int mode);

    int ORDERED = 0;
    int SHUFFLE = 1;
    int SINGLE = 2;

    void setOnEventListener(OnEventListener l);

    interface OnEventListener {
        default void onMusicListChanged(List<Music> oldList, List<Music> newList) {
        }
    }
}
