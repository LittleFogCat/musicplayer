package com.clearcrane.musicplayer.cache;

import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.clearcrane.musicplayer.common.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
@Deprecated
public class CacheManager implements ICacheManager {
    private static final String TAG = "CacheManager";
    private static final String FILENAME_CACHED_TABLE = "cached_table.txt";

    private static CacheManager sInstance;
    private DownloadManager mDownloadManager;
    private boolean mInit;
    private String mUrlTableFilePath;

    private List<CacheData> mCacheDataList;

    private CacheManager() {
        throw new UnsupportedOperationException("This class is deprecated");
    }

    public static ICacheManager getInstance() {
        if (sInstance == null) {
            sInstance = new CacheManager();
        }
        return sInstance;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void init(Context context) {
        if (mInit) {
            return;
        }
        mDownloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

        try {
            File tf = new File(context.getFilesDir(), FILENAME_CACHED_TABLE);
            tf.createNewFile();
            mUrlTableFilePath = tf.getAbsolutePath();
            mCacheDataList = (List<CacheData>) FileUtils.readObjectFromFile(mUrlTableFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }


        mInit = true;
    }

    public void download(String url) {
        if (mCacheDataList != null) {
            for (CacheData cacheData : mCacheDataList) {
                if (cacheData.url.equals(url)) {
                    Log.i(TAG, "download: 已经下载完毕");
                    return;
                }
            }
        }
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        mDownloadManager.enqueue(request);
    }

    private void saveIdToFile(long id, String url) {
        Log.d(TAG, "下载完毕，保存下载信息到本地: " + id + ", " + url);
        CacheData cacheData = new CacheData();
        cacheData.id = id;
        cacheData.url = url;

        ArrayList<CacheData> cacheDataList = (ArrayList<CacheData>) FileUtils.readObjectFromFile(mUrlTableFilePath);
        if (cacheDataList == null) {
            cacheDataList = new ArrayList<>();
        }
        cacheDataList.add(cacheData);
        FileUtils.writeObjectToFile(cacheDataList, mUrlTableFilePath);
    }

    public void notifyDownloadComplete(String id) {
        long lid = Long.parseLong(id);
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(lid);
        Cursor c = mDownloadManager.query(query);
        if (c.moveToFirst()) {
            String url = c.getString(c.getColumnIndex(DownloadManager.COLUMN_URI));
            saveIdToFile(lid, url);
        }
        c.close();
    }

    @Override
    public List<Uri> loadCache() {
        ArrayList<CacheData> list = (ArrayList<CacheData>) FileUtils.readObjectFromFile(mUrlTableFilePath);
        Log.d(TAG, "loadCache: " + list);
        List<Uri> uriList = new ArrayList<>();
        if (list == null) {
            return null;
        }
        for (CacheData cacheData : list) {
            long id = cacheData.id;
            DownloadManager.Query query = new DownloadManager.Query()
                    .setFilterById(id)
                    .setFilterByStatus(DownloadManager.STATUS_SUCCESSFUL);
            Cursor c = mDownloadManager.query(query);
            if (c.moveToFirst()) {
                String uri = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                Log.d(TAG, "loadCache: uri = " + uri);
                uriList.add(Uri.parse(uri));
            }
            c.close();
        }
        return uriList;
    }
}
