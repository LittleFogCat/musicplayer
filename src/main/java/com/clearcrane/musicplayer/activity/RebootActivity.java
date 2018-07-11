package com.clearcrane.musicplayer.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;

import com.clearcrane.musicplayer.R;
import com.clearcrane.musicplayer.common.utils.SystemUtils;

public class RebootActivity extends Activity {
    private Handler mHandler = new Handler();
    private boolean mNeedRestart = true;
    private Runnable mCallback = () -> {
        if (mNeedRestart) {
            SystemUtils.reboot(this);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reboot);

        mHandler.postDelayed(mCallback, 30 * 1000);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        mNeedRestart = false;
        mHandler.removeCallbacks(mCallback);
        finish();
        return super.onKeyDown(keyCode, event);
    }
}
