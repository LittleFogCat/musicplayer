package com.clearcrane.musicplayer;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.clearcrane.musicplayer.manager.IMusicManager;
import com.clearcrane.musicplayer.manager.MusicManager;
import com.clearcrane.musicplayer.service.MusicService;
import com.clearcrane.musicplayer.view.WrapperView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.clearcrane.musicplayer.utils.Preconditions.checkIndexInBounds;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final String[] Modes = {"顺序播放", "随机播放", "单曲循环"};
    private Button mBtnInit;
    private ImageButton mBtnNext;
    private ImageButton mBtnPre;
    private ImageButton mBtnPlay;
    private Button mBtnMode;

    private TextView mTvCurrentPlay;
    private TextView mTvProgress;
    private TextView mTvVolume;
    private SeekBar mSeekBar;
    private SeekBar mSbVolume;
    private ViewGroup mVolumeLayout;
    private ImageView mIvCover;

    private WrapperView mWrapper;

    private IMusicManager mManager;

    private MusicManager.Music mCurrentMusic;

    private boolean mSeeking = false;
    private int mCurrentProgress = 0;
    private SeekBar.OnSeekBarChangeListener mSeekBarListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            Log.d(TAG, "onSeekBarProgressChanged: " + progress);
            if (fromUser) {
                mManager.setProgress(mManager.getDuration() * progress / 100);
            }
            mCurrentProgress = progress;
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            mSeeking = true;
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            int progress = seekBar.getProgress();
            Log.d(TAG, "onStopTrackingTouch: " + progress);
            int position = mManager.getDuration() * progress / 100;
            mManager.setProgress(position);
            mSeeking = false;
        }
    };
    private SeekBar.OnSeekBarChangeListener mVolumeSeekBarListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                mManager.setVolume(progress);
                mTvVolume.setText(String.valueOf(progress));
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBtnInit = findViewById(R.id.btn);
//        mBtnInit.setOnClickListener(v -> initMusicManager());
        mBtnNext = findViewById(R.id.btnPlayNext);
        mBtnNext.setOnClickListener(v -> mManager.playNext());
        mBtnPre = findViewById(R.id.btnPlayPrevious);
        mBtnPre.setOnClickListener(v -> mManager.playPrevious());
        mBtnPlay = findViewById(R.id.btnPlay);
        mBtnPlay.setOnClickListener(v -> changePlayState());
        mBtnMode = findViewById(R.id.btnPlayMode);
        mBtnMode.setOnClickListener(v -> changePlayMode());

        mTvCurrentPlay = findViewById(R.id.tvCurrentPlay);
        mTvProgress = findViewById(R.id.tvProgress);
        mTvVolume = findViewById(R.id.tvVolume);
        mSeekBar = findViewById(R.id.pbMusic);
        mSeekBar.setOnSeekBarChangeListener(mSeekBarListener);
        mSeekBar.setOnClickListener(v -> changePlayState());
        mSbVolume = findViewById(R.id.sbVolume);
        mSbVolume.setOnSeekBarChangeListener(mVolumeSeekBarListener);
        mVolumeLayout = findViewById(R.id.volumeLayout);
        mVolumeLayout.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() != KeyEvent.ACTION_DOWN) {
                return false;
            }
            if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                mSbVolume.onKeyDown(keyCode, event);
                return true;
            }
            return false;
        });
        mIvCover = findViewById(R.id.ivCover);
        mWrapper = findViewById(R.id.focusBox);
        ViewTreeObserver observer = getWindow().getDecorView().getViewTreeObserver();
        observer.addOnGlobalFocusChangeListener(mWrapper);

        IntentFilter intentFilter = new IntentFilter("com.clearcrane.musicplayer.intent.action.serviceoncreate");
        registerReceiver(new ServiceOnCreateReceiver(), intentFilter);

        try {
            mManager = MusicManager.getInstance();
            initMusicManager(false);
        } catch (Exception e) {
            Intent intent = new Intent(this, MusicService.class);
            startService(intent);
        }
    }

    private void changePlayState() {
        if (mManager.isPlaying()) {
            Log.d(TAG, "changePlayState: is playing...");
            mBtnPlay.setImageResource(R.drawable.ic_pause);
        } else {
            Log.d(TAG, "changePlayState: is not playing...");
            mBtnPlay.setImageResource(R.drawable.ic_play);
        }
        mManager.playOrPause();
    }

    private void changePlayMode() {
        String currentMode = mBtnMode.getText().toString();
        int nextIndex = 0;
        for (int i = 0; i < Modes.length; i++) {
            if (currentMode.equals(Modes[i])) {
                nextIndex = i == Modes.length - 1 ? 0 : i + 1;
                break;
            }
        }
        changePlayMode(nextIndex);
    }

    private void changePlayMode(int index) {
        checkIndexInBounds(Modes, index);
        mBtnMode.setText(Modes[index]);
    }

    private void initMusicManager(boolean first) {
        if (first) {
            List<MusicManager.Music> musicList = listMusic("/data/local/tmp/");
            mManager.setPlayList(musicList);
            mManager.startPlay(0);
        }
        mManager.setOnProgressListener(this::onProgressChanged);
        int volume = mManager.getVolume();
        mTvVolume.setText(String.valueOf(volume));
        mSbVolume.setProgress(volume);
    }

    @SuppressLint("DefaultLocale")
    private String getTimeString(int millisecond) {
        int totalSecond = millisecond / 1000;
        int minute = totalSecond / 60;
        int second = totalSecond - 60 * minute;
        return String.format("%02d:%02d", minute, second);
    }

    private List<MusicManager.Music> listMusic(String path) {
        File dir = new File(path);
        Log.d(TAG, "listMusic: " + dir);
        if (!dir.exists() || !dir.isDirectory()) {
            return null;
        }

        String[] files = dir.list();
        List<MusicManager.Music> musicList = new ArrayList<>();
        for (String file : files) {
            if (!(file.endsWith(".mp3") || file.endsWith("m4a") || file.endsWith("wav"))) {
                continue;
            }
            String uri = path + File.separator + file;
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(uri);
            String title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            String artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            String author = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_AUTHOR);
            String album = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
            String year = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_YEAR);
            byte[] cover = retriever.getEmbeddedPicture();
            MusicManager.Music music = new MusicManager.Music(title, uri, artist, author, album, year, cover);
            musicList.add(music);
        }

        return musicList;
    }

    private void onProgressChanged(MusicManager.Music music, int progress, int duration) {
        Log.d(TAG, "onProgressChanged: " + progress);
        String progressStr = getTimeString(progress) + "/" + getTimeString(duration);
        mTvProgress.setText(progressStr);
        int seekProgress = progress * 100 / duration;
        if (!mSeeking && (seekProgress > mCurrentProgress || seekProgress <= 1)) {
            mSeekBar.setProgress(seekProgress);
        }
        // 切歌
        if (!music.equals(mCurrentMusic)) {
            mCurrentMusic = music;
            String showText = music.name + "(artist: " + music.artist + ")";
            mTvCurrentPlay.setText(showText);
            if (music.albumCover != null) {
                mIvCover.setImageBitmap(
                        BitmapFactory.decodeByteArray(
                                music.albumCover, 0, music.albumCover.length
                        )
                );
            } else {
                mIvCover.setImageResource(R.drawable.album_default);
            }
        }

    }

    private class ServiceOnCreateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive: ServiceOnCreate");
            mManager = MusicManager.getInstance();
            initMusicManager(true);
            unregisterReceiver(this);
        }
    }

}
