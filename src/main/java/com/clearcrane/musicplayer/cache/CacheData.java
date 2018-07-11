package com.clearcrane.musicplayer.cache;

import java.io.Serializable;

public class CacheData implements Serializable {
    public long id;
    public String url;

    @Override
    public String toString() {
        return "CacheData{" +
                "id=" + id +
                ", url='" + url + '\'' +
                '}';
    }
}
