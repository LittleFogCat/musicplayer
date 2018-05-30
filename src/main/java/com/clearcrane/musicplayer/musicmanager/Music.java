package com.clearcrane.musicplayer.musicmanager;

import android.text.TextUtils;

public class Music {
    public String name;
    public String url;
    public String artist;
    public String author;
    public String album;
    public String publishYear;
    public byte[] albumCover;

    public Music() {
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