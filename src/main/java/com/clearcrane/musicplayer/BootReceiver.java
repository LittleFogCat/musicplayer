package com.clearcrane.musicplayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.clearcrane.musicplayer.controller.CoreService;

public class BootReceiver extends BroadcastReceiver {
    private static final String TAG = "BootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Log.w(TAG, "onReceive: " + intent.getAction());
            return;
        }
        Log.d(TAG, "onReceive: " + intent.getAction());
        Intent i = new Intent(context, CoreService.class);
        context.startService(i);
    }
}
