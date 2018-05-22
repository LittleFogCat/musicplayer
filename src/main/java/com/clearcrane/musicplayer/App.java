package com.clearcrane.musicplayer;

import android.app.Application;

import com.clearcrane.musicplayer.controller.Controller;

/**
 * Created by jjy on 2018/5/22.
 */

public class App extends Application {
    private boolean mFirst = true;

    @Override
    public void onCreate() {
        super.onCreate();
        if (mFirst) {
            initController();
        }
        mFirst = false;
    }

    private void initController() {
        Controller controller = Controller.getInstance();
        controller.startWork();
    }
}
