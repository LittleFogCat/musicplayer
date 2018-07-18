package com.clearcrane.musicplayer;

import android.app.Application;
import android.content.Intent;

import com.clearcrane.musicplayer.activity.MainActivity;
import com.clearcrane.musicplayer.common.utils.SpUtils;
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

        // 工具类在很多地方要用到，进入app时就应该初始化，避免出错。其他类在Activity或者Service中初始化。
        SystemUtils.getInstance().init(this);
        SpUtils.init(this);
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
