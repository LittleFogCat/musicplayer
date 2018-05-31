package com.clearcrane.musicplayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.clearcrane.musicplayer.controller.CoreService;

/**
 * 入口之开机
 */
public class BootReceiver extends BroadcastReceiver {
    private static final String TAG = "BootReceiver";
    public static final String ACTION = "com.clearcrane.musicplayer.intent.action.boot";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction()) &&
                !ACTION.equalsIgnoreCase(intent.getAction())) {
            Log.w(TAG, "onReceive: " + intent.getAction());
            return;
        }
        Log.d(TAG, "onReceive: " + intent.getAction());
        Intent i = new Intent(context, CoreService.class);
        context.startService(i);
    }
}
