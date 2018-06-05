package com.clearcrane.musicplayer.common.utils;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by jjy on 2018/6/4.
 * <p>
 * localTime + delta = serverTime
 */

public class DateUtil {
    private static boolean init = false;
    private static long delta = 0;

    public static void init() {
        ExecutorService service = Executors.newCachedThreadPool();
        service.execute(() -> {
            try {
                final URL url = new URL("http://www.baidu.com");
                URLConnection conn = url.openConnection();
                conn.connect();
                long serviceMills = conn.getDate();
                long localMills = System.currentTimeMillis();
                delta = serviceMills - localMills;
                init = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public static boolean isInit() {
        return init;
    }

    public static long getLocalTimeMillis() {
        return System.currentTimeMillis();
    }

    public static long getServerTimeMillis() {
        return System.currentTimeMillis() + delta;
    }

    public static long getServerTimeMillis(long localTime) {
        return localTime + delta;
    }

    public static long getLocalTimeMillis(long serverTime) {
        return serverTime - delta;
    }
}
