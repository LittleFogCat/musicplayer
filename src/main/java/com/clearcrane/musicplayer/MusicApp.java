package com.clearcrane.musicplayer;

import android.app.Application;
import android.content.Intent;

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
 */

@SuppressWarnings("ALL")
public class MusicApp extends Application implements Thread.UncaughtExceptionHandler {
    @Override
    public void onCreate() {
        super.onCreate();
        Thread.setDefaultUncaughtExceptionHandler(this);
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

        Intent intent = new Intent(BootReceiver.ACTION);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                sendBroadcast(intent);
            }
        }, 3000);

        System.exit(1);
    }
}
