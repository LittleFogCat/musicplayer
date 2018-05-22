package com.clearcrane.musicplayer;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.clearcrane.musicplayer.controller.Controller;

/**
 * 音乐盒子启动服务
 */
public class CoreService extends Service {
    private Controller mController;

    public CoreService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mController = Controller.getInstance();
        mController.startWork();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
