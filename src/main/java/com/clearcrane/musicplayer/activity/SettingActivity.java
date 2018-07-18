package com.clearcrane.musicplayer.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;

import com.clearcrane.musicplayer.R;
import com.clearcrane.musicplayer.common.ToastUtil;
import com.clearcrane.musicplayer.common.utils.SpUtils;

import java.util.Timer;
import java.util.TimerTask;

import static com.clearcrane.musicplayer.common.Constant.DEFAULT_CACHE_NUM;
import static com.clearcrane.musicplayer.common.Constant.DEFAULT_CACHE_THREADS;
import static com.clearcrane.musicplayer.common.Constant.DEFAULT_WS_ADDR;
import static com.clearcrane.musicplayer.common.Constant.SP_KEY_CACHE_NUM;
import static com.clearcrane.musicplayer.common.Constant.SP_KEY_CACHE_THREADS;
import static com.clearcrane.musicplayer.common.Constant.SP_KEY_HOME_URL;
import static com.clearcrane.musicplayer.common.Constant.SP_KEY_IS_LOCAL;
import static com.clearcrane.musicplayer.common.ToastUtil.LENGTH_SHORT;

public class SettingActivity extends Activity {
    private Button mBtnSave;
    private EditText mEtHome;
    private EditText mEtCacheThreads;
    private Switch mSwLocal;
    private SpUtils mSpUtils;
    private EditText mEtCachedNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        mSpUtils = SpUtils.getInstance();

        mBtnSave = findViewById(R.id.btnSave);
        mBtnSave.setOnClickListener(this::saveSettings);

        mEtHome = findViewById(R.id.etHome);
        mEtHome.setText(mSpUtils.get(SP_KEY_HOME_URL, DEFAULT_WS_ADDR));

        mEtCacheThreads = findViewById(R.id.etCacheThreads);
        mEtCacheThreads.setText(String.valueOf(mSpUtils.get(SP_KEY_CACHE_THREADS, DEFAULT_CACHE_THREADS)));

        mSwLocal = findViewById(R.id.swLocal);
        boolean enableLocal = mSpUtils.get(SP_KEY_IS_LOCAL, false);
        mSwLocal.setChecked(enableLocal);
        mSwLocal.setOnCheckedChangeListener((buttonView, isChecked) -> {
            mSpUtils.save(SP_KEY_IS_LOCAL, isChecked);
            ToastUtil.show(this, "已保存", LENGTH_SHORT);
            mEtCachedNum.setEnabled(isChecked);
        });

        mEtCachedNum = findViewById(R.id.etCachedMusicNum);
        mEtCachedNum.setEnabled(enableLocal);
        mEtCachedNum.setText(String.valueOf(mSpUtils.get(SP_KEY_CACHE_NUM, DEFAULT_CACHE_NUM)));
    }

    private void saveSettings(View view) {
        String homeUrl = mEtHome.getText().toString();
        mSpUtils.save(SP_KEY_HOME_URL, homeUrl);

        int cts = Integer.valueOf(mEtCacheThreads.getText().toString());
        mSpUtils.save(SP_KEY_CACHE_THREADS, cts);

        if (mEtCachedNum.isEnabled()) {
            int cacheNum = Integer.parseInt(mEtCachedNum.getText().toString());
            mSpUtils.save(SP_KEY_CACHE_NUM, cacheNum);
        }

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                getApplicationContext().startActivity(intent);
            }
        }, 2000);

        System.exit(0);
    }

}
