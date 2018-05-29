package com.clearcrane.musicplayer.controller;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.clearcrane.musicplayer.musicservice.MusicService;

/**
 * 音乐盒子核心服务
 */
public class CoreService extends Service {
    private static final String TAG = "CoreService";
    private Controller mController;

    public CoreService() {
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate: ");
        super.onCreate();
        initController();// 初始化Controller
        startMusicService();// 启动音乐播放服务
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: ");
        return super.onStartCommand(intent, flags, startId);
    }

    private void initController() {
        mController = Controller.getInstance();
        mController.startWork();
    }

    private void startMusicService() {
        Intent intent = new Intent(this, MusicService.class);
        startService(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

}
