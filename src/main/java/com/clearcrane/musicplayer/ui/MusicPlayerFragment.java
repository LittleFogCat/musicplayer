package com.clearcrane.musicplayer.ui;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.clearcrane.musicplayer.R;
import com.clearcrane.musicplayer.common.utils.SystemUtils;
import com.clearcrane.musicplayer.controller.Controller;
import com.clearcrane.musicplayer.musicmanager.IMusicManager;
import com.clearcrane.musicplayer.musicmanager.Music;
import com.clearcrane.musicplayer.musicmanager.MusicManager;
import com.clearcrane.musicplayer.view.WrapperView;

import static com.clearcrane.musicplayer.common.utils.Preconditions.checkIndexInBounds;

/**
 * Created by jjy on 2018/5/30.
 * <p>
 * 界面
 */
@SuppressWarnings({"ConstantConditions", "FieldCanBeLocal"})
public class MusicPlayerFragment extends Fragment implements UI {
    private static final String TAG = "MusicPlayerFragment";
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

    private IMusicManager mManager = MusicManager.getInstance();

    private Music mCurrentMusic;

    private boolean mSeeking = false;
    private int mCurrentProgress = 0;
    private SeekBar.OnSeekBarChangeListener mSeekBarListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
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

    private boolean mIsPlaying;// 当前播放状态

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.music_player_fragment, container, false);
        Controller.getInstance().setUI(this);
        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
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
        mSeekBar = findViewById(R.id.pbMusic);
        mSeekBar.setOnSeekBarChangeListener(mSeekBarListener);
        mSeekBar.setOnClickListener(v -> changePlayState());

        initVolumeLayout();

        mIvCover = findViewById(R.id.ivCover);
        mWrapper = findViewById(R.id.focusBox);

        SystemUtils.setOnVolumeChangeListener(getActivity(), this::onVolumeChanged);
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        ViewTreeObserver observer = getActivity().getWindow().getDecorView().getViewTreeObserver();
        observer.addOnGlobalFocusChangeListener(mWrapper);
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().getWindow().getDecorView().getViewTreeObserver().removeOnGlobalFocusChangeListener(mWrapper);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void initVolumeLayout() {
        mTvVolume = findViewById(R.id.tvVolume);
        mSbVolume = findViewById(R.id.sbVolume);
        mSbVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    SystemUtils.setVolume(getActivity(), progress);
                    mTvVolume.setText(String.valueOf(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
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
        mSbVolume.setMax(SystemUtils.getMaxVolume(getActivity()));
        onVolumeChanged();
    }

    private void changePlayState() {
        onPlayingStateUpdate(!mManager.isPlaying());
        mManager.playOrPause();
    }

    private void onPlayingStateUpdate(boolean playing) {
        if (mIsPlaying == playing) {
            return;
        }
        if (playing) {
            // 播放状态下，图标变成暂停
            mBtnPlay.setImageResource(R.drawable.ic_pause);
        } else {
            mBtnPlay.setImageResource(R.drawable.ic_play);
        }
        mIsPlaying = playing;
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
        // todo 循环模式
        checkIndexInBounds(Modes, index);
        mBtnMode.setText(Modes[index]);
    }

    @SuppressLint("DefaultLocale")
    private String getTimeString(int millisecond) {
        int totalSecond = millisecond / 1000;
        int minute = totalSecond / 60;
        int second = totalSecond - 60 * minute;
        return String.format("%02d:%02d", minute, second);
    }

    @Override
    public void onMusicProgress(Music music, int progress, int duration, boolean isPlaying) {
        Log.v(TAG, "onProgressChanged: " + progress);
        if (duration == 0) {
            duration = 1;
        }
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
                mIvCover.setImageBitmap(BitmapFactory.decodeByteArray(
                        music.albumCover, 0, music.albumCover.length
                ));
            } else {
                mIvCover.setImageResource(R.drawable.album_default);
            }
        }
        onPlayingStateUpdate(isPlaying);
    }

    private void onVolumeChanged() {
        int volume = SystemUtils.getCurrentVolume(getActivity());
        mTvVolume.setText(String.valueOf(volume));
        mSbVolume.setProgress(volume);
    }

    private <T extends View> T findViewById(int id) {
        return getView() == null ? null : getView().findViewById(id);
    }
}
