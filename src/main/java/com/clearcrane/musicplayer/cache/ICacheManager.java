package com.clearcrane.musicplayer.cache;

import android.content.Context;
import android.net.Uri;

import java.util.List;

public interface ICacheManager {
    /**
     * 初始化
     */
    void init(Context context);

    /**
     * 下载文件
     *
     * @param url 要下载文件的url
     */
    void download(String url);

    /**
     * 通知CacheManager下载完毕
     *
     * @param id 下载任务的唯一标识符
     */
    void notifyDownloadComplete(String id);

    /**
     * @return 获取缓存的文件
     */
    List<Uri> loadCache();
}
