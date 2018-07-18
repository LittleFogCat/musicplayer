package com.clearcrane.musicplayer.cache;

import android.net.Uri;

import java.util.List;

/**
 * 管理缓存的接口。
 */
public interface ICacheManager {

    /**
     * 下载文件
     *
     * @param url 要下载文件的url
     */
    void download(String url);

    /**
     * 通知CacheManager下载完毕
     *
     * @param id 下载任务的唯一标识符，可能是DownloadManager的id、远端文件url等。
     */
    void notifyDownloadComplete(String id);

    /**
     * @return 获取缓存的文件列表
     */
    List<Uri> loadCache();
}
