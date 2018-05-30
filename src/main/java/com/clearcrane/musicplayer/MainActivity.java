package com.clearcrane.musicplayer;

import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.clearcrane.musicplayer.controller.CoreService;
import com.clearcrane.musicplayer.musicmanager.IMusicManager;
import com.clearcrane.musicplayer.musicmanager.Music;
import com.clearcrane.musicplayer.musicmanager.MusicManager;
import com.clearcrane.musicplayer.ui.MusicPlayerFragment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startService(new Intent(this, CoreService.class));
        initMusicManager(false);
        initUI();
    }

    private void initUI() {
        getSupportFragmentManager().beginTransaction()
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
            mManager.startPlay(0);
        }
    }

    private List<Music> listMusic() {
        File dir = new File("/data/local/tmp/");
        Log.d(TAG, "listMusic: " + dir);
        if (!dir.exists() || !dir.isDirectory()) {
            return null;
        }

        String[] files = dir.list();
        if (files == null) return null;
        List<Music> musicList = new ArrayList<>();
        for (String file : files) {
            if (!(file.endsWith(".mp3") || file.endsWith("m4a") || file.endsWith("wav"))) {
                continue;
            }
            String uri = "/data/local/tmp/" + File.separator + file;
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

}
