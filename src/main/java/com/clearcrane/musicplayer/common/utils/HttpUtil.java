package com.clearcrane.musicplayer.common.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by jjy on 2018/5/26.
 */

public class HttpUtil {
    private HttpURLConnection mConn;
    private ExecutorService thread = Executors.newCachedThreadPool();

    public HttpUtil(String url) {
        try {
            URL URL = new URL(url);
            mConn = (HttpURLConnection) URL.openConnection();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void get(Callback callback) {
        StringBuilder content = new StringBuilder();
        thread.execute(() -> {
            try {
                InputStreamReader reader = new InputStreamReader(mConn.getInputStream());
                BufferedReader br = new BufferedReader(reader);
                String line;
                while ((line = br.readLine()) != null) {
                    content.append(line).append("\n");
                }
                callback.onResponse(content.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public interface Callback {
        void onResponse(String response);
    }
}
