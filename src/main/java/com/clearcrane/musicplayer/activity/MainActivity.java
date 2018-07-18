package com.clearcrane.musicplayer.activity;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

import com.clearcrane.musicplayer.R;
import com.clearcrane.musicplayer.common.DpadRecorder;
import com.clearcrane.musicplayer.controller.CoreService;
import com.clearcrane.musicplayer.musicmanager.IMusicManager;
import com.clearcrane.musicplayer.musicmanager.Music;
import com.clearcrane.musicplayer.musicmanager.MusicManager;
import com.clearcrane.musicplayer.ui.MusicPlayerFragment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 入口之Launcher
 */
public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    public static File LOCAL_MUSIC_DIR;
    private DpadRecorder mRecorder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LOCAL_MUSIC_DIR = new File(getFilesDir(), "music");
        setContentView(R.layout.activity_main);
        startService(new Intent(this, CoreService.class));
        initMusicManager(true);
        initUI();
        mRecorder = DpadRecorder.getInstance();
        mRecorder.addCallback("uuddlrlr", () -> {
            Intent intent = new Intent(MainActivity.this, SettingActivity.class);
            startActivity(intent);
        });
    }

    private void initUI() {
        getFragmentManager().beginTransaction()
                .add(R.id.container, new MusicPlayerFragment())
                .commit();
    }

    private void initMusicManager(@SuppressWarnings("SameParameterValue") boolean local) {
        IMusicManager mManager = MusicManager.getInstance();
        if (local) {
            List<Music> musicList = listMusic();
            if (musicList == null) {
                return;
            }
            mManager.setPlayList(musicList);
//            mManager.startPlay(0);
        }
    }

    private List<Music> listMusic() {
        File dir = LOCAL_MUSIC_DIR;
        Log.d(TAG, "listMusic: " + dir);
        if (!dir.exists() && !dir.mkdirs() || !dir.isDirectory()) {
            return null;
        }

        String[] files = dir.list();
        if (files == null) return null;
        List<Music> musicList = new ArrayList<>();
        for (String file : files) {
            if (!(file.endsWith(".mp3") || file.endsWith("m4a") || file.endsWith("wav"))) {
                continue;
            }
            String uri = LOCAL_MUSIC_DIR.getAbsolutePath() + File.separator + file;
            Log.d(TAG, "listMusic: uri = " + uri);
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(uri);
            String title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            String artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            String author = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_AUTHOR);
            String album = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
            String year = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_YEAR);
            byte[] cover = retriever.getEmbeddedPicture();
            Music music = new Music(title, uri, artist, author, album, year, cover);
            musicList.add(music);
        }

        return musicList;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (mRecorder != null) mRecorder.onKeyDown(event.getKeyCode(), event);
        }
        return super.dispatchKeyEvent(event);
    }

}
