package com.clearcrane.musicplayer.common.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
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

    public void getAsync(Callback callback) {
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

    public InputStream getInputStream() {
        try {
            return mConn.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String head(String field){
        return mConn.getHeaderField(field);
    }

    public interface Callback {
        void onResponse(String response);
    }
}
