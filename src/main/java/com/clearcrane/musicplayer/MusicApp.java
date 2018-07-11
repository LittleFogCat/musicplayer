package com.clearcrane.musicplayer;

import android.app.Application;
import android.content.Intent;

import com.clearcrane.musicplayer.activity.MainActivity;
import com.clearcrane.musicplayer.common.utils.SPUtils;
import com.clearcrane.musicplayer.common.utils.SystemUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by jjy on 2018/5/30.
 * <p>
 * 错误重启并保存日志
 */

@SuppressWarnings("ALL")
public class MusicApp extends Application implements Thread.UncaughtExceptionHandler {
    @Override
    public void onCreate() {
        super.onCreate();
        Thread.setDefaultUncaughtExceptionHandler(this);
        SystemUtils.getInstance().init(this);
        SPUtils.init(this);
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        File filesDir = getFilesDir();
        if (!filesDir.exists()) {
            filesDir.mkdirs();
        }
        String filename = "errlog_" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        File logFile = new File(filesDir, filename);
        try {
            logFile.createNewFile();
            FileWriter fw = new FileWriter(logFile);

            PrintWriter pw = new PrintWriter(fw);
            e.printStackTrace(pw);

            while (e.getCause() != null) {
                e.getCause().printStackTrace(pw);
                e = e.getCause();
            }

            pw.flush();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                startActivity(intent);
            }
        }, 3000);

        System.exit(1);
    }
}
