package com.clearcrane.musicplayer.controller;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import com.clearcrane.musicplayer.RebootActivity;
import com.clearcrane.musicplayer.common.utils.DateUtil;
import com.clearcrane.musicplayer.musicservice.MusicService;

import java.util.Calendar;

/**
 * 音乐盒子核心服务
 * <p>
 * 不管从BootReceiver还是mainactivity最终都是进入这个服务
 */
public class CoreService extends Service {
    private static final String TAG = "CoreService";
    private Controller mController;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    public CoreService() {
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate: ");
        super.onCreate();
        initController();// 初始化Controller
        startMusicService();// 启动音乐播放服务
        DateUtil.init();// 初始化DateUtil
        startRebootTask();// 启动自动重启任务
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

    /**
     * 重启任务
     */
    private void startRebootTask() {
        mHandler.postDelayed(() -> {
            // 延迟30秒执行
            long serverTime = DateUtil.getServerTimeMillis();

            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(serverTime);
            int hourOfDay = c.get(Calendar.HOUR_OF_DAY);
            if (hourOfDay >= 4) {
                c.setTimeInMillis(c.getTimeInMillis() + 86400000);// 时间到第二天
            }
            // 4点重启
            c.set(Calendar.HOUR_OF_DAY, 4);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            long restartTimeLocal = DateUtil.getLocalTimeMillis(c.getTimeInMillis());
            AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
            if (am != null) {
                PendingIntent pi = PendingIntent.getActivity(CoreService.this, 0, new Intent(this, RebootActivity.class), 0);
                am.set(AlarmManager.RTC, restartTimeLocal, pi);
            } else {
                mHandler.postAtTime(() -> startActivity(new Intent(CoreService.this, RebootActivity.class)), restartTimeLocal);
            }
        }, 30000);
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

}
