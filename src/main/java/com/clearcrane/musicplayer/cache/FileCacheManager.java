package com.clearcrane.musicplayer.cache;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.clearcrane.musicplayer.common.Constant;
import com.clearcrane.musicplayer.common.utils.FileUtils;
import com.clearcrane.musicplayer.common.utils.HttpUtil;
import com.clearcrane.musicplayer.common.utils.SpUtils;

import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class FileCacheManager implements ICacheManager {
    private static final String TAG = "FileCacheManager";
    private static final String DIR_NAME = "music";
    private static ICacheManager sInstance;

    private File mDir;
    private ExecutorService mExecutor;

    private FileCacheManager(Context context) {
        int nThreads = SpUtils.getInstance().get(Constant.SP_KEY_CACHE_THREADS, Constant.DEFAULT_CACHE_THREADS);
        mExecutor = Executors.newFixedThreadPool(nThreads);

        mDir = new File(context.getFilesDir(), DIR_NAME);
        if (mDir.exists() || !mDir.isDirectory()) {
            mDir.delete();
            mDir.mkdirs();
        }
        Log.d(TAG, "Constructor: nThreads = " + nThreads + ", mDir = " + mDir);
    }

    public static ICacheManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new FileCacheManager(context);
        }
        return sInstance;
    }

    @Override
    public void download(String url) {
        Log.d(TAG, "开始下载: " + url);
        mExecutor.execute(new DownloadTask(url));
    }

    @Override
    public void notifyDownloadComplete(String url) {
        Log.d(TAG, "文件下载完毕: " + url);
    }

    @Override
    public List<Uri> loadCache() {
        File[] files = mDir.listFiles();
        if (files == null || files.length == 0) {
            return null;
        }
        List<Uri> res = new ArrayList<>();
        for (File file : files) {
            res.add(Uri.fromFile(file));
        }

        return res;
    }

    private class DownloadTask implements Runnable {
        private String mUrl;
        private HttpUtil mHttp;

        DownloadTask(String url) {
            mUrl = url;
            mHttp = new HttpUtil(url);
        }

        @Override
        public void run() {
            File file = checkFile();
            if (file == null) {
                return;
            }
            try {
                InputStream is = mHttp.getInputStream();
                if (is == null) {
                    Log.w(TAG, "DownloadTask: read input stream failed!");
                    return;
                }
                FileUtils.writeToFile(file, is);
                is.close();
                notifyDownloadComplete(mUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        /**
         * 如果文件已经下载完毕，则返回null。如果文件已经存在，但是文件大小和远端不同，则删除本地文件，重新下载。
         * 如果url有误，返回null。
         * <p>
         * 如果url正确并且文件不存在，那么创建文件，并返回该文件。
         * todo 断点续传
         *
         * @return 如果文件已经下载完毕，或者Url有误，返回null，否则返回文件
         */
        @Nullable
        private File checkFile() {
            int lastIndexOfX = mUrl.lastIndexOf("/");
            if (lastIndexOfX >= mUrl.length() - 1) {
                return null;
            }
            String filename = mUrl.substring(lastIndexOfX + 1);
            File file = new File(mDir, filename);
            if (file.exists()) {
                long fileSize = file.length();
                long remoteSize = Long.parseLong(mHttp.head("Content-Length"));
                Log.d(TAG, "filesize = " + fileSize + ", remotesize = " + remoteSize);
                if (fileSize == remoteSize) {
                    Log.d(TAG, "已经下载完毕: url = " + mUrl + ", file = " + file.getAbsolutePath());
                    return null;
                } else {
                    Log.d(TAG, "未下载完毕，开始下载");
                    file.delete();
                }
            }
            try {
                file.createNewFile();
                return file;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}
