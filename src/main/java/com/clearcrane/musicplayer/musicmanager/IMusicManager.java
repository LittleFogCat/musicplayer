package com.clearcrane.musicplayer.musicmanager;

import android.support.annotation.IntDef;
import android.text.TextUtils;

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
     * 保存当前播放的音乐
     *
     * @param position 当前播放的音乐的序号
     */
    void setCurrentPlaying(int position);

    void playOrPause();

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

    void setOnProgressListener(OnProgressListener listener);

    interface OnProgressListener {
        void onProgress(Music music, int progress, int duration);
    }

    /**
     * 提高音量
     *
     * @return 当前音量；或-1表示已经到最大音量；或-2其他错误
     */
    int volumeUp();

    /**
     * 降低音量
     *
     * @return 当前音量；或-1表示已经到最小音量；或-2其他错误
     */
    int volumeDown();

    int getVolume();

    void setVolume(int volume);

    /**
     * 设置播放模式
     *
     * @param mode 模式
     */
    void setPlayMode(@PlayMode int mode);

    @IntDef({ORDERED, SHUFFLE, SINGLE})
    @interface PlayMode {
    }

    int ORDERED = 0;
    int SHUFFLE = 1;
    int SINGLE = 2;

    class Music {
        public String name;
        public String url;
        public String artist;
        public String author;
        public String album;
        public String publishYear;
        public byte[] albumCover;

        protected Music() {
        }

        public Music(String name, String url, String artist) {
            this.name = checkNotNull(name);
            this.url = checkNotNull(url);
            this.artist = checkNotNull(artist);
        }

        public Music(String name, String url, String artist, String author, String album, String publishYear, byte[] albumCover) {
            this.name = checkNotNull(name);
            this.url = checkNotNull(url);
            this.artist = checkNotNull(artist);
            this.author = checkNotNull(author);
            this.album = checkNotNull(album);
            this.publishYear = checkNotNull(publishYear);
            this.albumCover = albumCover;
        }

        private static String checkNotNull(String obj) {
            return TextUtils.isEmpty(obj) ? "UNKNOWN" : obj;
        }

        @SuppressWarnings("ImplicitArrayToString")
        @Override
        public String toString() {
            return "Music{" +
                    "name='" + name + '\'' +
                    ", url='" + url + '\'' +
                    ", artist='" + artist + '\'' +
                    ", author='" + author + '\'' +
                    ", album='" + album + '\'' +
                    ", publishYear='" + publishYear + '\'' +
                    ", albumCover=" + albumCover +
                    '}';
        }
    }

    void setOnServiceStartListener(OnServiceStartedListener listener);

    interface OnServiceStartedListener {
        void onServiceStarted(IMusicService service);
    }
}
